package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillTransUpdateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.PaymentStatusUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.TransactionUI;
import com.digitald4.budget.store.BillStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;

import org.joda.time.DateTime;

import java.util.List;

public class BillService extends DualProtoService<BillUI, Bill> {
	
	private final BillStore store;
	private final TemplateStore templateStore;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private static Function<BillUI, Bill> converter = new Function<BillUI, Bill>() {
		@Override
		public BillUI execute(Bill bill) {
			BillUI.Builder billUI = BillUI.newBuilder()
					.setId(bill.getId())
					.setPortfolioId(bill.getPortfolioId())
					.setAccountId(bill.getAccountId())
					.setTemplateId(bill.getTemplateId())
					.setDueDate(bill.getDueDate())
					.setAmountDue(bill.getAmountDue())
					.setName(bill.getName())
					.setPaymentDate(bill.getPaymentDate())
					.setRank(bill.getRank())
					.setStatus(PaymentStatusUI.valueOf(bill.getStatus().getNumber()));
			for (Transaction trans : bill.getTransactionList()) {
				billUI.addTransaction(TransactionUI.newBuilder()
						.setDebitAccountId(trans.getDebitAccountId())
						.setAmount(trans.getAmount())
						.setPaymentDate(trans.getPaymentDate())
						.setStatus(PaymentStatusUI.valueOf(trans.getStatus().getNumber()))
						.build());
			}
			return billUI.build();
		}
	};
	
	private static Function<Bill, BillUI> reverse = new Function<Bill, BillUI>() {
		@Override
		public Bill execute(BillUI billUI) {
			Bill.Builder bill = Bill.newBuilder()
					.setId(billUI.getId())
					.setPortfolioId(billUI.getPortfolioId())
					.setAccountId(billUI.getAccountId())
					.setTemplateId(billUI.getTemplateId())
					.setDueDate(billUI.getDueDate())
					.setAmountDue(billUI.getAmountDue())
					.setName(billUI.getName())
					.setPaymentDate(billUI.getPaymentDate())
					.setRank(billUI.getRank())
					.setStatus(PaymentStatus.valueOf(billUI.getStatus().getNumber()));
			for (TransactionUI trans : billUI.getTransactionList()) {
				bill.addTransaction(reverseTrans.execute(trans));
			}
			return bill.build();
		}
	};
	
	private static Function<Transaction, TransactionUI> reverseTrans =
			new Function<Transaction, TransactionUI>() {
				@Override
				public Transaction execute(TransactionUI trans) {
					return Transaction.newBuilder()
							.setDebitAccountId(trans.getDebitAccountId())
							.setAmount(trans.getAmount())
							.setPaymentDate(trans.getPaymentDate())
							.setStatus(PaymentStatus.valueOf(trans.getStatus().getNumber()))
							.build();
				}
	};
	
	public BillService(BillStore store, TemplateStore templateStore) {
		super(BillUI.class, store);
		this.store = store;
		this.templateStore = templateStore;
	}
	
	@Override
	public Function<BillUI, Bill> getConverter() {
		return converter;
	}
	
	@Override
	public Function<Bill, BillUI> getReverseConverter() {
		return reverse;
	}
	
	public BillUI create(BillCreateRequest request) throws DD4StorageException {
		return getConverter().execute(store.create(reverse.execute(request.getBill())));
	}
	
	public List<BillUI> list(BillListRequest request) throws DD4StorageException {
		DateTime start = new DateTime(request.getRefDate());
		DateTime end = null;
		switch (request.getDateRange()) {
			case DAY: end = start.plusDays(1); break;
			case WEEK: end = start.plusDays(7); break;
			case YEAR: end = start.plusYears(1); break;
			case CAL_MONTH: start = start.minusDays(start.getDayOfMonth() - 1);
				end = start.plusMonths(1).minusDays(1);
				start = start.minusDays(start.getDayOfWeek() - 1);
				end = end.plusDays(7 - end.getDayOfWeek());
				break;
			case MONTH:
			case UNSPECIFIED: end = start.plusMonths(1); break;
		}
		return threader.parDo(store.getByDateRange(request.getPortfolioId(), start, end), converter);
	}

	public BillUI updateTransaction(final BillTransUpdateRequest request)
			throws DD4StorageException {
		return getConverter().execute(store.update(request.getBillId(), new Function<Bill, Bill>() {
			@Override
			public Bill execute(Bill bill) {
				Bill.Builder builder = bill.toBuilder()
						.clearTransaction();
				for (TransactionUI trans : request.getTransactionList()) {
						builder.addTransaction(reverseTrans.execute(trans));
				}
				return builder.build();
			}
		}));
	}
	
	public List<BillUI> applyTemplate(ApplyTemplateRequest request) throws DD4StorageException {
		return threader.parDo(store.applyTemplate(templateStore.get(request.getTemplateId()),
				new DateTime(request.getRefDate())), getConverter());
	}
}
