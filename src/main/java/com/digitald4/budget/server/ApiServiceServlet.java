package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.ProviderThreadLocalImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {
	private final ProviderThreadLocalImpl<SecurityManager> securityManagerProvider = new ProviderThreadLocalImpl<>();
	private final PortfolioUserStore portfolioUserStore;

	public ApiServiceServlet() throws ServletException {
		portfolioUserStore = new PortfolioUserStore(dataAccessObjectProvider, securityManagerProvider);
		addService("portfolioUser", new PortfolioUserService(portfolioUserStore, securityManagerProvider));

		PortfolioStore portfolioStore = new PortfolioStore(dataAccessObjectProvider, portfolioUserStore);
		addService("portfolio", new PortfolioService(portfolioStore, securityManagerProvider, userProvider));

		GenericStore<Account> accountStore = new GenericStore<>(Account.class, dataAccessObjectProvider);
		addService("account", new BudgetService<>(accountStore, securityManagerProvider));
		
		GenericStore<Template> templateStore = new GenericStore<>(Template.class, dataAccessObjectProvider);
		addService("template", new BudgetService<>(templateStore, securityManagerProvider));

		GenericStore<TemplateBill> templateBillStore = new GenericStore<>(TemplateBill.class, dataAccessObjectProvider);
		addService("templateBill", new TemplateBillService(templateBillStore, securityManagerProvider, templateStore));

		BalanceStore balanceStore = new BalanceStore(dataAccessObjectProvider);

		BillStore billStore = new BillStore(dataAccessObjectProvider, balanceStore, templateBillStore);
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
