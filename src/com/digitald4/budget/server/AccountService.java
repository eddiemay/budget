package com.digitald4.budget.server;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Account.Balance;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountGetRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountListRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountSummaryRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI.AccountSummary;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI.BalanceUI;
import com.digitald4.budget.storage.AccountStore;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.server.DualProtoService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

public class AccountService extends DualProtoService<AccountUI, Account> {
	
	private final AccountStore store;

	private class Converter implements Function<Account, AccountUI> {
		
		private final DateTime refDate;
		public Converter(DateTime refDate) {
			this.refDate = refDate;
		}
		
		public Converter(long refDate) {
			this(new DateTime(refDate));
		}
		
		@Override
		public AccountUI apply(Account account) {
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
	
	private static Function<AccountUI, Account> reverse = new Function<AccountUI, Account>() {
		@Override
		public Account apply(AccountUI account) {
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
		super(AccountUI.class, store);
		this.store = store;
	}
	
	public Function<Account, AccountUI> getConverter() {
		return new Converter(0);
	}
	
	public Function<Account, AccountUI> getConverter(long refDate) {
		return new Converter(refDate);
	}
	
	@Override
	public Function<AccountUI, Account> getReverseConverter() {
		return reverse;
	}
	
	public List<AccountUI> list(AccountListRequest request) throws DD4StorageException {
		return store.getByPortfolio(request.getPortfolioId()).stream()
				.map(new Converter(request.getRefDate()))
				.sorted(AccountComparator)
				.collect(Collectors.toList());
	}
	
	public AccountUI get(AccountGetRequest request) throws DD4StorageException {
		return getConverter(request.getRefDate()).apply(store.get(request.getAccountId()));
	}
	
	public AccountUI create(AccountCreateRequest request) throws DD4StorageException {
		return getConverter(0).apply(store.create(reverse.apply(request.getAccount())));
	}
	
	public List<AccountUI> getSummary(AccountSummaryRequest request)
			throws DD4StorageException {
		return store.getByPortfolio(request.getPortfolioId()).stream().map(
				new AccountSummaryConverter(request.getYear())).collect(Collectors.toList());
	}

	private static final Comparator<AccountUI> AccountComparator = new Comparator<AccountUI>() {
		public int compare(AccountUI account, AccountUI account2) {
			int ret = account.getName().compareTo(account2.getName());
			if (ret == 0) {
				if (account.getId() < account2.getId()) {
					ret = -1;
				} else if (account.getId() > account2.getId()) {
					ret = 1;
				}
			}
			return ret;
		}
	};
	
	private class AccountSummaryConverter implements Function<Account, AccountUI> {
		private final int year;

		public AccountSummaryConverter(int year) {
			this.year = year;
		}
		
		@Override
		public AccountUI apply(Account account) {
			String thisYear = year + "-01";
			String nextYear = (year + 1) + "-01";
			AccountUI.Builder builder = new Converter(0).apply(account).toBuilder();
			for (int x = 1; x <= 12; x++) {
				builder.addSummary(AccountSummary.newBuilder()
						.setMonth(year + (x < 10 ? "-0" : "-") + x));
			}
			int start = 0;
			double total = 0;
			while (start < account.getBalanceCount()
					&& account.getBalance(start).getDate().compareTo(nextYear) > 0) {
				start++;
			}
			for (int index = start; index < account.getBalanceCount(); index++) {
				Balance post = account.getBalance(index);
				if (thisYear.compareTo(post.getDate()) > -1) {
					break;
				}
				Balance pre = (index + 1 < account.getBalanceCount()) ? account.getBalance(index + 1)
						: Balance.getDefaultInstance();
				int month = (Integer.valueOf(post.getDate().substring(5, 7)) - 2) % 12;
				total += post.getBalance() - pre.getBalance();
				builder.setSummary(month, builder.getSummary(month).toBuilder()
						.setTotal(post.getBalance() - pre.getBalance())
						.build());
			}
			return builder
					.addSummary(AccountSummary.newBuilder()
							.setMonth(String.valueOf(year))
							.setTotal(total))
					.build();
		}
	}
}
