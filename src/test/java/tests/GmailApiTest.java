package tests;

import Utils.GmailUtils;
import forms.MainPage;
import forms.NewsletterPage;
import forms.SubscriptionConfirmedPage;
import forms.UnsubscribePage;
import org.testng.Assert;
import org.testng.annotations.Test;


public class GmailApiTest extends BaseTest {
    @Test
    public void gmailApiTests(){
        logger.info("Step 1 - Main page of Euronews is opened");
        MainPage mainPage = new MainPage();
        Assert.assertTrue(mainPage.state().waitForDisplayed(), "home page was not displayed");

        logger.info("Step 2 - go to Newsletter");
        mainPage.acceptCookies();
        mainPage.goToNewsletterPage();
        NewsletterPage newsletterPage = new NewsletterPage();
        Assert.assertTrue(newsletterPage.state().waitForDisplayed(),"the newsletter page was not displayed");

        logger.info("Step 3 -  Choose a random newsletter subscription plan");
        String selectedNewsletter = newsletterPage.selectRandomNewsletter();
        Assert.assertTrue(newsletterPage.isEmailFormDisplayed(),"The email subscription form was not displayed");

        logger.info("Step 4 - Enter email, click submit button");
        newsletterPage.enterEmailSubsForm(testData.getValue("/email").toString());
        newsletterPage.clickSubmitButton();
        Assert.assertTrue(GmailUtils.isNewMailExists(), "no new email with a request to confirm subscription");

        logger.info("Step 5 -  Follow the link received from the letter");
        String urlToConfirmSubscription = GmailUtils.getUrlToConfirmSubscription();
        browser.goTo(urlToConfirmSubscription);

        SubscriptionConfirmedPage subscriptionConfirmedPage = new SubscriptionConfirmedPage();
        Assert.assertTrue(subscriptionConfirmedPage.state().waitForDisplayed(), "page with subscription confirmation was not displayed");
        logger.info("Marking all Euro News new mails as read");
        GmailUtils.markAllEmailsAsRead();

        logger.info("Step 6 - Click Back to the site");
        subscriptionConfirmedPage.clickOnBackToTheSiteButton();
        Assert.assertTrue(mainPage.state().waitForDisplayed(), "home page was not displayed");

        logger.info("Step 7 - Follow the link Newsletters in the header, choose the same newsletter subscription plan as in the step 3, click See preview");
        mainPage.goToNewsletterPage();
        newsletterPage.openPreviewOfNewsletter(selectedNewsletter);
        Assert.assertTrue(newsletterPage.previewForNewsletterIsOpen(selectedNewsletter), "preview of selected newsletter is not open");

        logger.info("Step 8 -  On preview find and get a link to unsubscribe from the mailing list , follow this link in the browser");
        browser.goTo(newsletterPage.getUnsubscribeUrlFromNewsletterPreview());
        UnsubscribePage unsubscribePage = new UnsubscribePage();
        Assert.assertTrue(unsubscribePage.state().waitForDisplayed(), "unsubscribe page was not displayed");

        logger.info("Step 9 - Enter email, click Submit button");
        unsubscribePage.enterEmailToUnsubscribe(testData.getValue("/email").toString());
        unsubscribePage.clickUnsubscribeButton();
        Assert.assertTrue(unsubscribePage.isUnsubscribeMessageExists(), "unsubscribe message has not appeared");

        logger.info("Step 10 - Make sure that you haven't received an email with a message about canceling your subscription");
        Assert.assertFalse(GmailUtils.isNewMailExists());

    }
}
