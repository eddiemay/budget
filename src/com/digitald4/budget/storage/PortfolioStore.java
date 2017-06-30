package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;

import java.util.List;

public class PortfolioStore extends GenericStore<Portfolio> {
	
	public PortfolioStore(DAO<Portfolio> dao) {
		super(dao);
	}
	
	public List<Portfolio> getByUser(int userId) throws DD4StorageException {
		return get(Filter.newBuilder()
				.setColumn("user_id").setOperan("=").setValue(String.valueOf(userId)).build());
	}
}
