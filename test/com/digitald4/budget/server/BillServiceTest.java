package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;

import java.util.List;

import com.googlecode.protobuf.format.JsonFormat;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

public class BillServiceTest extends TestCase {
	@Mock BillStore mockStore = mock(BillStore.class);

	@Test
	public void testCreateBill() throws Exception {
		when(mockStore.getType()).thenReturn(Bill.getDefaultInstance());
		when(mockStore.create(any(Bill.class))).thenAnswer(i -> i.getArguments()[0]);
		BillService service = new BillService(mockStore, null);

		service.create(CreateRequest.newBuilder()
				.setProto(JsonFormat.printToString(BillUI.newBuilder()
						.setName("Test")
						.setAccountId(5)
						.setAmountDue(500)
						.setDueDate(1481788800000L)
						.addTransaction(BillUI.TransactionUI.newBuilder()
								.setDebitAccountId(71)
								.setAmount(500))
						.build()))
				.build());

		service.create(CreateRequest.newBuilder()
			.setProto("{\"trans\":[{\"debit_account_id\":87,\"$$hashKey\":\"object:72\",\"amount\":null},{\"debit_account_id\":71,\"$$hashKey\":\"object:73\",\"amount\":500},{\"debit_account_id\":106,\"$$hashKey\":\"object:74\",\"amount\":null}],\"due_date\":1481788800000,\"portfolio_id\":3,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":[{\"debit_account_id\":71,\"$$hashKey\":\"object:73\",\"amount\":500}]}")
			.build());

		service.create(CreateRequest.newBuilder()
				.setProto("{\"trans\":[{\"debit_account_id\":87,\"$$hashKey\":\"object:712\",\"amount\":null},{\"debit_account_id\":71,\"$$hashKey\":\"object:73\",\"amount\":500},{\"debit_account_id\":106,\"$$hashKey\":\"object:74\",\"amount\":null}],\"due_date\":1481788800000,\"portfolio_id\":3,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":[{\"debit_account_id\":71,\"$$hashKey\":\"object:73\",\"amount\":500}]}")
				.build());

		service.create(CreateRequest.newBuilder()
				.setProto("{\"trans\":[],\"due_date\":1451635200000,\"portfolio_id\":11,\"account_id\":185,\"amount_due\":1,\"status\":1,\"transaction\":[]}")
				.build());
	}

	@Test
	public void testGetBills() throws DD4StorageException {
		BillStore store = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), null);
		TemplateStore templateStore = new TemplateStore(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-06-01").getMillis())
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
	}
	
	@Test @Ignore
	public void applyTemplate() throws DD4StorageException {
		AccountStore accountStore = new AccountStore(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		BillStore store = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), accountStore);
		TemplateStore templateStore = new TemplateStore(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<BillUI> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-08-01").getMillis())
				.build());
		System.out.println(bills);
		assertTrue(bills.isEmpty());
		
		bills = service.applyTemplate(ApplyTemplateRequest.newBuilder()
				.setTemplateId(1)
				.setRefDate(DateTime.parse("2016-08-01").getMillis())
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionCount() > 0);
		assertTrue(bills.get(0).getTransaction(0).getAmount() != 0);
	}
}
