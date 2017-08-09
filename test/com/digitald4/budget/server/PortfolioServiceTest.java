package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioSQLDao;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.util.Provider;
import org.junit.Test;
import org.mockito.Mock;

public class PortfolioServiceTest extends TestCase {
	private final User user = User.newBuilder().setId(1).build();
	private final Provider<User> userProvider = () -> user;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetPortfolios() throws DD4StorageException {
		PortfolioStore store = new PortfolioStore(new PortfolioSQLDao(dbConnector), null);
		PortfolioService service = new PortfolioService(store, securityManagerProvider, userProvider);

		ListResponse response = service.list(ListRequest.newBuilder().build());
		assertTrue(response.getResultList().size() > 0);
		Portfolio portfolio = response.getResultList().get(0).unpack(Portfolio.class);
		assertTrue(portfolio.getPortfolioUserCount() > 0);
		assertTrue(portfolio.getPortfolioUser(0).getUserId() == 1);
	}
}
