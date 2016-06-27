package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.Template.TemplateBill.TemplateTransaction;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI.TemplateTransactionUI;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;

import java.util.List;

public class TemplateService {
	
	private final TemplateStore store;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private static Function<TemplateUI, Template> converter = new Function<TemplateUI, Template>() {
		@Override
		public TemplateUI execute(Template template) {
			TemplateUI.Builder templateUI = TemplateUI.newBuilder()
					.setId(template.getId())
					.setPortfolioId(template.getPortfolioId())
					.setName(template.getName())
					.setDescription(template.getDescription());
			for (TemplateBill bill : template.getBillList()) {
				TemplateBillUI.Builder billUI = TemplateBillUI.newBuilder()
						.setId(bill.getId())
						.setTemplateId(bill.getTemplateId())
						.setAccountId(bill.getAccountId())
						.setDueDay(bill.getDueDay())
						.setNameD(bill.getNameD())
						.setAmountDue(bill.getAmountDue())
						.setRank(bill.getRank())
						.setActive(bill.getActive())
						.setDescription(bill.getDescription());
				for (TemplateTransaction transaction : bill.getTransactionList()) {
					billUI.addTransaction(TemplateTransactionUI.newBuilder()
							.setId(transaction.getId())
							.setTemplateBillId(transaction.getTemplateBillId())
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
		public Template execute(TemplateUI template) {
			return Template.newBuilder()
					.setId(template.getId())
					.setPortfolioId(template.getPortfolioId())
					.setName(template.getName())
					.setDescription(template.getDescription())
					.build();
		}
	};
	
	public TemplateService(TemplateStore store) {
		this.store = store;
	}
	
	public List<TemplateUI> list(TemplateListRequest request) throws DD4StorageException {
		return threader.parDo(store.getByPortfolio(request.getPortfolioId()), converter);
	}
	
	public TemplateUI get(TemplateGetRequest request) throws DD4StorageException {
		return converter.execute(store.read(request.getTemplateId()));
	}
	
	public TemplateUI create(TemplateCreateRequest request) throws DD4StorageException {
		return converter.execute(store.create(reverse.execute(request.getTemplate())));
	}
	
	public boolean delete(TemplateDeleteRequest request) throws DD4StorageException {
		store.delete(request.getTemplateId());
		return true;
	}
}
