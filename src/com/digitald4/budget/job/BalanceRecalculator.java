package com.digitald4.budget.job;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;

public class BalanceRecalculator {

	public static void main(String[] args) throws Exception {
		DBConnector dbConnector = new DBConnectorThreadPoolImpl(
				"org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true",
				"dd4_user", "getSchooled85");
		final AccountStore accountStore = new AccountStore(
				new DAOProtoSQLImpl<>(Account.class, dbConnector));
		final BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector), accountStore);
		PortfolioStore portfolioStore = new PortfolioStore(
				new DAOProtoSQLImpl<>(Portfolio.class, dbConnector));
		MultiCoreThreader threader = new MultiCoreThreader();
		threader.parDo(portfolioStore.getAll(), new Function<Boolean, Portfolio>() {
			@Override
			public Boolean execute(Portfolio portfolio) {
				try {
					return accountStore.recalculateBalance(portfolio.getId(), billStore);
				} catch (DD4StorageException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
	}
}
