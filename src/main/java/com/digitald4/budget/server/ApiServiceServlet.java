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
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.ProviderThreadLocalImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {
	private final ProviderThreadLocalImpl<SecurityManager> securityManagerProvider = new ProviderThreadLocalImpl<>();
	private final PortfolioUserStore portfolioUserStore;

	public ApiServiceServlet() throws ServletException {
		super(false);
		portfolioUserStore = new PortfolioUserStore(
				new DAOConnectorImpl<>(PortfolioUser.class, dataConnectorProvider), securityManagerProvider);
		addService("portfolioUser", new PortfolioUserService(portfolioUserStore, securityManagerProvider));

		PortfolioStore portfolioStore =
				new PortfolioStore(new DAOConnectorImpl<>(Portfolio.class, dataConnectorProvider), portfolioUserStore);
		addService("portfolio", new PortfolioService(portfolioStore, securityManagerProvider, userProvider));

		GenericStore<Account> accountStore =
				new GenericStore<>(new DAOConnectorImpl<>(Account.class, dataConnectorProvider));
		addService("account", new BudgetService<>(accountStore, securityManagerProvider));
		
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

	public boolean checkLogin(HttpServletRequest request, HttpServletResponse response, int level) throws Exception {
		if (super.checkLogin(request, response, level)) {
			securityManagerProvider.set(new SecurityManager(userProvider.get(), portfolioUserStore));
			return true;
		}
		return false;
	}
}
