package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.QueryResult;
import com.google.api.server.spi.config.Named;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import java.util.List;
import java.util.stream.Collectors;
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
	public Portfolio update(UpdateRequest request) {
		securityManagerProvider.get().checkWriteAccess(request.getId());
		return super.update(request);
	}

	@Override
	public Empty delete(long id) {
		securityManagerProvider.get().checkDeleteAccess(id);
		return super.delete(id);
	}
}
