package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.storage.*;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.storage.DAOConnectorImpl;
import com.digitald4.common.storage.DataConnectorSQLImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.ProviderThreadLocalImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {
	private final ProviderThreadLocalImpl<SecurityManager> securityManagerProvider = new ProviderThreadLocalImpl<>();
	private final PortfolioUserStore portfolioUserStore;

	public ApiServiceServlet() throws ServletException {
		portfolioUserStore = new PortfolioUserStore(
				new DAOConnectorImpl<>(PortfolioUser.class, dataConnectorProvider), securityManagerProvider);
		addService("portfolioUser", new PortfolioUserService(portfolioUserStore, securityManagerProvider));

		PortfolioStore portfolioStore =
				new PortfolioStore(new DAOConnectorImpl<>(Portfolio.class, dataConnectorProvider), portfolioUserStore);
		addService("portfolio", new PortfolioService(portfolioStore, securityManagerProvider, userProvider));

		GenericStore<Account> accountStore =
				new GenericStore<>(new DAOConnectorImpl<>(Account.class, dataConnectorProvider));
		addService("account", new AccountService(accountStore, securityManagerProvider));
		
		GenericStore<Template> templateStore =
				new GenericStore<>(new DAOConnectorImpl<>(Template.class, dataConnectorProvider));
		addService("template", new BudgetService<>(templateStore, securityManagerProvider));

		GenericStore<TemplateBill> templateBillStore =
				new GenericStore<>(new DAOConnectorImpl<>(TemplateBill.class, dataConnectorProvider));
		addService("templateBill", new TemplateBillService(templateBillStore, securityManagerProvider, templateStore));

		BalanceStore balanceStore = new BalanceStore(new DAOConnectorImpl<>(Balance.class, dataConnectorProvider));

		BillStore billStore = new BillStore(
				new DAOConnectorImpl<>(Bill.class, dataConnectorProvider), balanceStore, templateBillStore);
		addService("bill", new BillService(billStore, securityManagerProvider, templateStore, accountStore));
		addService("balance", new BalanceService(balanceStore, securityManagerProvider, portfolioStore, billStore));
	}

	@Override
	public void init() {
		super.init();
		if (serverType == ServerType.TOMCAT) {
			((DataConnectorSQLImpl) dataConnectorProvider.get())
					.setView(Balance.class, "V_Balance")
					.setView(Bill.class, "V_Bill")
					.setView(Portfolio.class, "V_Portfolio")
					.setView(TemplateBill.class, "V_Template_Bill");
		}
	}

	public boolean checkLogin(HttpServletRequest request, HttpServletResponse response, int level) throws Exception {
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
