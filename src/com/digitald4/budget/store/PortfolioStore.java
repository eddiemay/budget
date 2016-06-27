package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

public class PortfolioStore extends GenericDAOStore<Portfolio> {
	
	public PortfolioStore(DAO<Portfolio> dao) {
		super(dao);
	}
	
	public List<Portfolio> getByUser(int userId) throws DD4StorageException {
		return query(new QueryParam("user_id", "=", userId));
	}
}
