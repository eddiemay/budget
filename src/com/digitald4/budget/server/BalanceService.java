package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.common.server.SingleProtoService;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class BalanceService extends SingleProtoService<Balance> {
	private final BalanceStore store;

	BalanceService(BalanceStore store) {
		super(store);
		this.store = store;
	}

	@Override
	public JSONObject get(JSONObject request) {
		return convertToJSON(get(transformJSONRequest(BalanceGetRequest.getDefaultInstance(), request)));
	}

	private Balance get(BalanceGetRequest request) {
		return store.get(request.getAccountId(), request.getYear(), request.getMonth());
	}

	@Override
	public JSONObject list(JSONObject request) {
		return list(transformJSONRequest(BalanceListRequest.getDefaultInstance(), request));
	}

	private JSONObject list(BalanceListRequest request) {
		if (request.getYear() == 0) {
			throw new IllegalArgumentException("Year is required");
		}
		if (request.getMonth() != 0) {
			return listToJSON.apply(store.getByPortfolioId(request.getPortfolioId(), request.getYear(), request.getMonth()));
		} else {
			JSONObject months = new JSONObject();
			store.getByPortfolioId(request.getPortfolioId(), request.getYear()).getItemsList().stream()
					.collect(Collectors.groupingBy(Balance::getMonth))
					.forEach((month, balances) -> {
						JSONObject json = new JSONObject();
						balances.forEach(balance -> json.put("" + balance.getAccountId(), convertToJSON(balance)));
						months.put("" + month, json);
					});
			return months;
		}
	}
}
