CREATE OR REPLACE VIEW AccountView AS
    SELECT Account.*
    FROM Account;

CREATE OR REPLACE VIEW BalanceView AS
    SELECT Balance.*, CONCAT(Balance.year, '-', LPAD(Balance.month, 2, '0')) AS bal_date,
        Account.portfolio_id, Account.name AS account_name
    FROM Balance, Account
    WHERE Balance.account_id = Account.id;

CREATE OR REPLACE VIEW BillView AS
    SELECT Bill.*, Account.portfolio_id, Account.name AS account_name
    FROM Bill, Account
    WHERE Bill.account_id = Account.id;

CREATE OR REPLACE VIEW GeneralDataView AS
    SELECT GeneralData.*
    FROM GeneralData;

CREATE OR REPLACE VIEW PortfolioView AS
		SELECT Portfolio.*
		FROM Portfolio;

CREATE OR REPLACE VIEW PortfolioUserView AS
    SELECT PortfolioUser.*
    FROM PortfolioUser;

CREATE OR REPLACE VIEW TemplateView AS
    SELECT Template.*
    FROM Template;

CREATE OR REPLACE VIEW TemplateBillView AS
    SELECT TemplateBill.*, Account.name AS account_name, Template.portfolio_id portfolio_id
    FROM TemplateBill, Account, Template
    WHERE TemplateBill.account_id = Account.id AND TemplateBill.template_id = Template.id;

CREATE OR REPLACE VIEW TransHistView AS
    SELECT TransHist.*
    FROM TransHist;

CREATE OR REPLACE VIEW UserView AS
    SELECT User.*
    FROM User;
