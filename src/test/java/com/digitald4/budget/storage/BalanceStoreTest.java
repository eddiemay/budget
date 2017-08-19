package com.digitald4.budget.storage;

import static org.junit.Assert.*;

import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.test.TestCase;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class BalanceStoreTest extends TestCase {
	
	@Test
	public void testBalanceUpdater() {
		List<Balance> balances = new ArrayList<>();
		balances = new BalanceStore.BalanceUpdater(1, 2016,6, 100)
				.apply(balances);
		assertEquals(1, balances.size());
		assertEquals(2016, balances.get(0).getYear());
		assertEquals(7, balances.get(0).getMonth());
		assertEquals(100, balances.get(0).getBalance(), .001);
		assertEquals(100, balances.get(0).getBalanceYTD(), .001);
		
		balances = new BalanceStore.BalanceUpdater(1, 2016, 6, 25.42)
				.apply(balances);
		assertEquals(1, balances.size());
		assertEquals(2016, balances.get(0).getYear());
		assertEquals(7, balances.get(0).getMonth());
		assertEquals(125.42, balances.get(0).getBalance(), .001);
		assertEquals(125.42, balances.get(0).getBalanceYTD(), .001);
		
		balances = new BalanceStore.BalanceUpdater(1, 2016, 7, 50).apply(balances);
		assertEquals(2, balances.size());
		assertEquals(2016, balances.get(0).getYear());
		assertEquals(8, balances.get(0).getMonth());
		assertEquals(175.42, balances.get(0).getBalance(), .001);
		assertEquals(175.42, balances.get(0).getBalanceYTD(), .001);
		assertEquals(2016, balances.get(1).getYear());
		assertEquals(7, balances.get(1).getMonth());
		assertEquals(125.42, balances.get(1).getBalance(), .001);
		assertEquals(125.42, balances.get(1).getBalanceYTD(), .001);
		
		balances = new BalanceStore.BalanceUpdater(1, 2016, 4, 20)
				.apply(balances);
		assertEquals(3, balances.size());
		assertEquals(2016, balances.get(0).getYear());
		assertEquals(8, balances.get(0).getMonth());
		assertEquals(195.42, balances.get(0).getBalance(), .001);
		assertEquals(195.42, balances.get(0).getBalanceYTD(), .001);
		assertEquals(2016, balances.get(1).getYear());
		assertEquals(7, balances.get(1).getMonth());
		assertEquals(145.42, balances.get(1).getBalance(), .001);
		assertEquals(145.42, balances.get(1).getBalanceYTD(), .001);
		assertEquals(2016, balances.get(2).getYear());
		assertEquals(5, balances.get(2).getMonth());
		assertEquals(20, balances.get(2).getBalance(), .001);
		assertEquals(20, balances.get(2).getBalanceYTD(), .001);
	}
}
