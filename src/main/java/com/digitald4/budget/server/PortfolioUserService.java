package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.model.UpdateRequest;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.protobuf.Empty;
import javax.inject.Provider;

public class PortfolioUserService extends BudgetService<PortfolioUser> {
	private final PortfolioUserStore portfolioUserStore;
	private final Provider<SecurityManager> securityManagerProvider;

	PortfolioUserService(PortfolioUserStore portfolioUserStore, Provider<SecurityManager> securityManagerProvider) {
		super(portfolioUserStore, securityManagerProvider);
		this.portfolioUserStore = portfolioUserStore;
		this.securityManagerProvider = securityManagerProvider;
	}

	@Override
	public PortfolioUser create(PortfolioUser portfolioUser) {
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.create(portfolioUser);
	}

	@Override
	@ApiMethod(httpMethod = ApiMethod.HttpMethod.PUT, path = "{id}")
	public PortfolioUser update(@Named("id") long id, UpdateRequest<PortfolioUser> request) {
		PortfolioUser portfolioUser = portfolioUserStore.get(id);
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.update(id, request);
	}

	@Override
	public Empty delete(@Named("id") long id) {
		PortfolioUser portfolioUser = portfolioUserStore.get(id);
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.delete(id);
	}
}
