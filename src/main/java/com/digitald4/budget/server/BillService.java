package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillListRequest;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import javax.inject.Provider;
import org.joda.time.DateTime;
import org.json.JSONObject;

public class BillService extends BudgetService<Bill> {
	private static final String BILL_FILTER = "portfolio_id = %d, year = %d, month = %d";
	
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

	public QueryResult<Bill> list(BillListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return super.list(ListRequest.newBuilder()
				.setFilter(String.format(BILL_FILTER, request.getPortfolioId(), request.getYear(), request.getMonth()))
				.build());
	}
	
	QueryResult<Bill> applyTemplate(ApplyTemplateRequest request) {
		Template template = templateStore.get(request.getTemplateId());
		securityManagerProvider.get().checkWriteAccess(template.getPortfolioId());
		return toListResponse(store.applyTemplate(template,
				DateTime.parse(request.getYear() + "-" + request.getMonth() + "-01")));
	}

	static class BillJSONService extends BudgetJSONService<Bill> {

		private final BillService billService;
		public BillJSONService(BillService billService) {
			super(billService);
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
