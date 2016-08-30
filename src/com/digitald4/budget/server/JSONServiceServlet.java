package com.digitald4.budget.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountSummaryRequest;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillTransUpdateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioSQLDao;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.TemplateStore;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.GetRequest;
import com.digitald4.common.proto.DD4UIProtos.LoginRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.server.ServiceServlet;
import com.digitald4.common.storage.DAOProtoSQLImpl;

@WebServlet(name = "JSON Service Servlet", urlPatterns = {"/json/*"})
public class JSONServiceServlet extends ServiceServlet {
	public enum ACTIONS {PORTFOLIO, PORTFOLIOS, CREATE_PORTFOLIO, UPDATE_PORTFOLIO, DELETE_PORTFOLIO,
			ACCOUNT, ACCOUNTS, CREATE_ACCOUNT, UPDATE_ACCOUNT, DELETE_ACCOUNT,
			BILL, BILLS, CREATE_BILL, UPDATE_BILL, UPDATE_BILL_TRANS, DELETE_BILL,
			TEMPLATE, TEMPLATES, CREATE_TEMPLATE, UPDATE_TEMPLATE, DELETE_TEMPLATE, APPLY_TEMPLATE,
			ACCOUNT_SUMMARY};
	
	private PortfolioService portfolioService;
	private AccountService accountService;
	private BillService billService;
	private TemplateService templateService;

	public void init() throws ServletException {
		super.init();
		DBConnector dbConnector = getDBConnector();
		
		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		portfolioService = new PortfolioService(portfolioStore, userProvider);
		
		AccountStore accountStore = new AccountStore(
				new DAOProtoSQLImpl<>(Account.class, dbConnector));
		accountService = new AccountService(accountStore);
		
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<>(Template.class, dbConnector));
		templateService = new TemplateService(templateStore);
		
		BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<>(Bill.class, dbConnector), accountStore);
		billService = new BillService(billStore, templateStore);
	}

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		try {
			JSONObject json = new JSONObject();
			try {
				String action = request.getRequestURL().toString();
				action = action.substring(action.lastIndexOf("/") + 1).toUpperCase();
				if (action.equals("LOGIN")) {
					json.put("data", handleLogin(request, response,
							transformJSONRequest(LoginRequest.getDefaultInstance(), request)));
				} else {
					if (!checkLogin(request, response)) return;
					switch (ACTIONS.valueOf(action)) {
						case PORTFOLIO:
							json.put("data", convertToJSON(portfolioService.get(
									transformJSONRequest(GetRequest.getDefaultInstance(), request))));
							break;
						case PORTFOLIOS:
							json.put("data", convertToJSON(portfolioService.list(
									transformJSONRequest(PortfolioListRequest.getDefaultInstance(), request))));
							break;
						case CREATE_PORTFOLIO:
							json.put("data", convertToJSON(portfolioService.create(
									transformJSONRequest(PortfolioCreateRequest.getDefaultInstance(), request))));
							break;
						case UPDATE_PORTFOLIO:
							json.put("data", convertToJSON(portfolioService.update(
									transformJSONRequest(UpdateRequest.getDefaultInstance(), request))));
							break;
						case DELETE_PORTFOLIO:
							json.put("data", convertToJSON(portfolioService.delete(
									transformJSONRequest(DeleteRequest.getDefaultInstance(), request))));
							break;
						case ACCOUNT:
							json.put("data", convertToJSON(accountService.get(
									transformJSONRequest(AccountGetRequest.getDefaultInstance(), request))));
							break;
						case ACCOUNTS:
							json.put("data", convertToJSON(accountService.list(
									transformJSONRequest(AccountListRequest.getDefaultInstance(), request))));
							break;
						case CREATE_ACCOUNT:
							json.put("data", convertToJSON(accountService.create(
									transformJSONRequest(AccountCreateRequest.getDefaultInstance(), request))));
							break;
						case UPDATE_ACCOUNT:
							json.put("data", convertToJSON(accountService.update(
									transformJSONRequest(UpdateRequest.getDefaultInstance(), request))));
							break;
						case DELETE_ACCOUNT:
							json.put("data", convertToJSON(accountService.delete(
									transformJSONRequest(DeleteRequest.getDefaultInstance(), request))));
							break;
						case BILL:
							json.put("data", convertToJSON(billService.get(
									transformJSONRequest(GetRequest.getDefaultInstance(), request))));
							break;
						case BILLS:
							json.put("data", convertToJSON(billService.list(
									transformJSONRequest(BillListRequest.getDefaultInstance(), request))));
							break;
						case CREATE_BILL:
							json.put("data", convertToJSON(billService.create(
									transformJSONRequest(BillCreateRequest.getDefaultInstance(), request))));
							break;
						case UPDATE_BILL:
							json.put("data", convertToJSON(billService.update(
									transformJSONRequest(UpdateRequest.getDefaultInstance(), request))));
							break;
						case UPDATE_BILL_TRANS:
							json.put("data", convertToJSON(billService.updateTransaction(
									transformJSONRequest(BillTransUpdateRequest.getDefaultInstance(), request))));
							break;
						case DELETE_BILL:
							json.put("data", convertToJSON(billService.delete(
									transformJSONRequest(DeleteRequest.getDefaultInstance(), request))));
							break;
						case TEMPLATE:
							json.put("data", convertToJSON(templateService.get(
									transformJSONRequest(GetRequest.getDefaultInstance(), request))));
							break;
						case TEMPLATES:
							json.put("data", convertToJSON(templateService.list(
									transformJSONRequest(TemplateListRequest.getDefaultInstance(), request))));
							break;
						case CREATE_TEMPLATE:
							json.put("data", convertToJSON(templateService.create(
									transformJSONRequest(TemplateCreateRequest.getDefaultInstance(), request))));
							break;
						case UPDATE_TEMPLATE:
							json.put("data", convertToJSON(templateService.update(
									transformJSONRequest(UpdateRequest.getDefaultInstance(), request))));
							break;
						case DELETE_TEMPLATE:
							json.put("data", convertToJSON(templateService.delete(
									transformJSONRequest(DeleteRequest.getDefaultInstance(), request))));
							break;
						case APPLY_TEMPLATE:
							json.put("data", convertToJSON(billService.applyTemplate(
									transformJSONRequest(ApplyTemplateRequest.getDefaultInstance(), request))));
							break;
						case ACCOUNT_SUMMARY:
							json.put("data", convertToJSON(accountService.getSummary(
									transformJSONRequest(AccountSummaryRequest.getDefaultInstance(), request))));
							break;
					}
				}
				json.put("valid", true);
			} catch (Exception e) {
				json.put("valid", false)
						.put("error", e.getMessage())
						.put("stackTrace", formatStackTrace(e))
						.put("requestParams", "" + request.getParameterMap().keySet())
						.put("queryString", request.getQueryString());
				e.printStackTrace();
			} finally {
				response.setContentType("application/json");
				response.setHeader("Cache-Control", "no-cache, must-revalidate");
				response.getWriter().println(json);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
