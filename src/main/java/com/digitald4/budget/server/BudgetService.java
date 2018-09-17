package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.server.JSONServiceImpl;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class BudgetService<T extends GeneratedMessageV3> extends SingleProtoService<T> {
	private final Provider<SecurityManager> securityManagerProvider;
	private final FieldDescriptor portfolioIdDescriptor;

	BudgetService(Store<T> store, Provider<SecurityManager> securityManagerProvider) {
		super(store);
		this.securityManagerProvider = securityManagerProvider;
		portfolioIdDescriptor = store.getType().getDescriptorForType().findFieldByName("portfolio_id");
	}

	@Override
	public T create(T t) {
		securityManagerProvider.get().checkWriteAccess((Long) t.getField(portfolioIdDescriptor));
		return super.create(t);
	}

	@Override
	public T get(long id) {
		T t = super.get(id);
		if (t == null) {
			throw new DD4StorageException("Not Found", HttpServletResponse.SC_NOT_FOUND);
		}
		securityManagerProvider.get().checkReadAccess((Long) t.getField(portfolioIdDescriptor));
		return t;
	}

	public QueryResult<T> list(BudgetListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(request.getPortfolioId())))
				.setOrderBy("name")
				.build());
	}

	@Override
	public T update(UpdateRequest request) {
		T t = get(request.getId());
		securityManagerProvider.get().checkWriteAccess((Long) t.getField(portfolioIdDescriptor));
		return super.update(request);
	}

	@Override
	public Empty delete(long id) {
		T t = get(id);
		securityManagerProvider.get().checkWriteAccess((Long) t.getField(portfolioIdDescriptor));
		return super.delete(id);
	}

	static class BudgetJSONService<T extends GeneratedMessageV3> extends JSONServiceImpl<T> {
		private final BudgetService budgetService;
		BudgetJSONService(Class<T> cls, BudgetService<T> budgetService) {
			super(cls, budgetService, true);
			this.budgetService = budgetService;
		}

		@Override
		public JSONObject performAction(String action, JSONObject request){
			if (action.equals("list")) {
				return ProtoUtil.toJSON(budgetService.list(ProtoUtil.toProto(BudgetListRequest.getDefaultInstance(), request)));
			}
			return super.performAction(action, request);
		}
	}
}
