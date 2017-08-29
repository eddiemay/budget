package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Empty;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class BalanceService extends BudgetService<Balance> {
	private final BalanceStore balanceStore;
	private final Provider<SecurityManager> securityManagerProvider;
	private final PortfolioStore portfolioStore;
	private final BillStore billStore;

	public BalanceService(BalanceStore balanceStore,
								 Provider<SecurityManager> securityManagerProvider,
								 PortfolioStore portfolioStore,
								 BillStore billStore) {
		super(balanceStore, securityManagerProvider);
		this.balanceStore = balanceStore;
		this.securityManagerProvider = securityManagerProvider;
		this.portfolioStore = portfolioStore;
		this.billStore = billStore;
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
		Balance balance = balanceStore.get(request.getAccountId(), request.getYear(), request.getMonth());
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
					toListResponse(balanceStore.list(request.getPortfolioId(), request.getYear(), request.getMonth())));
		} else {
			JSONObject months = new JSONObject();
			balanceStore.list(request.getPortfolioId(), request.getYear()).getResultList()
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

	private JSONObject recalculate(JSONObject request) {
		return convertToJSON(recalculate());
	}

	public Empty recalculate() {
		portfolioStore.list(ListRequest.getDefaultInstance()).getResultList()
				.forEach(portfolio -> balanceStore.recalculateBalance(portfolio.getId(), billStore));
		return Empty.getDefaultInstance();
	}

	@Override
	public JSONObject performAction(String action, JSONObject jsonRequest) {
		if ("recalculate".equals(action)) {
			return recalculate(jsonRequest);
		}
		return super.performAction(action, jsonRequest);
	}
}
