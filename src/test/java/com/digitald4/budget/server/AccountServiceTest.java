package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.TestCase;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.util.ProtoUtil;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.inject.Provider;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

public class AccountServiceTest extends TestCase {
	@Mock private DAO dao = mock(DAO.class);
	private Provider<DAO> daoProvider = () -> dao;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;
	private BudgetService<Account> service = new BudgetService<>(
			new GenericStore<>(Account.class, daoProvider), securityManagerProvider);

	@Test
	public void testListAccounts() {
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
				.getResults();
		assertTrue(accounts.size() > 0);
		assertEquals(3, accounts.get(0).getPortfolioId());
		
		Account last = accounts.get(0);
		for (Account account : accounts) {
			assertTrue(last.getName().compareTo(account.getName()) < 1);
			last = account;
		}

		JSONObject response = new BudgetService.BudgetJSONService<>(service)
				.performAction("list", new JSONObject().put("portfolio_id", 3));
		assertEquals(accounts.size(), response.getJSONArray("result").length());

		QueryResult<Account> result = new QueryResult<>(accounts, accounts.size());
		assertEquals(accounts.size(), result.getResults().size());


		String json = ProtoUtil.toJSON(result).toString();
		System.out.println(json);
	}
	
	@Test
	public void testGetAccount() {
		when(dao.get(Account.class, 71L))
				.thenReturn(Account.newBuilder().setId(71L).setName("Account 71").build());
		Account account = service.get(71L);
		assertEquals(71L, account.getId());
	}
}
