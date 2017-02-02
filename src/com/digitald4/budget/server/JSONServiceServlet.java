package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioSQLDao;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.TemplateStore;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.server.ServiceServlet;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "JSON Service Servlet", urlPatterns = {"/json/*"})
public class JSONServiceServlet extends ServiceServlet {

	public void init() throws ServletException {
		super.init();
		DBConnector dbConnector = getDBConnector();

		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		addService("portfolio", new PortfolioService(portfolioStore, userProvider));

		AccountStore accountStore = new AccountStore(
				new DAOProtoSQLImpl<>(Account.class, dbConnector));
		addService("account", new AccountService(accountStore));
		
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<>(Template.class, dbConnector));
		addService("template", new TemplateService(templateStore));
		
		BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector), accountStore);
		addService("bill", new BillService(billStore, templateStore));
	}
}
