package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Empty;

public class PortfolioUserService extends BudgetService<PortfolioUser> {
	private final PortfolioUserStore portfolioUserStore;
	private final Provider<SecurityManager> securityManagerProvider;

	PortfolioUserService(PortfolioUserStore portfolioUserStore, Provider<SecurityManager> securityManagerProvider) {
		super(portfolioUserStore, securityManagerProvider);
		this.portfolioUserStore = portfolioUserStore;
		this.securityManagerProvider = securityManagerProvider;
	}

	@Override
	public PortfolioUser create(CreateRequest request) {
		securityManagerProvider.get().checkDeleteAccess(request.getEntity().unpack(PortfolioUser.class).getPortfolioId());
		return super.create(request);
	}

	@Override
	public PortfolioUser update(UpdateRequest request) {
		PortfolioUser portfolioUser = portfolioUserStore.get(request.getId());
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.update(request);
	}

	@Override
	public Empty delete(DeleteRequest request) {
		PortfolioUser portfolioUser = portfolioUserStore.get(request.getId());
		securityManagerProvider.get().checkDeleteAccess(portfolioUser.getPortfolioId());
		return super.delete(request);
	}
}
