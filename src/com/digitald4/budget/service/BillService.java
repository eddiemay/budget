package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.PaymentStatusUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.TransactionUI;
import com.digitald4.budget.store.BillStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;

import org.joda.time.DateTime;

import java.util.List;

public class BillService {
	
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
					.setStatus(PaymentStatusUI.valueOf(bill.getStatus().getNumber()))
					.setActive(bill.getActive())
					.setDescription(bill.getDescription());
			for (Transaction trans : bill.getTransactionList()) {
				billUI.addTransaction(TransactionUI.newBuilder()
						.setId(trans.getId())
						.setDebitAccountId(trans.getDebitAccountId())
						.setAmount(trans.getAmount())
						.setPaymentDate(trans.getPaymentDate())
						.setStatus(PaymentStatusUI.valueOf(trans.getStatus().getNumber()))
						.setActive(trans.getActive())
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
					.setStatus(PaymentStatus.valueOf(billUI.getStatus().getNumber()))
					.setActive(billUI.getActive())
					.setDescription(billUI.getDescription());
			for (TransactionUI trans : billUI.getTransactionList()) {
				bill.addTransaction(Transaction.newBuilder()
						.setId(trans.getId())
						.setDebitAccountId(trans.getDebitAccountId())
						.setAmount(trans.getAmount())
						.setPaymentDate(trans.getPaymentDate())
						.setStatus(PaymentStatus.valueOf(trans.getStatus().getNumber()))
						.setActive(trans.getActive())
						.build());
			}
			return bill.build();
		}
	};
	
	public BillService(BillStore store, TemplateStore templateStore) {
		this.store = store;
		this.templateStore = templateStore;
	}
	
	public BillUI create(BillCreateRequest request) throws DD4StorageException {
		return converter.execute(store.create(reverse.execute(request.getBill())));
	}
	
	public BillUI get(BillGetRequest request) throws DD4StorageException {
		return converter.execute(store.get(request.getBillId()));
	}
	
	public List<BillUI> list(BillListRequest request) throws DD4StorageException {
		DateTime start = new DateTime(request.getRefDate());
		DateTime end = null;
		switch (request.getDateRange()) {
			case DAY: end = start.plusDays(1); break;
			case WEEK: end = start.plusDays(7); break;
			case YEAR: end = start.plusYears(1); break;
			case MONTH:
		  default: end = start.plusMonths(1); break;
		}
		return threader.parDo(store.getByDateRange(request.getPortfolioId(), start, end), converter);
	}
	
	public boolean delete(BillDeleteRequest request) throws DD4StorageException {
		store.delete(request.getBillId());
		return true;
	}
	
	public List<BillUI> applyTemplate(ApplyTemplateRequest request) throws DD4StorageException {
		return threader.parDo(store.applyTemplate(templateStore.get(request.getTemplateId()),
				new DateTime(request.getRefDate())), converter);
	}
}
