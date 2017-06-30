package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.GenericStore;
import org.json.JSONArray;
import org.json.JSONObject;

public class TemplateBillService extends SingleProtoService<TemplateBill> {

	private final GenericStore<TemplateBill> store;

	TemplateBillService(GenericStore<TemplateBill> store) {
		super(store);
		this.store = store;
	}

	@Override
	public JSONArray list(JSONObject request) {
		return convertToJSON(store.get(Filter.newBuilder()
				.setColumn("TEMPLATE_ID").setOperan("=").setValue(request.getString("template_id")).build()));
	}
}
