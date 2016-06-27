package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

public class TemplateStore extends GenericDAOStore<Template> {
	
	public TemplateStore(DAO<Template> dao) {
		super(dao);
	}
	
	public List<Template> getByPortfolio(int portfolioId) throws DD4StorageException {
		return query(new QueryParam("portfolio_id", "=", portfolioId));
	}
}
