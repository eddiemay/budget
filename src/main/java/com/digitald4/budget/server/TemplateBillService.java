package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateBillListRequest;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import org.json.JSONObject;

public class TemplateBillService extends BudgetService<TemplateBill> {

	private final Store<Template> templateStore;
	private final Provider<SecurityManager> securityManagerProvider;

	TemplateBillService(Store<TemplateBill> templateBillStore,
											Provider<SecurityManager> securityManagerProvider,
											Store<Template> templateStore) {
		super(templateBillStore, securityManagerProvider);
		this.securityManagerProvider = securityManagerProvider;
		this.templateStore = templateStore;
	}

	public ListResponse list(TemplateBillListRequest request) {
		Template template = templateStore.get(request.getTemplateId());
		if (template == null) {
			throw new DD4StorageException("Not Found");
		}
		securityManagerProvider.get().checkReadAccess(template.getPortfolioId());
		return super.list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("template_id").setValue(String.valueOf(request.getTemplateId())))
				.build());
	}

	@Override
	public JSONObject performAction(String action, JSONObject request) {
		if ("list".equals(action)) {
			return toJSON(list(toProto(TemplateBillListRequest.getDefaultInstance(), request)));
		}
		return super.performAction(action, request);
	}
}
