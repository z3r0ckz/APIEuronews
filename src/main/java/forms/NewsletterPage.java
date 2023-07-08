package forms;

import Utils.RandomUtils;
import Utils.StringUtils;
import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.logging.Logger;
import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.elements.interfaces.ITextBox;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import java.util.List;

public class NewsletterPage extends Form {
    public NewsletterPage() {
        super(By.xpath("//span[contains(@class, 'h1') and contains(., 'newsletters')]"),"Newsletter Page");
    }
    private final Logger logger = Logger.getInstance();
    //----------Locators-------------
    private final List<ITextBox> boxListNewsletter = getElementFactory().findElements(
            By.xpath("//div[contains(@class, 'bg-white')]"), ITextBox.class);
    private final ITextBox emailForm = getElementFactory().getTextBox(
            By.xpath("//section[contains(@class, 'sticky')]"), "subscription email form at the bottom of the page");

    private final ITextBox inputBoxEmail = getElementFactory().getTextBox(
            By.xpath("//input[@class='w-full']"),"email input box");
    private final IButton btnSubmitEmail = getElementFactory().getButton(
            By.xpath("//input[@value='Submit']"),"Submit email form");
    private final ITextBox openNewsletterPreview = getElementFactory().getTextBox(
            By.xpath("//div[contains(@id, '_previews') and contains(@style, 'opacity: 1') and not(contains(@style, 'display: none'))]"), "currently open newsletter preview");

    private ITextBox randomNewsletter = null;
    //--------Methods------
    public String selectRandomNewsletter() {
        randomNewsletter = boxListNewsletter.get(RandomUtils.getRandomIntInRange(boxListNewsletter.size()));
        logger.info("Subscribing to: " + randomNewsletter.findChildElement(By.xpath("//h2"),ITextBox.class).getText());
        //click in the submit button of the child element
        randomNewsletter.findChildElement(By.xpath
                ("//label[contains(@class, 'btn-tertiary') and not (contains(@class, 'hidden'))]")
                ,"select newsletter button", IButton.class).clickAndWait();
        return randomNewsletter.findChildElement(By.xpath("//h2"), ITextBox.class).getText();
    }
    public boolean isEmailFormDisplayed(){ return emailForm.state().waitForDisplayed(); }

    public void enterEmailSubsForm(String email){
        inputBoxEmail.type(email);
    }
    public void clickSubmitButton(){ btnSubmitEmail.clickAndWait();}

    public void openPreviewOfNewsletter(String selectedNewsletter) {
        String xpath = "//div[contains(@class, 'bg-white')]//h2[contains(text(), '%s')]//following-sibling::a";
        IButton newsletterPreviewLink = getElementFactory().getButton(
                By.xpath(String.format(xpath, selectedNewsletter)), "newsletter preview link");

        newsletterPreviewLink.click();
    }

    public boolean previewForNewsletterIsOpen(String newsletterName) {
        if (!openNewsletterPreview.state().waitForDisplayed()) {
            return false;
        }
        return StringUtils.idContainsNewsletterName(newsletterName, openNewsletterPreview.getAttribute("id"));
    }

    public String getUnsubscribeUrlFromNewsletterPreview() {
        String parentId = openNewsletterPreview.getAttribute("id");
        String xpath = "//div[@id='%s']//iframe";
        IButton newsletterPreviewIframe = getElementFactory().getButton(By.xpath(String.format(xpath, parentId)), "iframe inside newsletter preview");

        AqualityServices.getBrowser().getDriver().switchTo().frame(newsletterPreviewIframe.getElement());
        ILink unsubscribeLink = getElementFactory().getLink(By.xpath("//a[contains(text(), 'unsubscribe')]"), "unsubscribe link");
        String unsubscribeUrl = unsubscribeLink.getHref();
        AqualityServices.getBrowser().getDriver().switchTo().defaultContent();

        return unsubscribeUrl;
    }

}
