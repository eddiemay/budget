package com.digitald4.budget.test;

import com.digitald4.common.jdbc.DBConnector;
import com.digitald4.common.jdbc.DBConnectorThreadPoolImpl;

public class TestCase {
	
	protected static DBConnector dbConnector = new DBConnectorThreadPoolImpl(
				"org.gjt.mm.mysql.Driver",
						"jdbc:mysql://localhost/budget?autoReconnect=true",
						"dd4_user", "getSchooled85");
}
