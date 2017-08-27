package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.storage.DAOConnectorImpl;
import com.digitald4.common.storage.DataConnector;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

public class TemplateServiceTest {
	@Mock private DataConnector dataConnector = mock(DataConnector.class);
	private Provider<DataConnector> dataConnectorProvider = () -> dataConnector;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetTemplates() throws DD4StorageException {
		Store<Template> store = new GenericStore<>(new DAOConnectorImpl<>(Template.class, dataConnectorProvider));
		BudgetService<Template> service = new BudgetService<>(store, securityManagerProvider);

		when(dataConnector.list(eq(Template.class), any(ListRequest.class)))
				.thenReturn(ListResponse.<Template>newBuilder()
						.addResult(Template.newBuilder().setPortfolioId(3).build())
						.addResult(Template.newBuilder().setPortfolioId(3).build())
						.addResult(Template.newBuilder().setPortfolioId(3).build())
						.setTotalSize(3)
						.build());
		
		JSONObject templates = service.list(new JSONObject().put("portfolio_id", 3));
		assertEquals(3, templates.getJSONArray("result").length());
		assertEquals(3, templates.getInt("totalSize"));
		assertEquals(3, templates.getJSONArray("result").getJSONObject(0).getInt("portfolioId"));
	}
}
