package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import com.googlecode.protobuf.format.JsonFormat;
import java.util.List;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class BillService extends SingleProtoService<Bill> {
	
	private final BillStore store;
	private final Store<Template> templateStore;
	
	BillService(BillStore store, Store<Template> templateStore) {
		super(store);
		this.store = store;
		this.templateStore = templateStore;
	}
	
	public List<Bill> list(BillListRequest request) throws DD4StorageException {
		return store.get(
				Filter.newBuilder().setColumn("PORTFOLIO_ID").setOperan("=").setValue(String.valueOf(request.getPortfolioId())).build(),
				Filter.newBuilder().setColumn("YEAR").setOperan("=").setValue(String.valueOf(request.getYear())).build(),
				Filter.newBuilder().setColumn("MONTH").setOperan("=").setValue(String.valueOf(request.getMonth())).build());
	}
	
	List<Bill> applyTemplate(ApplyTemplateRequest request) throws DD4StorageException {
		return store.applyTemplate(templateStore.get(request.getTemplateId()),
				DateTime.parse(request.getYear() + "-" + request.getMonth() + "-01"));
	}

	@Override
	public Object performAction(String action, JSONObject jsonRequest)
			throws DD4StorageException, JSONException, JsonFormat.ParseException {
		switch (action) {
			case "list":
				return convertToJSON(list(transformJSONRequest(BillListRequest.getDefaultInstance(), jsonRequest)));
			case "applyTemplate":
				return convertToJSON(applyTemplate(
						transformJSONRequest(ApplyTemplateRequest.getDefaultInstance(), jsonRequest)));
			default: return super.performAction(action, jsonRequest);
		}
	}
}
