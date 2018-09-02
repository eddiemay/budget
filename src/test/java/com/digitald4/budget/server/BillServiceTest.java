package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.common.util.Provider;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

public class BillServiceTest {
	@Mock private DAO dao = mock(DAO.class);
	private Provider<DAO> daoProvider = () -> dao;
	@Mock private BillStore mockStore = mock(BillStore.class);
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testCreateBill() {
		when(mockStore.getType()).thenReturn(Bill.getDefaultInstance());
		when(mockStore.create(any(Bill.class))).thenAnswer(i -> i.getArguments()[0]);
		when(dao.get(eq(Account.class), any(Integer.class))).thenReturn(Account.newBuilder().setPortfolioId(3).build());
		BillService service = new BillService(mockStore, securityManagerProvider, null);

		service.create(CreateRequest.newBuilder()
				.setEntity(Any.pack(Bill.newBuilder()
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

		service.performAction("create", new JSONObject()
				.put("entity",
						new JSONObject("{@type: \"type.googleapis.com/budget.Bill\", \"year\":2015,\"month\":12,\"day\":15,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":{71:{\"amount\":500}}}")));

		service.performAction("create", new JSONObject()
				.put("entity",
						new JSONObject("{@type: \"type.googleapis.com/budget.Bill\", \"year\":2015,\"month\":12,\"day\":15,\"account_id\":91,\"name\":\"Loan to Mother\",\"amount_due\":500,\"transaction\":{71:{\"amount\":500}}}")));

		service.performAction("create", new JSONObject()
				.put("entity",
						new JSONObject("{@type: \"type.googleapis.com/budget.Bill\", \"year\":2016,\"month\":1,\"day\":1,\"account_id\":185,\"amount_due\":1,\"status\":1,\"transaction\":{}}")));
	}

	@Test
	public void testGetBills() throws DD4StorageException {
		BillStore store = new BillStore(daoProvider, null, null);
		BillService service = new BillService(store, securityManagerProvider, null);

		when(dao.list(eq(Bill.class), any(Query.class)))
				.thenReturn(new QueryResult<>(
						ImmutableList.of(
								Bill.newBuilder()
										.setPortfolioId(3)
										.setName("Account A")
										.putTransaction(5, Transaction.newBuilder().setAmount(19.95).build())
										.build(),
								Bill.newBuilder().setPortfolioId(3).setName("Account B").build(),
								Bill.newBuilder().setPortfolioId(3).setName("Account C").build()),
						3));

		List<Bill> bills = service
				.list(BillListRequest.newBuilder()
						.setPortfolioId(3)
						.setYear(2016)
						.setMonth(6)
						.build())
				.getResultList()
				.stream()
				.map(any -> ProtoUtil.unpack(Bill.class, any))
				.collect(Collectors.toList());
		assertEquals(3, bills.size());
		assertEquals(1, bills.get(0).getTransactionMap().size());
		assertEquals(19.95, bills.get(0).getTransactionMap().values().iterator().next().getAmount(), .001);
	}
	
	@Test @Ignore
	public void applyTemplate() throws DD4StorageException {
		BalanceStore balanceStore = new BalanceStore(daoProvider);
		Store<TemplateBill> templateBillStore = new GenericStore<>(TemplateBill.class, daoProvider);
		BillStore store = new BillStore(daoProvider, balanceStore, templateBillStore);
		Store<Template> templateStore = new GenericStore<>(Template.class, daoProvider);
		BillService service = new BillService(store, securityManagerProvider, templateStore);
		
		List<Bill> bills = service
				.list(BillListRequest.newBuilder()
					.setPortfolioId(3)
					.setYear(2017)
					.setMonth(7)
					.build())
				.getResultList()
				.stream()
				.map(any -> ProtoUtil.unpack(Bill.class, any))
				.collect(Collectors.toList());
		assertTrue(bills.isEmpty());
		
		bills = service
				.applyTemplate(ApplyTemplateRequest.newBuilder()
					.setTemplateId(1)
					.setYear(2017)
					.setMonth(6)
					.build())
				.getResultList()
				.stream()
				.map(any -> ProtoUtil.unpack(Bill.class, any))
				.collect(Collectors.toList());
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

		Map<Long, Transaction> map = new HashMap<>();
		map.put(1L, Transaction.newBuilder().setAmount(10).build());
		map.put(2L, Transaction.newBuilder().setAmount(20).build());
		map.put(3L, Transaction.newBuilder().setAmount(30).build());
		bill = bill.toBuilder()
				.putAllTransaction(map)
				.build();
		bill.getTransactionMap().forEach((acctId, trans) -> System.out.println(acctId + " " + trans));

		String json = JsonFormat.printer().print(bill);
		System.out.println(json);
		Bill.Builder builder = Bill.newBuilder();
		JsonFormat.parser().merge(json, builder);
		bill = builder.build();
		bill.getTransactionMap();
		for (Map.Entry<Long, Transaction> entry : bill.getTransactionMap().entrySet()) {
			// System.out.println(entry.getKey() * 2);
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		bill.getTransactionMap().forEach((acctId, trans) -> System.out.println(acctId + " " + trans));
	}
}
