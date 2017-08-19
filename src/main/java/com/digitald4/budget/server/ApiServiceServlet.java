package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.storage.*;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.proto.DD4Protos.User.UserType;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.ProviderThreadLocalImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "API Service Servlet", urlPatterns = {"/api/*"})
public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {
	private final ProviderThreadLocalImpl<SecurityManager> securityManagerProvider = new ProviderThreadLocalImpl<>();
	private final PortfolioUserStore portfolioUserStore;

	public ApiServiceServlet() throws ServletException {
		DBConnector dbConnector = getDBConnector();


		portfolioUserStore = new PortfolioUserStore(new DAOProtoSQLImpl<>(PortfolioUser.class, dbConnector), securityManagerProvider);
		addService("portfolioUser", new PortfolioUserService(portfolioUserStore, securityManagerProvider));

		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector), portfolioUserStore);
		addService("portfolio", new PortfolioService(portfolioStore, securityManagerProvider, userProvider));

		GenericStore<Account> accountStore = new GenericStore<>(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		addService("account", new AccountService(accountStore, securityManagerProvider));
		
		GenericStore<Template> templateStore = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		addService("template", new BudgetService<>(templateStore, securityManagerProvider));

		GenericStore<TemplateBill> templateBillStore = new GenericStore<>(
				new DAOProtoSQLImpl<>(TemplateBill.class, dbConnector, "V_TemplateBill"));
		addService("templateBill", new TemplateBillService(templateBillStore, securityManagerProvider, templateStore));

		BalanceStore balanceStore = new BalanceStore(new DAOProtoSQLImpl<>(Balance.class, dbConnector, "V_Balance"));
		addService("balance", new BalanceService(balanceStore, securityManagerProvider));
		
		BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector, "V_BILL"), balanceStore, templateBillStore);
		addService("bill", new BillService(billStore, securityManagerProvider, templateStore, accountStore));
	}

	public boolean checkLogin(HttpServletRequest request, HttpServletResponse response, UserType level) throws Exception {
		if (super.checkLogin(request, response, level)) {
			HttpSession session = request.getSession(true);
			SecurityManager securityManager = (SecurityManager) session.getAttribute("securityManager");
			if (securityManager == null) {
				securityManager = new SecurityManager(userProvider.get(), portfolioUserStore);
				session.setAttribute("securityManager", securityManager);
			}
			securityManagerProvider.set(securityManager);
			return true;
		}
		return false;
	}
}
