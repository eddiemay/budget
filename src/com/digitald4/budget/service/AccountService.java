package com.digitald4.budget.service;

import static com.digitald4.common.util.Calculate.parseInt;
import static com.digitald4.common.util.Calculate.parseDouble;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digitald4.budget.model.Account;
import com.digitald4.budget.model.Bill;
import com.digitald4.budget.model.GenData;
import com.digitald4.budget.model.Portfolio;
import com.digitald4.budget.model.Template;
import com.digitald4.budget.model.TemplateBill;
import com.digitald4.budget.model.TemplateTransaction;
import com.digitald4.budget.model.Transaction;
import com.digitald4.budget.model.UserPortfolio;
import com.digitald4.common.model.User;
import com.digitald4.common.util.Calculate;
import com.digitald4.common.util.DisplayWindow;
import com.digitald4.common.util.FormatText;
import com.digitald4.common.util.Pair;

public class AccountService {
	
	private final EntityManager entityManager;
	
	public AccountService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public JSONObject getPortfolios(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		Portfolio activePortfolio = getActivePortfolio(request);
		for (UserPortfolio userPortfolio : user.getCollection(UserPortfolio.class, new String[]{"USER_ID"}, user.getId())) {
			json.put(userPortfolio.getPortfolio().toJSON());
			if (activePortfolio == null) {
				activePortfolio = userPortfolio.getPortfolio();
			}
		}
		return new JSONObject().put("user", user.toJSON())
				.put("activePortfolio", activePortfolio.toJSON())
				.put("portfolios", json);
	}
	
	public JSONObject addPortfolio(HttpServletRequest request) throws JSONException, Exception {
		HttpSession session = request.getSession();
		Portfolio portfolio = (Portfolio) new Portfolio(entityManager).setName(request.getParameter("name")).save();
		portfolio.addUserPortfolio(new UserPortfolio(entityManager).setUser((User)session.getAttribute("user")).setRole(GenData.UserPortfolioRole_OWNER.get(entityManager)));
		session.setAttribute("portfolio", portfolio);
		return getPortfolios(request);
	}
	
	public JSONObject updatePortfolio(HttpServletRequest request) throws JSONException, Exception {
		return entityManager.find(Portfolio.class, parseInt(request.getParameter("id")))
				.setPropertyValue(request.getParameter("property"), request.getParameter("value"))
				.save().toJSON();
	}
	
	public JSONArray getAccounts(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		for (Account account : getActivePortfolio(request).getAccounts()) {
			json.put(account.toJSON());
		}
		return json;
	}
	
	public JSONArray getPaymentAccounts(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		for (Account account : getActivePortfolio(request).getAccounts()) {
			if (account.isPaymentAccount()) {
				json.put(account.toJSON());
			}
		}
		return json;
	}
	
	public JSONArray addAccount(HttpServletRequest request) throws JSONException, Exception {
		String name = request.getParameter("name");
		int parentAccountId = parseInt(request.getParameter("parentAccountId"));
		Portfolio portfolio = getActivePortfolio(request);
		portfolio.addAccount(new Account(entityManager).setName(name)
				.setParentAccountId(parentAccountId));
		return getAccounts(request);
	}
	
	public Portfolio getActivePortfolio(HttpServletRequest request) throws Exception {
		return entityManager.find(Portfolio.class, parseInt(request.getParameter("portfolioId")));
	}

	public JSONObject updateAccount(HttpServletRequest request) throws JSONException, Exception {
		return entityManager.find(Account.class, parseInt(request.getParameter("id")))
				.setPropertyValue(request.getParameter("property"), request.getParameter("value"))
				.save().toJSON();
	}
	
	public JSONArray getTransactions(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		String sdStr = request.getParameter("startDate");
		String edStr = request.getParameter("endDate");
		Date startDate = sdStr != null ? DateTime.parse(sdStr).toDate() : null;
		Date endDate = edStr != null ? DateTime.parse(edStr).toDate() : null;
		for (Transaction transaction : getActivePortfolio(request).getTransactions(startDate, endDate)) {
			json.put(transaction.toJSON());
		}
		return json;
	}

