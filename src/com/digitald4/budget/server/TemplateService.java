package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import org.json.JSONObject;

public class TemplateService extends SingleProtoService<Template> {
	
	TemplateService(Store<Template> store) {
		super(store);
	}

	@Override
	public JSONObject list(JSONObject request) {
		return convertToJSON(list(transformJSONRequest(TemplateListRequest.getDefaultInstance(), request)));
	}

	public ListResponse list(TemplateListRequest request) {
		return list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder()
						.setColumn("PORTFOLIO_ID").setOperan("=").setValue(String.valueOf(request.getPortfolioId())))
				.build());
	}
}
