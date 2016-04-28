package com.digitald4.budget.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.digitald4.budget.service.AccountService;
import com.digitald4.common.servlet.ParentServlet;


/**
 * The Budget Service Servlet.
 * 
 * @author Eddie Mayfield (eddiemay@gmail.com)
 */
@WebServlet(name = "Service Servlet", urlPatterns = {"/bs"})
public class ServiceServlet extends ParentServlet {
	public enum ACTIONS {getPortfolios, addPortfolio, updatePortfolio, getSummaryData,
			getAccounts, addAccount, updateAccount, getPaymentAccounts,
			getTransactions, addTransaction, updateTransaction, getBills, addBill, updateBill, updateBillTrans,
			getTemplates, addTemplate, updateTemplate, applyTemplate,
			getTemplateBills, addTemplateBill, updateTemplateBill, updateTemplateBillTrans};
	private AccountService accountService;
	
	public void init() throws ServletException {
		super.init();
		accountService = new AccountService(getEntityManager());
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			JSONObject json = new JSONObject();
			try {
				if (!checkLoginAutoRedirect(request, response)) return;
				String action = request.getParameter("action");
				switch (ACTIONS.valueOf(action)) {
					case getPortfolios: json.put("data", accountService.getPortfolios(request)); break;
					case addPortfolio: json.put("data", accountService.addPortfolio(request)); break;
					case updatePortfolio: json.put("data", accountService.updatePortfolio(request)); break;
					case getAccounts: json.put("data", accountService.getAccounts(request)); break;
					case addAccount: json.put("data", accountService.addAccount(request)); break;
					case updateAccount: json.put("data", accountService.updateAccount(request)); break;
					case getPaymentAccounts: json.put("data", accountService.getPaymentAccounts(request)); break;
					case getTransactions: json.put("data", accountService.getTransactions(request)); break;
					case addTransaction: json.put("data", accountService.addTransaction(request)); break;
					case updateTransaction: json.put("data", accountService.updateTransaction(request)); break;
					case getBills: json.put("data", accountService.getBills(request)); break;
					case addBill: json.put("data", accountService.addBill(request)); break;
					case updateBill: json.put("data", accountService.updateBill(request)); break;
					case updateBillTrans: json.put("data", accountService.updateBillTrans(request)); break;
					case getSummaryData: json.put("data", accountService.getSummaryData(request)); break;
					case getTemplates: json.put("data", accountService.getTemplates(request)); break;
					case addTemplate: json.put("data", accountService.addTemplate(request)); break;
					case updateTemplate: json.put("data", accountService.updateTemplate(request)); break;
					case getTemplateBills: json.put("data", accountService.getTemplateBills(request)); break;
					case addTemplateBill: json.put("data", accountService.addTemplateBill(request)); break;
					case updateTemplateBill: json.put("data", accountService.updateTemplateBill(request)); break;
					case updateTemplateBillTrans: json.put("data", accountService.updateTemplateBillTrans(request)); break;
					case applyTemplate: json.put("data", accountService.applyTemplate(request)); break;
				}
				json.put("valid", true);
			} catch (Exception e) {
				json.put("valid", false).put("error", e.getMessage()).put("stackTrace", formatStackTrace(e));
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request,response);
	}
}
