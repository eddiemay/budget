package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import org.joda.time.DateTime;
import org.json.JSONObject;

public class BillService extends BudgetService<Bill> {
	
	private final BillStore store;
	private final Store<Account> accountStore;
	private final Store<Template> templateStore;
	private final Provider<SecurityManager> securityManagerProvider;
	
	BillService(BillStore store,
							Provider<SecurityManager> securityManagerProvider,
							Store<Template> templateStore,
							Store<Account> accountStore) {
		super(store, securityManagerProvider);
		this.store = store;
		this.securityManagerProvider = securityManagerProvider;
		this.templateStore = templateStore;
		this.accountStore = accountStore;
	}

	@Override
	public JSONObject list(JSONObject request) {
		return convertToJSON(list(transformJSONRequest(BillListRequest.getDefaultInstance(), request)));
	}
	
	public ListResponse list(BillListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return super.list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(request.getPortfolioId())))
				.addFilter(Filter.newBuilder().setColumn("year").setValue(String.valueOf(request.getYear())))
				.addFilter(Filter.newBuilder().setColumn("month").setValue(String.valueOf(request.getMonth())))
				.build());
	}

	private JSONObject applyTemplate(JSONObject request) {
		return convertToJSON(applyTemplate(transformJSONRequest(ApplyTemplateRequest.getDefaultInstance(), request)));
	}
	
	ListResponse applyTemplate(ApplyTemplateRequest request) {
		Template template = templateStore.get(request.getTemplateId());
		securityManagerProvider.get().checkWriteAccess(template.getPortfolioId());
		return toListResponse(store.applyTemplate(template,
				DateTime.parse(request.getYear() + "-" + request.getMonth() + "-01")));
	}

	@Override
	public JSONObject performAction(String action, JSONObject jsonRequest) {
		if ("applyTemplate".equals(action)) {
			return applyTemplate(jsonRequest);
		}
		return super.performAction(action, jsonRequest);
	}
}
