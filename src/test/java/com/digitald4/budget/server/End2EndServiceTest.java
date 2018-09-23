package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.budget.TestCase;
import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Template;
import com.digitald4.budget.proto.BudgetProtos.TemplateBill;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.budget.proto.BudgetUIProtos.ApplyTemplateRequest;
import com.digitald4.budget.proto.BudgetUIProtos.BudgetListRequest;
import com.digitald4.budget.storage.BalanceStore;
import com.digitald4.budget.storage.BillStore;
import com.digitald4.budget.storage.PortfolioStore;
import com.digitald4.budget.storage.PortfolioUserStore;
import com.digitald4.budget.storage.SecurityManager;
import com.digitald4.common.server.JSONService;
import com.digitald4.common.server.UpdateRequest;
import com.digitald4.common.storage.DAO;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.storage.testing.DAOTestingImpl;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.JsonFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.json.JSONObject;
import org.junit.Test;

public class End2EndServiceTest extends TestCase {
	private final User user = User.newBuilder().setId(962).build();
	private final Provider<User> userProvider = () -> user;
	private final DAO dao = new DAOTestingImpl();
	private final Provider<DAO> daoProvider = () -> dao;
	private SecurityManager securityManager = null;
	private final Provider<SecurityManager> securityManagerProvider = () -> securityManager;

