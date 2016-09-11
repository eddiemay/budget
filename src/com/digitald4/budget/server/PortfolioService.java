package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Portfolio.PortfolioUser;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI.PortfolioUserUI;
import com.digitald4.budget.proto.BudgetUIProtos.UserRoleUI;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;
import com.digitald4.common.util.UserProvider;

import java.util.List;
import java.util.stream.Collectors;

public class PortfolioService extends DualProtoService<PortfolioUI, Portfolio> {
	
	private static Function<Portfolio, PortfolioUI> converter = new Function<Portfolio, PortfolioUI>() {
		@Override
		public PortfolioUI apply(Portfolio portfolio) {
			PortfolioUI.Builder result = PortfolioUI.newBuilder()
					.setId(portfolio.getId())
					.setName(portfolio.getName());
			for (PortfolioUser portUser : portfolio.getPortfolioUserList()) {
				result.addPortfolioUser(PortfolioUserUI.newBuilder()
						.setUserId(portUser.getUserId())
						.setRole(UserRoleUI.valueOf(portUser.getRole().getNumber()))
						.build());
			}
			return result.build();
		}
	};
	
	private static Function<PortfolioUI, Portfolio> reverse = new Function<PortfolioUI, Portfolio>() {
		@Override
		public Portfolio apply(PortfolioUI template) {
			return Portfolio.newBuilder()
					.setId(template.getId())
					.setName(template.getName())
					.build();
		}
	};
	
	private final PortfolioStore store;
	private final UserProvider userProvider;
	
	public PortfolioService(PortfolioStore store, UserProvider userProvider) {
		super(PortfolioUI.class, store);
		this.store = store;
		this.userProvider = userProvider;
	}
	
	@Override
	public Function<Portfolio, PortfolioUI> getConverter() {
		return converter;
	}
	
	@Override
	public Function<PortfolioUI, Portfolio> getReverseConverter() {
		return reverse;
	}
	
	public List<PortfolioUI> list(PortfolioListRequest request) throws DD4StorageException {
		return store.getByUser(userProvider.get().getId()).stream().map(getConverter()).collect(Collectors.toList());
	}
	
	public PortfolioUI create(PortfolioCreateRequest request) throws DD4StorageException {
		return getConverter().apply(store.create(reverse.apply(request.getPortfolio())));
	}
}
