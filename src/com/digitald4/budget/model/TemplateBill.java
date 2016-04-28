package com.digitald4.budget.model;
import java.util.List;

import com.digitald4.budget.dao.TemplateBillDAO;
import com.digitald4.common.util.FormatText;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@Entity
@Table(schema="budget",name="template_bill")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM TemplateBill o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM TemplateBill o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM TemplateBill o"),//AUTO-GENERATED
	@NamedQuery(name = "findByTemplate", query="SELECT o FROM TemplateBill o WHERE o.TEMPLATE_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM template_bill o WHERE o.ID=?"),//AUTO-GENERATED
})
public class TemplateBill extends TemplateBillDAO{
	public TemplateBill(EntityManager entityManager) {
		super(entityManager);
	}
	
	public TemplateBill(EntityManager entityManager, Integer id){
		super(entityManager, id);
	}
	
	public TemplateBill(EntityManager entityManager, TemplateBill orig){
		super(entityManager, orig);
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
		TemplateTransaction first = null;
		List<TemplateTransaction> transactions = getTemplateTransactions();
		if (!transactions.isEmpty()) {
			first = transactions.get(0);
		}
		Account account = getAccount();
		if (account == null) {
			return super.toJSON();
		}
		acctBalPre = first == null ? 0 : account.getBalancePre(first);
		for (Account ba : account.getPortfolio().getAccounts()) {
			if (!ba.isPaymentAccount()) {
				continue;
			}
			double amount = 0;
			Integer id = null;
			for (TemplateTransaction trans : transactions) {
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
					.put("templateBillId", getId())
					.put("preBal", start)
					.put("amount", amount)
					.put("postBal", FormatText.formatCurrency(start - amount)));
		}
		return super.toJSON()
				.put("acctBalPre", acctBalPre)
				.put("acctBalPost", FormatText.formatCurrency(acctBalPre + amountDue))
				.put("accounts", accounts)
				.put("name", getName());
	}
	
	@Override
	public TemplateBill setPropertyValue(String property, String value) throws Exception {
		try {
			return super.setPropertyValue(property, value);
		} catch (Exception e) {
			return super.setPropertyValue(property + "D", value);
		}
	}
	
	@Override
	public int compareTo(Object o) {
		TemplateBill bill = (TemplateBill)o;
		int ret = getDueDay() - bill.getDueDay();
		if (ret != 0) {
			return ret / Math.abs(ret);
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
