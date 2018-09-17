package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.TestCase;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.QueryResult;
import com.google.common.collect.ImmutableList;
import javax.inject.Provider;
import org.junit.Test;
import org.mockito.Mock;

public class PortfolioServiceTest extends TestCase {
	@Mock private DAO dao = mock(DAO.class);
	private Provider<DAO> daoProvider = () -> dao;
	private static final long USER_ID = 859239L;
	private final User user = User.newBuilder().setId(USER_ID).build();
	private final Provider<User> userProvider = () -> user;
	private static final long PORTFOLIO_ID1 = 859239L;
	private static final long PORTFOLIO_ID2 = 24L;
	private static final long PORTFOLIO_ID3 = 32L;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetPortfolios() throws DD4StorageException {
		PortfolioStore store = new PortfolioStore(daoProvider,
				new PortfolioUserStore(daoProvider, securityManagerProvider));
		PortfolioService service = new PortfolioService(store, securityManagerProvider, userProvider);

		when(dao.list(eq(PortfolioUser.class), any(Query.class)))
				.thenReturn(new QueryResult<>(
						ImmutableList.of(
								PortfolioUser.newBuilder()
										.setUserId(USER_ID).setRole(UserRole.UR_OWNER).setPortfolioId(PORTFOLIO_ID1).build(),
								PortfolioUser.newBuilder()
										.setUserId(USER_ID).setRole(UserRole.UR_CAN_EDIT).setPortfolioId(PORTFOLIO_ID2).build(),
								PortfolioUser.newBuilder()
										.setUserId(USER_ID).setRole(UserRole.UR_READONLY).setPortfolioId(PORTFOLIO_ID3).build()),
						3));
		when(dao.get(Portfolio.class, PORTFOLIO_ID1))
				.thenReturn(Portfolio.newBuilder().setId(PORTFOLIO_ID1).putUser(USER_ID, UserRole.UR_OWNER).build());
		when(dao.get(Portfolio.class, PORTFOLIO_ID2))
				.thenReturn(Portfolio.newBuilder().setId(PORTFOLIO_ID2).putUser(USER_ID, UserRole.UR_CAN_EDIT).build());
		when(dao.get(Portfolio.class, PORTFOLIO_ID3))
				.thenReturn(Portfolio.newBuilder().setId(PORTFOLIO_ID3).putUser(USER_ID, UserRole.UR_READONLY).build());

		QueryResult<Portfolio> response = service.list(ListRequest.newBuilder().build());
		assertTrue(response.getResults().size() > 0);
		Portfolio portfolio = response.getResults().get(0);
		assertEquals(UserRole.UR_OWNER, portfolio.getUserOrThrow(user.getId()));
	}
}
