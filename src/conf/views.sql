CREATE OR REPLACE VIEW V_PORTFOLIO AS
		SELECT Portfolio.*, PortfolioUser.*
		FROM Portfolio
		LEFT JOIN PortfolioUser ON Portfolio.ID = PortfolioUser.PORTFOLIO_ID;

CREATE OR REPLACE VIEW V_BILL AS
    SELECT Bill.*, Account.portfolio_id, Account.name AS account_name
    FROM Bill, Account
    WHERE Bill.ACCOUNT_ID = ACCOUNT.ID;

CREATE OR REPLACE VIEW V_TemplateBill AS
    SELECT TemplateBill.*, ACCOUNT.NAME AS ACCOUNT_NAME
    FROM TemplateBill, ACCOUNT
    WHERE TemplateBill.ACCOUNT_ID = ACCOUNT.ID;

CREATE OR REPLACE VIEW V_Balance AS
    SELECT Balance.*, CONCAT(Balance.year, '-', LPAD(Balance.month, 2, '0')) AS bal_date,
        Account.portfolio_id, Account.name AS account_name
    FROM Balance, Account
    WHERE Balance.account_id = Account.id;
