package com.digitald4.budget.servlet;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.service.TemplateService;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.server.ServiceServlet;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

@WebServlet(name = "JSON Service Servlet", urlPatterns = {"/json/*"}) 
public class JSONServiceServlet extends ServiceServlet {
	public enum ACTIONS {TEMPLATE, TEMPLATES, CREATE_TEMPLATE, DELETE_TEMPLATE};
	private TemplateService templateService;

	public void init() throws ServletException {
		super.init();
		DBConnector dbConnector = getDBConnector();
		
		TemplateStore sessionStore = new TemplateStore(
				new DAOProtoSQLImpl<>(Template.getDefaultInstance(), dbConnector));
		templateService = new TemplateService(sessionStore);
	}

	protected void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		try {
			JSONObject json = new JSONObject();
			try {
				String action = request.getRequestURL().toString();
				action = action.substring(action.lastIndexOf("/") + 1).toUpperCase();
				switch (ACTIONS.valueOf(action)) {
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
