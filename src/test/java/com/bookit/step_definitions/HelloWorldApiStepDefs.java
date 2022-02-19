package com.bookit.step_definitions;

import com.bookit.utilities.ConfigurationReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
public class HelloWorldApiStepDefs {
    Response response;

    @Then("status code is {int}")
    public void status_code_is(Integer expStatusCode) {
       assertThat(response.statusCode(),equalTo(expStatusCode));

    }

    @Then("response body contains {string}")
    public void response_body_contains(String message) {
        assertThat(response.body().path("message"),equalTo(message));

    }

    @Given("user sends get request to hello world api")
    public void userSendsGetRequestToHelloWorldApi() {
        response = given().accept(ContentType.JSON)
                .when().get(ConfigurationReader.getProperty("hello.world.api"));
    }
}
