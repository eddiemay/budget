package com.digitald4.budget.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.DAOConnectorImpl;
import com.digitald4.common.storage.DataConnector;
import com.digitald4.common.util.Provider;
import org.junit.Test;
import org.mockito.Mock;

public class PortfolioServiceTest {
	@Mock private DataConnector dataConnector = mock(DataConnector.class);
	private Provider<DataConnector> dataConnectorProvider = () -> dataConnector;
	private final User user = User.newBuilder().setId(1L).build();
	private final Provider<User> userProvider = () -> user;
	@Mock private SecurityManager securityManager = mock(SecurityManager.class);
	private Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testGetPortfolios() throws DD4StorageException {
		PortfolioStore store = new PortfolioStore(new DAOConnectorImpl<>(Portfolio.class, dataConnectorProvider), null);
		PortfolioService service = new PortfolioService(store, securityManagerProvider, userProvider);

		when(dataConnector.list(eq(Portfolio.class), any(ListRequest.class)))
				.thenReturn(com.digitald4.common.storage.ListResponse.<Portfolio>newBuilder()
						.addResult(Portfolio.newBuilder().setUserId(user.getId()).setRole(UserRole.UR_OWNER).build())
						.addResult(Portfolio.newBuilder().setUserId(user.getId()).setRole(UserRole.UR_CAN_EDIT).build())
						.addResult(Portfolio.newBuilder().setUserId(user.getId()).setRole(UserRole.UR_READONLY).build())
						.setTotalSize(3)
						.build());

		ListResponse response = service.list(ListRequest.newBuilder().build());
		assertTrue(response.getResultList().size() > 0);
		Portfolio portfolio = response.getResultList().get(0).unpack(Portfolio.class);
		assertTrue(portfolio.getUserId() == 1);
		assertTrue(portfolio.getRole() == UserRole.UR_OWNER);
	}
}
