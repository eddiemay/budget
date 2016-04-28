package com.digitald4.budget.model;
import com.digitald4.budget.dao.TemplateTransactionDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
@Entity
@Table(name="template_transaction")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM TemplateTransaction o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM TemplateTransaction o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM TemplateTransaction o"),//AUTO-GENERATED
	@NamedQuery(name = "findByTemplateBill", query="SELECT o FROM TemplateTransaction o WHERE o.TEMPLATE_BILL_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM template_transaction o WHERE o.ID=?"),//AUTO-GENERATED
})
public class TemplateTransaction extends TemplateTransactionDAO{
	public TemplateTransaction(EntityManager entityManager) {
		super(entityManager);
	}
	public TemplateTransaction(EntityManager entityManager, Integer id){
		super(entityManager, id);
	}
	public TemplateTransaction(EntityManager entityManager, TemplateTransaction orig){
		super(entityManager, orig);
	}
}
