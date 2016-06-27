package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.store.AccountStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;

public class AccountServiceTest extends TestCase {

	@Test
	public void testListAccounts() throws DD4StorageException {
		AccountStore store = new AccountStore(
				new DAOProtoSQLImpl<Account>(Account.getDefaultInstance(), dbConnector));
		AccountService service = new AccountService(store);
		
		List<AccountUI> accounts = service.list(AccountListRequest.newBuilder()
				.setPortfolioId(3)
				.build());
		assertTrue(accounts.size() > 0);
		assertTrue(accounts.get(0).getPortfolioId() == 3);
	}
}
