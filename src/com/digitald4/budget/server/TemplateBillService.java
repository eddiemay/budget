package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateBillListRequest;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.GenericStore;
import org.json.JSONObject;

public class TemplateBillService extends SingleProtoService<TemplateBill> {

	TemplateBillService(GenericStore<TemplateBill> store) {
		super(store);
	}


	@Override
	public JSONObject list(JSONObject request) throws DD4StorageException {
		return convertToJSON(list(transformJSONRequest(TemplateBillListRequest.getDefaultInstance(), request)));
	}

	public ListResponse list(TemplateBillListRequest request) {
		return super.list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder()
						.setColumn("TEMPLATE_ID").setOperan("=").setValue(String.valueOf(request.getTemplateId())))
				.build());
	}
}
