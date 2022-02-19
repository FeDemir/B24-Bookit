package com.bookit.pages;

import com.bookit.utilities.BrowserUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {
    @FindBy (linkText = "my")
    public WebElement myMenu;
    @FindBy (linkText = "hunt")
    public WebElement huntMenu;
    @FindBy (linkText = "self")
    public WebElement mySelf;
    public void gotoSelf(){
        BrowserUtils.highlight(myMenu);
        BrowserUtils.hover(myMenu);

        BrowserUtils.highlight(mySelf);
        mySelf.click();
    }

}
