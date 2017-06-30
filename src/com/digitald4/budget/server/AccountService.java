package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class AccountService extends SingleProtoService<Account> {
	
	private final Store<Account> store;

	AccountService(Store<Account> store) {
		super(store);
		this.store = store;
	}

	@Override
	public JSONArray list(JSONObject request) throws DD4StorageException {
		return convertToJSON(list(transformJSONRequest(AccountListRequest.getDefaultInstance(), request)));
	}

	public List<Account> list(AccountListRequest request) {
		return store.get(
				Filter.newBuilder()
						.setColumn("portfolio_id")
						.setOperan("=")
						.setValue(String.valueOf(request.getPortfolioId()))
						.build())
				.stream()
				.sorted(Comparator.comparing(Account::getName))
				.collect(Collectors.toList());
	}
}
