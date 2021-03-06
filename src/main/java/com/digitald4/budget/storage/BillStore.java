package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.PaymentStatus;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4Protos.Query.Filter;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Pair;
import java.util.function.UnaryOperator;

import java.util.stream.Collectors;
import javax.inject.Provider;
import org.joda.time.DateTime;

public class BillStore extends GenericStore<Bill> {
	
	private final BalanceStore balanceStore;
	private final Store<TemplateBill> templateBillStore;

	public BillStore(Provider<DAO> daoProvider, BalanceStore balanceStore, Store<TemplateBill> templateBillStore) {
		super(Bill.class, daoProvider);
		this.balanceStore = balanceStore;
		this.templateBillStore = templateBillStore;
	}
	
	@Override
	public Bill create(Bill bill_) {
		Bill bill = super.create(bill_);
		balanceStore
				.applyUpdate(bill.getPortfolioId(), bill.getAccountId(), bill.getYear(), bill.getMonth(), bill.getAmountDue());
		bill.getTransactionMap()
				.forEach((acctId, trans) -> balanceStore
						.applyUpdate(bill.getPortfolioId(), acctId, bill.getYear(), bill.getMonth(), -trans.getAmount()));
		return bill;
	}
	
	@Override
	public Bill update(long id, final UnaryOperator<Bill> updater) {
		Bill orig = get(id);
		Bill bill = super.update(id, latest -> {
			Bill updated = updater.apply(latest);
			// If the amount due changed and there is a transaction that is of the same amount
			// as the original amount due, update that transaction to the new value.
			if (latest.getAmountDue() != updated.getAmountDue()) {
				double newAmount = updated.getAmountDue();
				updated = updated.toBuilder()
						.clearField(Bill.getDescriptor().findFieldByName("transaction"))
						.putAllTransaction(updated.getTransactionMap().entrySet().stream()
								.map(entry -> Pair.of(entry.getKey(), latest.getAmountDue() == entry.getValue().getAmount()
										? entry.getValue().toBuilder().setAmount(newAmount).build() : entry.getValue()))
								.collect(Collectors.toMap(Pair::getLeft, Pair::getRight)))
						.build();
			}
			return updated;
		});
		// The best way to keep everything synced is to minus off the transaction from the balance...
		balanceStore
				.applyUpdate(orig.getPortfolioId(), orig.getAccountId(), orig.getYear(), orig.getMonth(), -orig.getAmountDue());
		orig.getTransactionMap()
				.forEach((acctId, trans) -> balanceStore.applyUpdate(
						orig.getPortfolioId(), acctId, orig.getYear(), orig.getMonth(), trans.getAmount()));

		// and then add it back to the balance. This approch handles amount due, date and account changes.
		balanceStore.applyUpdate(
				bill.getPortfolioId(), bill.getAccountId(), bill.getYear(), bill.getMonth(), bill.getAmountDue());
		bill.getTransactionMap()
				.forEach((acctId, trans) -> balanceStore.applyUpdate(
						bill.getPortfolioId(), acctId, bill.getYear(), bill.getMonth(), -trans.getAmount()));
		return bill;
	}
	
	@Override
	public void delete(long id) {
		Bill bill = get(id);
		super.delete(id);
		balanceStore.applyUpdate(
				bill.getPortfolioId(), bill.getAccountId(), bill.getYear(), bill.getMonth(), -bill.getAmountDue());
		bill.getTransactionMap()
				.forEach((acctId, trans) -> balanceStore.applyUpdate(
						bill.getPortfolioId(), acctId, bill.getYear(), bill.getMonth(), trans.getAmount()));
	}
	
	public QueryResult<Bill> applyTemplate(Template template, DateTime refDate) {
		templateBillStore
				.list(Query.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("template_id").setOperator("=").setValue("" + template.getId()))
						.build())
				.getResults()
				.forEach(tempBill -> {
					DateTime date = refDate.plusDays(tempBill.getDueDay() - 1);
					create(Bill.newBuilder()
							.setPortfolioId(template.getPortfolioId())
							.setAccountId(tempBill.getAccountId())
							.setTemplateId(template.getId())
							.setYear(date.getYear())
							.setMonth(date.getMonthOfYear())
							.setDay(date.getDayOfMonth())
							.setAmountDue(tempBill.getAmountDue())
							.setName(tempBill.getName())
							.setStatus(PaymentStatus.PS_ESTIMATED)
							.setInActive(false)
							.putAllTransaction(tempBill.getTransactionMap().entrySet().stream()
									.map(entry -> Pair.of(entry.getKey(), Transaction.newBuilder()
											.setAmount(entry.getValue())
											.setStatus(PaymentStatus.PS_ESTIMATED)
											.build()))
									.collect(Collectors.toMap(Pair::getLeft, Pair::getRight)))
							.build());
				});
		return list(Query.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(template.getPortfolioId())))
				.addFilter(Filter.newBuilder().setColumn("year").setValue(String.valueOf(refDate.getYear())))
				.addFilter(Filter.newBuilder().setColumn("month").setValue(String.valueOf(refDate.getMonthOfYear())))
				.build());
	}
}
