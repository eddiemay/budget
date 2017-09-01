package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.GetRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import java.util.List;
import java.util.stream.Collectors;

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
	public Portfolio get(GetRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getId());
	 	return super.get(request);
	}

	@Override
	public ListResponse list(ListRequest request) {
		List<Portfolio> portfolios = portfolioStore.listBy(userProvider.get().getId());
		return ListResponse.newBuilder()
				.addAllResult(portfolios.stream()
						.map(portfolio -> Any.pack(portfolio))
						.collect(Collectors.toList()))
				.setTotalSize(portfolios.size())
				.build();
	}

	@Override
	public Portfolio update(UpdateRequest request) {
		securityManagerProvider.get().checkWriteAccess(request.getId());
		return super.update(request);
	}

	@Override
	public Empty delete(DeleteRequest request) {
		securityManagerProvider.get().checkDeleteAccess(request.getId());
		return super.delete(request);
	}
}
