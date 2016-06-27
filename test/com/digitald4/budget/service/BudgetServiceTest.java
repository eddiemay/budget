package com.digitald4.budget.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.model.User;

public class BudgetServiceTest {
	static EntityManager entityManager;

  private HttpServletRequest request = mock(HttpServletRequest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		entityManager = EntityManagerHelper.getEntityManagerFactory("DD4JPA2",
				"org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true",
				"dd4_user", "getSchooled85").createEntityManager();
		User.setActiveUser(entityManager.find(User.class, 1));
	}

	@Test
	public void testGetBills() throws JSONException, Exception {
		when(request.getParameter("portfolioId")).thenReturn("3");
		when(request.getParameter("refDate")).thenReturn(null);
		when(request.getParameter("startDate")).thenReturn("2015-09-01T07:00:00.000Z");
		when(request.getParameter("endDate")).thenReturn("2015-09-30T07:00:00.000Z");
		JSONArray array = new BudgetService(entityManager).getBills(request);
		assertTrue(array.length() > 3);
		for (int x = 1; x < array.length(); x++) {
			JSONObject current = (JSONObject)array.get(x);
			System.out.println(current);
		}
	}
}
