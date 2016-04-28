package com.digitald4.budget.service;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.easymock.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.model.User;

public class AccountServiceTest {
	static EntityManager entityManager;

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private HttpServletRequest request; // 1
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		entityManager = EntityManagerHelper.getEntityManagerFactory("DD4JPA2", "org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true", "eddiemay", "").createEntityManager();
		User.setActiveUser(entityManager.find(User.class, 1));
	}

	@Test
	public void testGetAccounts() throws JSONException, Exception {
		expect(request.getParameter("portfolioId")).andStubReturn("8");
		replay(request);
		assertEquals("8", request.getParameter("portfolioId"));
		JSONArray array = new AccountService(entityManager).getAccounts(request);
		assertTrue(array.length() > 5);
		JSONObject prev = (JSONObject)array.get(0);
		for (int x = 1; x < array.length(); x++) {
			JSONObject current = (JSONObject)array.get(x);
			System.out.println(current.getString("name"));
			assertTrue(prev.getString("name").compareTo(current.getString("name")) < 1);
			prev = current;
		}
	}

	@Test
	public void testGetBills() throws JSONException, Exception {
		expect(request.getParameter("portfolioId")).andStubReturn("3");
		expect(request.getParameter("refDate")).andStubReturn(null);
		expect(request.getParameter("startDate")).andStubReturn("2015-09-01T07:00:00.000Z");
		expect(request.getParameter("endDate")).andStubReturn("2015-09-30T07:00:00.000Z");
		replay(request);
		JSONArray array = new AccountService(entityManager).getBills(request);
		assertTrue(array.length() > 3);
		for (int x = 1; x < array.length(); x++) {
			JSONObject current = (JSONObject)array.get(x);
			System.out.println(current);
		}
	}

	@Test
	public void testGetTemplateBills() throws JSONException, Exception {
		expect(request.getParameter("portfolioId")).andStubReturn("3");
		expect(request.getParameter("templateId")).andStubReturn("1");
		replay(request);
		JSONArray array = new AccountService(entityManager).getTemplateBills(request);
		assertTrue(array.length() > 3);
		for (int x = 1; x < array.length(); x++) {
			JSONObject current = (JSONObject)array.get(x);
			System.out.println(current);
		}
	}

	@Test
	@Ignore
	public void testGetPortfolios() throws JSONException, Exception {
		replay(request);
		JSONObject json = new AccountService(entityManager).getPortfolios(request);
		assertNotNull(json);
	}
}
