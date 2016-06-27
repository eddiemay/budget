package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountDeleteRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.store.AccountStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;

import java.util.List;

public class AccountService {
	
	private final AccountStore store;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private static Function<AccountUI, Account> converter = new Function<AccountUI, Account>() {
		@Override
		public AccountUI execute(Account account) {
			AccountUI.Builder accountUI = AccountUI.newBuilder()
					.setId(account.getId())
					.setPortfolioId(account.getPortfolioId())
					.setName(account.getName())
					.setDescription(account.getDescription())
					.setPaymentAccount(account.getPaymentAccount())
					.setParentAccountId(account.getParentAccountId());
			return accountUI.build();
		}
	};
	
	private static Function<Account, AccountUI> reverse = new Function<Account, AccountUI>() {
		@Override
		public Account execute(AccountUI account) {
			return Account.newBuilder()
					.setId(account.getId())
					.setPortfolioId(account.getPortfolioId())
					.setName(account.getName())
					.setDescription(account.getDescription())
					.setPaymentAccount(account.getPaymentAccount())
					.setParentAccountId(account.getParentAccountId())
					.build();
		}
	};
	
	public AccountService(AccountStore store) {
		this.store = store;
	}
	
	public List<AccountUI> list(AccountListRequest request) throws DD4StorageException {
		return threader.parDo(store.getByPortfolio(request.getPortfolioId()), converter);
	}
	
	public AccountUI get(AccountGetRequest request) throws DD4StorageException {
		return converter.execute(store.read(request.getAccountId()));
	}
	
	public AccountUI create(AccountCreateRequest request) throws DD4StorageException {
		return converter.execute(store.create(reverse.execute(request.getAccount())));
	}
	
	public boolean delete(AccountDeleteRequest request) throws DD4StorageException {
		store.delete(request.getAccountId());
		return true;
	}
}
