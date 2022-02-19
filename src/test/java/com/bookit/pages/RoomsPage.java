package com.bookit.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class RoomsPage extends BasePage{
    @FindBy (xpath = "//button[.='book']")
    public List<WebElement> availableRooms;

    @FindBy (xpath = "//p[@class='title is-size-4']")
    public List<WebElement> availableRoomNames;
}
