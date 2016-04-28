package com.digitald4.budget.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digitald4.budget.dao.AccountDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Table(schema="budget",name="account")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Account o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Account o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Account o"),//AUTO-GENERATED
	@NamedQuery(name = "findByPortfolio", query="SELECT o FROM Account o WHERE o.PORTFOLIO_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByParent", query="SELECT o FROM Account o WHERE o.PARENT_ACCOUNT_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM account o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Account extends AccountDAO{
	public Account(EntityManager entityManager){
		super(entityManager);
	}
	public Account(EntityManager entityManager, Integer id){
		super(entityManager, id);
	}
	public Account(EntityManager entityManager, Account orig){
		super(entityManager, orig);
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		return super.toJSON().put("sortValue", toString());
	}
	
	public String toString() {
		return getName();
	}
	
	public List<Transaction> getTransactions() {
		List<Transaction> transactions = new ArrayList<Transaction>(getDebitTransactions());
		transactions.addAll(getCreditTransactions());
		for (Bill bill : getBills()) {
			for (Transaction trans : bill.getTransactions()) {
				if (trans.getCreditAccountId() == null) {
					transactions.add(trans);
				}
			}
		}
		return transactions;
	}
	
	public List<Transaction> getTransactions(Date startDate, Date endDate) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (Transaction trans : getTransactions()) {
			if ((startDate == null || !trans.getDate().before(startDate)) &&
					(endDate == null || !trans.getDate().after(endDate))) {
				transactions.add(trans);
			}
		}
		return transactions;
	}
	
	public List<Bill> getBills(Date startDate, Date endDate) {
		List<Bill> bills = new ArrayList<Bill>();
		for (Bill bill : getBills()) {
			if ((startDate == null || !bill.getDueDate().before(startDate)) &&
					(endDate == null || !bill.getDueDate().after(endDate))) {
				bills.add(bill);
			}
		}
		return bills;
	}
	
	public double getBalancePre(Transaction asOf) {
		double bal = 0;
		for (Transaction trans : getTransactions()) {
			if (trans.isBefore(asOf)) {
				if (trans.getCreditAccount() == this) {
					bal += trans.getAmount();
				}
				if (trans.getDebitAccount() == this) {
					bal -= trans.getAmount();
				}
			}
		}
		return bal;
	}
	
	public double getBalancePre(TemplateTransaction asOf) {
		double bal = 0;
		TemplateBill bill = asOf.getTemplateBill();
		Template template = bill.getTemplate();
		for (TemplateBill templateBill : template.getTemplateBills()) {
			for (TemplateTransaction trans : templateBill.getTemplateTransactions()) {
				if (trans.getTemplateBill().compareTo(bill) < 0) {
					if (templateBill.getAccount() == this) {
						bal += trans.getAmount();
					}
					if (trans.getDebitAccount() == this) {
						bal -= trans.getAmount();
					}
				}
			}
		}
		return bal;
	}
	
	public double getBalancePost(Transaction asOf) {
		double bal = getBalancePre(asOf);
		if (asOf.getCreditAccount() == this) {
			bal += asOf.getAmount();
		}
		if (asOf.getDebitAccount() == this) {
			bal -= asOf.getAmount();
		}
		return bal;
	}
	
	public double[] getMonthTotals(int year) {
		double[] totals = new double[13];
		for (Transaction transaction : getTransactions(DateTime.parse(year + "-01-01").toDate(),
				DateTime.parse(year + "-12-31").toDate())) {
			double amount = transaction.getAmount();
			if (transaction.getCreditAccount() == this) {
				totals[new DateTime(transaction.getDate()).getMonthOfYear() - 1] += amount;
				totals[12] += amount;
			}
			if (transaction.getDebitAccount() == this) {
				totals[new DateTime(transaction.getDate()).getMonthOfYear() - 1] -= amount;
				totals[12] -= amount;
			}
		}
		return totals;
	}
}
