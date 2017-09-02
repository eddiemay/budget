package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateBillListRequest;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import org.json.JSONObject;

public class TemplateBillService extends BudgetService<TemplateBill> {

	private final Store<Template> templateStore;
	private final Provider<SecurityManager> securityManagerProvider;

	TemplateBillService(Store<TemplateBill> store,
											Provider<SecurityManager> securityManagerProvider,
											Store<Template> templateStore) {
		super(store, securityManagerProvider);
		this.securityManagerProvider = securityManagerProvider;
		this.templateStore = templateStore;
	}

	@Override
	public TemplateBill create(CreateRequest request) {
		Template template = templateStore.get(request.getProto().unpack(TemplateBill.class).getTemplateId());
		if (template == null) {
			throw new DD4StorageException("Not Found");
		}
		securityManagerProvider.get().checkWriteAccess(template.getPortfolioId());
		return super.create(request);
	}

	@Override
	public JSONObject list(JSONObject request) {
		return convertToJSON(list(transformJSONRequest(TemplateBillListRequest.getDefaultInstance(), request)));
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
}
