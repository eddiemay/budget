package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Portfolio.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;

import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Pair;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioStore extends GenericStore<Portfolio> {

	private final Store<PortfolioUser> portfolioUserStore;
	public PortfolioStore(DAO<Portfolio> dao, Store<PortfolioUser> portfolioUserStore) {
		super(dao);
		this.portfolioUserStore = portfolioUserStore;
	}

	@Override
	public Portfolio create(Portfolio portfolio_) {
		List<PortfolioUser> portfolioUsers = portfolio_.getPortfolioUserList();
		Portfolio portfolio = super.create(portfolio_.toBuilder().clearPortfolioUser().build());
		return portfolio.toBuilder()
				.addAllPortfolioUser(portfolioUsers.stream()
						.map(portfolioUser -> portfolioUserStore.create(portfolioUser.toBuilder()
								.setPortfolioId(portfolio.getId())
								.build()))
						.collect(Collectors.toList()))
				.build();
	}
}
