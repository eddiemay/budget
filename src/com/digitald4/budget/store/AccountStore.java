package com.digitald4.budget.store;

import java.util.List;

import com.digitald4.budget.model.Account;
import com.digitald4.common.util.Pair;

public interface AccountStore {
	public Account create(Account account);
	
	public Account readAccount(Integer id);
	
	public List<Account> readAccounts(Pair<String, Object>... valuePairs);
	
	public List<Account> readAccounts(String[] props, Object... values);
	
	public List<Account> readAccounts(String jpql, Object... values);
	
	public List<Account> readAccountsByNamedQuery(String name, Object... values);
	
	public Account update(Account account);
	
	public void delete(Account account);
}
