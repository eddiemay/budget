package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;

public class TemplateServiceTest extends TestCase {

	@Test
	public void testGetTemplates() throws DD4StorageException {
		TemplateStore store = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		TemplateService service = new TemplateService(store);
		
		List<TemplateUI> templates = service.list(TemplateListRequest.newBuilder()
				.setPortfolioId(3)
				.build());
		assertTrue(templates.size() > 0);
		assertTrue(templates.get(0).getBillCount() > 3);
		assertTrue(templates.get(0).getBill(0).getTransactionCount() > 0);
	}
}
