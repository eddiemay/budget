package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.OrderBy;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;

public class AccountService extends BudgetService<Account> {

	private final Provider<SecurityManager> securityManagerProvider;
	
	AccountService(Store<Account> store, Provider<SecurityManager> securityManagerProvider) {
		super(store, securityManagerProvider);
		this.securityManagerProvider = securityManagerProvider;
	}

	public ListResponse list(BudgetListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder()
						.setColumn("portfolio_id")
						.setOperan("=")
						.setValue(String.valueOf(request.getPortfolioId())))
				.addOrderBy(OrderBy.newBuilder().setColumn("Name"))
				.build());
	}
}