	public JSONArray addTransaction(HttpServletRequest request) throws JSONException, Exception {
		String date = request.getParameter("date");
		String name = request.getParameter("name");
		String billId = request.getParameter("billId");
		int debitAccountId = parseInt(request.getParameter("debitAccountId"));
		int creditAccountId = parseInt(request.getParameter("creditAccountId"));
		double amount = Double.parseDouble(request.getParameter("amount"));
		Transaction trans = new Transaction(entityManager).setNameD(name).setDateD(FormatText.parseDate(date))
				.setDebitAccountId(debitAccountId).setCreditAccountId(creditAccountId).setAmount(amount);
		if (billId != null) {
			trans.setBillId(parseInt(billId));
		}
		trans.save();
		return getTransactions(request);
	}

	public JSONArray updateTransaction(HttpServletRequest request) throws JSONException, Exception {
		entityManager.find(Transaction.class, parseInt(request.getParameter("id")))
				.setPropertyValue(request.getParameter("property"), request.getParameter("value")).save();
		return getTransactions(request);
	}
	
	private Pair<DateTime, DateTime> getDateRange(HttpServletRequest request, DateTime refDate) {
		String sdStr = request.getParameter("startDate");
		String edStr = request.getParameter("endDate");
		DateTime startDate = sdStr != null ? DateTime.parse(sdStr) : null;
		DateTime endDate = edStr != null ? DateTime.parse(edStr) : null;
		if (startDate == null && endDate == null && refDate != null) {
			DisplayWindow displayWin = DisplayWindow.values()[parseInt(request.getParameter("displayWindow"))];
			return Calculate.getDateRange(displayWin, refDate);
		}
		return Pair.of(startDate, endDate);
	}
	
