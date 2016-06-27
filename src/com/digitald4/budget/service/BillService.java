package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.store.BillStore;

import org.joda.time.DateTime;
import org.json.JSONArray;

public class BillService {
	
	private BillStore store;
	
	public BillService(BillStore store) {
		this.store = store;
	}
	
	public JSONArray applyTemplate(ApplyTemplateRequest request) {
		store.applyTemplate(request.getPortfolioId(), request.getTemplateId(),
				new DateTime(request.getRefDate()));
		return null;
	}
}
