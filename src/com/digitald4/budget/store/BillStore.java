package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

import org.joda.time.DateTime;

public class BillStore extends GenericDAOStore<Bill> {
	
	public BillStore(DAO<Bill> dao) {
		super(dao);
	}
	
	public List<Bill> getByDateRange(int portfolioId, DateTime start, DateTime end)
			throws DD4StorageException {
		return get(new QueryParam("portfolio_id", "=", portfolioId),
				new QueryParam("due_date", ">=", start.getMillis()),
				new QueryParam("due_date", "<", end.getMillis()));
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
					.setStatus(PaymentStatus.PS_NOT_SCHEDULED)
					.setActive(true);
			for (TemplateTransaction tempTrans : tempBill.getTransactionList()) {
				bill.addTransaction(Transaction.newBuilder()
						.setDebitAccountId(tempTrans.getDebitAccountId())
						.setAmount(tempTrans.getAmount())
						.setStatus(PaymentStatus.PS_NOT_SCHEDULED)
						.setActive(true));
			}
			create(bill.build());
		}
		return getByDateRange(template.getPortfolioId(), refDate, refDate.plusMonths(1));
	}
}
