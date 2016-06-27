package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.digitald4.budget.dao.PortfolioSQLDao;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI;
import com.digitald4.budget.store.PortfolioStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.util.UserProvider;

public class PortfolioServiceTest extends TestCase {

	@Test
	public void testGetPortfolios() throws DD4StorageException {
		PortfolioStore store = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		UserProvider userProvider = new UserProvider();
		userProvider.set(User.newBuilder()
				.setId(1)
				.build());
		PortfolioService service = new PortfolioService(store, userProvider);
		
		List<PortfolioUI> results = service.list(PortfolioListRequest.newBuilder()
				.build());
		assertTrue(results.size() > 0);
		assertTrue(results.get(0).getPortfolioUserCount() > 0);
		assertTrue(results.get(0).getPortfolioUser(0).getUserId() == 1);
	}
}
