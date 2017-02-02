package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillTransUpdateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.PaymentStatusUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.TransactionUI;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.TemplateStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;
import com.digitald4.common.server.JSONService;
import com.googlecode.protobuf.format.JsonFormat;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BillService extends DualProtoService<BillUI, Bill> {
	
	private final BillStore store;
	private final TemplateStore templateStore;

	private static Function<Bill, BillUI> converter = new Function<Bill, BillUI>() {
		@Override
		public BillUI apply(Bill bill) {
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
	
	private static Function<BillUI, Bill> reverse = new Function<BillUI, Bill>() {
		@Override
		public Bill apply(BillUI billUI) {
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
				bill.addTransaction(reverseTrans.apply(trans));
			}
			return bill.build();
		}
	};
	
	private static Function<TransactionUI, Transaction> reverseTrans =
			new Function<TransactionUI, Transaction>() {
				@Override
				public Transaction apply(TransactionUI trans) {
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
	public Function<Bill, BillUI> getConverter() {
		return converter;
	}
	
	@Override
	public Function<BillUI, Bill> getReverseConverter() {
		return reverse;
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
		return store.getByDateRange(request.getPortfolioId(), start, end).stream()
				.map(converter)
				.collect(Collectors.toList());
	}

	public BillUI updateTransaction(final BillTransUpdateRequest request)
			throws DD4StorageException {
		return getConverter().apply(store.update(request.getBillId(), new UnaryOperator<Bill>() {
			@Override
			public Bill apply(Bill bill) {
				Bill.Builder builder = bill.toBuilder()
						.clearTransaction();
				for (TransactionUI trans : request.getTransactionList()) {
						builder.addTransaction(reverseTrans.apply(trans));
				}
				return builder.build();
			}
		}));
	}
	
	public List<BillUI> applyTemplate(ApplyTemplateRequest request) throws DD4StorageException {
		return store.applyTemplate(templateStore.get(request.getTemplateId()),
				new DateTime(request.getRefDate())).stream().map(getConverter()).collect(Collectors.toList());
	}

	@Override
	public Object performAction(String action, String jsonRequest)
			throws DD4StorageException, JSONException, JsonFormat.ParseException {
		switch (action) {
			case "list":
				return JSONService.convertToJSON(list(
						JSONService.transformJSONRequest(BillListRequest.getDefaultInstance(), jsonRequest)));
			case "updateTrans":
				return JSONService.convertToJSON(updateTransaction(
						JSONService.transformJSONRequest(BillTransUpdateRequest.getDefaultInstance(), jsonRequest)));
			case "applyTemplate":
				return JSONService.convertToJSON(applyTemplate(
						JSONService.transformJSONRequest(ApplyTemplateRequest.getDefaultInstance(), jsonRequest)));
			default: return super.performAction(action, jsonRequest);
		}
	}
}
