package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PortfolioStore extends GenericStore<Portfolio> {

	private final PortfolioUserStore portfolioUserStore;

	public PortfolioStore(DAO<Portfolio> dao, PortfolioUserStore portfolioUserStore) {
		super(dao);
		this.portfolioUserStore = portfolioUserStore;
	}

	@Override
	public Portfolio create(Portfolio portfolio) {
		Portfolio portfolio_ = super.create(portfolio);
		portfolio_.getUserMap()
				.forEach((userId, role) -> portfolioUserStore.create(PortfolioUser.newBuilder()
						.setUserId(userId)
						.setPortfolioId(portfolio_.getId())
						.setRole(role)
						.build()));
		return portfolio_;
	}

	public List<Portfolio> listBy(long userId) {
		return portfolioUserStore
				.list(ListRequest.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("user_id").setValue(String.valueOf(userId))).build())
				.getResultList()
				.stream()
				.map(portfolioUser -> get(portfolioUser.getPortfolioId()))
				.collect(Collectors.toList());

	}
}