	private JSONArray getBills(HttpServletRequest request, DateTime refDate) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		Pair<DateTime, DateTime> dateRange = getDateRange(request, refDate);
		Date startDate = dateRange.getLeft().toDate();
		Date endDate = dateRange.getRight().toDate();
		for (Bill bill : getActivePortfolio(request).getBills(startDate, endDate)) {
			JSONObject jo = bill.toJSON();
			if (bill.isNewInstance()) {
				jo.put("transId", bill.getTransactions().get(0).getId());
			}
			json.put(jo);
		}
		return json;
	}
	
	public JSONArray getBills(HttpServletRequest request) throws JSONException, Exception {
		String refDate = request.getParameter("refDate");
		return getBills(request, refDate == null ? null : DateTime.parse(refDate));
	}

	public JSONArray addBill(HttpServletRequest request) throws JSONException, Exception {
		Bill bill = new Bill(entityManager).setDueDate(FormatText.parseDate(request.getParameter("dueDate")))
				.setAmountDue(parseDouble(request.getParameter("amountDue")))
				.setAccountId(parseInt(request.getParameter("accountId")))
				.setNameD(request.getParameter("name"));
		for (int a = 0; a < 10; a++) {
			double amount = parseDouble(request.getParameter("accounts[" + a + "][amount]"));
			if (amount != 0) {
				bill.addTransaction(new Transaction(entityManager).setAmount(amount)
						.setDebitAccountId(parseInt(request.getParameter("accounts[" + a + "][id]"))));
			}
		}
		bill.save();
		return getBills(request, new DateTime(bill.getDueDate()));
	}

	public JSONArray updateBill(HttpServletRequest request) throws JSONException, Exception {
		Bill bill = entityManager.find(Bill.class, parseInt(request.getParameter("id")));
		if (bill == null) {
			Transaction transaction = entityManager.find(Transaction.class,
					parseInt(request.getParameter("transId")));
			bill = new Bill(entityManager).addTransaction(transaction)
					.setAccount(transaction.getCreditAccount())
					.setAmountDue(transaction.getAmount())
					.setDueDate(transaction.getDate());
		}
		bill.setPropertyValue(request.getParameter("property"), request.getParameter("value")).save();
		return getBills(request, new DateTime(bill.getDueDate()));
	}

	public JSONArray updateBillTrans(HttpServletRequest request) throws JSONException, Exception {
		String id = request.getParameter("id");
		Transaction trans = id != null ? entityManager.find(Transaction.class, parseInt(id)) : 
				new Transaction(entityManager).setBillId(parseInt(request.getParameter("billId")))
						.setDebitAccountId(parseInt(request.getParameter("accountId")));
		trans.setPropertyValue(request.getParameter("property"), request.getParameter("value")).save();
		return getBills(request, new DateTime(trans.getBill().getDueDate()));
	}
	
	public JSONArray applyTemplate(HttpServletRequest request) throws JSONException, Exception {
		Template template = entityManager.find(Template.class,
				parseInt(request.getParameter("templateId")));
		DateTime refDate = DateTime.parse(request.getParameter("refDate"));
		for (TemplateBill templateBill : template.getTemplateBills()) {
			templateBill.getAccount().addBill(new Bill(templateBill, refDate));
		}
		return getBills(request);
	}
	
	public JSONArray getSummaryData(HttpServletRequest request) throws JSONException, Exception {
		int year = parseInt(request.getParameter("year"));
		JSONArray json = new JSONArray();
		Portfolio portfolio = getActivePortfolio(request);
		for (Account cat : portfolio.getAccounts()) {
			if (cat.getParent() != null) {
				continue;
			}
			JSONArray accts = new JSONArray();
			double[] cmt = new double[13];
			for (Account account : cat.getAccounts()) {
				double[] mt = account.getMonthTotals(year);
				JSONArray monthTotals = new JSONArray();
				for (int x = 0; x < cmt.length; x++) {
					cmt[x] += mt[x];
					monthTotals.put(new JSONObject().put("month", x + 1).put("total", mt[x]));
				}
				accts.put(account.toJSON()
						.put("monthTotals", monthTotals));
			}
			if (accts.length() > 0) {
				JSONArray monthTotals = new JSONArray();
				for (int x = 0; x < cmt.length; x++) {
					monthTotals.put(new JSONObject().put("month", x + 1).put("total", cmt[x]));
				}
				json.put(cat.toJSON()
						.put("monthTotals", monthTotals)
						.put("accounts", accts));
			}
		}
		return json;
	}
	
	public JSONArray getTemplates(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		for (Template template : getActivePortfolio(request).getTemplates()) {
			json.put(template.toJSON());
		}
		return json;
	}
	
	public JSONArray addTemplate(HttpServletRequest request) throws JSONException, Exception {
		Portfolio portfolio = getActivePortfolio(request);
		portfolio.addTemplate(new Template(entityManager).setName(request.getParameter("name")));
		return getTemplates(request);
	}

	public JSONObject updateTemplate(HttpServletRequest request) throws JSONException, Exception {
		return entityManager.find(Template.class, parseInt(request.getParameter("id")))
				.setPropertyValue(request.getParameter("property"), request.getParameter("value"))
				.save().toJSON();
	}
	
	public JSONArray getTemplateBills(HttpServletRequest request) throws JSONException, Exception {
		JSONArray json = new JSONArray();
		Template template = entityManager.find(Template.class, parseInt(request.getParameter("templateId")));
		for (TemplateBill templateBill : template.getTemplateBills()) {
			json.put(templateBill.toJSON());
		}
		return json;
	}
	
	public JSONArray addTemplateBill(HttpServletRequest request) throws JSONException, Exception {
		entityManager.find(Template.class, parseInt(request.getParameter("templateId")))
				.addTemplateBill(new TemplateBill(entityManager)
						.setNameD(request.getParameter("nameD"))
						.setAccountId(parseInt(request.getParameter("accountId")))
						.setDueDay(parseInt(request.getParameter("dueDay")))
						.setAmountDue(parseDouble(request.getParameter("amountDue"))));
		return getTemplateBills(request);
	}

	public JSONArray updateTemplateBill(HttpServletRequest request) throws JSONException, Exception {
		entityManager.find(TemplateBill.class, parseInt(request.getParameter("id")))
				.setPropertyValue(request.getParameter("property"), request.getParameter("value")).save();
		return getTemplateBills(request);
	}

	public JSONArray updateTemplateBillTrans(HttpServletRequest request)
			throws JSONException, Exception {
		String id = request.getParameter("id");
		TemplateTransaction trans = id != null ? entityManager.find(TemplateTransaction.class, parseInt(id)) : 
				new TemplateTransaction(entityManager).setTemplateBillId(parseInt(request.getParameter("templateBillId")))
						.setDebitAccountId(parseInt(request.getParameter("accountId")));
		trans.setPropertyValue(request.getParameter("property"), request.getParameter("value")).save();
		return getTemplateBills(request);
	}
}
