package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
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
	public PortfolioUser update(UpdateRequest request) {
		PortfolioUser portfolioUser = portfolioUserStore.get(request.getId());
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.update(request);
	}

	@Override
	public Empty delete(@Named("id") long id) {
		PortfolioUser portfolioUser = portfolioUserStore.get(id);
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.delete(id);
	}
}
