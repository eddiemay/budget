package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.storage.*;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.storage.DAOProtoSQLImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "API Service Servlet", urlPatterns = {"/api/*"})
public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {

	public ApiServiceServlet() throws ServletException {
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
