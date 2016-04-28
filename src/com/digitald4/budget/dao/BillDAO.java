package com.digitald4.budget.dao;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.Bill;
import com.digitald4.common.model.GeneralData;
import com.digitald4.budget.model.Template;
import com.digitald4.budget.model.Transaction;
import com.digitald4.common.dao.DataAccessObject;
import com.digitald4.common.jpa.PrimaryKey;
import com.digitald4.common.util.FormatText;
import com.digitald4.common.util.SortedList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/** TODO Copy Right*/
/**Description of class, (we need to get this from somewhere, database? xml?)*/
public abstract class BillDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,ACCOUNT_ID,TEMPLATE_ID,DUE_DATE,NAME_D,PAYMENT_DATE_D,AMOUNT_DUE_D,STATUS_ID,ACTIVE,DESCRIPTION};
	private Integer id;
	private Integer accountId;
	private Integer templateId;
	private Date dueDate;
	private String nameD;
	private Date paymentDateD;
	private double amountDueD;
	private Integer statusId;
	private boolean active = true;
	private String description;
	private List<Transaction> transactions;
	private Account account;
	private GeneralData status;
	private Template template;
	public BillDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public BillDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public BillDAO(EntityManager entityManager, BillDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(BillDAO orig){
		this.accountId = orig.getAccountId();
		this.templateId = orig.getTemplateId();
		this.dueDate = orig.getDueDate();
		this.nameD = orig.getNameD();
		this.paymentDateD = orig.getPaymentDateD();
		this.amountDueD = orig.getAmountDueD();
		this.statusId = orig.getStatusId();
		this.active = orig.isActive();
		this.description = orig.getDescription();
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
	public Bill setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="ACCOUNT_ID",nullable=false)
	public Integer getAccountId(){
		return accountId;
	}
	public Bill setAccountId(Integer accountId) throws Exception  {
		Integer oldValue = getAccountId();
		if (!isSame(accountId, oldValue)) {
			this.accountId = accountId;
			setProperty("ACCOUNT_ID", accountId, oldValue);
			account=null;
		}
		return (Bill)this;
	}
	@Column(name="TEMPLATE_ID",nullable=true)
	public Integer getTemplateId(){
		return templateId;
	}
	public Bill setTemplateId(Integer templateId) throws Exception  {
		Integer oldValue = getTemplateId();
		if (!isSame(templateId, oldValue)) {
			this.templateId = templateId;
			setProperty("TEMPLATE_ID", templateId, oldValue);
			template=null;
		}
		return (Bill)this;
	}
	@Column(name="DUE_DATE",nullable=true)
	public Date getDueDate(){
		return dueDate;
	}
	public Bill setDueDate(Date dueDate) throws Exception  {
		Date oldValue = getDueDate();
		if (!isSame(dueDate, oldValue)) {
			this.dueDate = dueDate;
			setProperty("DUE_DATE", dueDate, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="NAME_D",nullable=true,length=50)
	public String getNameD(){
		return nameD;
	}
	public Bill setNameD(String nameD) throws Exception  {
		String oldValue = getNameD();
		if (!isSame(nameD, oldValue)) {
			this.nameD = nameD;
			setProperty("NAME_D", nameD, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="PAYMENT_DATE_D",nullable=true)
	public Date getPaymentDateD(){
		return paymentDateD;
	}
	public Bill setPaymentDateD(Date paymentDateD) throws Exception  {
		Date oldValue = getPaymentDateD();
		if (!isSame(paymentDateD, oldValue)) {
			this.paymentDateD = paymentDateD;
			setProperty("PAYMENT_DATE_D", paymentDateD, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="AMOUNT_DUE_D",nullable=true)
	public double getAmountDueD(){
		return amountDueD;
	}
	public Bill setAmountDueD(double amountDueD) throws Exception  {
		double oldValue = getAmountDueD();
		if (!isSame(amountDueD, oldValue)) {
			this.amountDueD = amountDueD;
			setProperty("AMOUNT_DUE_D", amountDueD, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="STATUS_ID",nullable=true)
	public Integer getStatusId(){
		return statusId;
	}
	public Bill setStatusId(Integer statusId) throws Exception  {
		Integer oldValue = getStatusId();
		if (!isSame(statusId, oldValue)) {
			this.statusId = statusId;
			setProperty("STATUS_ID", statusId, oldValue);
			status=null;
		}
		return (Bill)this;
	}
	@Column(name="ACTIVE",nullable=true)
	public boolean isActive(){
		return active;
	}
	public Bill setActive(boolean active) throws Exception  {
		boolean oldValue = isActive();
		if (!isSame(active, oldValue)) {
			this.active = active;
			setProperty("ACTIVE", active, oldValue);
		}
		return (Bill)this;
	}
	@Column(name="DESCRIPTION",nullable=true,length=256)
	public String getDescription(){
		return description;
	}
	public Bill setDescription(String description) throws Exception  {
		String oldValue = getDescription();
		if (!isSame(description, oldValue)) {
			this.description = description;
			setProperty("DESCRIPTION", description, oldValue);
		}
		return (Bill)this;
	}
	public Account getAccount() {
		if (account == null) {
			return getEntityManager().find(Account.class, getAccountId());
		}
		return account;
	}
	public Bill setAccount(Account account) throws Exception {
		setAccountId(account==null?null:account.getId());
		this.account=account;
		return (Bill)this;
	}
	public GeneralData getStatus() {
		if (status == null) {
			return getEntityManager().find(GeneralData.class, getStatusId());
		}
		return status;
	}
	public Bill setStatus(GeneralData status) throws Exception {
		setStatusId(status==null?null:status.getId());
		this.status=status;
		return (Bill)this;
	}
	public Template getTemplate() {
		if (template == null) {
			return getEntityManager().find(Template.class, getTemplateId());
		}
		return template;
	}
	public Bill setTemplate(Template template) throws Exception {
		setTemplateId(template==null?null:template.getId());
		this.template=template;
		return (Bill)this;
	}
	public List<Transaction> getTransactions() {
		if (isNewInstance() || transactions != null) {
			if (transactions == null) {
				transactions = new SortedList<Transaction>();
			}
			return transactions;
		}
		return getNamedCollection(Transaction.class, "findByBill", getId());
	}
	public Bill addTransaction(Transaction transaction) throws Exception {
		transaction.setBill((Bill)this);
		if(isNewInstance() || transactions != null)
			getTransactions().add(transaction);
		else
			transaction.insert();
		return (Bill)this;
	}
	public Bill removeTransaction(Transaction transaction) throws Exception {
		if(isNewInstance() || transactions != null)
			getTransactions().remove(transaction);
		else
			transaction.delete();
		return (Bill)this;
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

	public Bill setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (Bill)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case ACCOUNT_ID: return getAccountId();
			case TEMPLATE_ID: return getTemplateId();
			case DUE_DATE: return getDueDate();
			case NAME_D: return getNameD();
			case PAYMENT_DATE_D: return getPaymentDateD();
			case AMOUNT_DUE_D: return getAmountDueD();
			case STATUS_ID: return getStatusId();
			case ACTIVE: return isActive();
			case DESCRIPTION: return getDescription();
		}
		return null;
	}

	@Override
	public Bill setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (Bill)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public Bill setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case ACCOUNT_ID:setAccountId(Integer.valueOf(value)); break;
			case TEMPLATE_ID:setTemplateId(Integer.valueOf(value)); break;
			case DUE_DATE:setDueDate(FormatText.parseDate(value)); break;
			case NAME_D:setNameD(String.valueOf(value)); break;
			case PAYMENT_DATE_D:setPaymentDateD(FormatText.parseDate(value)); break;
			case AMOUNT_DUE_D:setAmountDueD(Double.valueOf(value)); break;
			case STATUS_ID:setStatusId(Integer.valueOf(value)); break;
			case ACTIVE:setActive(Boolean.valueOf(value)); break;
			case DESCRIPTION:setDescription(String.valueOf(value)); break;
		}
		return (Bill)this;
	}

	public Bill copy() throws Exception {
		Bill cp = new Bill(getEntityManager(), (Bill)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(BillDAO cp) throws Exception {
		super.copyChildrenTo(cp);
		for(Transaction child:getTransactions())
			cp.addTransaction(child.copy());
	}
	public Vector<String> getDifference(BillDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getAccountId(),o.getAccountId())) diffs.add("ACCOUNT_ID");
		if(!isSame(getTemplateId(),o.getTemplateId())) diffs.add("TEMPLATE_ID");
		if(!isSame(getDueDate(),o.getDueDate())) diffs.add("DUE_DATE");
		if(!isSame(getNameD(),o.getNameD())) diffs.add("NAME_D");
		if(!isSame(getPaymentDateD(),o.getPaymentDateD())) diffs.add("PAYMENT_DATE_D");
		if(!isSame(getAmountDueD(),o.getAmountDueD())) diffs.add("AMOUNT_DUE_D");
		if(!isSame(getStatusId(),o.getStatusId())) diffs.add("STATUS_ID");
		if(!isSame(isActive(),o.isActive())) diffs.add("ACTIVE");
		if(!isSame(getDescription(),o.getDescription())) diffs.add("DESCRIPTION");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(account != null && account.isNewInstance())
				account.insert();
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getAccountId()))
			 throw new Exception("ACCOUNT_ID is required.");
	}
	@Override
	public void insertChildren() throws Exception {
		if (transactions != null) {
			for (Transaction transaction : getTransactions()) {
				transaction.setBill((Bill)this);
			}
		}
		if (transactions != null) {
			for (Transaction transaction : getTransactions()) {
				transaction.insert();
			}
			transactions = null;
		}
	}
}
