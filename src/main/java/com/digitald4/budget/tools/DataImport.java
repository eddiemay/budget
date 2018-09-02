package com.digitald4.budget.tools;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;
import com.digitald4.common.proto.DD4Protos.GeneralData;
import com.digitald4.common.server.APIConnector;
import com.digitald4.common.storage.DAOSQLImpl;
import com.digitald4.common.tools.DataImporter;

public class DataImport {
	public static void main(String[] args) throws Exception {
		DataImporter importer = new DataImporter(
				new APIConnector("https://ledger-178106.appspot.com/api").login(),
				new DAOSQLImpl(new DBConnectorThreadPoolImpl("org.gjt.mm.mysql.Driver",
						"jdbc:mysql://localhost/budget?autoReconnect=true",
						"dd4_user", "getSchooled85")));
		// importer.runFor(GeneralData.class);
		// importer.runFor(Portfolio.class);
		// importer.runFor(Account.class);
		// importer.runFor(Template.class);
		// importer.runFor(TemplateBill.class);
		// importer.runFor(Bill.class);
	}
}
