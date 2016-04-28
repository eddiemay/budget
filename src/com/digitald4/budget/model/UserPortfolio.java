package com.digitald4.budget.model;
import com.digitald4.budget.dao.UserPortfolioDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
@Entity
@Table(schema="budget",name="user_portfolio")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM UserPortfolio o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM UserPortfolio o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM UserPortfolio o"),//AUTO-GENERATED
	@NamedQuery(name = "findByPortfolio", query="SELECT o FROM UserPortfolio o WHERE o.PORTFOLIO_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM user_portfolio o WHERE o.ID=?"),//AUTO-GENERATED
})
public class UserPortfolio extends UserPortfolioDAO{
	public UserPortfolio(EntityManager entityManager) {
		super(entityManager);
	}
	public UserPortfolio(EntityManager entityManager, Integer id) {
		super(entityManager, id);
	}
	public UserPortfolio(EntityManager entityManager, UserPortfolio orig) {
		super(entityManager, orig);
	}
}
