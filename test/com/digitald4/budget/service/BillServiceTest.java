package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.digitald4.budget.dao.BillDAODualReadImpl;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.store.BillStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.DateRangeType;

public class BillServiceTest extends TestCase {
	
	@Test @Ignore
	public void migrate() throws DD4StorageException {
		BillStore twoTableStore = new BillStore(new BillDAODualReadImpl(dbConnector,
				new DAOProtoSQLImpl<Transaction>(Transaction.getDefaultInstance(), dbConnector)));
		BillStore singleTableStore = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector));
		Bill bill1053 = twoTableStore.get(1053);
		assertEquals(2, bill1053.getTransactionCount());
		List<Bill> bills = twoTableStore.getAll();
		assertTrue(bills.size() > 500);
		boolean found1054 = false;
		for (Bill bill : bills) {
			if (bill.getId() == 1054) {
				assertEquals(2, bill.getTransactionCount());
				found1054 = true;
				break;
			}
		}
		assertTrue(found1054);
		
		for (final Bill bill : bills) {
			if (bill.getTransactionCount() > 0) {
				singleTableStore.update(bill.getId(), new Function<Bill, Bill>() {
					@Override
					public Bill execute(Bill bill_) {
						return bill_.toBuilder()
								.clearTransaction()
								.addAllTransaction(bill.getTransactionList())
								.build();
					}
				});
			}
		}
	}

	@Test
	public void testGetBills() throws DD4StorageException {
		BillStore store = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector));
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-06-01").getMillis())
				.setDateRange(DateRangeType.MONTH)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
	}
	
	@Test
	public void applyTemplate() throws DD4StorageException {
		BillStore store = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector));
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.setDateRange(DateRangeType.MONTH)
				.build());
		System.out.println(bills);
		assertTrue(bills.isEmpty());
		
		bills = service.applyTemplate(ApplyTemplateRequest.newBuilder()
				.setTemplateId(1)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.setDateRange(DateRangeType.MONTH)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
	}
}
