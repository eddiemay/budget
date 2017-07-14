package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class TemplateServiceTest extends TestCase {

	@Test
	public void testGetTemplates() throws DD4StorageException {
		Store<Template> store = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		TemplateService service = new TemplateService(store);
		
		JSONObject templates = service.list(new JSONObject().put("portfolio_id", 3));
		assertTrue(templates.getJSONArray("item").length() > 0);
		assertTrue(templates.getInt("total_size") > 0);
		assertEquals(templates.getInt("total_size"), templates.getJSONArray("item").length() );
	}
}
