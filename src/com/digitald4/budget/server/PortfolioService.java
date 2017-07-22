package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.util.Provider;
import org.json.JSONObject;

public class PortfolioService extends SingleProtoService<Portfolio> {
	
	private final Provider<User> userProvider;
	
	 PortfolioService(PortfolioStore store, Provider<User> userProvider) {
		super(store);
		this.userProvider = userProvider;
	}

	@Override
	public ListResponse list(ListRequest request) throws DD4StorageException {
	 	int userId = userProvider.get().getId();
		return super.list(request.toBuilder()
				.addFilter(Filter.newBuilder().setColumn("user_id").setOperan("=").setValue(String.valueOf(userId)))
				.build());
	}
}
