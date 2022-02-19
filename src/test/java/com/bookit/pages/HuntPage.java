package com.bookit.pages;

import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HuntPage extends BasePage{
    
    @FindBy (id = "mat-input-0")
    public WebElement dateField;

    @FindBy(id = "mat-select-0")
    public WebElement fromField;

    @FindBy(id = "mat-select-1")
    public WebElement toField;

    @FindBy(xpath = "//mat-icon[.='search']")
    public WebElement searchBtn;

    public void selectStartTime(String startTime) {
        fromField.click();
        BrowserUtils.waitFor(2);
        Driver.getDriver().findElement(By.xpath("//span[contains(text(),'" + startTime + "')]")).click();
    }

    public void selectFinishTime(String endTime) {
        toField.click();
        BrowserUtils.waitFor(2);
        Driver.getDriver().findElement(By.xpath("//span[contains(text(),'" + endTime + "')]")).click();
    }


}
