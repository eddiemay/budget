package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.QueryParam;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

import org.joda.time.DateTime;

public class BillStore extends GenericDAOStore<Bill> {
	
	private final AccountStore accountStore;
	public BillStore(DAO<Bill> dao, AccountStore accountStore) {
		super(dao);
		this.accountStore = accountStore;
	}
	
	public List<Bill> getByDateRange(int portfolioId, DateTime start, DateTime end)
			throws DD4StorageException {
		return get(
				QueryParam.newBuilder().setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build(),
				QueryParam.newBuilder().setColumn("due_date").setOperan(">=").setValue(String.valueOf(start.getMillis())).build(),
				QueryParam.newBuilder().setColumn("due_date").setOperan("<").setValue(String.valueOf(end.getMillis())).build());
	}
	
	@Override
	public Bill create(Bill bill) throws DD4StorageException {
		bill = super.create(bill);
		accountStore.updateBalance(bill.getAccountId(), bill.getDueDate(), bill.getAmountDue());
		for (Transaction trans : bill.getTransactionList()) {
			accountStore.updateBalance(trans.getDebitAccountId(), bill.getDueDate(), -trans.getAmount());
		}
		return bill;
	}
	
	@Override
	public Bill update(int id, Function<Bill, Bill> updater) throws DD4StorageException {
		Bill orig = get(id);
		Bill bill = super.update(id, updater);
		accountStore.updateBalance(bill.getAccountId(), bill.getDueDate(),
				bill.getAmountDue() - orig.getAmountDue());
		for (Transaction trans : bill.getTransactionList()) {
			for (Transaction origTrans : orig.getTransactionList()) {
				if (trans.getDebitAccountId() == origTrans.getDebitAccountId()) {
					accountStore.updateBalance(trans.getDebitAccountId(), bill.getDueDate(),
							origTrans.getAmount() - trans.getAmount());
				}
			}
		}
		return bill;
	}
	
	@Override
	public void delete(int id) throws DD4StorageException {
		Bill bill = get(id);
		accountStore.updateBalance(bill.getAccountId(), bill.getDueDate(), -bill.getAmountDue());
		for (Transaction trans : bill.getTransactionList()) {
			accountStore.updateBalance(trans.getDebitAccountId(), bill.getDueDate(), trans.getAmount());
		}
	}
	
	public List<Bill> applyTemplate(Template template, DateTime refDate) throws DD4StorageException {
		for (TemplateBill tempBill : template.getBillList()) {
			Bill.Builder bill = Bill.newBuilder()
					.setPortfolioId(template.getPortfolioId())
					.setAccountId(tempBill.getAccountId())
					.setTemplateId(template.getId())
					.setDueDate(refDate.plusDays(tempBill.getDueDay() - 1).getMillis())
					.setAmountDue(tempBill.getAmountDue())
					.setName(tempBill.getName())
					.setStatus(PaymentStatus.PS_ESTIMATED_AMOUNT)
					.setActive(true);
			for (TemplateTransaction tempTrans : tempBill.getTransactionList()) {
				bill.addTransaction(Transaction.newBuilder()
						.setDebitAccountId(tempTrans.getDebitAccountId())
						.setAmount(tempTrans.getAmount())
						.setStatus(PaymentStatus.PS_ESTIMATED_AMOUNT)
						.setActive(true));
			}
			create(bill.build());
		}
		return getByDateRange(template.getPortfolioId(), refDate, refDate.plusMonths(1));
	}
}
