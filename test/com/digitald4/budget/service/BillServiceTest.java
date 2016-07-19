package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.store.BillStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.DateRange;

public class BillServiceTest extends TestCase {

	@Test
	public void testGetBills() throws DD4StorageException {
		BillStore store = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector), null);
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-06-01").getMillis())
				.setDateRange(DateRange.MONTH)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
		
		BillUI prev = bills.get(0);
		for (BillUI bill : bills) {
			assertTrue(bill.getDueDate() >= prev.getDueDate());
			prev = bill;
		}
	}
	
	@Test @Ignore
	public void applyTemplate() throws DD4StorageException {
		BillStore store = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector), null);
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.setDateRange(DateRange.MONTH)
				.build());
		System.out.println(bills);
		assertTrue(bills.isEmpty());
		
		bills = service.applyTemplate(ApplyTemplateRequest.newBuilder()
				.setTemplateId(1)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.setDateRange(DateRange.MONTH)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
	}
}
