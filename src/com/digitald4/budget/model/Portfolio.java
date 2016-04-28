package com.digitald4.budget.model;
import java.util.Date;
import java.util.TreeSet;

import com.digitald4.budget.dao.PortfolioDAO;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(schema="budget",name="portfolio")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Portfolio o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Portfolio o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Portfolio o"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM portfolio o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Portfolio extends PortfolioDAO {
	public Portfolio(EntityManager entityManager) {
		super(entityManager);
	}
	
	public Portfolio(EntityManager entityManager, Integer id) {
		super(entityManager, id);
	}
	
	public Portfolio(EntityManager entityManager, Portfolio orig) {
		super(entityManager, orig);
	}
	
	public TreeSet<Bill> getBills(Date startDate, Date endDate) throws Exception {
		TreeSet<Bill> bills = new TreeSet<Bill>();
		for (Account account : getAccounts()) {
			bills.addAll(account.getBills(startDate, endDate));
			for (Transaction transaction : account.getTransactions(startDate, endDate)) {
				if (transaction.getBillId() != null) {
					bills.add(transaction.getBill());
				} else {
					bills.add(new Bill(getEntityManager()).addTransaction(transaction)
							.setAccount(transaction.getCreditAccount())
							.setAmountDue(transaction.getAmount())
							.setDueDate(transaction.getDate()));
				}
			}
		}
		return bills;
	}

	public TreeSet<Transaction> getTransactions(Date startDate, Date endDate) {
		TreeSet<Transaction> transactions = new TreeSet<Transaction>();
		for (Account account : getAccounts()) {
			transactions.addAll(account.getTransactions(startDate, endDate));
		}
		return transactions;
	}
}
