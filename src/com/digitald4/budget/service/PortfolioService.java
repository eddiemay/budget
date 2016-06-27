package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Portfolio.PortfolioUser;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI.PortfolioUserUI;
import com.digitald4.budget.proto.BudgetUIProtos.UserRoleUI;
import com.digitald4.budget.store.PortfolioStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.util.UserProvider;

import java.util.List;

public class PortfolioService {
	
	private static Function<PortfolioUI, Portfolio> converter = new Function<PortfolioUI, Portfolio>() {
		@Override
		public PortfolioUI execute(Portfolio portfolio) {
			PortfolioUI.Builder result = PortfolioUI.newBuilder()
					.setId(portfolio.getId())
					.setName(portfolio.getName())
					.setDescription(portfolio.getDescription());
			for (PortfolioUser portUser : portfolio.getPortfolioUserList()) {
				result.addPortfolioUser(PortfolioUserUI.newBuilder()
						.setPortfolioId(portfolio.getId())
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
					.setDescription(template.getDescription())
					.build();
		}
	};
	
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private final PortfolioStore store;
	private final UserProvider userProvider;
	
	public PortfolioService(PortfolioStore store, UserProvider userProvider) {
		this.store = store;
		this.userProvider = userProvider;
	}
	
	public List<PortfolioUI> list(PortfolioListRequest request) throws DD4StorageException {
		return threader.parDo(store.getByUser(userProvider.get().getId()), converter);
	}
	
	public PortfolioUI get(PortfolioGetRequest request) throws DD4StorageException {
		return converter.execute(store.read(request.getPortfolioId()));
	}
	
	public PortfolioUI create(PortfolioCreateRequest request) throws DD4StorageException {
		return converter.execute(store.create(reverse.execute(request.getPortfolio())));
	}
	
	public boolean delete(PortfolioDeleteRequest request) throws DD4StorageException {
		store.delete(request.getPortfolioId());
		return true;
	}
}
