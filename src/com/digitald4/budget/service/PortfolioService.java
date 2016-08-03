package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Portfolio.PortfolioUser;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI.PortfolioUserUI;
import com.digitald4.budget.proto.BudgetUIProtos.UserRoleUI;
import com.digitald4.budget.store.PortfolioStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;
import com.digitald4.common.util.UserProvider;

import java.util.List;

public class PortfolioService extends DualProtoService<PortfolioUI, Portfolio> {
	
	private static Function<PortfolioUI, Portfolio> converter = new Function<PortfolioUI, Portfolio>() {
		@Override
		public PortfolioUI execute(Portfolio portfolio) {
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
	
	private static Function<Portfolio, PortfolioUI> reverse = new Function<Portfolio, PortfolioUI>() {
		@Override
		public Portfolio execute(PortfolioUI template) {
			return Portfolio.newBuilder()
					.setId(template.getId())
					.setName(template.getName())
					.build();
		}
	};
	
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private final PortfolioStore store;
	private final UserProvider userProvider;
	
	public PortfolioService(PortfolioStore store, UserProvider userProvider) {
		super(PortfolioUI.class, store);
		this.store = store;
		this.userProvider = userProvider;
	}
	
	@Override
	public Function<PortfolioUI, Portfolio> getConverter() {
		return converter;
	}
	
	@Override
	public Function<Portfolio, PortfolioUI> getReverseConverter() {
		return reverse;
	}
	
	public List<PortfolioUI> list(PortfolioListRequest request) throws DD4StorageException {
		return threader.parDo(store.getByUser(userProvider.get().getId()), getConverter());
	}
	
	public PortfolioUI create(PortfolioCreateRequest request) throws DD4StorageException {
		return getConverter().execute(store.create(reverse.execute(request.getPortfolio())));
	}
}
