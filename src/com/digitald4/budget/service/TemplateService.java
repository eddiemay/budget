package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI.TemplateTransactionUI;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;

import java.util.List;

public class TemplateService extends DualProtoService<TemplateUI, Template> {
	
	private final TemplateStore store;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private static Function<TemplateUI, Template> converter = new Function<TemplateUI, Template>() {
		@Override
		public TemplateUI execute(Template template) {
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
	
	private static Function<Template, TemplateUI> reverse = new Function<Template, TemplateUI>() {
		@Override
		public Template execute(TemplateUI templateUI) {
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
	public Function<TemplateUI, Template> getConverter() {
		return converter;
	}
	
	@Override
	public Function<Template, TemplateUI> getReverseConverter() {
		return reverse;
	}
	
	public List<TemplateUI> list(TemplateListRequest request) throws DD4StorageException {
		return threader.parDo(store.getByPortfolio(request.getPortfolioId()), converter);
	}
	
	public TemplateUI create(TemplateCreateRequest request) throws DD4StorageException {
		return converter.execute(store.create(reverse.execute(request.getTemplate())));
	}
}
