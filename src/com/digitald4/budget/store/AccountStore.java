package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

public class AccountStore extends GenericDAOStore<Account> {
	
	public AccountStore(DAO<Account> dao) {
		super(dao);
	}
	
	public List<Account> getByPortfolio(int portfolioId) throws DD4StorageException {
		return get(new QueryParam("portfolio_id", "=", portfolioId));
	}
}
