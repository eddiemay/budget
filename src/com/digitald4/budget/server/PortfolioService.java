package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.util.Provider;
import java.util.List;

public class PortfolioService extends SingleProtoService<Portfolio> {
	
	private final PortfolioStore store;
	private final Provider<User> userProvider;
	
	 PortfolioService(PortfolioStore store, Provider<User> userProvider) {
		super(store);
		this.store = store;
		this.userProvider = userProvider;
	}

	@Override
	public List<Portfolio> list(ListRequest request) throws DD4StorageException {
		return store.getByUser(userProvider.get().getId());
	}
}
