package com.digitald4.budget.dao;

import com.digitald4.budget.model.Portfolio;
import com.digitald4.budget.model.Template;
import com.digitald4.budget.model.TemplateBill;
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
public abstract class TemplateDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,PORTFOLIO_ID,NAME};
	private Integer id;
	private Integer portfolioId;
	private String name;
	private List<TemplateBill> templateBills;
	private Portfolio portfolio;
	public TemplateDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public TemplateDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public TemplateDAO(EntityManager entityManager, TemplateDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(TemplateDAO orig){
		this.portfolioId = orig.getPortfolioId();
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
	public Template setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (Template)this;
	}
	@Column(name="PORTFOLIO_ID",nullable=false)
	public Integer getPortfolioId(){
		return portfolioId;
	}
	public Template setPortfolioId(Integer portfolioId) throws Exception  {
		Integer oldValue = getPortfolioId();
		if (!isSame(portfolioId, oldValue)) {
			this.portfolioId = portfolioId;
			setProperty("PORTFOLIO_ID", portfolioId, oldValue);
			portfolio=null;
		}
		return (Template)this;
	}
	@Column(name="NAME",nullable=false,length=32)
	public String getName(){
		return name;
	}
	public Template setName(String name) throws Exception  {
		String oldValue = getName();
		if (!isSame(name, oldValue)) {
			this.name = name;
			setProperty("NAME", name, oldValue);
		}
		return (Template)this;
	}
	public Portfolio getPortfolio() {
		if (portfolio == null) {
			return getEntityManager().find(Portfolio.class, getPortfolioId());
		}
		return portfolio;
	}
	public Template setPortfolio(Portfolio portfolio) throws Exception {
		setPortfolioId(portfolio==null?null:portfolio.getId());
		this.portfolio=portfolio;
		return (Template)this;
	}
	public List<TemplateBill> getTemplateBills() {
		if (isNewInstance() || templateBills != null) {
			if (templateBills == null) {
				templateBills = new SortedList<TemplateBill>();
			}
			return templateBills;
		}
		return getNamedCollection(TemplateBill.class, "findByTemplate", getId());
	}
	public Template addTemplateBill(TemplateBill templateBill) throws Exception {
		templateBill.setTemplate((Template)this);
		if(isNewInstance() || templateBills != null)
			getTemplateBills().add(templateBill);
		else
			templateBill.insert();
		return (Template)this;
	}
	public Template removeTemplateBill(TemplateBill templateBill) throws Exception {
		if(isNewInstance() || templateBills != null)
			getTemplateBills().remove(templateBill);
		else
			templateBill.delete();
		return (Template)this;
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

	public Template setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (Template)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case PORTFOLIO_ID: return getPortfolioId();
			case NAME: return getName();
		}
		return null;
	}

	@Override
	public Template setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (Template)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public Template setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case PORTFOLIO_ID:setPortfolioId(Integer.valueOf(value)); break;
			case NAME:setName(String.valueOf(value)); break;
		}
		return (Template)this;
	}

	public Template copy() throws Exception {
		Template cp = new Template(getEntityManager(), (Template)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(TemplateDAO cp) throws Exception {
		super.copyChildrenTo(cp);
		for(TemplateBill child:getTemplateBills())
			cp.addTemplateBill(child.copy());
	}
	public Vector<String> getDifference(TemplateDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getPortfolioId(),o.getPortfolioId())) diffs.add("PORTFOLIO_ID");
		if(!isSame(getName(),o.getName())) diffs.add("NAME");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(portfolio != null && portfolio.isNewInstance())
				portfolio.insert();
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getPortfolioId()))
			 throw new Exception("PORTFOLIO_ID is required.");
		if (isNull(getName()))
			 throw new Exception("NAME is required.");
	}
	@Override
	public void insertChildren() throws Exception {
		if (templateBills != null) {
			for (TemplateBill templateBill : getTemplateBills()) {
				templateBill.setTemplate((Template)this);
			}
		}
		if (templateBills != null) {
			for (TemplateBill templateBill : getTemplateBills()) {
				templateBill.insert();
			}
			templateBills = null;
		}
	}
}
