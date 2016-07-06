package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.digitald4.budget.dao.TemplateSQLDao;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.exception.DD4StorageException;

public class TemplateServiceTest extends TestCase {

	@Test
	public void testGetTemplatesOld() throws DD4StorageException {
		TemplateStore store = new TemplateStore(new TemplateSQLDao(dbConnector));
		TemplateService service = new TemplateService(store);
		
		List<TemplateUI> templates = service.list(TemplateListRequest.newBuilder()
				.setPortfolioId(3)
				.build());
		assertTrue(templates.size() > 0);
		assertTrue(templates.get(0).getBillCount() > 3);
		assertTrue(templates.get(0).getBill(0).getTransactionCount() > 0);
	}

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
	
	@Test
	public void migrateTemplates() throws DD4StorageException {
		TemplateStore oldStore = new TemplateStore(new TemplateSQLDao(dbConnector));
		TemplateStore newStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		
		for (final Template template : oldStore.getAll()) {
			newStore.update(template.getId(), new Function<Template, Template>() {
				@Override
				public Template execute(Template template_) {
					return template_.toBuilder()
							.clearBill()
							.addAllBill(template.getBillList())
							.build();
				}
			});
		}
	}
}
