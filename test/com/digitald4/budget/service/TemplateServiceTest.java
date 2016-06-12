package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;

public class TemplateServiceTest {
	
	private static DBConnector dbConnector = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dbConnector = new DBConnectorThreadPoolImpl(
				"org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true",
				"dd4_user", "getSchooled85");
	}

	@Test
	public void testGetTemplates() throws DD4StorageException {
		TemplateStore sessionStore = new TemplateStore(
				new DAOProtoSQLImpl<>(Template.getDefaultInstance(), dbConnector));
		TemplateService templateService = new TemplateService(sessionStore);
		
		List<TemplateUI> templates = templateService.list(TemplateListRequest.newBuilder()
				.setPortfolioId(3)
				.build());
		assertTrue(templates.size() > 0);
	}
}
