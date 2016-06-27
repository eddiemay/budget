package com.digitald4.budget.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.Portfolio.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.common.dao.sql.DAOProtoSQLImpl;
import com.digitald4.common.jdbc.DBConnector;

public class PortfolioSQLDao extends DAOProtoSQLImpl<Portfolio> {

	public PortfolioSQLDao(DBConnector dbConnector) {
		super(Portfolio.getDefaultInstance(), dbConnector);
	}
	
	@Override
	public String getTable() {
		return "V_PORTFOLIO";
	}
	
	@Override
	public List<Portfolio> process(ResultSet rs) throws SQLException {
		Map<Integer, Portfolio.Builder> portfolios = new HashMap<>();
		while (rs.next()) {
			int portfolioId = rs.getInt("id");
			Portfolio.Builder portfolio = portfolios.get(portfolioId);
			if (portfolio == null) {
				portfolio = Portfolio.newBuilder()
						.setId(portfolioId)
						.setName(rs.getString("name"));
				portfolios.put(portfolioId, portfolio);
			}
			portfolio.addPortfolioUser(PortfolioUser.newBuilder()
					.setPortfolioId(portfolioId)
					.setUserId(rs.getInt("user_id"))
					.setRole(UserRole.valueOf(rs.getInt("role_id"))));
		}
		List<Portfolio> results = new ArrayList<Portfolio>();
		for (Portfolio.Builder portfolio : portfolios.values()) {
			results.add(portfolio.build());
		}
		return results;
	}
}
