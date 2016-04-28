package com.digitald4.budget.dao;

import com.digitald4.common.model.GeneralData;
import com.digitald4.budget.model.Portfolio;
import com.digitald4.common.model.User;
import com.digitald4.budget.model.UserPortfolio;
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
public abstract class UserPortfolioDAO extends DataAccessObject{
	public enum KEY_PROPERTY{ID};
	public enum PROPERTY{ID,USER_ID,PORTFOLIO_ID,ROLE_ID};
	private Integer id;
	private Integer userId;
	private Integer portfolioId;
	private Integer roleId;
	private Portfolio portfolio;
	private GeneralData role;
	private User user;
	public UserPortfolioDAO(EntityManager entityManager) {
		super(entityManager);
	}
	public UserPortfolioDAO(EntityManager entityManager, Integer id) {
		super(entityManager);
		this.id=id;
	}
	public UserPortfolioDAO(EntityManager entityManager, UserPortfolioDAO orig) {
		super(entityManager, orig);
		copyFrom(orig);
	}
	public void copyFrom(UserPortfolioDAO orig){
		this.userId = orig.getUserId();
		this.portfolioId = orig.getPortfolioId();
		this.roleId = orig.getRoleId();
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
	public UserPortfolio setId(Integer id) throws Exception  {
		Integer oldValue = getId();
		if (!isSame(id, oldValue)) {
			this.id = id;
			setProperty("ID", id, oldValue);
		}
		return (UserPortfolio)this;
	}
	@Column(name="USER_ID",nullable=false)
	public Integer getUserId(){
		return userId;
	}
	public UserPortfolio setUserId(Integer userId) throws Exception  {
		Integer oldValue = getUserId();
		if (!isSame(userId, oldValue)) {
			this.userId = userId;
			setProperty("USER_ID", userId, oldValue);
			user=null;
		}
		return (UserPortfolio)this;
	}
	@Column(name="PORTFOLIO_ID",nullable=false)
	public Integer getPortfolioId(){
		return portfolioId;
	}
	public UserPortfolio setPortfolioId(Integer portfolioId) throws Exception  {
		Integer oldValue = getPortfolioId();
		if (!isSame(portfolioId, oldValue)) {
			this.portfolioId = portfolioId;
			setProperty("PORTFOLIO_ID", portfolioId, oldValue);
			portfolio=null;
		}
		return (UserPortfolio)this;
	}
	@Column(name="ROLE_ID",nullable=false)
	public Integer getRoleId(){
		return roleId;
	}
	public UserPortfolio setRoleId(Integer roleId) throws Exception  {
		Integer oldValue = getRoleId();
		if (!isSame(roleId, oldValue)) {
			this.roleId = roleId;
			setProperty("ROLE_ID", roleId, oldValue);
			role=null;
		}
		return (UserPortfolio)this;
	}
	public Portfolio getPortfolio() {
		if (portfolio == null) {
			return getEntityManager().find(Portfolio.class, getPortfolioId());
		}
		return portfolio;
	}
	public UserPortfolio setPortfolio(Portfolio portfolio) throws Exception {
		setPortfolioId(portfolio==null?null:portfolio.getId());
		this.portfolio=portfolio;
		return (UserPortfolio)this;
	}
	public GeneralData getRole() {
		if (role == null) {
			return getEntityManager().find(GeneralData.class, getRoleId());
		}
		return role;
	}
	public UserPortfolio setRole(GeneralData role) throws Exception {
		setRoleId(role==null?null:role.getId());
		this.role=role;
		return (UserPortfolio)this;
	}
	public User getUser() {
		if (user == null) {
			return getEntityManager().find(User.class, getUserId());
		}
		return user;
	}
	public UserPortfolio setUser(User user) throws Exception {
		setUserId(user==null?null:user.getId());
		this.user=user;
		return (UserPortfolio)this;
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

	public UserPortfolio setPropertyValues(Map<String,Object> data) throws Exception  {
		for(String key:data.keySet())
			setPropertyValue(key, data.get(key).toString());
		return (UserPortfolio)this;
	}

	@Override
	public Object getPropertyValue(String property) {
		return getPropertyValue(PROPERTY.valueOf(formatProperty(property)));
	}
	public Object getPropertyValue(PROPERTY property) {
		switch (property) {
			case ID: return getId();
			case USER_ID: return getUserId();
			case PORTFOLIO_ID: return getPortfolioId();
			case ROLE_ID: return getRoleId();
		}
		return null;
	}

	@Override
	public UserPortfolio setPropertyValue(String property, String value) throws Exception  {
		if(property == null) return (UserPortfolio)this;
		return setPropertyValue(PROPERTY.valueOf(formatProperty(property)),value);
	}

	public UserPortfolio setPropertyValue(PROPERTY property, String value) throws Exception  {
		switch (property) {
			case ID:setId(Integer.valueOf(value)); break;
			case USER_ID:setUserId(Integer.valueOf(value)); break;
			case PORTFOLIO_ID:setPortfolioId(Integer.valueOf(value)); break;
			case ROLE_ID:setRoleId(Integer.valueOf(value)); break;
		}
		return (UserPortfolio)this;
	}

	public UserPortfolio copy() throws Exception {
		UserPortfolio cp = new UserPortfolio(getEntityManager(), (UserPortfolio)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(UserPortfolioDAO cp) throws Exception {
		super.copyChildrenTo(cp);
	}
	public Vector<String> getDifference(UserPortfolioDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getUserId(),o.getUserId())) diffs.add("USER_ID");
		if(!isSame(getPortfolioId(),o.getPortfolioId())) diffs.add("PORTFOLIO_ID");
		if(!isSame(getRoleId(),o.getRoleId())) diffs.add("ROLE_ID");
		return diffs;
	}
	@Override
	public void insertParents() throws Exception {
		if(portfolio != null && portfolio.isNewInstance())
				portfolio.insert();
	}
	@Override
	public void insertPreCheck() throws Exception {
		if (isNull(getUserId()))
			 throw new Exception("USER_ID is required.");
		if (isNull(getPortfolioId()))
			 throw new Exception("PORTFOLIO_ID is required.");
		if (isNull(getRoleId()))
			 throw new Exception("ROLE_ID is required.");
	}
	@Override
	public void insertChildren() throws Exception {
	}
}
