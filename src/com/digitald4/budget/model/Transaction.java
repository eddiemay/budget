package com.digitald4.budget.model;

import java.util.Date;

import com.digitald4.budget.dao.TransactionDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;
@Entity
@Table(schema="budget",name="transaction")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Transaction o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Transaction o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Transaction o"),//AUTO-GENERATED
	@NamedQuery(name = "findByBill", query="SELECT o FROM Transaction o WHERE o.BILL_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByDebitAccount", query="SELECT o FROM Transaction o WHERE o.DEBIT_ACCOUNT_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByCreditAccount", query="SELECT o FROM Transaction o WHERE o.CREDIT_ACCOUNT_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM transaction o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Transaction extends TransactionDAO {
	public Transaction(EntityManager entityManager) {
		super(entityManager);
	}
	
	public Transaction(EntityManager entityManager, Integer id) {
		super(entityManager, id);
	}
	
	public Transaction(EntityManager entityManager, Transaction orig) {
		super(entityManager, orig);
	}
	
	public Transaction(TemplateTransaction templateTransaction) throws Exception {
		super(templateTransaction.getEntityManager());
		setDebitAccountId(templateTransaction.getDebitAccountId());
		setAmount(templateTransaction.getAmount());
	}

	public Date getPaymentDate() {
		Date date = getDate();
		if (date == null && getBill() != null) {
			date = getBill().getPaymentDate();
		}
		return date;
	}
	
	@Override
	public Account getCreditAccount() {
		Account account = super.getCreditAccount();
		if (account == null) {
			Bill bill = getBill();
			if (bill != null) {
				account = bill.getAccount();
			}
		}
		return account;
	}
	
	public double getDebitAcctBalPre() {
		return getDebitAccount().getBalancePre(this);
	}
	
	public double getDebitAcctBalPost() {
		return getDebitAccount().getBalancePost(this);
	}
	
	public double getCreditAcctBalPre() {
		return getCreditAccount().getBalancePre(this);
	}
	
	public double getCreditAcctBalPost() {
		return getCreditAccount().getBalancePost(this);
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON()
				.put("name", getName())
				.put("date", getDate());
		double amount = getAmount();
		double debitAcctBalPre = getDebitAcctBalPre();
		json.put("debitAcctBalPre", debitAcctBalPre)
				.put("debitAcctBalPost", (debitAcctBalPre - amount));
		Account creditAccount = getCreditAccount();
		if (creditAccount != null) {
			double creditAcctBalPre = getCreditAcctBalPre();
			json.put("creditAccountId", creditAccount.getId())
					.put("creditAcctBalPre", creditAcctBalPre)
					.put("creditAcctBalPost", (creditAcctBalPre + amount));
		}
		return json;
	}
	
	public String getName() {
		String name = getNameD();
		if (name == null) {
			Bill bill = getBill();
			if (bill != null) {
				name = bill.getName();
			}
		}
		return name;
	}
	
	public Date getDate() {
		Date date = getDateD();
		if (date == null) {
			Bill bill = getBill();
			if (bill != null) {
				date = bill.getDueDate();
			}
		}
		return date;
	}
	
	@Override
	public int compareTo(Object o) {
		Transaction trans = (Transaction)o;
		int ret = getDate().compareTo(trans.getDate());
		if (ret != 0) {
			return ret;
		}
		if (getAmount() > trans.getAmount()) {
			ret = -1;
		} else if (getAmount() < trans.getAmount()) {
			ret = 1;
		}
		if (getAmount() < 0 || trans.getAmount() < 0) {
			ret *= -1;
		}
		if (ret != 0) {
			return ret;
		}
		return super.compareTo(o);
	}

	public boolean isBefore(Transaction trans) {
		return compareTo(trans) == -1;
	}
}
