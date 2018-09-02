package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4UIProtos.GetRequest;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.common.util.Provider;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

public class AccountServiceTest {
	@Mock private DAO dao = mock(DAO.class);
	private Provider<DAO> daoProvider = () -> dao;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;
	private BudgetService<Account> service = new BudgetService<>(
			new GenericStore<>(Account.class, daoProvider), securityManagerProvider);

	@Test
	public void testListAccounts() throws Exception {
		when(dao.list(eq(Account.class), any(Query.class)))
				.thenReturn(new QueryResult<>(
						ImmutableList.of(
								Account.newBuilder().setPortfolioId(3).setName("Account A").build(),
								Account.newBuilder().setPortfolioId(3).setName("Account B").build(),
								Account.newBuilder().setPortfolioId(3).setName("Account C").build())
						, 3));

		List<Account> accounts = service
				.list(BudgetListRequest.newBuilder()
						.setPortfolioId(3)
						.build())
				.getResultList()
				.stream()
				.map(any -> ProtoUtil.unpack(Account.class, any))
				.collect(Collectors.toList());
		assertTrue(accounts.size() > 0);
		assertEquals(3, accounts.get(0).getPortfolioId());
		
		Account last = accounts.get(0);
		for (Account account : accounts) {
			assertTrue(last.getName().compareTo(account.getName()) < 1);
			last = account;
		}

		JSONObject response = service.performAction("list", new JSONObject().put("portfolio_id", 3));
		assertEquals(accounts.size(), response.getJSONArray("result").length());

		ListResponse listResponse = ListResponse.newBuilder()
				.addAllResult(accounts.stream()
						.map(Any::pack)
						.collect(Collectors.toList()))
				.setTotalSize(accounts.size())
				.build();
		assertEquals(accounts.size(), listResponse.getResultCount());


		JsonFormat.TypeRegistry registry =
				JsonFormat.TypeRegistry.newBuilder().add(Account.getDescriptor()).build();
		JsonFormat.Printer printer = JsonFormat.printer().usingTypeRegistry(registry);

		String json = printer.print(listResponse);
		System.out.println(json);
	}
	
	@Test
	public void testGetAccount() {
		when(dao.get(Account.class, 71L))
				.thenReturn(Account.newBuilder().setId(71L).setName("Account 71").build());
		Account account = service.get(GetRequest.newBuilder()
				.setId(71L)
				.build());
		assertEquals(71L, account.getId());
	}
}
