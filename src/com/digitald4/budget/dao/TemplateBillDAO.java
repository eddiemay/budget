package com.digitald4.budget.dao;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.Template;
import com.digitald4.budget.model.TemplateBill;
import com.digitald4.budget.model.TemplateTransaction;
import com.digitald4.common.dao.DataAccessObject;
import com.digitald4.common.jpa.PrimaryKey;
import com.digitald4.common.util.SortedList;
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
public abstract class TemplateBillDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,TEMPLATE_ID,ACCOUNT_ID,DUE_DAY,NAME_D,AMOUNT_DUE,ACTIVE,DESCRIPTION};
	private Integer id;
	private Integer templateId;
	private Integer accountId;
	private int dueDay;
	private String nameD;
	private double amountDue;
	private boolean active = true;
	private String description;
	private List<TemplateTransaction> templateTransactions;
	private Account account;
	private Template template;
	public TemplateBillDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public TemplateBillDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public TemplateBillDAO(EntityManager entityManager, TemplateBillDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(TemplateBillDAO orig){
		this.templateId = orig.getTemplateId();
		this.accountId = orig.getAccountId();
		this.dueDay = orig.getDueDay();
		this.nameD = orig.getNameD();
		this.amountDue = orig.getAmountDue();
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
	public TemplateBill setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (TemplateBill)this;
	}
	@Column(name="TEMPLATE_ID",nullable=false)
	public Integer getTemplateId(){
		return templateId;
	}
	public TemplateBill setTemplateId(Integer templateId) throws Exception  {
		Integer oldValue = getTemplateId();
		if (!isSame(templateId, oldValue)) {
			this.templateId = templateId;
			setProperty("TEMPLATE_ID", templateId, oldValue);
			template=null;
		}
		return (TemplateBill)this;
	}
	@Column(name="ACCOUNT_ID",nullable=false)
	public Integer getAccountId(){
		return accountId;
	}
	public TemplateBill setAccountId(Integer accountId) throws Exception  {
		Integer oldValue = getAccountId();
		if (!isSame(accountId, oldValue)) {
			this.accountId = accountId;
			setProperty("ACCOUNT_ID", accountId, oldValue);
			account=null;
		}
		return (TemplateBill)this;
	}
	@Column(name="DUE_DAY",nullable=true)
	public int getDueDay(){
		return dueDay;
	}
	public TemplateBill setDueDay(int dueDay) throws Exception  {
		int oldValue = getDueDay();
		if (!isSame(dueDay, oldValue)) {
			this.dueDay = dueDay;
			setProperty("DUE_DAY", dueDay, oldValue);
		}
		return (TemplateBill)this;
	}
	@Column(name="NAME_D",nullable=true,length=50)
	public String getNameD(){
		return nameD;
	}
	public TemplateBill setNameD(String nameD) throws Exception  {
		String oldValue = getNameD();
		if (!isSame(nameD, oldValue)) {
			this.nameD = nameD;
			setProperty("NAME_D", nameD, oldValue);
		}
		return (TemplateBill)this;
	}
	@Column(name="AMOUNT_DUE",nullable=true)
	public double getAmountDue(){
		return amountDue;
	}
	public TemplateBill setAmountDue(double amountDue) throws Exception  {
		double oldValue = getAmountDue();
		if (!isSame(amountDue, oldValue)) {
			this.amountDue = amountDue;
			setProperty("AMOUNT_DUE", amountDue, oldValue);
		}
		return (TemplateBill)this;
	}
	@Column(name="ACTIVE",nullable=true)
	public boolean isActive(){
		return active;
	}
	public TemplateBill setActive(boolean active) throws Exception  {
		boolean oldValue = isActive();
		if (!isSame(active, oldValue)) {
			this.active = active;
			setProperty("ACTIVE", active, oldValue);
		}
		return (TemplateBill)this;
	}
	@Column(name="DESCRIPTION",nullable=true,length=256)
	public String getDescription(){
		return description;
	}
	public TemplateBill setDescription(String description) throws Exception  {
		String oldValue = getDescription();
		if (!isSame(description, oldValue)) {
			this.description = description;
			setProperty("DESCRIPTION", description, oldValue);
		}
		return (TemplateBill)this;
	}
	public Account getAccount() {
		if (account == null) {
			return getEntityManager().find(Account.class, getAccountId());
		}
		return account;
	}
	public TemplateBill setAccount(Account account) throws Exception {
		setAccountId(account==null?null:account.getId());
		this.account=account;
		return (TemplateBill)this;
	}
	public Template getTemplate() {
		if (template == null) {
			return getEntityManager().find(Template.class, getTemplateId());
		}
		return template;
	}
	public TemplateBill setTemplate(Template template) throws Exception {
		setTemplateId(template==null?null:template.getId());
		this.template=template;
		return (TemplateBill)this;
	}
	public List<TemplateTransaction> getTemplateTransactions() {
		if (isNewInstance() || templateTransactions != null) {
			if (templateTransactions == null) {
				templateTransactions = new SortedList<TemplateTransaction>();
			}
			return templateTransactions;
		}
		return getNamedCollection(TemplateTransaction.class, "findByTemplateBill", getId());
	}
	public TemplateBill addTemplateTransaction(TemplateTransaction templateTransaction) throws Exception {
		templateTransaction.setTemplateBill((TemplateBill)this);
		if(isNewInstance() || templateTransactions != null)
			getTemplateTransactions().add(templateTransaction);
		else
			templateTransaction.insert();
		return (TemplateBill)this;
	}
	public TemplateBill removeTemplateTransaction(TemplateTransaction templateTransaction) throws Exception {
		if(isNewInstance() || templateTransactions != null)
			getTemplateTransactions().remove(templateTransaction);
		else
			templateTransaction.delete();
		return (TemplateBill)this;
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

	public TemplateBill setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (TemplateBill)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case TEMPLATE_ID: return getTemplateId();
			case ACCOUNT_ID: return getAccountId();
			case DUE_DAY: return getDueDay();
			case NAME_D: return getNameD();
			case AMOUNT_DUE: return getAmountDue();
			case ACTIVE: return isActive();
			case DESCRIPTION: return getDescription();
		}
		return null;
	}

	@Override
	public TemplateBill setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (TemplateBill)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public TemplateBill setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case TEMPLATE_ID:setTemplateId(Integer.valueOf(value)); break;
			case ACCOUNT_ID:setAccountId(Integer.valueOf(value)); break;
			case DUE_DAY:setDueDay(Integer.valueOf(value)); break;
			case NAME_D:setNameD(String.valueOf(value)); break;
			case AMOUNT_DUE:setAmountDue(Double.valueOf(value)); break;
			case ACTIVE:setActive(Boolean.valueOf(value)); break;
			case DESCRIPTION:setDescription(String.valueOf(value)); break;
		}
		return (TemplateBill)this;
	}

	public TemplateBill copy() throws Exception {
		TemplateBill cp = new TemplateBill(getEntityManager(), (TemplateBill)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(TemplateBillDAO cp) throws Exception {
		super.copyChildrenTo(cp);
		for(TemplateTransaction child:getTemplateTransactions())
			cp.addTemplateTransaction(child.copy());
	}
	public Vector<String> getDifference(TemplateBillDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getTemplateId(),o.getTemplateId())) diffs.add("TEMPLATE_ID");
		if(!isSame(getAccountId(),o.getAccountId())) diffs.add("ACCOUNT_ID");
		if(!isSame(getDueDay(),o.getDueDay())) diffs.add("DUE_DAY");
		if(!isSame(getNameD(),o.getNameD())) diffs.add("NAME_D");
		if(!isSame(getAmountDue(),o.getAmountDue())) diffs.add("AMOUNT_DUE");
		if(!isSame(isActive(),o.isActive())) diffs.add("ACTIVE");
		if(!isSame(getDescription(),o.getDescription())) diffs.add("DESCRIPTION");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(template != null && template.isNewInstance())
				template.insert();
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getTemplateId()))
			 throw new Exception("TEMPLATE_ID is required.");
		if (isNull(getAccountId()))
			 throw new Exception("ACCOUNT_ID is required.");
	}
	@Override
	public void insertChildren() throws Exception {
		if (templateTransactions != null) {
			for (TemplateTransaction templateTransaction : getTemplateTransactions()) {
				templateTransaction.setTemplateBill((TemplateBill)this);
			}
		}
		if (templateTransactions != null) {
			for (TemplateTransaction templateTransaction : getTemplateTransactions()) {
				templateTransaction.insert();
			}
			templateTransactions = null;
		}
	}
}
