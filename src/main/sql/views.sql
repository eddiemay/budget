CREATE OR REPLACE VIEW AccountView AS
    SELECT Account.*
    FROM Account;

CREATE OR REPLACE VIEW BalanceView AS
    SELECT Balance.*
    FROM Balance;

CREATE OR REPLACE VIEW BillView AS
    SELECT Bill.*
    FROM Bill;

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
    SELECT TemplateBill.*
    FROM TemplateBill;

CREATE OR REPLACE VIEW TransHistView AS
    SELECT TransHist.*
    FROM TransHist;

CREATE OR REPLACE VIEW UserView AS
    SELECT User.*
    FROM User;
