package com.digitald4.budget.server;

import static org.junit.Assert.*;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.budget.proto.BudgetProtos.Balance;
import com.digitald4.budget.proto.BudgetProtos.Bill;
import com.digitald4.budget.proto.BudgetProtos.Bill.Transaction;
import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
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
import com.digitald4.budget.test.TestCase;
import com.digitald4.common.storage.DAOProtoSQLImpl;
import com.digitald4.common.proto.DD4Protos.User;
import com.digitald4.common.proto.DD4UIProtos.CreateRequest;
import com.digitald4.common.proto.DD4UIProtos.DeleteRequest;
import com.digitald4.common.proto.DD4UIProtos.UpdateRequest;
import com.digitald4.common.storage.GenericStore;
import com.digitald4.common.storage.Store;
import com.digitald4.common.util.Provider;
import com.google.protobuf.Any;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.JsonFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

public class End2EndServiceTest extends TestCase {
	private final User user = User.newBuilder().setId(962).build();
	private final Provider<User> userProvider = () -> user;

	@Test @Ignore // TODO(eddiemay) Implement an in memory testing datastore.
	public void testEnd2End() throws Exception {
		SecurityManager securityManager = null;
		new SecurityManager(user, new PortfolioUserStore(
				new DAOProtoSQLImpl<>(PortfolioUser.class, dbConnector), null));
		Provider<SecurityManager> securityManagerProvider = () -> securityManager;
		PortfolioUserStore portfolioUserStore = new PortfolioUserStore(
				new DAOProtoSQLImpl<>(PortfolioUser.class, dbConnector), securityManagerProvider);
		PortfolioStore portfolioStore =
				new PortfolioStore(new DAOProtoSQLImpl<>(Portfolio.class, dbConnector), portfolioUserStore);
		PortfolioService portfolioService = new PortfolioService(portfolioStore, securityManagerProvider, userProvider);
		
		Store<Account> accountStore = new GenericStore<>(new DAOProtoSQLImpl<>(Account.class, dbConnector));
		AccountService accountService = new AccountService(accountStore, securityManagerProvider);

		BalanceStore balanceStore = new BalanceStore(new DAOProtoSQLImpl<>(Balance.class, dbConnector));
		
		Store<Template> templateStore = new GenericStore<>(new DAOProtoSQLImpl<>(Template.class, dbConnector));
		BudgetService<Template> templateService = new BudgetService<>(templateStore, securityManagerProvider);

		Store<TemplateBill> templateBillStore = new GenericStore<>(new DAOProtoSQLImpl<>(TemplateBill.class, dbConnector));
		
		BillStore billStore = new BillStore(new DAOProtoSQLImpl<>(Bill.class, dbConnector), balanceStore, templateBillStore);
		BillService billService = new BillService(billStore, securityManagerProvider, templateStore, accountStore);
		
		Portfolio portfolio = portfolioService.create(CreateRequest.newBuilder()
				.setProto(Any.pack(Portfolio.newBuilder()
						.setName("Test Portfolio")
						.putUser(user.getId(), UserRole.UR_OWNER)
						.build()))
				.build());
		assertTrue(portfolio.getId() > 0);
		
		List<Account> accounts = new ArrayList<>();
		Template template = null;
		List<Bill> bills = new ArrayList<>();
		List<TemplateBill> templateBills = new ArrayList<>();

		try {
			Account job = accountService.create(CreateRequest.newBuilder()
					.setProto(Any.pack(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Job")
							.build()))
					.build());
			accounts.add(job);
			assertTrue(job.getId() > 0);

			Account bankAccount = accountService.create(CreateRequest.newBuilder()
					.setProto(Any.pack(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Bank Account")
							.setPaymentAccount(true)
							.build()))
					.build());
			accounts.add(bankAccount);
			assertTrue(bankAccount.getId() > 0);


			Account rent = accountService.create(CreateRequest.newBuilder()
					.setProto(Any.pack(Account.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Rent")
							.build()))
					.build());
			accounts.add(rent);
			assertTrue(rent.getId() > 0);


			// type.googleapis.com/budget.Account
			JSONObject jsonCreditCard = accountService.create(new JSONObject()
					.put("proto", new JSONObject()
							.put("portfolioId", portfolio.getId())
							.put("name", "Bank Account")
							.put("paymentAccount", true)));
			Account.Builder builder = Account.newBuilder();
			JsonFormat.parser().merge(jsonCreditCard.toString(), builder);
			Account creditCard = builder.build();
			accounts.add(creditCard);
			assertTrue(jsonCreditCard.getInt("id") > 0);


			accounts = accountService
					.list(BudgetListRequest.newBuilder()
							.setPortfolioId(portfolio.getId())
							.build())
					.getResultList()
					.stream()
					.map(any -> any.unpack(Account.class))
					.collect(Collectors.toList());

			assertEquals(4, accounts.size());

			JSONObject json = accountService.list(new JSONObject()
					.put("portfolio_id", portfolio.getId()));
			assertEquals(4, json.getJSONArray("result").length());


			template = templateService.create(CreateRequest.newBuilder()
					.setProto(Any.pack(Template.newBuilder()
							.setPortfolioId(portfolio.getId())
							.setName("Standard Month")
							.build()))
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

			Bill payCreditCard = billService.create(CreateRequest.newBuilder()
					.setProto(Any.pack(Bill.newBuilder()
							.setAccountId(creditCard.getId())
							.setYear(2016)
							.setMonth(7)
							.setDay(20)
							.setAmountDue(500)
							//.addTransaction(TransactionUI.newBuilder()
							//	.setDebitAccountId(bankAccount.getId())
							//.setAmount(500))
							.build()))
					.build());
			bills.add(payCreditCard);
			assertTrue(payCreditCard.getId() > 0);

			bills = billService
					.applyTemplate(ApplyTemplateRequest.newBuilder()
							.setTemplateId(template.getId())
							.setYear(2016)
							.setMonth(7)
							.build())
					.getResultList()
					.stream()
					.map(any -> any.unpack(Bill.class))
					.collect(Collectors.toList());
			// Should return the 3 bills from the template and the 1 I created.
			assertEquals(4, bills.size());

			portfolio = portfolioService.update(UpdateRequest.newBuilder()
					.setId(portfolio.getId())
					.setProto(Any.pack(Portfolio.newBuilder().setName("Delete me").build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("name"))
					.build());
			assertEquals("Delete me", portfolio.getName());

			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto(Any.pack(Account.newBuilder().setName("HOH").build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("name"))
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(0, creditCard.getParentAccountId());

			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto(Any.pack(Account.newBuilder().setParentAccountId(rent.getId()).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("parent_account_id"))
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertFalse(rent.getPaymentAccount());

			creditCard = accountService.update(UpdateRequest.newBuilder()
					.setId(creditCard.getId())
					.setProto(Any.pack(Account.newBuilder().setPaymentAccount(true).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("payment_account"))
					.build());
			assertEquals("HOH", creditCard.getName());
			assertEquals(rent.getId(), creditCard.getParentAccountId());
			assertTrue(creditCard.getPaymentAccount());

			template = templateService.update(UpdateRequest.newBuilder()
					.setId(template.getId())
					.setProto(Any.pack(Template.newBuilder().setName("Normal Monthly Flow").build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("name"))
					.build());
			assertEquals("Normal Monthly Flow", template.getName());

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto(Any.pack(Bill.newBuilder().setName("July Payment").build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("name"))
					.build());
			assertEquals("July Payment", payCreditCard.getName());

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto(Any.pack(Bill.newBuilder().setAmountDue(520.25).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("amount_due"))
					.build());
			assertEquals(520.25, payCreditCard.getAmountDue(), .001);

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto(Any.pack(Bill.newBuilder().setDay(22).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("day"))
					.build());
			assertEquals(2016, payCreditCard.getYear());
			assertEquals(7, payCreditCard.getMonth());
			assertEquals(22, payCreditCard.getDay());

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto(Any.pack(Bill.newBuilder()
							.putTransaction(bankAccount.getId(), Transaction.newBuilder().setAmount(520.25).build()).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("transaction"))
					.build());
			assertEquals(520.25, payCreditCard.getTransactionMap().values().iterator().next().getAmount(), .001);

			payCreditCard = billService.update(UpdateRequest.newBuilder()
					.setId(payCreditCard.getId())
					.setProto(Any.pack(Bill.newBuilder()
							.putTransaction(bankAccount.getId(), Transaction.newBuilder().setAmount(102).build()).build()))
					.setUpdateMask(FieldMask.newBuilder().addPaths("transaction"))
					.build());
			assertEquals(102, payCreditCard.getTransactionMap().values().iterator().next().getAmount(), .001);
		} finally {
			bills.forEach(bill -> billService.delete(DeleteRequest.newBuilder()
					.setId(bill.getId())
					.build()));

			templateBills.forEach(templateBill -> templateBillStore.delete(templateBill.getId()));

			if (template != null) {
				templateService.delete(DeleteRequest.newBuilder()
						.setId(template.getId())
						.build());
			}

			accounts.forEach(account -> accountService.delete(DeleteRequest.newBuilder()
					.setId(account.getId())
					.build()));

			try {
				portfolioService.delete(DeleteRequest.newBuilder()
						.setId(portfolio.getId())
						.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
