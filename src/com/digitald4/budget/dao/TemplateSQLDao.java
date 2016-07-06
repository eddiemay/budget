package com.digitald4.budget.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.jdbc.DBConnector;

public class TemplateSQLDao extends DAOProtoSQLImpl<Template> {

	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	public TemplateSQLDao(DBConnector dbConnector) {
		super(Template.getDefaultInstance(), dbConnector);
	}
	
	@Override
	public String getTable() {
		return "V_TEMPLATE";
	}
	
	@Override
	public List<Template> process(ResultSet rs) throws SQLException {
		List<Template.Builder> results = new ArrayList<>();
		final Map<Integer, List<TemplateBill.Builder>> billsByTemplate = new HashMap<>();
		Map<Integer, TemplateBill.Builder> templateBills = new HashMap<>();
		while (rs.next()) {
			int templateBillId = rs.getInt("template_bill_id");
			TemplateBill.Builder templateBill = templateBills.get(templateBillId);
			if (templateBill == null) {
				int templateId = rs.getInt("id");
				List<TemplateBill.Builder> bills = billsByTemplate.get(templateId);
				if (bills == null) {
					bills = new ArrayList<>();
					billsByTemplate.put(templateId, bills);
					results.add(Template.newBuilder()
							.setId(templateId)
							.setPortfolioId(rs.getInt("portfolio_id"))
							.setName(rs.getString("name")));
				}
				templateBill = TemplateBill.newBuilder()
						.setId(templateBillId)
						.setTemplateId(templateId)
						.setAccountId(rs.getInt("account_id"))
						.setDueDay(rs.getInt("due_day"))
						.setAmountDue(rs.getDouble("amount_due"))
						.setActive(rs.getBoolean("active"))
						.setRank(rs.getInt("rank") != 0 ? rs.getInt("rank") : templateBillId);
				if (rs.getString("name_d") != null) {
					templateBill.setName(rs.getString("name_d"));
				}
				bills.add(templateBill);
				templateBills.put(templateBillId, templateBill);
			}
			templateBill.addTransaction(TemplateTransaction.newBuilder()
					.setDebitAccountId(rs.getInt("debit_account_id"))
					.setAmount(rs.getDouble("amount")));
		}
		System.out.println("Found " + results.size() + " templates");
		return threader.parDo(results, new Function<Template, Template.Builder>() {
			@Override
			public Template execute(Template.Builder builder) {
				for (TemplateBill.Builder bill : billsByTemplate.get(builder.getId())) {
					builder.addBill(bill);
				}
				return builder.build();
			}
		});
	}
}
