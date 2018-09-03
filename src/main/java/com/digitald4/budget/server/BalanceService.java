package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BalanceListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.server.JSONService;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class BalanceService implements JSONService {
	private final BalanceStore balanceStore;
	private final Provider<SecurityManager> securityManagerProvider;
	private final PortfolioStore portfolioStore;
	private final BillStore billStore;

	public BalanceService(BalanceStore balanceStore,
								 Provider<SecurityManager> securityManagerProvider,
								 PortfolioStore portfolioStore,
								 BillStore billStore) {
		this.balanceStore = balanceStore;
		this.securityManagerProvider = securityManagerProvider;
		this.portfolioStore = portfolioStore;
		this.billStore = billStore;
	}

	private Balance get(BalanceGetRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return balanceStore.get(request.getPortfolioId(), request.getAccountId(), request.getYear(), request.getMonth());
	}

	private JSONObject list(BalanceListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		if (request.getYear() == 0) {
			throw new IllegalArgumentException("Year is required");
		}
		if (request.getMonth() != 0) {
			return ProtoUtil.toJSON(
					toListResponse(balanceStore.list(request.getPortfolioId(), request.getYear(), request.getMonth())));
		} else {
			JSONObject months = new JSONObject();
			balanceStore.list(request.getPortfolioId(), request.getYear())
					.stream()
					.collect(Collectors.groupingBy(Balance::getMonth))
					.forEach((month, balances) -> {
						JSONObject json = new JSONObject();
						balances.forEach(balance -> json.put("" + balance.getAccountId(), ProtoUtil.toJSON(balance)));
						months.put("" + month, json);
					});
			return months;
		}
	}

	private ListResponse toListResponse(QueryResult<Balance> queryResult) {
		return ListResponse.newBuilder()
				.addAllResult(queryResult.stream()
						.map(Any::pack)
						.collect(Collectors.toList()))
				.setTotalSize(queryResult.getTotalSize())
				.build();
	}

	public Empty recalculate() {
		portfolioStore.list(Query.getDefaultInstance())
				.forEach(portfolio -> balanceStore.recalculateBalance(portfolio.getId(), billStore));
		return Empty.getDefaultInstance();
	}

	public boolean requiresLogin(String action) {
		return true;
	}

	@Override
	public JSONObject performAction(String action, JSONObject request) {
		switch (action) {
			case "get": return ProtoUtil.toJSON(get(ProtoUtil.toProto(BalanceGetRequest.getDefaultInstance(), request)));
			case "list": return list(ProtoUtil.toProto(BalanceListRequest.getDefaultInstance(), request));
			case "recalculate": return ProtoUtil.toJSON(recalculate());
			default: throw new DD4StorageException("Invalid action: " + action, HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