	@Test
	public void testEnd2End() throws Exception {
		PortfolioUserStore portfolioUserStore = new PortfolioUserStore(daoProvider, securityManagerProvider);
		securityManager = new SecurityManager(user, portfolioUserStore);
		PortfolioStore portfolioStore = new PortfolioStore(daoProvider, portfolioUserStore);
		PortfolioService portfolioService = new PortfolioService(portfolioStore, securityManagerProvider, userProvider);

		Store<Account> accountStore = new GenericStore<>(Account.class, daoProvider);
		BudgetService<Account> accountService = new BudgetService<>(accountStore, securityManagerProvider);

		BalanceStore balanceStore = new BalanceStore(daoProvider);
		
		Store<Template> templateStore = new GenericStore<>(Template.class, daoProvider);
		BudgetService<Template> templateService = new BudgetService<>(templateStore, securityManagerProvider);

		Store<TemplateBill> templateBillStore = new GenericStore<>(TemplateBill.class, daoProvider);
		
		BillStore billStore = new BillStore(daoProvider, balanceStore, templateBillStore);
		BillService billService = new BillService(billStore, securityManagerProvider, templateStore);
		
		Portfolio portfolio = portfolioService.create(Portfolio.newBuilder()
				.setName("Test Portfolio")
				.putUser(user.getId(), UserRole.UR_OWNER)
				.build());
		assertTrue(portfolio.getId() > 0);
		
		List<Account> accounts = new ArrayList<>();
		Template template = null;
		List<Bill> bills = new ArrayList<>();
		List<TemplateBill> templateBills = new ArrayList<>();

		try {
			Account job = accountService.create(Account.newBuilder()
					.setPortfolioId(portfolio.getId())
					.setName("Job")
					.build());
			accounts.add(job);
			assertTrue(job.getId() > 0);

			Account bankAccount = accountService.create(Account.newBuilder()
					.setPortfolioId(portfolio.getId())
					.setName("Bank Account")
					.setPaymentAccount(true)
					.build());
			accounts.add(bankAccount);
			assertTrue(bankAccount.getId() > 0);


			Account rent = accountService.create(Account.newBuilder()
					.setPortfolioId(portfolio.getId())
					.setName("Rent")
					.build());
			accounts.add(rent);
			assertTrue(rent.getId() > 0);

			JSONService jsonService = new BudgetService.BudgetJSONService<>(accountService);

			JSONObject jsonCreditCard = jsonService.performAction("create", new JSONObject()
							.put("portfolioId", portfolio.getId())
							.put("name", "Bank Account")
							.put("paymentAccount", true));
			Account.Builder builder = Account.newBuilder();
			JsonFormat.parser().merge(jsonCreditCard.toString(), builder);
			Account creditCard = builder.build();
			accounts.add(creditCard);
			assertTrue(jsonCreditCard.getInt("id") > 0);


			accounts = accountService
					.list(BudgetListRequest.newBuilder()
							.setPortfolioId(portfolio.getId())
							.build())
					.getResults();

			assertEquals(4, accounts.size());

			JSONObject json = jsonService.performAction("list", new JSONObject()
					.put("portfolio_id", portfolio.getId()));
			assertEquals(4, json.getJSONArray("result").length());


			template = templateService.create(Template.newBuilder()
					.setPortfolioId(portfolio.getId())
					.setName("Standard Month")
					.build());
			assertTrue(template.getId() > 0);

			templateBills.add(templateBillStore.create(TemplateBill.newBuilder()
					.setTemplateId(template.getId())
					.setDueDay(1)
					.setAccountId(job.getId())
					.setAmountDue(-1000.25)
					.putTransaction(bankAccount.getId(), -1000.25)
					.build()));
			templateBills.add(templateBillStore.create(TemplateBill.newBuilder()
					.setTemplateId(template.getId())
					.setDueDay(5)
					.setAccountId(rent.getId())
					.setAmountDue(750)
					.putTransaction(bankAccount.getId(), 750)
					.build()));
			templateBills.add(templateBillStore.create(TemplateBill.newBuilder()
					.setTemplateId(template.getId())
					.setDueDay(15)
					.setAccountId(job.getId())
					.setAmountDue(-1000.25)
					.putTransaction(bankAccount.getId(), -1000.25)
					.build()));

			Bill payCreditCard = billService.create(Bill.newBuilder()
					.setAccountId(creditCard.getId())
					.setPortfolioId(creditCard.getPortfolioId())
					.setYear(2016)
					.setMonth(7)
					.setDay(20)
					.setAmountDue(500)
					//.addTransaction(TransactionUI.newBuilder()
					//	.setDebitAccountId(bankAccount.getId())
					//.setAmount(500))
					.build());
			bills.add(payCreditCard);
			assertTrue(payCreditCard.getId() > 0);

			bills = billService
					.applyTemplate(ApplyTemplateRequest.newBuilder()
							.setTemplateId(template.getId())
							.setYear(2016)
							.setMonth(7)
							.build())
					.getResults();
			// Should return the 3 bills from the template and the 1 I created.
			assertEquals(4, bills.size());

			portfolio = portfolioService.update(
					portfolio.getId(),
					new UpdateRequest<>(
							Portfolio.newBuilder().setName("Delete me").build(),
							FieldMask.newBuilder().addPaths("name").build()));
			assertEquals("Delete me", portfolio.getName());

			creditCard = accountService.update(
					creditCard.getId(),
					new UpdateRequest<>(
							Account.newBuilder().setName("HOH").build(),
							FieldMask.newBuilder().addPaths("name").build()));
			assertEquals("HOH", creditCard.getName());
			assertEquals(0, creditCard.getParentAccountId());

			creditCard = accountService.update(
					creditCard.getId(),
					new UpdateRequest<>(
							Account.newBuilder().setParentAccountId(rent.getId()).build(),
							FieldMask.newBuilder().addPaths("parent_account_id").build()));
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertFalse(rent.getPaymentAccount());

			creditCard = accountService.update(
					creditCard.getId(),
					new UpdateRequest<>(
							Account.newBuilder().setPaymentAccount(true).build(),
							FieldMask.newBuilder().addPaths("payment_account").build()));
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertTrue(creditCard.getPaymentAccount());

			template = templateService.update(
					template.getId(),
					new UpdateRequest<>(
							Template.newBuilder().setName("Normal Monthly Flow").build(),
							FieldMask.newBuilder().addPaths("name").build()));
			assertEquals("Normal Monthly Flow", template.getName());

			payCreditCard = billService.update(
					payCreditCard.getId(),
					new UpdateRequest<>(
							Bill.newBuilder().setName("July Payment").build(),
							FieldMask.newBuilder().addPaths("name").build()));
			assertEquals("July Payment", payCreditCard.getName());

			payCreditCard = billService.update(
					payCreditCard.getId(),
					new UpdateRequest<>(
							Bill.newBuilder().setAmountDue(520.25).build(),
							FieldMask.newBuilder().addPaths("amount_due").build()));
			assertEquals(520.25, payCreditCard.getAmountDue(), .001);

			payCreditCard = billService.update(
					payCreditCard.getId(),
					new UpdateRequest<>(
							Bill.newBuilder().setDay(22).build(),
							FieldMask.newBuilder().addPaths("day").build()));
			assertEquals(2016, payCreditCard.getYear());
			assertEquals(7, payCreditCard.getMonth());
			assertEquals(22, payCreditCard.getDay());

			payCreditCard = billService.update(
					payCreditCard.getId(),
					new UpdateRequest<>(
							Bill.newBuilder().putTransaction(bankAccount.getId(), Transaction.newBuilder().setAmount(520.25).build()).build(),
							FieldMask.newBuilder().addPaths("transaction").build()));
			assertEquals(520.25, payCreditCard.getTransactionMap().values().iterator().next().getAmount(), .001);

			payCreditCard = billService.update(
					payCreditCard.getId(),
					new UpdateRequest<>(
							Bill.newBuilder().putTransaction(bankAccount.getId(), Transaction.newBuilder().setAmount(102).build()).build(),
							FieldMask.newBuilder().addPaths("transaction").build()));
			assertEquals(102, payCreditCard.getTransactionMap().values().iterator().next().getAmount(), .001);
		} finally {
			bills.forEach(bill -> billService.delete(bill.getId()));

			templateBills.forEach(templateBill -> templateBillStore.delete(templateBill.getId()));

			if (template != null) {
				templateService.delete(template.getId());
			}

			accounts.forEach(account -> accountService.delete(account.getId()));

			try {
				portfolioService.delete(portfolio.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
