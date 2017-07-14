package com.digitald4.budget.job;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;

public class BalanceRecalculator {

	public static void main(String[] args) throws Exception {
		DBConnector dbConnector = new DBConnectorThreadPoolImpl(
				"org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true",
				"dd4_user", "getSchooled85");
		final BalanceStore balanceStore = new BalanceStore(
				new DAOProtoSQLImpl<>(Balance.class, dbConnector, "V_Balance"));
		final BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector), balanceStore, null);
		PortfolioStore portfolioStore = new PortfolioStore(new DAOProtoSQLImpl<>(Portfolio.class, dbConnector), null);
		portfolioStore.list(ListRequest.getDefaultInstance()).getItemsList()
				.forEach(portfolio -> balanceStore.recalculateBalance(portfolio.getId(), billStore));
	}
}
