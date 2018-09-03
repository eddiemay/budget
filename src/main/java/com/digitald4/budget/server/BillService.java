package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.common.util.Provider;
import org.joda.time.DateTime;
import org.json.JSONObject;

public class BillService extends BudgetService<Bill> {
	
	private final BillStore store;
	private final Store<Template> templateStore;
	private final Provider<SecurityManager> securityManagerProvider;
	
	BillService(BillStore store,
							Provider<SecurityManager> securityManagerProvider,
							Store<Template> templateStore) {
		super(store, securityManagerProvider);
		this.store = store;
		this.securityManagerProvider = securityManagerProvider;
		this.templateStore = templateStore;
	}

	public ListResponse list(BillListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return super.list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(request.getPortfolioId())))
				.addFilter(Filter.newBuilder().setColumn("year").setValue(String.valueOf(request.getYear())))
				.addFilter(Filter.newBuilder().setColumn("month").setValue(String.valueOf(request.getMonth())))
				.build());
	}
	
	ListResponse applyTemplate(ApplyTemplateRequest request) {
		Template template = templateStore.get(request.getTemplateId());
		securityManagerProvider.get().checkWriteAccess(template.getPortfolioId());
		return toListResponse(store.applyTemplate(template,
				DateTime.parse(request.getYear() + "-" + request.getMonth() + "-01")));
	}

	static class BillJSONService extends BudgetJSONService<Bill> {

		private final BillService billService;
		public BillJSONService(BillService billService) {
			super(Bill.class, billService);
			this.billService = billService;
		}

		@Override
		public JSONObject performAction(String action, JSONObject request){
			if ("applyTemplate".equals(action)) {
				return ProtoUtil.toJSON(billService.applyTemplate(ProtoUtil.toProto(ApplyTemplateRequest.getDefaultInstance(), request)));
			} else if ("list".equals(action)) {
				return ProtoUtil.toJSON(billService.list(ProtoUtil.toProto(BillListRequest.getDefaultInstance(), request)));
			}
			return super.performAction(action, request);
		}
	}
}
