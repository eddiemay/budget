package com.digitald4.budget.job;

import com.digitald4.budget.server.BalanceService;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.DAOSQLImpl;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;
import javax.inject.Provider;

public class BalanceRecalculator {

	public static void main(String[] args) throws Exception {
		Provider<DAO> daoProvider = () -> new DAOSQLImpl(new DBConnectorThreadPoolImpl(
				"org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true",
				"dd4_user", "getSchooled85"));
		BalanceStore balanceStore = new BalanceStore(daoProvider);
		new BalanceService(balanceStore, null,
				new PortfolioStore(daoProvider, null),
				new BillStore(daoProvider, balanceStore, null))
				.recalculate();
	}
}
