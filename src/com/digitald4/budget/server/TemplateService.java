package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import org.json.JSONArray;
import org.json.JSONObject;

public class TemplateService extends SingleProtoService<Template> {
	
	private final Store<Template> store;
	
	TemplateService(Store<Template> store) {
		super(store);
		this.store = store;
	}

	@Override
	public JSONArray list(JSONObject request) {
		return convertToJSON(store.get(Filter.newBuilder()
				.setColumn("PORTFOLIO_ID").setOperan("=").setValue(request.getString("portfolio_id")).build()));
	}
}
