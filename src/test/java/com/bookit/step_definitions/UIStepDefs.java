package com.bookit.step_definitions;

import com.bookit.pages.*;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.Driver;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

public class UIStepDefs {
    WebDriver driver = Driver.getDriver();
    HomePage homePage = new HomePage();
    RoomsPage roomsPage = new RoomsPage();
    static List<String> uiAvailableRooms;
    @Given("User logged in to Bookit app as teacher role")
    public void user_logged_in_to_Bookit_app_as_teacher_role() {
        driver.get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.logIn(Environment.TEACHER_EMAIL,Environment.TEACHER_PASSWORD);
    }
    @Given("User is on self page")
    public void user_is_on_self_page() {

        homePage.gotoSelf();
    }
    @Given("User logged in to Bookit app as team lead role")
    public void user_logged_in_to_Bookit_app_as_team_lead_role() {
        driver.get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.logIn(Environment.LEADER_EMAIL,Environment.LEADER_PASSWORD);
    }

    @When("User goes to room hunt page")
    public void user_goes_to_room_hunt_page() {
        homePage.huntMenu.click();

    }

    @When("User searches for room with date:")
    public void user_searches_for_room_with_date(Map<String, String> inputMap) {
        System.out.println("inputMap = " + inputMap);
        HuntPage huntPage = new HuntPage();
        huntPage.dateField.sendKeys(inputMap.get("date"));
        huntPage.selectStartTime(inputMap.get("from"));
        huntPage.selectFinishTime(inputMap.get("to"));
        huntPage.searchBtn.click();
    }

    @Then("User should see available rooms")
    public void user_should_see_available_rooms() {
        assertThat(roomsPage.availableRooms.size(),greaterThan(0));
        uiAvailableRooms = BrowserUtils.getElementsText(roomsPage.availableRoomNames);
        System.out.println("roomsList = " + uiAvailableRooms);
        assertThat(uiAvailableRooms.size(),greaterThan(0));
    }

}
