package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;

import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import java.util.HashMap;
import java.util.List;

import com.googlecode.protobuf.format.JsonFormat;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

public class BillServiceTest extends TestCase {
	@Mock private BillStore mockStore = mock(BillStore.class);

	@Test
	public void testCreateBill() throws Exception {
		when(mockStore.getType()).thenReturn(Bill.getDefaultInstance());
		when(mockStore.create(any(Bill.class))).thenAnswer(i -> i.getArguments()[0]);
		BillService service = new BillService(mockStore, null);

		service.create(CreateRequest.newBuilder()
				.setProto(JsonFormat.printToString(Bill.newBuilder()
						.setName("Test")
						.setAccountId(5)
						.setAmountDue(500)
						.setYear(2015)
						.setMonth(12)
						.setDay(15)
						//.putAllTransaction(BillUI.TransactionUI.newBuilder()
							//	.setDebitAccountId(71)
								//.setAmount(500))
						.build()))
				.build());

		service.create(CreateRequest.newBuilder()
			.setProto("{\"trans\":[{\"debit_account_id\":87,\"amount\":null},{\"debit_account_id\":71,\"amount\":500},{\"debit_account_id\":106,\"amount\":null}],\"year\":2015,\"month\":12,\"day\":15,\"portfolio_id\":3,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":[{\"debit_account_id\":71,\"amount\":500}]}")
			.build());

		service.create(CreateRequest.newBuilder()
				.setProto("{\"trans\":[{\"debit_account_id\":87,\"amount\":null},{\"debit_account_id\":71,\"amount\":500},{\"debit_account_id\":106,\"amount\":null}],\"year\":2015,\"month\":12,\"day\":15,\"portfolio_id\":3,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":[{\"debit_account_id\":71,\"amount\":500}]}")
				.build());

		service.create(CreateRequest.newBuilder()
				.setProto("{\"trans\":[],\"year\":2016,\"month\":1,\"day\":1,\"portfolio_id\":11,\"account_id\":185,\"amount_due\":1,\"status\":1,\"transaction\":[]}")
				.build());
	}

	@Test
	public void testGetBills() throws DD4StorageException {
		BillStore store = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), null,null);
		BillService service = new BillService(store, null);
		
		List<Bill> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setYear(2016)
				.setMonth(6)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionMap().size() > 0);
		assertTrue(bills.get(0).getTransactionMap().values().iterator().next().getAmount() != 0);
	}
	
	@Test @Ignore
	public void applyTemplate() throws DD4StorageException {
		BalanceStore balanceStore = new BalanceStore(new DAOProtoSQLImpl<>(Balance.class, dbConnector));
		Store<TemplateBill> templateBillStore = new GenericStore<>(new DAOProtoSQLImpl<>(TemplateBill.class, dbConnector));
		BillStore store = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), balanceStore, templateBillStore);
		Store<Template> templateStore = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		BillService service = new BillService(store, templateStore);
		
		List<Bill> bills = service.list(BillListRequest.newBuilder()
				.setPortfolioId(3)
				.setYear(2017)
				.setMonth(7)
				.build());
		assertTrue(bills.isEmpty());
		
		bills = service.applyTemplate(ApplyTemplateRequest.newBuilder()
				.setTemplateId(1)
				.setYear(2017)
				.setMonth(6)
				.build());
		assertTrue(bills.size() > 0);
		assertTrue(bills.get(0).getTransactionMap().size() > 0);
		assertTrue(bills.get(0).getTransactionMap().values().iterator().next().getAmount() != 0);
	}

	@Test
	public void protoMapLambda() throws Exception {
		Bill bill = Bill.newBuilder()
				.setAmountDue(100)
				.build();
		bill.getTransactionMap().forEach((acctId, trans) -> System.out.println(acctId + " " + trans));

		Map<Integer, Transaction> map = new HashMap<>();
		map.put(1, Transaction.newBuilder().setAmount(10).build());
		map.put(2, Transaction.newBuilder().setAmount(20).build());
		map.put(3, Transaction.newBuilder().setAmount(30).build());
		bill = bill.toBuilder()
				.putAllTransaction(map)
				.build();
		bill.getTransactionMap().forEach((acctId, trans) -> System.out.println(acctId + " " + trans));

		String json = JsonFormat.printToString(bill);
		System.out.println(json);
		Bill.Builder builder = Bill.newBuilder();
		JsonFormat.merge(json, builder);
		bill = builder.build();
		bill.getTransactionMap();
		bill.getTransactionMap().entrySet();
		for (Map.Entry<Integer, Transaction> entry : bill.getTransactionMap().entrySet()) {
			// System.out.println(entry.getKey() * 2);
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		bill.getTransactionMap().forEach((acctId, trans) -> System.out.println(acctId + " " + trans));
	}
}
