package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.storage.Store;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SecurityManager {
	private final User user;
	private final Store<PortfolioUser> portfolioUserStore;
	private Map<Long, PortfolioUser> portfolios;

	public SecurityManager(User user, PortfolioUserStore portfolioUserStore) {
		this.user = user;
		this.portfolioUserStore = portfolioUserStore;
		refresh();
	}

	public SecurityManager refresh() {
		portfolios = portfolioUserStore
				.list(ListRequest.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("user_id").setValue(String.valueOf(user.getId())))
						.build())
				.getResultList()
				.stream()
				.collect(Collectors.toMap(PortfolioUser::getPortfolioId, Function.identity()));
		return this;
	}

	public void checkReadAccess(long portfolioId) {
		if (!portfolios.containsKey(portfolioId)) {
			throw new DD4StorageException("Not found");
		}
	}

	public void checkWriteAccess(long portfolioId) {
		PortfolioUser portfolioUser = portfolios.get(portfolioId);
		if (portfolioUser == null) {
			throw new DD4StorageException("Not found");
		} else if (portfolioUser.getRole() != UserRole.UR_CAN_EDIT && portfolioUser.getRole() != UserRole.UR_OWNER) {
			throw new DD4StorageException("Access Denied");
		}
	}

	public void checkDeleteAccess(long portfolioId) {
		PortfolioUser portfolioUser = portfolios.get(portfolioId);
		if (portfolioUser == null) {
			throw new DD4StorageException("Not found");
		} else if (portfolioUser.getRole() != UserRole.UR_OWNER) {
			throw new DD4StorageException("Access Denied");
		}
	}
}
