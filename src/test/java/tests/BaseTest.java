package tests;

import Utils.ConnectAPI;
import Utils.GmailUtils;
import aquality.selenium.browser.AqualityServices;
import aquality.selenium.browser.Browser;
import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import javax.naming.AuthenticationException;

public abstract class BaseTest {

    protected static ISettingsFile config = new JsonSettingsFile("config.json");
    protected static ISettingsFile testData = new JsonSettingsFile("TestData.json");
    Browser browser = AqualityServices.getBrowser();
    Logger logger = null;
    @BeforeClass
   public void getToken() {
        logger = Logger.getInstance();
        //ConnectAPI.getAuthorizationCode();
        //ConnectAPI.connectGmailAPI();
        try {
            Integer emailsMarkedAsRead = GmailUtils.markAllEmailsAsRead();
            logger.info("Number of emails marked as 'read' before testing begins: " + emailsMarkedAsRead);
        } catch (AuthenticationException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @BeforeTest
    public void setUp(){
        logger = Logger.getInstance();
        browser.maximize();
        browser.goTo(config.getValue("/euroNewsMainPage").toString());
    }
    @AfterClass
    public void tearDown() {
     //   browser.quit();
    }
}