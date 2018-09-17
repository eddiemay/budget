package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.Query;
import com.digitald4.common.proto.DD4Protos.Query.Filter;
import com.digitald4.common.proto.DD4Protos.Query.OrderBy;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.QueryResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.inject.Provider;

public class BalanceStore extends GenericStore<Balance> {

	public BalanceStore(Provider<DAO> daoProvider) {
		super(Balance.class, daoProvider);
	}

	public QueryResult<Balance> list(long portfolioId, int year) {
		return list(Query.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("portfolio_id").setOperator("=").setValue(String.valueOf(portfolioId)))
				.addFilter(Filter.newBuilder().setColumn("year").setOperator("=").setValue(String.valueOf(year)))
				.build());
	}

	@Override
	public QueryResult<Balance> list(Query query) throws DD4StorageException {
		return super.list(query);
	}

	public QueryResult<Balance> list(long portfolioId, int year, int month) {
		List<Balance> results = new ArrayList<>();
		list(
				Query.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("portfolio_id").setOperator("=").setValue(String.valueOf(portfolioId)))
						.addFilter(Filter.newBuilder().setColumn("year").setOperator("=").setValue(String.valueOf(year)))
						.addFilter(Filter.newBuilder().setColumn("month").setOperator("<=").setValue(String.valueOf(month)))
						.build())
				.getResults()
				.stream()
				.collect(Collectors.groupingBy(Balance::getAccountId))
				.forEach((accountId, balances) -> results.add(balances
						.stream()
						.sorted(Comparator.comparing(Balance::getYear).thenComparing(Balance::getMonth).reversed())
						.collect(Collectors.toList())
						.get(0)));
		return new QueryResult<>(results, results.size());
	}

	public Balance get(long portfolioId, long accountId, int year, int month) {
		List<Balance> balances = list(Query.newBuilder()
				.addFilter(Filter.newBuilder().setColumn("account_id").setOperator("=").setValue(String.valueOf(accountId)))
				.addFilter(Filter.newBuilder().setColumn("year").setOperator("=").setValue(String.valueOf(year)))
				.addFilter(Filter.newBuilder().setColumn("month").setOperator("<=").setValue(String.valueOf(month)))
				.addOrderBy(OrderBy.newBuilder().setColumn("month").setDesc(true))
				.setLimit(1)
				.build()).getResults();
		if (balances.isEmpty()) {
			balances = list(Query.newBuilder()
					.addFilter(Filter.newBuilder().setColumn("account_id").setOperator("=").setValue(String.valueOf(accountId)))
					.addFilter(Filter.newBuilder().setColumn("year").setOperator("<").setValue(String.valueOf(year)))
					.addOrderBy(OrderBy.newBuilder().setColumn("year").setDesc(true))
					.addOrderBy(OrderBy.newBuilder().setColumn("month").setDesc(true))
					.setLimit(1)
					.build()).getResults();
		}
		if (balances.isEmpty()) {
			return Balance.newBuilder()
					.setPortfolioId(portfolioId)
					.setAccountId(accountId)
					.setYear(year)
					.setMonth(month)
					.build();
		} else if (balances.get(0).getMonth() < month || balances.get(0).getYear() < year) {
			return Balance.newBuilder()
					.setPortfolioId(portfolioId)
					.setAccountId(accountId)
					.setYear(year)
					.setMonth(month)
					.setBalance(balances.get(0).getBalance())
					.setBalanceYTD(balances.get(0).getYear() == year ? balances.get(0).getBalanceYTD() : 0)
					.build();
		}
		return balances.get(0);
	}

	void applyUpdate(long portfolioId, long accountId, int year, int month, double delta) {
		int nextMonth = month < 12 ? month + 1 : 1;
		int nextYear = month < 12 ? year : year + 1;
		AtomicBoolean nextMonthFound = new AtomicBoolean(false);
		list(
				Query.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("account_id").setOperator("=").setValue(String.valueOf(accountId)))
						.addFilter(Filter.newBuilder().setColumn("year").setOperator(">=").setValue(String.valueOf(year)))
						.build())
				.getResults()
				.stream()
				.filter(balance -> balance.getYear() > year || balance.getMonth() >= nextMonth)
				.forEach(balance -> {
					if (balance.getYear() == nextYear && balance.getMonth() == nextMonth) {
						nextMonthFound.set(true);
					}
					update(balance.getId(), current -> current.toBuilder()
							.setBalance(current.getBalance() + delta)
							.setBalanceYTD(current.getBalanceYTD() + (current.getYear() == nextYear ? delta : 0))
							.build());
				});

		if (!nextMonthFound.get()) {
			Balance balance = get(portfolioId, accountId, nextYear, nextMonth);
			create(balance.toBuilder()
					.setBalance(balance.getBalance() + delta)
					.setBalanceYTD(balance.getBalanceYTD() + (balance.getYear() == year ? delta : 0))
					.build());
		}
	}

	public void recalculateBalance(long portfolioId, BillStore billStore) {
		Map<Long, List<Balance>> accountHash = list(
				Query.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(portfolioId))).build())
				.getResults()
				.stream()
				.map(balance -> balance.toBuilder().setBalance(0).setBalanceYTD(0).build())
				.sorted(Comparator.comparing(Balance::getYear).thenComparing(Balance::getMonth).reversed())
				.collect(Collectors.groupingBy(Balance::getAccountId));

		List<Bill> bills = billStore
				.list(Query.newBuilder()
						.addFilter(Filter.newBuilder().setColumn("portfolio_id").setValue(String.valueOf(portfolioId))).build())
				.getResults();
		for (Bill bill : bills) {
			accountHash.put(bill.getAccountId(),
					new BalanceUpdater(portfolioId, bill.getAccountId(), bill.getYear(), bill.getMonth(), bill.getAmountDue())
							.apply(accountHash.get(bill.getAccountId())));
			bill.getTransactionMap().forEach((acctId, trans) ->
					accountHash.put(acctId, new BalanceUpdater(portfolioId, acctId, bill.getYear(), bill.getMonth(), -trans.getAmount())
							.apply(accountHash.get(acctId))));
		}

		accountHash.values().forEach(balances -> balances.forEach(balance -> {
			try {
				if (balance.getId() == 0) {
					create(balance);
				} else {
					update(balance.getId(), balance1 -> balance);
				}
			} catch (DD4StorageException e) {
				e.printStackTrace();
			}
		}));
	}

	static class BalanceUpdater implements UnaryOperator<List<Balance>> {
		private final long portfolioId;
		private final long accountId;
		private final double delta;
		private final int year;
		private final int nextMonth;
		private final int nextYear;

		BalanceUpdater(long portfolioId, long accountId, int year, int month, double delta) {
			this.portfolioId = portfolioId;
			this.accountId = accountId;
			this.year = year;
			this.delta = delta;
			nextMonth = month < 12 ? month + 1 : 1;
			nextYear = month < 12 ? year : year + 1;
		}

		@Override
		public List<Balance> apply(List<Balance> balances) {
			if (balances == null) {
				balances = new ArrayList<>();
			}
			boolean foundNextMonth = false;
			Balance balance = null;
			int x = 0;
			for (; x < balances.size(); x++) {
				balance = balances.get(x);
				if (balance.getYear() > nextYear || balance.getYear() == nextYear && balance.getMonth() >= nextMonth) {
					if (balance.getYear() == nextYear && balance.getMonth() == nextMonth) {
						foundNextMonth = true;
					}
					Balance.Builder balanceBuilder = balance.toBuilder()
							.setBalance(balance.getBalance() + delta);
					// Carry over year2date only if the same year.
					if (balance.getYear() == year) {
						balanceBuilder.setBalanceYTD(balance.getBalanceYTD() + delta);
					}
					balance = balanceBuilder.build();
					balances.set(x, balance);
				} else {
					break;
				}
			}
			if (!foundNextMonth) {
				if (balance == null || balance.getYear() > nextYear
						|| balance.getYear() == nextYear && balance.getMonth() > nextMonth) {
					balance = Balance.getDefaultInstance();
				}
				Balance.Builder balanceBuilder = Balance.newBuilder()
						.setPortfolioId(portfolioId)
						.setAccountId(accountId)
						.setYear(nextYear)
						.setMonth(nextMonth)
						.setBalance(balance.getBalance() + delta);
				if (year == nextYear) {
					balanceBuilder.setBalanceYTD(balance.getBalanceYTD() + delta);
				}
				balances.add(x, balanceBuilder.build());
			}
			return balances;
		}
	}
}
