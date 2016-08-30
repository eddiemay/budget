package com.digitald4.budget.storage;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Account.Balance;
import com.digitald4.common.distributed.Function;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4UIProtos.ListRequest.QueryParam;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.storage.GenericDAOStore;
import com.google.annotations.VisibleForTesting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class AccountStore extends GenericDAOStore<Account> {
	
	public AccountStore(DAO<Account> dao) {
		super(dao);
	}
	
	public List<Account> getByPortfolio(int portfolioId) throws DD4StorageException {
		return get(QueryParam.newBuilder()
				.setColumn("portfolio_id")
				.setOperan("=")
				.setValue(String.valueOf(portfolioId))
				.build());
	}

	public Account updateBalance(int accountId, long dueDate, double delta)
			throws DD4StorageException {
		if (delta == 0) {
			return null;
		}
		return update(accountId, new BalanceUpdater(dueDate, delta));
	}
	
	public boolean recalculateBalance(int portfolioId, BillStore billStore) throws DD4StorageException {
		Map<Integer, Account> accountHash = new HashMap<>();
		for (Account account : getByPortfolio(portfolioId)) {
			accountHash.put(account.getId(), account.toBuilder().clearBalance().build());
		}
		
		List<Bill> bills = billStore.get(
				QueryParam.newBuilder().setColumn("portfolio_id").setOperan("=").setValue(String.valueOf(portfolioId)).build());
		for (Bill bill : bills) {
			accountHash.put(bill.getAccountId(),
					new BalanceUpdater(bill.getDueDate(), bill.getAmountDue())
							.execute(accountHash.get(bill.getAccountId())));
			for (Transaction trans : bill.getTransactionList()) {
				accountHash.put(trans.getDebitAccountId(),
						new BalanceUpdater(bill.getDueDate(), -trans.getAmount())
								.execute(accountHash.get(trans.getDebitAccountId())));
			}
		}
		
		for (final Account account : accountHash.values()) {
			try {
				update(account.getId(), new Function<Account, Account>() {
					@Override
					public Account execute(Account account_) {
						return account_.toBuilder()
								.clearBalance()
								.addAllBalance(account.getBalanceList())
								.build();
					}
				});
			} catch (DD4StorageException e) {
				e.printStackTrace();
			}
			System.out.println(account);
		}
		return true;
	}
	
	@VisibleForTesting static class BalanceUpdater implements Function<Account, Account> {
		private final long dueDate;
		private final double delta;
		public BalanceUpdater(long dueDate, double delta) {
			this.dueDate = dueDate;
			this.delta = delta;
		}
		
		@Override
		public Account execute(Account account) {
			DateTime date = new DateTime(dueDate);
			String dateStr = date.toString("yyyy-MM-dd");
			DateTime nextMonth = date.minusDays(date.getDayOfMonth() - 1)
					.plusMonths(1);
			String nextMonthStr = nextMonth.toString("yyyy-MM");
			Account.Builder builder = account.toBuilder();
			boolean foundNextMonth = false;
			Balance balance = null;
			int x = 0;
			for (; x < account.getBalanceCount(); x++) {
				balance = account.getBalance(x);
				if (balance.getDate().compareTo(dateStr) > 0) {
					if (balance.getDate().equals(nextMonthStr)) {
						foundNextMonth = true;
					}
					Balance.Builder balanceBuilder = balance.toBuilder()
							.setBalance(balance.getBalance() + delta);
					// Carry over year2date only if the same year.
					if (balance.getDate().substring(0, 4).equals(dateStr.substring(0, 4))) {
						balanceBuilder.setBalanceYearToDate(balance.getBalanceYearToDate() + delta);
					}
					builder.setBalance(x, balanceBuilder);
				} else {
					break;
				}
			}
			if (!foundNextMonth) {
				if (balance == null || balance.getDate().compareTo(dateStr) > 0) {
					balance = Balance.getDefaultInstance();
				}
				Balance.Builder balanceBuilder = Balance.newBuilder()
						.setDate(nextMonthStr)
						.setBalance(balance.getBalance() + delta);
				if (nextMonthStr.substring(0, 4).equals(dateStr.substring(0, 4))) {
					balanceBuilder .setBalanceYearToDate(balance.getBalanceYearToDate() + delta);
				}
				builder.addBalance(x, balanceBuilder);
			}
			return builder.build();
		}
	}
}
