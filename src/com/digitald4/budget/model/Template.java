package com.digitald4.budget.model;
import com.digitald4.budget.dao.TemplateDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
@Entity
@Table(schema="budget",name="template")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Template o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Template o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Template o"),//AUTO-GENERATED
	@NamedQuery(name = "findByPortfolio", query="SELECT o FROM Template o WHERE o.PORTFOLIO_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM template o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Template extends TemplateDAO{
	public Template(EntityManager entityManager) {
		super(entityManager);
	}
	
	public Template(EntityManager entityManager, Integer id) {
		super(entityManager, id);
	}
	
	public Template(EntityManager entityManager, Template orig) {
		super(entityManager, orig);
	}
}
