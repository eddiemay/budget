package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.common.server.DualProtoService;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioSQLDao;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class End2EndServiceTest extends TestCase {
	private final User user = User.newBuilder().setId(1).build();
	private final Provider<User> userProvider = () -> user;

	@Test
	public void testEnd2End() throws Exception {
		PortfolioStore portfolioStore = new PortfolioStore(new PortfolioSQLDao(dbConnector));
		PortfolioService portfolioService = new PortfolioService(portfolioStore, userProvider);
		
		Store<Account> accountStore = new GenericStore<>(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		AccountService accountService = new AccountService(accountStore);

		BalanceStore balanceStore = new BalanceStore(new DAOProtoSQLImpl<>(Balance.class, dbConnector));
		
		Store<Template> templateStore = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		TemplateService templateService = new TemplateService(templateStore);

		Store<TemplateBill> templateBillStore = new GenericStore<>(new DAOProtoSQLImpl<>(TemplateBill.class, dbConnector));
		
		BillStore billStore = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), balanceStore, templateBillStore);
		BillService billService = new BillService(billStore, templateStore);
		
		Portfolio portfolio = portfolioService.create(CreateRequest.newBuilder()
				.setProto(DualProtoService.convertToJSON(Portfolio.newBuilder()
						.setName("Test Portfolio")
						.build()).toString())
				.build());
		assertTrue(portfolio.getId() > 0);
		
		List<Account> accounts = new ArrayList<>();
		Template template = null;
		List<Bill> bills = new ArrayList<>();

		try {
			Account job = accountService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Job")
							.build()).toString())
					.build());
			assertTrue(job.getId() > 0);
			accounts.add(job);
			
			Account bankAccount = accountService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Bank Account")
							.setPaymentAccount(true)
							.build()).toString())
					.build());
			assertTrue(bankAccount.getId() > 0);
			accounts.add(bankAccount);
			
			Account rent = accountService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Rent")
							.build()).toString())
					.build());
			assertTrue(rent.getId() > 0);
			accounts.add(rent);
			
			Account creditCard = accountService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Credit Card")
							.build()).toString())
					.build());
			assertTrue(creditCard.getId() > 0);
			accounts.add(creditCard);
			
			template = templateService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Template.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Standard Month")
							/*.addBill(TemplateBill.newBuilder()
									.setDueDay(1)
									.setAccountId(job.getId())
									.setAmountDue(-1000.25)
									.addTransaction(TemplateTransaction.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(-1000.25)))
							.addBill(TemplateBill.newBuilder()
									.setDueDay(5)
									.setAccountId(rent.getId())
									.setAmountDue(750)
									.addTransaction(TemplateTransaction.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(750)))
							.addBill(TemplateBill.newBuilder()
									.setDueDay(15)
									.setAccountId(job.getId())
									.setAmountDue(-1000.25)
									.addTransaction(TemplateTransaction.newBuilder()
											.setDebitAccountId(bankAccount.getId())
											.setAmount(-1000.25)))*/
							.build()).toString())
					.build());
			assertTrue(template.getId() > 0);

			Bill payCreditCard = billService.create(CreateRequest.newBuilder()
					.setProto(DualProtoService.convertToJSON(Bill.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setAccountId(creditCard.getId())
							.setYear(2016)
							.setMonth(7)
							.setDay(20)
							.setAmountDue(500)
							//.addTransaction(TransactionUI.newBuilder()
								//	.setDebitAccountId(bankAccount.getId())
									//.setAmount(500))
							.build()).toString())
					.build());
			assertTrue(payCreditCard.getId() > 0);
			bills.add(payCreditCard);
			
			bills = billService.applyTemplate(ApplyTemplateRequest.newBuilder()
					.setTemplateId(template.getId())
					.setYear(2016)
					.setMonth(7)
					.build());
			// Should return the 3 bills from the template and the 1 I created.
			assertEquals(4, bills.size());
			
			portfolio = portfolioService.update(UpdateRequest.newBuilder()
					.setId(portfolio.getId())
					.setProto("{\"name\": \"Delete me\'}")
					.build());
			assertEquals("Delete me", portfolio.getName());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto("{\"name\": \"HOH\'}")
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(0, creditCard.getParentAccountId());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto("{\"parent_account_id\": " + rent.getId() + "}")
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertFalse(rent.getPaymentAccount());
			
			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto("{\"payment_account\": true}")
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertTrue(creditCard.getPaymentAccount());
			
			template = templateService.update(UpdateRequest.newBuilder()
					.setId(template.getId())
					.setProto("{\"name\": \"Normal Monthly Flow\"}")
					.build());
			assertEquals("Normal Monthly Flow", template.getName());
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto("{\"name\": \"July Payment\"}")
					.build());
			assertEquals("July Payment", payCreditCard.getName());
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto("{\"amount_due\": 520.25}")
					.build());
			assertEquals(520.25, payCreditCard.getAmountDue(), .001);
			
			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto("{\"day\": 22}")
					.build());
			assertEquals(2016, payCreditCard.getYear());
			assertEquals(7, payCreditCard.getMonth());
			assertEquals(22, payCreditCard.getDay());

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto("{\"tranaction\": [{amount: 520.25, debit_account_id: " + bankAccount.getId() + "}]}")
					.build());
			assertEquals(520.25, payCreditCard.getTransactionMap().values().iterator().next().getAmount(), .001);
		} finally {
			for (Bill bill : bills) {
				assertTrue(billService.delete(DeleteRequest.newBuilder()
						.setId(bill.getId())
						.build()));
			}

			if (template != null) {
				assertTrue(templateService.delete(DeleteRequest.newBuilder()
						.setId(template.getId())
						.build()));
			}
			
			for (Account account : accounts) {
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
