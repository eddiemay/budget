package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.Filter;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericStore;
import com.google.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BalanceStore extends GenericStore<Balance> {
	private final DAO<Balance> dao;

	public BalanceStore(DAO<Balance> dao) {
		super(dao);
		this.dao = dao;
	}

	public List<Balance> getByPortfolioId(int portfolioId, int year) {
		return dao.get(
				Filter.newBuilder().setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build(),
				Filter.newBuilder().setColumn("year").setOperan("=").setValue(String.valueOf(year)).build());
	}

	public List<Balance> getByPortfolioId(int portfolioId, int year, int month) {
		return dao.get(
				Filter.newBuilder().setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build(),
				Filter.newBuilder().setColumn("year").setOperan("=").setValue(String.valueOf(year)).build(),
				Filter.newBuilder().setColumn("month").setOperan("=").setValue(String.valueOf(month)).build());
	}

	public Balance get(int accountId, int year, int month) {
		List<Balance> balances = dao
				.get(Filter.newBuilder().setColumn("account_id").setOperan("=").setValue(String.valueOf(accountId)).build(),
						Filter.newBuilder().setColumn("year").setOperan("<=").setValue(String.valueOf(year)).build())
				.stream()
				.sorted(Comparator.comparing(Balance::getYear).reversed().thenComparing(Balance::getMonth).reversed())
				.collect(Collectors.toList());
		if (balances.isEmpty()) {
			return Balance.newBuilder()
					.setAccountId(accountId)
					.setYear(year)
					.setMonth(month)
					.build();
		} else if (balances.get(0).getMonth() < month || balances.get(0).getYear() < year) {
			return Balance.newBuilder()
					.setAccountId(accountId)
					.setYear(year)
					.setMonth(month)
					.setBalance(balances.get(0).getBalance())
					.setBalanceYTD(balances.get(0).getYear() == year ? balances.get(0).getBalanceYTD() : 0)
					.build();
		}
		return balances.get(0);
	}

	void applyUpdate(int accountId, int year, int month, double delta) {
		int nextMonth = month < 12 ? month + 1 : 1;
		int nextYear = month < 12 ? year : year + 1;
		AtomicBoolean nextMonthFound = new AtomicBoolean(false);
		dao.get(Filter.newBuilder().setColumn("account_id").setOperan("=").setValue(String.valueOf(accountId)).build(),
				Filter.newBuilder().setColumn("year").setOperan(">=").setValue(String.valueOf(year)).build())
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
			Balance balance = get(accountId, nextYear, nextMonth);
			create(balance.toBuilder()
					.setBalance(balance.getBalance() + delta)
					.setBalanceYTD(balance.getBalanceYTD() + (balance.getYear() == year ? delta : 0))
					.build());
		}
	}

	public void recalculateBalance(int portfolioId, BillStore billStore) {
		Map<Integer, List<Balance>> accountHash =
				get(Filter.newBuilder().setColumn("portfolio_id").setOperan("=").setValue("" + portfolioId).build())
				.stream()
						.map(balance -> balance.toBuilder().setBalance(0).setBalanceYTD(0).build())
						.sorted(Comparator.comparing(Balance::getYear).thenComparing(Balance::getMonth).reversed())
						.collect(Collectors.groupingBy(Balance::getAccountId));

		List<Bill> bills = billStore.get(
				Filter.newBuilder().setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build());
		for (Bill bill : bills) {
			accountHash.put(bill.getAccountId(),
					new BalanceUpdater(bill.getAccountId(), bill.getYear(), bill.getMonth(), bill.getAmountDue())
							.apply(accountHash.get(bill.getAccountId())));
			bill.getTransactionMap().forEach((acctId, trans) ->
					accountHash.put(acctId, new BalanceUpdater(acctId,  bill.getYear(), bill.getMonth(), -trans.getAmount())
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

	@VisibleForTesting static class BalanceUpdater implements UnaryOperator<List<Balance>> {
		private final int accountId;
		private final double delta;
		private final int year;
		private final int nextMonth;
		private final int nextYear;

		BalanceUpdater(int accountId, int year, int month, double delta) {
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
