package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.TestCase;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.server.JSONService;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import com.google.common.collect.ImmutableList;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

public class TemplateServiceTest extends TestCase {
	@Mock private DAO dao = mock(DAO.class);
	private Provider<DAO> daoProvider = () -> dao;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetTemplates() throws DD4StorageException {
		Store<Template> store = new GenericStore<>(Template.class, daoProvider);
		JSONService service = new BudgetService.BudgetJSONService<Template>(
				Template.class, new BudgetService<>(store, securityManagerProvider));

		when(dao.list(eq(Template.class), any(Query.class)))
				.thenReturn(new QueryResult<>(
						ImmutableList.of(
								Template.newBuilder().setPortfolioId(3L).build(),
								Template.newBuilder().setPortfolioId(3L).build(),
								Template.newBuilder().setPortfolioId(3L).build()), 3));

		JSONObject templates = service.performAction("list", new JSONObject().put("portfolio_id", 3L));
		assertEquals(3, templates.getJSONArray("result").length());
		assertEquals(3, templates.getInt("totalSize"));
		assertEquals(3L, templates.getJSONArray("result").getJSONObject(0).getInt("portfolioId"));
	}
}
