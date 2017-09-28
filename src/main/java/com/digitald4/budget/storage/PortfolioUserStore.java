package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.Provider;
import java.util.function.UnaryOperator;

public class PortfolioUserStore extends GenericStore<PortfolioUser> {
	private final Provider<SecurityManager> securityManagerProvider;

	public PortfolioUserStore(Provider<DAO> daoProvider, Provider<SecurityManager> securityManagerProvider) {
		super(PortfolioUser.class, daoProvider);
		this.securityManagerProvider = securityManagerProvider;
	}

	@Override
	public PortfolioUser create(PortfolioUser portfolioUser_) {
		PortfolioUser portfolioUser = super.create(portfolioUser_);
		if (securityManagerProvider != null && securityManagerProvider.get() != null) {
			securityManagerProvider.get().refresh();
		}
		return portfolioUser;
	}

	@Override
	public PortfolioUser update(long id, UnaryOperator<PortfolioUser> updater) {
		PortfolioUser portfolioUser = super.update(id, updater);
		if (securityManagerProvider != null && securityManagerProvider.get() != null) {
			securityManagerProvider.get().refresh();
		}
		return portfolioUser;
	}

	@Override
	public void delete(long id) {
		super.delete(id);
		if (securityManagerProvider != null && securityManagerProvider.get() != null) {
			securityManagerProvider.get().refresh();
		}
	}
}
