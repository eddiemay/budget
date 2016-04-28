package com.digitald4.budget.model;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.model.User;

public class BillTest {
	
	private static EntityManager em;

	private Account sce; 
	private Account chase; 
	private Account ally;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		em = EntityManagerHelper.getEntityManagerFactory("DD4JPA", "org.gjt.mm.mysql.Driver",
				"jdbc:mysql://localhost/budget?autoReconnect=true", "eddiemay", "").createEntityManager();
		User.setActiveUser(User.getInstance(em, 1));
	}
	
	@Before
	public void setup() throws Exception {
		sce = new Account(em).setName("Sce");
		chase = new Account(em).setName("Eddie's Checking").setPaymentAccount(true);
		ally = new Account(em).setName("Money Market").setPaymentAccount(true);
	}

	@Test
	public void testRemainingBalance() throws Exception {
		Bill bill = new Bill(em).setAmountDue(201.53).setDueDate(DateTime.parse("2014-10-24").toDate());
		sce.addBill(bill);
		assertEquals(201.53, bill.getAmountDue(), .0001);
		Transaction trans = new Transaction(em).setDebitAccount(chase).setAmount(150);
		bill.addTransaction(trans);
		assertEquals(150, bill.getPaid(), .0001);
		assertEquals(51.53, bill.getRemainingDue(), .0001);
		assertEquals(bill.getDueDate(), trans.getPaymentDate());
		assertTrue(bill.isActiveOnDay(DateTime.parse("2014-10-24").toDate()));
	}
	
	@Test
	public void testAccountBalances() throws Exception {
		Bill paycheck = new Bill(em).setAmountDue(-1000).setDueDate(DateTime.parse("2014-10-21").toDate());
		Transaction trans = new Transaction(em).setDebitAccount(chase).setAmount(-1000);
		paycheck.addTransaction(trans);
		chase.addDebitTransaction(trans);
		assertEquals(0, chase.getBalancePre(trans), .0001);
		assertEquals(1000, chase.getBalancePost(trans), .0001);
		assertEquals(0, ally.getBalancePre(trans), .0001);
		assertEquals(0, ally.getBalancePost(trans), .0001);
		
		Bill bill = new Bill(em).setAmountDue(201.53).setDueDate(DateTime.parse("2014-10-24").toDate());
		sce.addBill(bill);
		trans = new Transaction(em).setDebitAccount(chase).setAmount(150);
		bill.addTransaction(trans);
		chase.addDebitTransaction(trans);
		assertEquals(1000, chase.getBalancePre(trans), .0001);
		assertEquals(850, chase.getBalancePost(trans), .0001);
		assertEquals(0, ally.getBalancePre(trans), .0001);
		assertEquals(0, ally.getBalancePost(trans), .0001);
		
		Bill atmReimbursement = new Bill(em).setAmountDue(-10).setDueDate(DateTime.parse("2014-11-10").toDate());
		trans = new Transaction(em).setDebitAccount(ally).setAmount(-10);
		atmReimbursement.addTransaction(trans);
		ally.addDebitTransaction(trans);
		assertEquals(850, chase.getBalancePre(trans), .0001);
		assertEquals(850, chase.getBalancePost(trans), .0001);
		assertEquals(0, ally.getBalancePre(trans), .0001);
		assertEquals(10, ally.getBalancePost(trans), .0001);
	}
}
