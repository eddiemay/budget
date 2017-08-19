package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

public class TemplateServiceTest extends TestCase {
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetTemplates() throws DD4StorageException {
		Store<Template> store = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		BudgetService<Template> service = new BudgetService<>(store, securityManagerProvider);
		
		JSONObject templates = service.list(new JSONObject().put("portfolio_id", 3));
		assertTrue(templates.getJSONArray("result").length() > 0);
		assertTrue(templates.getInt("totalSize") > 0);
		assertEquals(templates.getInt("totalSize"), templates.getJSONArray("result").length() );
	}
}
