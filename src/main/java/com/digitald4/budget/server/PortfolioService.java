package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.server.UpdateRequest;
import com.digitald4.common.storage.QueryResult;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.protobuf.Empty;
import java.util.List;
import javax.inject.Provider;

public class PortfolioService extends SingleProtoService<Portfolio> {

	private final PortfolioStore portfolioStore;
	private final Provider<User> userProvider;
	private final Provider<SecurityManager> securityManagerProvider;
	
	 PortfolioService(PortfolioStore portfolioStore,
										Provider<SecurityManager> securityManagerProvider,
										Provider<User> userProvider) {
		super(portfolioStore);
		this.portfolioStore = portfolioStore;
		this.securityManagerProvider = securityManagerProvider;
		this.userProvider = userProvider;
	}

	@Override
	public Portfolio get(long id) {
		securityManagerProvider.get().checkReadAccess(id);
	 	return super.get(id);
	}

	@Override
	public QueryResult<Portfolio> list(ListRequest request) {
		List<Portfolio> portfolios = portfolioStore.listBy(userProvider.get().getId());
		return new QueryResult<>(portfolios, portfolios.size());
	}

	@Override
	@ApiMethod(httpMethod = ApiMethod.HttpMethod.PUT, path = "{id}")
	public Portfolio update(@Named("id") long id, UpdateRequest<Portfolio> request) {
		securityManagerProvider.get().checkWriteAccess(id);
		return super.update(id, request);
	}

	@Override
	public Empty delete(long id) {
		securityManagerProvider.get().checkDeleteAccess(id);
		return super.delete(id);
	}
}
