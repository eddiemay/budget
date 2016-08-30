package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.QueryParam;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericDAOStore;

import java.util.List;

public class TemplateStore extends GenericDAOStore<Template> {
	
	public TemplateStore(DAO<Template> dao) {
		super(dao);
	}
	
	public List<Template> getByPortfolio(int portfolioId) throws DD4StorageException {
		return get(QueryParam.newBuilder()
				.setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build());
	}
}
