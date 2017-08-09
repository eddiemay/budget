package com.digitald4.budget.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digitald4.budget.proto.BudgetProtos.Portfolio;
import com.digitald4.budget.proto.BudgetProtos.PortfolioUser;
import com.digitald4.budget.proto.BudgetProtos.UserRole;
import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.storage.DAOProtoSQLImpl;

public class PortfolioSQLDao extends DAOProtoSQLImpl<Portfolio> {

	public PortfolioSQLDao(DBConnector dbConnector) {
		super(Portfolio.class, dbConnector);
	}
	
	@Override
	public String getView() {
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
					.setRole(UserRole.forNumber(rs.getInt("role"))));
		}
		List<Portfolio> results = new ArrayList<>();
		for (Portfolio.Builder portfolio : portfolios.values()) {
			results.add(portfolio.build());
		}
		return results;
	}
}
