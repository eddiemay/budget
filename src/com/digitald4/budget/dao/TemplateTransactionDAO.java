package com.digitald4.budget.dao;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.TemplateBill;
import com.digitald4.budget.model.TemplateTransaction;
import com.digitald4.common.dao.DataAccessObject;
import com.digitald4.common.jpa.PrimaryKey;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/** TODO Copy Right*/
/**Description of class, (we need to get this from somewhere, database? xml?)*/
public abstract class TemplateTransactionDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,TEMPLATE_BILL_ID,DEBIT_ACCOUNT_ID,AMOUNT};
	private Integer id;
	private Integer templateBillId;
	private Integer debitAccountId;
	private double amount;
	private Account debitAccount;
	private TemplateBill templateBill;
	public TemplateTransactionDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public TemplateTransactionDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public TemplateTransactionDAO(EntityManager entityManager, TemplateTransactionDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(TemplateTransactionDAO orig){
		this.templateBillId = orig.getTemplateBillId();
		this.debitAccountId = orig.getDebitAccountId();
		this.amount = orig.getAmount();
	}
	@Override
	public String getHashKey() {
		return getHashKey(getKeyValues());
	}
	public Object[] getKeyValues() {
		return new Object[]{id};
	}
	@Override
	public int hashCode() {
		return PrimaryKey.hashCode(getKeyValues());
	}
	@Id
	@GeneratedValue
	@Column(name="ID",nullable=false)
	public Integer getId(){
		return id;
	}
	public TemplateTransaction setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (TemplateTransaction)this;
	}
	@Column(name="TEMPLATE_BILL_ID",nullable=true)
	public Integer getTemplateBillId(){
		return templateBillId;
	}
	public TemplateTransaction setTemplateBillId(Integer templateBillId) throws Exception  {
		Integer oldValue = getTemplateBillId();
		if (!isSame(templateBillId, oldValue)) {
			this.templateBillId = templateBillId;
			setProperty("TEMPLATE_BILL_ID", templateBillId, oldValue);
			templateBill=null;
		}
		return (TemplateTransaction)this;
	}
	@Column(name="DEBIT_ACCOUNT_ID",nullable=false)
	public Integer getDebitAccountId(){
		return debitAccountId;
	}
	public TemplateTransaction setDebitAccountId(Integer debitAccountId) throws Exception  {
		Integer oldValue = getDebitAccountId();
		if (!isSame(debitAccountId, oldValue)) {
			this.debitAccountId = debitAccountId;
			setProperty("DEBIT_ACCOUNT_ID", debitAccountId, oldValue);
			debitAccount=null;
		}
		return (TemplateTransaction)this;
	}
	@Column(name="AMOUNT",nullable=true)
	public double getAmount(){
		return amount;
	}
	public TemplateTransaction setAmount(double amount) throws Exception  {
		double oldValue = getAmount();
		if (!isSame(amount, oldValue)) {
			this.amount = amount;
			setProperty("AMOUNT", amount, oldValue);
		}
		return (TemplateTransaction)this;
	}
	public Account getDebitAccount() {
		if (debitAccount == null) {
			return getEntityManager().find(Account.class, getDebitAccountId());
		}
		return debitAccount;
	}
	public TemplateTransaction setDebitAccount(Account debitAccount) throws Exception {
		setDebitAccountId(debitAccount==null?null:debitAccount.getId());
		this.debitAccount=debitAccount;
		return (TemplateTransaction)this;
	}
	public TemplateBill getTemplateBill() {
		if (templateBill == null) {
			return getEntityManager().find(TemplateBill.class, getTemplateBillId());
		}
		return templateBill;
	}
	public TemplateTransaction setTemplateBill(TemplateBill templateBill) throws Exception {
		setTemplateBillId(templateBill==null?null:templateBill.getId());
		this.templateBill=templateBill;
		return (TemplateTransaction)this;
	}
	public Map<String,Object> getPropertyValues() {
		Hashtable<String,Object> values = new Hashtable<String,Object>();
		for(PROPERTY prop:PROPERTY.values()) {
			Object value = getPropertyValue(prop);
			if(value!=null)
				values.put(""+prop,value);
		}
		return values;
	}

	public TemplateTransaction setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (TemplateTransaction)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case TEMPLATE_BILL_ID: return getTemplateBillId();
			case DEBIT_ACCOUNT_ID: return getDebitAccountId();
			case AMOUNT: return getAmount();
		}
		return null;
	}

	@Override
	public TemplateTransaction setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (TemplateTransaction)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public TemplateTransaction setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case TEMPLATE_BILL_ID:setTemplateBillId(Integer.valueOf(value)); break;
			case DEBIT_ACCOUNT_ID:setDebitAccountId(Integer.valueOf(value)); break;
			case AMOUNT:setAmount(Double.valueOf(value)); break;
		}
		return (TemplateTransaction)this;
	}

	public TemplateTransaction copy() throws Exception {
		TemplateTransaction cp = new TemplateTransaction(getEntityManager(), (TemplateTransaction)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(TemplateTransactionDAO cp) throws Exception {
		super.copyChildrenTo(cp);
	}
	public Vector<String> getDifference(TemplateTransactionDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getTemplateBillId(),o.getTemplateBillId())) diffs.add("TEMPLATE_BILL_ID");
		if(!isSame(getDebitAccountId(),o.getDebitAccountId())) diffs.add("DEBIT_ACCOUNT_ID");
		if(!isSame(getAmount(),o.getAmount())) diffs.add("AMOUNT");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(templateBill != null && templateBill.isNewInstance())
				templateBill.insert();
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getDebitAccountId()))
			 throw new Exception("DEBIT_ACCOUNT_ID is required.");
	}
	@Override
	public void insertChildren() throws Exception {
	}
}
