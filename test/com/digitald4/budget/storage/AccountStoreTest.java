package com.digitald4.budget.storage;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.test.TestCase;

public class AccountStoreTest extends TestCase {
	
	@Test
	public void testBalanceUpdater() {
		Account account = Account.newBuilder()
				.build();
		account = new AccountStore.BalanceUpdater(DateTime.parse("2016-06-15").getMillis(), 100)
				.apply(account);
		assertEquals(1, account.getBalanceCount());
		assertEquals("2016-07", account.getBalance(0).getDate());
		assertEquals(100, account.getBalance(0).getBalance(), .001);
		assertEquals(100, account.getBalance(0).getBalanceYearToDate(), .001);
		
		account = new AccountStore.BalanceUpdater(DateTime.parse("2016-06-16").getMillis(), 25.42)
				.apply(account);
		assertEquals(1, account.getBalanceCount());
		assertEquals("2016-07", account.getBalance(0).getDate());
		assertEquals(125.42, account.getBalance(0).getBalance(), .001);
		assertEquals(125.42, account.getBalance(0).getBalanceYearToDate(), .001);
		
		account = new AccountStore.BalanceUpdater(DateTime.parse("2016-07-08").getMillis(), 50)
				.apply(account);
		assertEquals(2, account.getBalanceCount());
		assertEquals("2016-08", account.getBalance(0).getDate());
		assertEquals(175.42, account.getBalance(0).getBalance(), .001);
		assertEquals(175.42, account.getBalance(0).getBalanceYearToDate(), .001);
		assertEquals("2016-07", account.getBalance(1).getDate());
		assertEquals(125.42, account.getBalance(1).getBalance(), .001);
		assertEquals(125.42, account.getBalance(1).getBalanceYearToDate(), .001);
		
		account = new AccountStore.BalanceUpdater(DateTime.parse("2016-04-20").getMillis(), 20)
				.apply(account);
		assertEquals(3, account.getBalanceCount());
		assertEquals("2016-08", account.getBalance(0).getDate());
		assertEquals(195.42, account.getBalance(0).getBalance(), .001);
		assertEquals(195.42, account.getBalance(0).getBalanceYearToDate(), .001);
		assertEquals("2016-07", account.getBalance(1).getDate());
		assertEquals(145.42, account.getBalance(1).getBalance(), .001);
		assertEquals(145.42, account.getBalance(1).getBalanceYearToDate(), .001);
		assertEquals("2016-05", account.getBalance(2).getDate());
		assertEquals(20, account.getBalance(2).getBalance(), .001);
		assertEquals(20, account.getBalance(2).getBalanceYearToDate(), .001);
	}
}
