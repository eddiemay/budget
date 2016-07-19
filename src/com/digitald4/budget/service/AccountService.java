package com.digitald4.budget.service;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Account.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI.BalanceUI;
import com.digitald4.budget.store.AccountStore;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.distributed.MultiCoreThreader;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;

public class AccountService extends DualProtoService<AccountUI, Account> {
	
	private final AccountStore store;
	private final MultiCoreThreader threader = new MultiCoreThreader();
	
	private class Converter implements Function<AccountUI, Account> {
		
		private final DateTime refDate;
		public Converter(DateTime refDate) {
			this.refDate = refDate;
		}
		
		public Converter(long refDate) {
			this(new DateTime(refDate));
		}
		
		@Override
		public AccountUI execute(Account account) {
			AccountUI.Builder accountUI = AccountUI.newBuilder()
					.setId(account.getId())
					.setPortfolioId(account.getPortfolioId())
					.setName(account.getName())
					.setPaymentAccount(account.getPaymentAccount())
					.setParentAccountId(account.getParentAccountId());
			String refDateStr = refDate.toString("yyyy-MM");
			for (Balance balance : account.getBalanceList()) {
				// Balance should be put in by date in reverse order so that the newest date is first.
				if (balance.getDate().compareTo(refDateStr) < 1) {
					accountUI.setBalance(BalanceUI.newBuilder()
							.setDate(balance.getDate())
							.setBalance(balance.getBalance())
							.setBalanceYearToDate(balance.getBalanceYearToDate()));
					break;
				}
			}
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
					.setPaymentAccount(account.getPaymentAccount())
					.setParentAccountId(account.getParentAccountId())
					.build();
		}
	};
	
	public AccountService(AccountStore store) {
		super(store);
		this.store = store;
	}
	
	public Function<AccountUI, Account> getConverter() {
		return new Converter(0);
	}
	
	public Function<AccountUI, Account> getConverter(long refDate) {
		return new Converter(refDate);
	}
	
	@Override
	public Function<Account, AccountUI> getReverseConverter() {
		return reverse;
	}
	
	public List<AccountUI> list(AccountListRequest request) throws DD4StorageException {
		TreeSet<DefaultComparator> sorted = new TreeSet<>(threader.parDo(store.getByPortfolio(
				request.getPortfolioId()), new SortWrapper(new Converter(request.getRefDate()))));
		List<AccountUI> result = new ArrayList<>();
		for (DefaultComparator dc : sorted) {
			result.add(dc.account);
		}
		return result;
	}
	
	public AccountUI get(AccountGetRequest request) throws DD4StorageException {
		return getConverter(request.getRefDate()).execute(store.get(request.getAccountId()));
	}
	
	public AccountUI create(AccountCreateRequest request) throws DD4StorageException {
		return getConverter(0).execute(store.create(reverse.execute(request.getAccount())));
	}
	
	private static class DefaultComparator implements Comparable<DefaultComparator> {
		private final AccountUI account;
		public DefaultComparator(AccountUI account) {
			this.account = account;
		}
		
		@Override
		public int compareTo(DefaultComparator dc) {
			int ret = account.getName().compareTo(dc.account.getName());
			if (ret == 0) {
				if (account.getId() < dc.account.getId()) {
					ret = -1;
				} else if (account.getId() > dc.account.getId()) {
					ret = 1;
				}
			}
			return ret;
		}
	}
	
	private class SortWrapper implements Function<DefaultComparator, Account> {
		
		private final Converter converter;
		public SortWrapper(Converter converter) {
			this.converter = converter;
		}
		
		@Override
		public DefaultComparator execute(Account account) {
			return new DefaultComparator(converter.execute(account));
		}
	}
}
