package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.util.Provider;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class BalanceService extends BudgetService<Balance> {
	private final BalanceStore store;
	private final Provider<SecurityManager> securityManagerProvider;

	BalanceService(BalanceStore store, Provider<SecurityManager> securityManagerProvider) {
		super(store, securityManagerProvider);
		this.store = store;
		this.securityManagerProvider = securityManagerProvider;
	}

	@Override
	public JSONObject create(JSONObject request) {
		throw new UnsupportedOperationException("Unimplemented");
	}

	@Override
	public JSONObject get(JSONObject request) {
		return convertToJSON(get(transformJSONRequest(BalanceGetRequest.getDefaultInstance(), request)));
	}

	private Balance get(BalanceGetRequest request) {
		Balance balance = store.get(request.getAccountId(), request.getYear(), request.getMonth());
		securityManagerProvider.get().checkReadAccess(balance.getPortfolioId());
		return balance;
	}

	@Override
	public JSONObject list(JSONObject request) {
		return list(transformJSONRequest(BalanceListRequest.getDefaultInstance(), request));
	}

	private JSONObject list(BalanceListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		if (request.getYear() == 0) {
			throw new IllegalArgumentException("Year is required");
		}
		if (request.getMonth() != 0) {
			return convertToJSON(
					toListResponse(store.list(request.getPortfolioId(), request.getYear(), request.getMonth())));
		} else {
			JSONObject months = new JSONObject();
			store.list(request.getPortfolioId(), request.getYear()).getResultList()
					.stream()
					.collect(Collectors.groupingBy(Balance::getMonth))
					.forEach((month, balances) -> {
						JSONObject json = new JSONObject();
						balances.forEach(balance -> json.put("" + balance.getAccountId(), convertToJSON(balance)));
						months.put("" + month, json);
					});
			return months;
		}
	}

	@Override
	public JSONObject update(JSONObject request) {
		throw new UnsupportedOperationException("Unimplemented");
	}

	@Override
	public JSONObject delete(JSONObject request) {
		throw new UnsupportedOperationException("Unimplemented");
	}
}
