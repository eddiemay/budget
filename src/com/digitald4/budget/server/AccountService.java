package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.OrderBy;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import org.json.JSONObject;

public class AccountService extends SingleProtoService<Account> {
	
	AccountService(Store<Account> store) {
		super(store);
	}

	@Override
	public JSONObject list(JSONObject request) throws DD4StorageException {
		return convertToJSON(list(transformJSONRequest(AccountListRequest.getDefaultInstance(), request)));
	}

	public ListResponse list(AccountListRequest request) {
		return list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder()
						.setColumn("portfolio_id")
						.setOperan("=")
						.setValue(String.valueOf(request.getPortfolioId())))
				.addOrderBy(OrderBy.newBuilder().setColumn("Name"))
				.build());
	}
}
