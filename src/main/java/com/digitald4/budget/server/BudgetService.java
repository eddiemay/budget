package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.GetRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.proto.DD4UIProtos.ListResponse;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.server.SingleProtoService;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.ProtoUtil;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class BudgetService<T extends GeneratedMessageV3> extends SingleProtoService<T> {
	private final Store<T> store;
	private final Provider<SecurityManager> securityManagerProvider;
	private final FieldDescriptor portfolioIdDescriptor;

	BudgetService(Store<T> store, Provider<SecurityManager> securityManagerProvider) {
		super(store);
		this.store = store;
		this.securityManagerProvider = securityManagerProvider;
		portfolioIdDescriptor = store.getType().getDescriptorForType().findFieldByName("portfolio_id");
	}

	@Override
	public T create(CreateRequest request) {
		securityManagerProvider.get().checkWriteAccess(
				(Long) ProtoUtil.unpack(store.getType().getClass(), request.getEntity()).getField(portfolioIdDescriptor));
		return super.create(request);
	}

	@Override
	public T get(GetRequest request) {
		T t = super.get(request);
		securityManagerProvider.get().checkReadAccess((Long) t.getField(portfolioIdDescriptor));
		return t;
	}

	public ListResponse list(BudgetListRequest request) {
		securityManagerProvider.get().checkReadAccess(request.getPortfolioId());
		return list(ListRequest.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(request.getPortfolioId())))
				.setOrderBy("name")
				.build());
	}

	@Override
	public T update(UpdateRequest request) {
		T t = store.get(request.getId());
		if (t == null) {
			throw new DD4StorageException("Not Found", HttpServletResponse.SC_NOT_FOUND);
		}
		securityManagerProvider.get().checkWriteAccess((Long) t.getField(portfolioIdDescriptor));
		return super.update(request);
	}

	@Override
	public Empty delete(DeleteRequest request) {
		T t = store.get(request.getId());
		if (t == null) {
			throw new DD4StorageException("Not Found", HttpServletResponse.SC_NOT_FOUND);
		}
		securityManagerProvider.get().checkWriteAccess((Long) t.getField(portfolioIdDescriptor));
		return super.delete(request);
	}

	@Override
	public JSONObject performAction(String action, JSONObject request) {
		if (action.equals("list")) {
			return toJSON(list(toProto(BudgetListRequest.getDefaultInstance(), request)));
		}
		return super.performAction(action, request);
	}

}
