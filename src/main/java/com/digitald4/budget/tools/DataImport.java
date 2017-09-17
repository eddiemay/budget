package com.digitald4.budget.tools;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;
import com.digitald4.common.storage.DataConnectorSQLImpl;
import com.digitald4.common.tools.DataImporter;

public class DataImport {
	public static void main(String[] args) throws Exception {
		DataImporter importer = new DataImporter(
				new DataConnectorSQLImpl(new DBConnectorThreadPoolImpl("org.gjt.mm.mysql.Driver",
						"jdbc:mysql://localhost/budget?autoReconnect=true",
						"dd4_user", "getSchooled85")),
				"https://ledger-178106.appspot.com/api");
		importer.login();
		importer.runFor(Portfolio.class);
		importer.runFor(Account.class);
	}
}
