package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.server.BillService.BillJSONService;
import com.digitald4.budget.server.BudgetService.BudgetJSONService;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.server.service.JSONServiceImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.util.ProviderThreadLocalImpl;
import java.time.Clock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiServiceServlet extends com.digitald4.common.server.ApiServiceServlet {
	private final ProviderThreadLocalImpl<SecurityManager> securityManagerProvider = new ProviderThreadLocalImpl<>();
	private final PortfolioUserStore portfolioUserStore;
	private final Clock clock;

	public ApiServiceServlet() {
		portfolioUserStore = new PortfolioUserStore(daoProvider, securityManagerProvider);
		addService("portfolioUser", new BudgetJSONService<>(
				new PortfolioUserService(portfolioUserStore, securityManagerProvider)));

		PortfolioStore portfolioStore = new PortfolioStore(daoProvider, portfolioUserStore);
		addService("portfolio", new JSONServiceImpl<>(
				new PortfolioService(portfolioStore, securityManagerProvider, userProvider), true));

		GenericStore<Account> accountStore = new GenericStore<>(Account.class, daoProvider);
		addService("account", new BudgetJSONService<>(new BudgetService<>(accountStore, securityManagerProvider)));
		
		GenericStore<Template> templateStore = new GenericStore<>(Template.class, daoProvider);
		addService("template", new BudgetJSONService<>(new BudgetService<>(templateStore, securityManagerProvider)));

		GenericStore<TemplateBill> templateBillStore = new GenericStore<>(TemplateBill.class, daoProvider);
		addService("templateBill", new TemplateBillService.TemplateJSONService(
				new TemplateBillService(templateBillStore, securityManagerProvider, templateStore)));

		BalanceStore balanceStore = new BalanceStore(daoProvider);

		BillStore billStore = new BillStore(daoProvider, balanceStore, templateBillStore);
		addService("bill", new BillJSONService(new BillService(billStore, securityManagerProvider, templateStore)));
		addService("balance", new BalanceService(balanceStore, securityManagerProvider, portfolioStore, billStore));
	}

	public void checkLogin(HttpServletRequest request, HttpServletResponse response, int level) {
		super.checkLogin(request, response, level);
		securityManagerProvider.set(new SecurityManager(userProvider.get(), portfolioUserStore));
	}
}
