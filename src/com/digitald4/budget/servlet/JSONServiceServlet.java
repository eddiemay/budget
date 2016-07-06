package com.digitald4.budget.servlet;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digitald4.budget.dao.PortfolioSQLDao;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.service.AccountService;
import com.digitald4.budget.service.PortfolioService;
import com.digitald4.budget.service.TemplateService;
import com.digitald4.budget.store.AccountStore;
import com.digitald4.budget.store.PortfolioStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.server.ServiceServlet;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

@WebServlet(name = "JSON Service Servlet", urlPatterns = {"/json/*"}) 
public class JSONServiceServlet extends ServiceServlet {
	public enum ACTIONS {PORTFOLIO, PORTFOLIOS, CREATE_PORTFOLIO, DELETE_PORTFOLIO,
			ACCOUNT, ACCOUNTS, CREATE_ACCOUNT, DELETE_ACCOUNT,
			TEMPLATE, TEMPLATES, CREATE_TEMPLATE, DELETE_TEMPLATE};
	
	private PortfolioService portfolioService;
	private AccountService accountService;
	private TemplateService templateService;

	public void init() throws ServletException {
		super.init();
		DBConnector dbConnector = getDBConnector();
		
		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		portfolioService = new PortfolioService(portfolioStore, userProvider);
		
		AccountStore accountStore = new AccountStore(
				new DAOProtoSQLImpl<Account>(Account.getDefaultInstance(), dbConnector));
		accountService = new AccountService(accountStore);
		
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		templateService = new TemplateService(templateStore);
	}

	protected void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		try {
			JSONObject json = new JSONObject();
			try {
				if (!checkLoginAutoRedirect(request, response)) return;
				String action = request.getRequestURL().toString();
				action = action.substring(action.lastIndexOf("/") + 1).toUpperCase();
				switch (ACTIONS.valueOf(action)) {
					case PORTFOLIO: json.put("data", convertToJSON(portfolioService.get(
							transformJSONRequest(PortfolioGetRequest.getDefaultInstance(), request))));
					break;
					case PORTFOLIOS: json.put("data", convertToJSON(portfolioService.list(
							transformJSONRequest(PortfolioListRequest.getDefaultInstance(), request))));
					break;
					case CREATE_PORTFOLIO: json.put("data", convertToJSON(portfolioService.create(
							transformJSONRequest(PortfolioCreateRequest.getDefaultInstance(), request))));
					break;
					case DELETE_PORTFOLIO: json.put("data", convertToJSON(portfolioService.delete(
							transformJSONRequest(PortfolioDeleteRequest.getDefaultInstance(), request))));
					break;
					case ACCOUNT: json.put("data", convertToJSON(accountService.get(
							transformJSONRequest(AccountGetRequest.getDefaultInstance(), request))));
					break;
					case ACCOUNTS: json.put("data", convertToJSON(accountService.list(
							transformJSONRequest(AccountListRequest.getDefaultInstance(), request))));
					break;
					case CREATE_ACCOUNT: json.put("data", convertToJSON(accountService.create(
							transformJSONRequest(AccountCreateRequest.getDefaultInstance(), request))));
					break;
					case DELETE_ACCOUNT: json.put("data", convertToJSON(accountService.delete(
							transformJSONRequest(AccountDeleteRequest.getDefaultInstance(), request))));
					break;
					case TEMPLATE: json.put("data", convertToJSON(templateService.get(
							transformJSONRequest(TemplateGetRequest.getDefaultInstance(), request))));
					break;
					case TEMPLATES: json.put("data", convertToJSON(templateService.list(
							transformJSONRequest(TemplateListRequest.getDefaultInstance(), request))));
					break;
					case CREATE_TEMPLATE: json.put("data", convertToJSON(templateService.create(
							transformJSONRequest(TemplateCreateRequest.getDefaultInstance(), request))));
					break;
					case DELETE_TEMPLATE: json.put("data", convertToJSON(templateService.delete(
							transformJSONRequest(TemplateDeleteRequest.getDefaultInstance(), request))));
					break;
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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		process(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		process(request, response);
	}
	
	public JSONArray convertToJSON(List<? extends Message> items) throws JSONException {
		JSONArray array = new JSONArray();
		for (Message item : items) {
			array.put(convertToJSON(item));
		}
		return array;
	}
	
	public JSONObject convertToJSON(Message item) throws JSONException {
		return new JSONObject(JsonFormat.printToString(item));
	}
	
	public JSONObject convertToJSON(boolean bool) throws JSONException {
		return new JSONObject(bool);
	}
}
