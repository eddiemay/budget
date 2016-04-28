package com.digitald4.budget.model;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.model.GeneralData;
import com.digitald4.common.model.User;

public class GenDataTest {
static EntityManager entityManager; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		entityManager = EntityManagerHelper.getEntityManagerFactory("DD4JPA2", "org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true", "eddiemay", "").createEntityManager();
		User.setActiveUser(entityManager.find(User.class, 1));
	}

	@Test
	public void test() {
		GeneralData userPortfolioRole = entityManager.find(GeneralData.class, 11);
		GeneralData gd = GenData.UserPortfolioRole.get(entityManager);
		assertSame(userPortfolioRole, gd);
	}
}
