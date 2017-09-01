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
import com.google.protobuf.Empty;

public class PortfolioService extends SingleProtoService<Portfolio> {

	private final Provider<User> userProvider;
	private final Provider<SecurityManager> securityManagerProvider;
	
	 PortfolioService(PortfolioStore portfolioStore,
										Provider<SecurityManager> securityManagerProvider,
										Provider<User> userProvider) {
		super(portfolioStore);
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
	 	long userId = userProvider.get().getId();
		return super.list(request.toBuilder()
				.addFilter(Filter.newBuilder().setColumn("user_id").setOperan("=").setValue(String.valueOf(userId)))
				.build());
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
