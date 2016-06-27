package com.digitald4.budget.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.digitald4.budget.service.BudgetService;
import com.digitald4.common.servlet.ParentServlet;


/**
 * The Budget Service Servlet.
 * 
 * @author Eddie Mayfield (eddiemay@gmail.com)
 */
@WebServlet(name = "Service Servlet", urlPatterns = {"/bs"})
public class JPAServiceServlet extends ParentServlet {
	public enum ACTIONS {getBills, addBill, updateBill, updateBillTrans,
			getTransactions, addTransaction, updateTransaction, getSummaryData};
	private BudgetService budgetService;
	
	public void init() throws ServletException {
		super.init();
		budgetService = new BudgetService(getEntityManager());
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			JSONObject json = new JSONObject();
			try {
				if (!checkLoginAutoRedirect(request, response)) return;
				String action = request.getParameter("action");
				switch (ACTIONS.valueOf(action)) {
					case getTransactions: json.put("data", budgetService.getTransactions(request)); break;
					case addTransaction: json.put("data", budgetService.addTransaction(request)); break;
					case updateTransaction: json.put("data", budgetService.updateTransaction(request)); break;
					case getBills: json.put("data", budgetService.getBills(request)); break;
					case addBill: json.put("data", budgetService.addBill(request)); break;
					case updateBill: json.put("data", budgetService.updateBill(request)); break;
					case updateBillTrans: json.put("data", budgetService.updateBillTrans(request)); break;
					case getSummaryData: json.put("data", budgetService.getSummaryData(request)); break;
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
