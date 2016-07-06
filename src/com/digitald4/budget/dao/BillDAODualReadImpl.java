package com.digitald4.budget.dao;

import java.util.List;

import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.common.dao.DAO;
import com.digitald4.common.dao.QueryParam;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.jdbc.DBConnector;

public class BillDAODualReadImpl  extends DAOProtoSQLImpl<Bill> {
	private final DAO<Transaction> transactionDAO;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private final Function<Bill, Bill> converter = new Function<Bill, Bill>() {
		@Override
		public Bill execute(Bill bill) {
			try {
				return bill.toBuilder()
						.addAllTransaction(transactionDAO.get(new QueryParam("bill_id", "=", bill.getId())))
						.build();
			} catch (DD4StorageException e) {
				e.printStackTrace();
			}
			return bill;
		}
	};
	
	public BillDAODualReadImpl(DBConnector connector, DAO<Transaction> transactionDAO) {
		super(Bill.getDefaultInstance(), connector);
		this.transactionDAO = transactionDAO;
	}
	
	@Override
	public Bill get(int id) throws DD4StorageException {
		return converter.execute(super.get(id));
	}
	
	@Override
	public List<Bill> get(QueryParam... params) throws DD4StorageException {
		return threader.parDo(super.get(params), converter);
	}
	
	@Override
	public List<Bill> get(List<QueryParam> params) throws DD4StorageException {
		return threader.parDo(super.get(params), converter);
	}
	
	@Override
	public List<Bill> getAll() throws DD4StorageException {
		return threader.parDo(super.getAll(), converter);
	}
}
