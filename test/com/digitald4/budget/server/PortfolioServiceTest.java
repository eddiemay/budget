package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioSQLDao;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.util.Provider;
import java.util.List;
import org.junit.Test;

public class PortfolioServiceTest extends TestCase {
	private final User user = User.newBuilder().setId(1).build();
	private final Provider<User> userProvider = () -> user;

	@Test
	public void testGetPortfolios() throws DD4StorageException {
		PortfolioStore store = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		PortfolioService service = new PortfolioService(store, userProvider);

		List<Portfolio> results = service.list(ListRequest.newBuilder().build());
		assertTrue(results.size() > 0);
		assertTrue(results.get(0).getPortfolioUserCount() > 0);
		assertTrue(results.get(0).getPortfolioUser(0).getUserId() == 1);
	}
}
