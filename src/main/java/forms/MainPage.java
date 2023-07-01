package forms;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class MainPage extends Form {
    public MainPage() {
        super(By.xpath("//a[@aria-label='Euronews Logo']//h1//*[name()='svg']"),"Euronews Main Page");
    }
    //------Locators---------
    private final IButton btnAcceptCookies = getElementFactory().getButton(
            By.xpath("//button[@id='didomi-notice-agree-button']"),"accept cookies button");
    private final IButton btnNewsLetter = getElementFactory().getButton(
            By.xpath("//span[@class='u-margin-start-1'][normalize-space()='Newsletters']"),"Newsletter button");

    //------Methods------------
    public void acceptCookies(){ btnAcceptCookies.clickAndWait();}
    public void goToNewsletterPage(){ btnNewsLetter.clickAndWait();}

}
