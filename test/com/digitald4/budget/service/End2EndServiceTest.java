package com.digitald4.budget.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.digitald4.budget.dao.PortfolioSQLDao;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetUIProtos.AccountCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.AccountUI;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillTransUpdateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI;
import com.digitald4.budget.proto.BudgetUIProtos.BillUI.TransactionUI;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.PortfolioUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateCreateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI;
import com.digitald4.budget.proto.BudgetUIProtos.TemplateUI.TemplateBillUI.TemplateTransactionUI;
import com.digitald4.budget.store.AccountStore;
import com.digitald4.budget.store.BillStore;
import com.digitald4.budget.store.PortfolioStore;
import com.digitald4.budget.store.TemplateStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.DateRange;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.util.UserProvider;

public class End2EndServiceTest extends TestCase {

	@Test
	public void testEnd2End() throws DD4StorageException {
		UserProvider userProvider = new UserProvider();
		userProvider.set(User.newBuilder()
				.setId(1)
				.build());
		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		PortfolioService portfolioService = new PortfolioService(portfolioStore, userProvider);
		
		AccountStore accountStore = new AccountStore(
				new DAOProtoSQLImpl<Account>(Account.getDefaultInstance(), dbConnector));
		AccountService accountService = new AccountService(accountStore);
		
		TemplateStore templateStore = new TemplateStore(
				new DAOProtoSQLImpl<Template>(Template.getDefaultInstance(), dbConnector));
		TemplateService templateService = new TemplateService(templateStore);
		
		BillStore billStore = new BillStore(
				new DAOProtoSQLImpl<Bill>(Bill.getDefaultInstance(), dbConnector), accountStore);
		BillService billService = new BillService(billStore, templateStore);
		
		PortfolioUI portfolio = portfolioService.create(PortfolioCreateRequest.newBuilder()
				.setPortfolio(PortfolioUI.newBuilder()
						.setName("Test Portfolio"))
				.build());
		assertTrue(portfolio.getId() > 0);
		
		List<AccountUI> accounts = new ArrayList<AccountUI>();
		TemplateUI template = null;
		List<BillUI> bills = new ArrayList<BillUI>();
		
			try {
			AccountUI job = accountService.create(AccountCreateRequest.newBuilder()
					.setAccount(AccountUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Job"))
					.build());
			assertTrue(job.getId() > 0);
			accounts.add(job);
			
			AccountUI bankAccount = accountService.create(AccountCreateRequest.newBuilder()
					.setAccount(AccountUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Bank Account")
							.setPaymentAccount(true))
					.build());
			assertTrue(bankAccount.getId() > 0);
			accounts.add(bankAccount);
			
			AccountUI rent = accountService.create(AccountCreateRequest.newBuilder()
					.setAccount(AccountUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Rent"))
					.build());
			assertTrue(rent.getId() > 0);
			accounts.add(rent);
			
			AccountUI creditCard = accountService.create(AccountCreateRequest.newBuilder()
					.setAccount(AccountUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Credit Card"))
					.build());
			assertTrue(creditCard.getId() > 0);
			accounts.add(creditCard);
			
			template = templateService.create(TemplateCreateRequest.newBuilder()
					.setTemplate(TemplateUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Standard Month")
							.addBill(TemplateBillUI.newBuilder()
									.setDueDay(1)
									.setAccountId(job.getId())
									.setAmountDue(-1000.25)
									.addTransaction(TemplateTransactionUI.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(-1000.25)))
							.addBill(TemplateBillUI.newBuilder()
									.setDueDay(5)
									.setAccountId(rent.getId())
									.setAmountDue(750)
									.addTransaction(TemplateTransactionUI.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(750)))
							.addBill(TemplateBillUI.newBuilder()
									.setDueDay(15)
									.setAccountId(job.getId())
									.setAmountDue(-1000.25)
									.addTransaction(TemplateTransactionUI.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(-1000.25))))
					.build());
			assertTrue(template.getId() > 0);
			assertEquals(3, template.getBillCount());
			assertEquals(1, template.getBill(0).getTransactionCount());
			
			BillUI payCreditCard = billService.create(BillCreateRequest.newBuilder()
					.setBill(BillUI.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setAccountId(creditCard.getId())
							.setDueDate(DateTime.parse("2016-07-20").getMillis())
							.setAmountDue(500)
							.addTransaction(TransactionUI.newBuilder()
									.setDebitAccountId(bankAccount.getId())
									.setAmount(500)))
					.build());
			assertTrue(payCreditCard.getId() > 0);
			bills.add(payCreditCard);
			
			bills = billService.applyTemplate(ApplyTemplateRequest.newBuilder()
					.setTemplateId(template.getId())
					.setRefDate(DateTime.parse("2016-07-01").getMillis())
					.setDateRange(DateRange.MONTH)
					.build());
			// Should return the 3 bills from the template and the 1 I created.
			assertEquals(4, bills.size());
			
			portfolio = portfolioService.update(UpdateRequest.newBuilder()
					.setId(portfolio.getId())
					.setProperty("name")
					.setValue("Delete me")
					.build());
			assertEquals("Delete me", portfolio.getName());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProperty("name")
					.setValue("HOH")
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(0, creditCard.getParentAccountId());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProperty("parent_account_id")
					.setValue("" + rent.getId())
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertFalse(rent.getPaymentAccount());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProperty("payment_account")
					.setValue("true")
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertTrue(creditCard.getPaymentAccount());
			
			template = templateService.update(UpdateRequest.newBuilder()
					.setId(template.getId())
					.setProperty("name")
					.setValue("Normal Monthly Flow")
					.build());
			assertEquals("Normal Monthly Flow", template.getName());
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProperty("name")
					.setValue("July Payment")
					.build());
			assertEquals("July Payment", payCreditCard.getName());
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProperty("amount_due")
					.setValue("520.25")
					.build());
			assertEquals(520.25, payCreditCard.getAmountDue(), .001);
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProperty("due_date")
					.setValue("" + DateTime.parse("2016-07-22").getMillis())
					.build());
			assertEquals(DateTime.parse("2016-07-22").getMillis(), payCreditCard.getDueDate());
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProperty("transaction")
					.setValue("{transaction: [{amount: 520.25, debit_account_id: " + bankAccount.getId()
							+ "}]}")
					.build());
			assertEquals(520.25, payCreditCard.getTransaction(0).getAmount(), .001);
			
			payCreditCard = billService.updateTransaction(BillTransUpdateRequest.newBuilder()
					.setBillId(payCreditCard.getId())
					.build());
			assertEquals(0, payCreditCard.getTransactionCount());
			
			payCreditCard = billService.updateTransaction(BillTransUpdateRequest.newBuilder()
					.setBillId(payCreditCard.getId())
					.addTransaction(TransactionUI.newBuilder()
							.setAmount(520.20)
							.setDebitAccountId(bankAccount.getId()))
					.build());
			assertEquals(520.20, payCreditCard.getTransaction(0).getAmount(), .001);
		} finally {
			for (BillUI bill : bills) {
				assertTrue(billService.delete(DeleteRequest.newBuilder()
						.setId(bill.getId())
						.build()));
			}
			
			assertTrue(templateService.delete(DeleteRequest.newBuilder()
					.setId(template.getId())
					.build()));
			
			for (AccountUI account : accounts) {
				assertTrue(accountService.delete(DeleteRequest.newBuilder()
						.setId(account.getId())
						.build()));
			}
			
			assertTrue(portfolioService.delete(DeleteRequest.newBuilder()
					.setId(portfolio.getId())
					.build()));
		}
	}
}
