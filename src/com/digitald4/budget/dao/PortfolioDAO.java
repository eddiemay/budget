package com.digitald4.budget.dao;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.Portfolio;
import com.digitald4.budget.model.Template;
import com.digitald4.budget.model.UserPortfolio;
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
public abstract class PortfolioDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,NAME};
	private Integer id;
	private String name;
	private List<Account> accounts;
	private List<Template> templates;
	private List<UserPortfolio> userPortfolios;
	public PortfolioDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public PortfolioDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public PortfolioDAO(EntityManager entityManager, PortfolioDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(PortfolioDAO orig){
		this.name = orig.getName();
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
	public Portfolio setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (Portfolio)this;
	}
	@Column(name="NAME",nullable=false,length=32)
	public String getName(){
		return name;
	}
	public Portfolio setName(String name) throws Exception  {
		String oldValue = getName();
		if (!isSame(name, oldValue)) {
			this.name = name;
			setProperty("NAME", name, oldValue);
		}
		return (Portfolio)this;
	}
	public List<Account> getAccounts() {
		if (isNewInstance() || accounts != null) {
			if (accounts == null) {
				accounts = new SortedList<Account>();
			}
			return accounts;
		}
		return getNamedCollection(Account.class, "findByPortfolio", getId());
	}
	public Portfolio addAccount(Account account) throws Exception {
		account.setPortfolio((Portfolio)this);
		if(isNewInstance() || accounts != null)
			getAccounts().add(account);
		else
			account.insert();
		return (Portfolio)this;
	}
	public Portfolio removeAccount(Account account) throws Exception {
		if(isNewInstance() || accounts != null)
			getAccounts().remove(account);
		else
			account.delete();
		return (Portfolio)this;
	}
	public List<Template> getTemplates() {
		if (isNewInstance() || templates != null) {
			if (templates == null) {
				templates = new SortedList<Template>();
			}
			return templates;
		}
		return getNamedCollection(Template.class, "findByPortfolio", getId());
	}
	public Portfolio addTemplate(Template template) throws Exception {
		template.setPortfolio((Portfolio)this);
		if(isNewInstance() || templates != null)
			getTemplates().add(template);
		else
			template.insert();
		return (Portfolio)this;
	}
	public Portfolio removeTemplate(Template template) throws Exception {
		if(isNewInstance() || templates != null)
			getTemplates().remove(template);
		else
			template.delete();
		return (Portfolio)this;
	}
	public List<UserPortfolio> getUserPortfolios() {
		if (isNewInstance() || userPortfolios != null) {
			if (userPortfolios == null) {
				userPortfolios = new SortedList<UserPortfolio>();
			}
			return userPortfolios;
		}
		return getNamedCollection(UserPortfolio.class, "findByPortfolio", getId());
	}
	public Portfolio addUserPortfolio(UserPortfolio userPortfolio) throws Exception {
		userPortfolio.setPortfolio((Portfolio)this);
		if(isNewInstance() || userPortfolios != null)
			getUserPortfolios().add(userPortfolio);
		else
			userPortfolio.insert();
		return (Portfolio)this;
	}
	public Portfolio removeUserPortfolio(UserPortfolio userPortfolio) throws Exception {
		if(isNewInstance() || userPortfolios != null)
			getUserPortfolios().remove(userPortfolio);
		else
			userPortfolio.delete();
		return (Portfolio)this;
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

	public Portfolio setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (Portfolio)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case NAME: return getName();
		}
		return null;
	}

	@Override
	public Portfolio setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (Portfolio)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public Portfolio setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case NAME:setName(String.valueOf(value)); break;
		}
		return (Portfolio)this;
	}

	public Portfolio copy() throws Exception {
		Portfolio cp = new Portfolio(getEntityManager(), (Portfolio)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(PortfolioDAO cp) throws Exception {
		super.copyChildrenTo(cp);
		for(Account child:getAccounts())
			cp.addAccount(child.copy());
		for(Template child:getTemplates())
			cp.addTemplate(child.copy());
		for(UserPortfolio child:getUserPortfolios())
			cp.addUserPortfolio(child.copy());
	}
	public Vector<String> getDifference(PortfolioDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getName(),o.getName())) diffs.add("NAME");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getName()))
			 throw new Exception("NAME is required.");
	}
	@Override
	public void insertChildren() throws Exception {
		if (accounts != null) {
			for (Account account : getAccounts()) {
				account.setPortfolio((Portfolio)this);
			}
		}
		if (templates != null) {
			for (Template template : getTemplates()) {
				template.setPortfolio((Portfolio)this);
			}
		}
		if (userPortfolios != null) {
			for (UserPortfolio userPortfolio : getUserPortfolios()) {
				userPortfolio.setPortfolio((Portfolio)this);
			}
		}
		if (accounts != null) {
			for (Account account : getAccounts()) {
				account.insert();
			}
			accounts = null;
		}
		if (templates != null) {
			for (Template template : getTemplates()) {
				template.insert();
			}
			templates = null;
		}
		if (userPortfolios != null) {
			for (UserPortfolio userPortfolio : getUserPortfolios()) {
				userPortfolio.insert();
			}
			userPortfolios = null;
		}
	}
}
