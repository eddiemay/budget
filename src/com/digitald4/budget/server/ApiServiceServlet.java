package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.storage.*;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.storage.DAOProtoSQLImpl;

import com.digitald4.common.storage.GenericStore;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "API Service Servlet", urlPatterns = {"/api/*"})
public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {

	public ApiServiceServlet() throws ServletException {
		DBConnector dbConnector = getDBConnector();

		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		addService("portfolio", new PortfolioService(portfolioStore, userProvider));

		GenericStore<Account> accountStore = new GenericStore<>(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		addService("account", new AccountService(accountStore));
		
		GenericStore<Template> templateStore = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		addService("template", new TemplateService(templateStore));

		GenericStore<TemplateBill> templateBillStore = new GenericStore<>(
				new DAOProtoSQLImpl<>(TemplateBill.class, dbConnector, "V_TemplateBill"));
		addService("templateBill", new TemplateBillService(templateBillStore));

		BalanceStore balanceStore = new BalanceStore(new DAOProtoSQLImpl<>(Balance.class, dbConnector, "V_Balance"));
		addService("balance", new BalanceService(balanceStore));
		
		BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector, "V_BILL"), balanceStore, templateBillStore);
		addService("bill", new BillService(billStore, templateStore));
	}
}
