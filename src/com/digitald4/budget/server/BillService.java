package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.ListResponse;
import com.digitald4.common.storage.Store;
import org.joda.time.DateTime;
import org.json.JSONObject;

public class BillService extends SingleProtoService<Bill> {
	
	private final BillStore store;
	private final Store<Template> templateStore;
	
	BillService(BillStore store, Store<Template> templateStore) {
		super(store);
		this.store = store;
		this.templateStore = templateStore;
	}

	public JSONObject list(JSONObject request) {
		return listToJSON.apply(list(transformJSONRequest(BillListRequest.getDefaultInstance(), request)));
	}
	
	public ListResponse<Bill> list(BillListRequest request) {
		return super.list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("PORTFOLIO_ID").setOperan("=").setValue(String.valueOf(request.getPortfolioId())))
				.addFilter(Filter.newBuilder().setColumn("YEAR").setOperan("=").setValue(String.valueOf(request.getYear())))
				.addFilter(Filter.newBuilder().setColumn("MONTH").setOperan("=").setValue(String.valueOf(request.getMonth())))
				.build());
	}

	private JSONObject applyTemplate(JSONObject request) {
		return listToJSON.apply(applyTemplate(transformJSONRequest(ApplyTemplateRequest.getDefaultInstance(), request)));
	}
	
	ListResponse<Bill> applyTemplate(ApplyTemplateRequest request) {
		return store.applyTemplate(templateStore.get(request.getTemplateId()),
				DateTime.parse(request.getYear() + "-" + request.getMonth() + "-01"));
	}

	@Override
	public JSONObject performAction(String action, JSONObject jsonRequest) {
		switch (action) {
			case "applyTemplate": return applyTemplate(jsonRequest);
			default: return super.performAction(action, jsonRequest);
		}
	}
}
