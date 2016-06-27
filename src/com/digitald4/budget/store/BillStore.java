package com.digitald4.budget.store;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.store.impl.GenericDAOStore;

import java.util.List;

import org.joda.time.DateTime;

public class BillStore extends GenericDAOStore<Bill> {
	
	public BillStore(DAO<Bill> dao) {
		super(dao);
	}
	
	public List<Bill> getByDateRange(int portfolioId, DateTime start, DateTime end)
			throws DD4StorageException {
		return query(new QueryParam("due_date", ">=", start.getMillis()),
				new QueryParam("due_date", "<", end.getMillis()));
	}
	
	public List<Bill> applyTemplate(int portfolioId, int templateId, DateTime refDate) {
		// TODO(eddiemay) Write this.
		return null;
	}
}
