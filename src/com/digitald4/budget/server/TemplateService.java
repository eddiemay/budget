package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI.TemplateTransactionUI;
import com.digitald4.budget.storage.TemplateStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;
import com.digitald4.common.server.JSONService;
import com.googlecode.protobuf.format.JsonFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TemplateService extends DualProtoService<TemplateUI, Template> {
	
	private final TemplateStore store;

	private static Function<Template, TemplateUI> converter = new Function<Template, TemplateUI>() {
		@Override
		public TemplateUI apply(Template template) {
			TemplateUI.Builder templateUI = TemplateUI.newBuilder()
					.setId(template.getId())
					.setPortfolioId(template.getPortfolioId())
					.setName(template.getName());
			for (TemplateBill bill : template.getBillList()) {
				TemplateBillUI.Builder billUI = TemplateBillUI.newBuilder()
						.setAccountId(bill.getAccountId())
						.setDueDay(bill.getDueDay())
						.setName(bill.getName())
						.setAmountDue(bill.getAmountDue());
				for (TemplateTransaction transaction : bill.getTransactionList()) {
					billUI.addTransaction(TemplateTransactionUI.newBuilder()
							.setDebitAccountId(transaction.getDebitAccountId())
							.setAmount(transaction.getAmount()));
				}
				templateUI.addBill(billUI);
			}
			return templateUI.build();
		}
	};
	
	private static Function<TemplateUI, Template> reverse = new Function<TemplateUI, Template>() {
		@Override
		public Template apply(TemplateUI templateUI) {
			Template.Builder template = Template.newBuilder()
					.setId(templateUI.getId())
					.setPortfolioId(templateUI.getPortfolioId())
					.setName(templateUI.getName());
			for (TemplateBillUI billUI : templateUI.getBillList()) {
				TemplateBill.Builder bill = TemplateBill.newBuilder()
						.setAccountId(billUI.getAccountId())
						.setDueDay(billUI.getDueDay())
						.setName(billUI.getName())
						.setAmountDue(billUI.getAmountDue());
				for (TemplateTransactionUI transactionUI : billUI.getTransactionList()) {
					bill.addTransaction(TemplateTransaction.newBuilder()
							.setDebitAccountId(transactionUI.getDebitAccountId())
							.setAmount(transactionUI.getAmount()));
				}
				template.addBill(bill);
			}
			return template.build();
		}
	};
	
	public TemplateService(TemplateStore store) {
		super(TemplateUI.class, store);
		this.store = store;
	}
	
	@Override
	public Function<Template, TemplateUI> getConverter() {
		return converter;
	}
	
	@Override
	public Function<TemplateUI, Template> getReverseConverter() {
		return reverse;
	}
	
	public List<TemplateUI> list(TemplateListRequest request) throws DD4StorageException {
		return store.getByPortfolio(request.getPortfolioId()).stream().map(converter).collect(Collectors.toList());
	}

	@Override
	public Object performAction(String action, JSONObject jsonRequest)
			throws DD4StorageException, JSONException, JsonFormat.ParseException {
		switch (action) {
			case "list":
				return JSONService.convertToJSON(list(
						JSONService.transformJSONRequest(TemplateListRequest.getDefaultInstance(), jsonRequest)));
			default: return super.performAction(action, jsonRequest);
		}
	}
}
