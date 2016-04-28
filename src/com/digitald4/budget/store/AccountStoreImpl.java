package com.digitald4.budget.store;

import java.util.List;

import javax.persistence.EntityManager;

import com.digitald4.budget.model.Account;
import com.digitald4.common.util.Pair;

public class AccountStoreImpl implements AccountStore {
	
	private EntityManager em;

	public AccountStoreImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public Account create(Account account) {
		em.persist(account);
		return account;
	}

	@Override
	public Account readAccount(Integer id) {
		return em.find(Account.class, id);
	}

	@Override
	public List<Account> readAccounts(Pair<String, Object>... valuePairs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Account> readAccounts(String[] props, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Account> readAccounts(String jpql, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Account> readAccountsByNamedQuery(String name, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account update(Account account) {
		return em.merge(account);
	}

	@Override
	public void delete(Account account) {
		em.remove(this);
	}
}
