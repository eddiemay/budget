package com.digitald4.budget.model;
import java.util.Date;

import com.digitald4.budget.dao.BillDAO;
import com.digitald4.common.component.CalEvent;
import com.digitald4.common.component.Notification;
import com.digitald4.common.util.FormatText;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@Entity
@Table(schema="budget",name="bill")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Bill o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Bill o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Bill o"),//AUTO-GENERATED
	@NamedQuery(name = "findByAccount", query="SELECT o FROM Bill o WHERE o.ACCOUNT_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM bill o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Bill extends BillDAO implements CalEvent {
	public Bill(EntityManager entityManager) {
		super(entityManager);
	}
	
	public Bill(EntityManager entityManager, Integer id) {
		super(entityManager, id);
	}
	
	public Bill(EntityManager entityManager, Bill orig) {
		super(entityManager, orig);
	}

	public Bill(TemplateBill templateBill, DateTime refDate) throws Exception {
		super(templateBill.getEntityManager());
		setAccountId(templateBill.getAccountId());
		setDueDate(refDate.plusDays(templateBill.getDueDay() - refDate.getDayOfMonth()).toDate());
		setNameD(templateBill.getNameD());
		setAmountDue(templateBill.getAmountDue());
		for (TemplateTransaction templateTransaction : templateBill.getTemplateTransactions()) {
			addTransaction(new Transaction(templateTransaction));
		}
		
		/*this.accountId = orig.getAccountId();
		this.templateId = orig.getTemplateId();
		this.dueDate = orig.getDueDate();
		this.nameD = orig.getNameD();
		this.paymentDateD = orig.getPaymentDateD();
		this.amountDue = orig.getAmountDue();
		this.statusId = orig.getStatusId();
		this.active = orig.isActive();
		this.description = orig.getDescription();*/
	}

	public double getPaid() {
		double paid = 0;
		for (Transaction trans : getTransactions()) {
			paid += trans.getAmount();
		}
		return paid;
	}
	
	public double getRemainingDue() {
		return getAmountDue() - getPaid();
	}
	
	public Date getPaymentDate() {
		Date date = getPaymentDateD();
		if (date == null) {
			date = getDueDate();
		}
		return date;
	}
	
	public double getAmountDue() {
		return getAmountDueD();
	}
	
	public Bill setAmountDue(double amountDue) throws Exception {
		double prior = getAmountDue();
		setAmountDueD(amountDue);
		for (Transaction trans : getTransactions()) {
			if (prior == trans.getAmount()) {
				trans.setAmount(amountDue).save();
			}
		}
		return this;
	}

	@Override
	public DateTime getStart() {
		return new DateTime(getPaymentDate());
	}

	@Override
	public DateTime getEnd() {
		return getStart();
	}

	@Override
	public String getTitle() {
		return getAccount().getName();
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public boolean isActiveOnDay(Date date) {
		return getPaymentDate().equals(date);
	}

	@Override
	public boolean isActiveBetween(DateTime start, DateTime end) {
		start = start.minusMillis(1);
		DateTime st = getStart();
		DateTime et = getEnd();
		// Did this event start any time between these periods or did these period start any time during this event
		return (start.isBefore(st) && end.isAfter(st) || st.isBefore(start) && et.isAfter(start));
	}

	@Override
	public boolean isCancelled() {
		return !isActive();
	}

	@Override
	public Notification<CalEvent> getNotification() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getName() {
		String name = getNameD();
		if (name == null) {
			name = "" + getAccount();
		}
		return name;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		double amountDue = getAmountDue();
		double acctBalPre = 0;
		JSONArray accounts = new JSONArray();
		Transaction first = null;
		if (!getTransactions().isEmpty()) {
			first = getTransactions().get(0);
		}
		Account account = getAccount();
		acctBalPre = first == null ? 0 : account.getBalancePre(first);
		for (Account ba : getAccount().getPortfolio().getAccounts()) {
			if (!ba.isPaymentAccount()) {
				continue;
			}
			double amount = 0;
			Integer id = null;
			for (Transaction trans : getTransactions()) {
				if (trans.getDebitAccount() == ba) {
					amount = trans.getAmount();
					id = trans.getId();
				}
			}
			if (account == ba) {
				amount -= amountDue;
			}
			double start = first == null ? 0 : ba.getBalancePre(first);
			accounts.put(new JSONObject().put("id", id)
					.put("accountId", ba.getId())
					.put("billId", getId())
					.put("preBal", start)
					.put("amount", amount)
					.put("postBal", FormatText.formatCurrency(start - amount)));
		}
		return super.toJSON()
				.put("acctBalPre", acctBalPre)
				.put("acctBalPost", FormatText.formatCurrency(acctBalPre + amountDue))
				.put("accounts", accounts)
				.put("amountDue", amountDue)
				.put("name", getName());
	}
	
	@Override
	public Bill setPropertyValue(String property, String value) throws Exception {
		if (property.equals("amountDue")) {
			setAmountDue(Double.parseDouble(value));
			return this;
		}
		try {
			return super.setPropertyValue(property, value);
		} catch (Exception e) {
			return super.setPropertyValue(property + "D", value);
		}
	}
	
	@Override
	public int compareTo(Object o) {
		Bill bill = (Bill)o;
		int ret = getDueDate().compareTo(bill.getDueDate());
		if (ret != 0) {
			return ret;
		}
		if (getAmountDue() > bill.getAmountDue()) {
			ret = -1;
		} else if (getAmountDue() < bill.getAmountDue()) {
			ret = 1;
		}
		if (getAmountDue() < 0 || bill.getAmountDue() < 0) {
			ret *= -1;
		}
		if (ret != 0) {
			return ret;
		}
		return super.compareTo(o);
	}
}
