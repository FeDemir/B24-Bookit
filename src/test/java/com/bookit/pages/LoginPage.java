package com.bookit.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{
    @FindBy (name = "email")
    public WebElement email;

    @FindBy (name = "password")
    public WebElement password;

    @FindBy (tagName = "button")
    public WebElement singInBtn;
    public void logIn(String email, String password){
        this.email.sendKeys(email);
        this.password.sendKeys(password);
        this.singInBtn.click();
    }
}
