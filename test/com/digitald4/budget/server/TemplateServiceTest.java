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
		
		JSONArray templates = service.list(new JSONObject().put("portfolio_id", 3));
		assertTrue(templates.length() > 0);
	}
}
