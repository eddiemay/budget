package com.digitald4.budget.dao;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.Bill;
import com.digitald4.common.model.GeneralData;
import com.digitald4.budget.model.Transaction;
import com.digitald4.common.dao.DataAccessObject;
import com.digitald4.common.jpa.PrimaryKey;
import com.digitald4.common.util.FormatText;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/** TODO Copy Right*/
/**Description of class, (we need to get this from somewhere, database? xml?)*/
public abstract class TransactionDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,DATE_D,NAME_D,BILL_ID,DEBIT_ACCOUNT_ID,CREDIT_ACCOUNT_ID,AMOUNT,STATUS_ID,ACTIVE,DESCRIPTION};
	private Integer id;
	private Date dateD;
	private String nameD;
	private Integer billId;
	private Integer debitAccountId;
	private Integer creditAccountId;
	private double amount;
	private Integer statusId;
	private boolean active = true;
	private String description;
	private Bill bill;
	private Account creditAccount;
	private Account debitAccount;
	private GeneralData status;
	public TransactionDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public TransactionDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public TransactionDAO(EntityManager entityManager, TransactionDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(TransactionDAO orig){
		this.dateD=orig.getDateD();
		this.nameD=orig.getNameD();
		this.billId=orig.getBillId();
		this.debitAccountId=orig.getDebitAccountId();
		this.creditAccountId=orig.getCreditAccountId();
		this.amount=orig.getAmount();
		this.statusId=orig.getStatusId();
		this.active=orig.isActive();
		this.description=orig.getDescription();
	}
	@Override
	public String getHashKey(){
		return getHashKey(getKeyValues());
	}
	public Object[] getKeyValues(){
		return new Object[]{id};
	}
	@Override
	public int hashCode(){
		return PrimaryKey.hashCode(getKeyValues());
	}
	@Id
	@GeneratedValue
	@Column(name="ID",nullable=false)
	public Integer getId(){
		return id;
	}
	public Transaction setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (Transaction)this;
	}
	@Column(name="DATE_D",nullable=true)
	public Date getDateD(){
		return dateD;
	}
	public Transaction setDateD(Date dateD) throws Exception  {
		Date oldValue = getDateD();
		if (!isSame(dateD, oldValue)) {
			this.dateD = dateD;
			setProperty("DATE_D", dateD, oldValue);
		}
		return (Transaction)this;
	}
	@Column(name="NAME_D",nullable=true,length=64)
	public String getNameD(){
		return nameD;
	}
	public Transaction setNameD(String nameD) throws Exception  {
		String oldValue = getNameD();
		if (!isSame(nameD, oldValue)) {
			this.nameD = nameD;
			setProperty("NAME_D", nameD, oldValue);
		}
		return (Transaction)this;
	}
	@Column(name="BILL_ID",nullable=true)
	public Integer getBillId(){
		return billId;
	}
	public Transaction setBillId(Integer billId) throws Exception  {
		Integer oldValue = getBillId();
		if (!isSame(billId, oldValue)) {
			this.billId = billId;
			setProperty("BILL_ID", billId, oldValue);
			bill=null;
		}
		return (Transaction)this;
	}
	@Column(name="DEBIT_ACCOUNT_ID",nullable=false)
	public Integer getDebitAccountId(){
		return debitAccountId;
	}
	public Transaction setDebitAccountId(Integer debitAccountId) throws Exception  {
		Integer oldValue = getDebitAccountId();
		if (!isSame(debitAccountId, oldValue)) {
			this.debitAccountId = debitAccountId;
			setProperty("DEBIT_ACCOUNT_ID", debitAccountId, oldValue);
			debitAccount=null;
		}
		return (Transaction)this;
	}
	@Column(name="CREDIT_ACCOUNT_ID",nullable=true)
	public Integer getCreditAccountId(){
		return creditAccountId;
	}
	public Transaction setCreditAccountId(Integer creditAccountId) throws Exception  {
		Integer oldValue = getCreditAccountId();
		if (!isSame(creditAccountId, oldValue)) {
			this.creditAccountId = creditAccountId;
			setProperty("CREDIT_ACCOUNT_ID", creditAccountId, oldValue);
			creditAccount=null;
		}
		return (Transaction)this;
	}
	@Column(name="AMOUNT",nullable=true)
	public double getAmount(){
		return amount;
	}
	public Transaction setAmount(double amount) throws Exception  {
		double oldValue = getAmount();
		if (!isSame(amount, oldValue)) {
			this.amount = amount;
			setProperty("AMOUNT", amount, oldValue);
		}
		return (Transaction)this;
	}
	@Column(name="STATUS_ID",nullable=true)
	public Integer getStatusId(){
		return statusId;
	}
	public Transaction setStatusId(Integer statusId) throws Exception  {
		Integer oldValue = getStatusId();
		if (!isSame(statusId, oldValue)) {
			this.statusId = statusId;
			setProperty("STATUS_ID", statusId, oldValue);
			status=null;
		}
		return (Transaction)this;
	}
	@Column(name="ACTIVE",nullable=true)
	public boolean isActive(){
		return active;
	}
	public Transaction setActive(boolean active) throws Exception  {
		boolean oldValue = isActive();
		if (!isSame(active, oldValue)) {
			this.active = active;
			setProperty("ACTIVE", active, oldValue);
		}
		return (Transaction)this;
	}
	@Column(name="DESCRIPTION",nullable=true,length=256)
	public String getDescription(){
		return description;
	}
	public Transaction setDescription(String description) throws Exception  {
		String oldValue = getDescription();
		if (!isSame(description, oldValue)) {
			this.description = description;
			setProperty("DESCRIPTION", description, oldValue);
		}
		return (Transaction)this;
	}
	public Bill getBill() {
		if (bill == null) {
			return getEntityManager().find(Bill.class, getBillId());
		}
		return bill;
	}
	public Transaction setBill(Bill bill) throws Exception {
		setBillId(bill==null?null:bill.getId());
		this.bill=bill;
		return (Transaction)this;
	}
	public Account getCreditAccount() {
		if (creditAccount == null) {
			return getEntityManager().find(Account.class, getCreditAccountId());
		}
		return creditAccount;
	}
	public Transaction setCreditAccount(Account creditAccount) throws Exception {
		setCreditAccountId(creditAccount==null?null:creditAccount.getId());
		this.creditAccount=creditAccount;
		return (Transaction)this;
	}
	public Account getDebitAccount() {
		if (debitAccount == null) {
			return getEntityManager().find(Account.class, getDebitAccountId());
		}
		return debitAccount;
	}
	public Transaction setDebitAccount(Account debitAccount) throws Exception {
		setDebitAccountId(debitAccount==null?null:debitAccount.getId());
		this.debitAccount=debitAccount;
		return (Transaction)this;
	}
	public GeneralData getStatus() {
		if (status == null) {
			return getEntityManager().find(GeneralData.class, getStatusId());
		}
		return status;
	}
	public Transaction setStatus(GeneralData status) throws Exception {
		setStatusId(status==null?null:status.getId());
		this.status=status;
		return (Transaction)this;
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

	public Transaction setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (Transaction)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case DATE_D: return getDateD();
			case NAME_D: return getNameD();
			case BILL_ID: return getBillId();
			case DEBIT_ACCOUNT_ID: return getDebitAccountId();
			case CREDIT_ACCOUNT_ID: return getCreditAccountId();
			case AMOUNT: return getAmount();
			case STATUS_ID: return getStatusId();
			case ACTIVE: return isActive();
			case DESCRIPTION: return getDescription();
		}
		return null;
	}

	@Override
	public Transaction setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (Transaction)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public Transaction setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case DATE_D:setDateD(FormatText.parseDate(value)); break;
			case NAME_D:setNameD(String.valueOf(value)); break;
			case BILL_ID:setBillId(Integer.valueOf(value)); break;
			case DEBIT_ACCOUNT_ID:setDebitAccountId(Integer.valueOf(value)); break;
			case CREDIT_ACCOUNT_ID:setCreditAccountId(Integer.valueOf(value)); break;
			case AMOUNT:setAmount(Double.valueOf(value)); break;
			case STATUS_ID:setStatusId(Integer.valueOf(value)); break;
			case ACTIVE:setActive(Boolean.valueOf(value)); break;
			case DESCRIPTION:setDescription(String.valueOf(value)); break;
		}
		return (Transaction)this;
	}

	public Transaction copy() throws Exception {
		Transaction cp = new Transaction(getEntityManager(), (Transaction)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(TransactionDAO cp) throws Exception {
		super.copyChildrenTo(cp);
	}
	public Vector<String> getDifference(TransactionDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getDateD(),o.getDateD())) diffs.add("DATE_D");
		if(!isSame(getNameD(),o.getNameD())) diffs.add("NAME_D");
		if(!isSame(getBillId(),o.getBillId())) diffs.add("BILL_ID");
		if(!isSame(getDebitAccountId(),o.getDebitAccountId())) diffs.add("DEBIT_ACCOUNT_ID");
		if(!isSame(getCreditAccountId(),o.getCreditAccountId())) diffs.add("CREDIT_ACCOUNT_ID");
		if(!isSame(getAmount(),o.getAmount())) diffs.add("AMOUNT");
		if(!isSame(getStatusId(),o.getStatusId())) diffs.add("STATUS_ID");
		if(!isSame(isActive(),o.isActive())) diffs.add("ACTIVE");
		if(!isSame(getDescription(),o.getDescription())) diffs.add("DESCRIPTION");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(bill != null && bill.isNewInstance())
				bill.insert();
		if(creditAccount != null && creditAccount.isNewInstance())
				creditAccount.insert();
		if(debitAccount != null && debitAccount.isNewInstance())
				debitAccount.insert();
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
