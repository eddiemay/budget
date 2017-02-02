package com.digitald4.budget.server;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountSummaryRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;

public class AccountServiceTest extends TestCase {

	@Test
	public void testListAccounts() throws DD4StorageException {
		AccountStore store = new AccountStore(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		AccountService service = new AccountService(store);
		
		List<AccountUI> accounts = service.list(AccountListRequest.newBuilder()
				.setPortfolioId(3)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.build());
		assertTrue(accounts.size() > 0);
		assertEquals(3, accounts.get(0).getPortfolioId());
		
		AccountUI last = accounts.get(0);
		for (AccountUI account : accounts) {
			assertTrue(last.getName().compareTo(account.getName()) < 1);
			last = account;
		}
	}
	
	@Test
	public void testGetAccount() throws DD4StorageException {
		AccountStore store = new AccountStore(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		AccountService service = new AccountService(store);
		
		AccountUI account = service.get(AccountGetRequest.newBuilder()
				.setAccountId(71)
				.setRefDate(DateTime.parse("2016-07-01").getMillis())
				.build());
		assertEquals(71, account.getId());
		assertEquals("2016-07", account.getBalance().getDate());
	}

	@Test
	public void testGetSummary() throws DD4StorageException {
		AccountStore store = new AccountStore(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		AccountService service = new AccountService(store);

		List<AccountUI> accounts = service.getSummary(AccountSummaryRequest.newBuilder()
				.setPortfolioId(3)
				.setYear(2016)
				.build());
		assertTrue(accounts.size() > 0);
		assertEquals(3, accounts.get(0).getPortfolioId());
		assertTrue(accounts.get(0).getSummaryCount() > 0);
		// assertTrue(accounts.get(0).getSummary(0).getMonth().startsWith("2016-"));
	}
}
