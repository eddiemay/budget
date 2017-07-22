package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.common.proto.DD4UIProtos.GetRequest;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.storage.GenericStore;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Test;

public class AccountServiceTest extends TestCase {
	private AccountService service =
			new AccountService(new GenericStore<>(new DAOProtoSQLImpl<>(Account.class, dbConnector)));

	@Test
	public void testListAccounts() throws Exception {
		List<Account> accounts = service
				.list(AccountListRequest.newBuilder()
						.setPortfolioId(3)
						.build())
				.getResultList()
				.stream()
				.map(any -> any.unpack(Account.class))
				.collect(Collectors.toList());
		assertTrue(accounts.size() > 0);
		assertEquals(3, accounts.get(0).getPortfolioId());
		
		Account last = accounts.get(0);
		for (Account account : accounts) {
			assertTrue(last.getName().compareTo(account.getName()) < 1);
			last = account;
		}

		JSONObject response = service.list(new JSONObject().put("portfolio_id", 3));
		assertEquals(accounts.size(), response.getJSONArray("items").length());

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
		Account account = service.get(GetRequest.newBuilder()
				.setId(71)
				.build());
		assertEquals(71, account.getId());
	}
}
