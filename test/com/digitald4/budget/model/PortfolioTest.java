package com.digitald4.budget.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.model.User;

public class PortfolioTest {
static EntityManager entityManager; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		entityManager = EntityManagerHelper.getEntityManagerFactory("DD4JPA2", "org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true", "eddiemay", "").createEntityManager();
		User.setActiveUser(entityManager.find(User.class, 1));
	}
	
	@Test
	public void testRead() throws Exception {
		Portfolio portfolio = entityManager.find(Portfolio.class, 8);
		assertNotNull(portfolio);
	}
	
	@Test
	public void testReadPaymentAccounts() throws Exception {
		Portfolio portfolio = entityManager.find(Portfolio.class, 8);
		List<Account> list = new ArrayList<Account>();
		for (Account account : portfolio.getAccounts()) {
			if (account.isPaymentAccount()) {
				list.add(account);
			}
		}
		assertTrue(list.size() > 0);
	}
}
