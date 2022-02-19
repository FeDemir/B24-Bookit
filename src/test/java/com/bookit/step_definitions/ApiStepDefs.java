package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import javax.xml.bind.SchemaOutputResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
public class ApiStepDefs {
    Response response;
    String accessToken;
    Map<String,String> newRecordMap;
    List<String> apiAvailableRooms;
    @Given("user logged in BookIt api as teacher role")
    public void user_logged_in_BookIt_api_as_teacher_role() {
        accessToken = BookItApiUtil.getAccessToken(Environment.TEACHER_EMAIL,Environment.TEACHER_PASSWORD);
        System.out.println("Environment.TEACHER_EMAIL = " + Environment.TEACHER_EMAIL);
        System.out.println("Environment.TEACHER_PASSWORD = " + Environment.TEACHER_PASSWORD);
        System.out.println("accessToken = " + accessToken);

    }

    @Given("User sends request  to {string}")
    public void user_sends_request_to(String path) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .when().get(Environment.BASE_URL+path);
        System.out.println("API End Point  = " + Environment.BASE_URL+path);
        response.then().log().all();
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expStatusCode) {
        assertThat(response.statusCode(),equalTo(expStatusCode));
    }

    @Then("content type is {string}")
    public void content_type_is(String expContent) {
        assertThat(response.contentType(),equalTo(expContent));
    }

    @And("role is {string}")
    public void roleIs(String expRole) {
        assertThat(response.body().path("role"),equalTo(expRole));
        assertThat(response.body().jsonPath().getString("role"),equalTo(expRole));

    }

    @Then("User should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {
        Map<String,String> apiUserMap = response.body().as(Map.class);
        String apiFullName = apiUserMap.get("firstName")+" "+apiUserMap.get("lastName");
        SelfPage selfPage = new SelfPage();
        String uiFullName = selfPage.name.getText();
        String uiRole = selfPage.role.getText();
        assertThat(uiFullName,equalTo(apiFullName));
        assertThat(uiRole,equalTo(apiUserMap.get("role")));
    }

    int newEntryId;

    @When("Users sends POST request to {string} with following info:")
    public void usersSendsPOSTRequestToWithFollowingInfo(String path, Map<String, String> newEntryInfo) {

        System.out.println("studentInfo = " + newEntryInfo);
        newRecordMap=newEntryInfo;
        if (path.contains("teams")) deleteTeamBeforeAdd(newEntryInfo.get("team-name"));
        response = given().accept(ContentType.JSON)
                //.and().contentType(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .and().queryParams(newEntryInfo)
                .when().post(Environment.BASE_URL+path);

        //Get new student's id from result of post return
        newEntryId = response.then().log().all()
                .and().extract().body().jsonPath().getInt("entryiId");
    }

    @And("User deletes previously created student")
    public void userDeletesPreviouslyCreatedStudent() {
        System.out.println("EXCLUDE STUDENT "+newEntryId);
        given().header("Authorization",accessToken)
                .when().delete(Environment.BASE_URL+"/api/students/"+newEntryId)
                .then().assertThat().statusCode(204).log().all();
    }

    @And("User sends GET request to {string} with {string}")
    public void userSendsGETRequestToWith(String path, String teamId) {
        response = given().accept(ContentType.JSON).log().all()
                .and().header("Authorization",accessToken)
                .and().pathParam("id",teamId)
                .when().get(Environment.BASE_URL + path);
    }

    @And("Team name should be {string} in response")
    public void teamNameShouldBeInResponse(String expTeamName) {
        response.prettyPrint();
        assertThat(response.path("name"),equalTo(expTeamName));
    }

    @And("Database query should have same {string} and {string}")
    public void databaseQueryShouldHaveSameAnd(String expTeamId, String expTeamName) {
        String query = "select  * from team where id="+expTeamId;
        Map<String,Object> actTeamInfo = DBUtils.getRowMap(query);
        System.out.println("actTeamInfo = " + actTeamInfo);
        assertThat(actTeamInfo.get("id"),equalTo(Long.parseLong(expTeamId)));
        assertThat(actTeamInfo.get("name"),equalTo(expTeamName));
    }



    @And("Database should persist same team info")
    public void databaseShouldPersistSameTeamInfo() {
        String query = "select  * from team where id="+newEntryId;
        Map<String,Object> actTeamInfo = DBUtils.getRowMap(query);
        System.out.println("actTeamInfo = " + actTeamInfo);
        assertThat(newRecordMap.get("team-name"),equalTo(actTeamInfo.get("name")));
        assertThat(newRecordMap.get("batch-number"),equalTo(actTeamInfo.get("batch_number")+""));
    }

    @And("User deletes previously created team")
    public void userDeletesPreviouslyCreatedTeam() {
        given().header("Authorization", accessToken)
            .when().delete(Environment.BASE_URL + "/api/teams/" + newEntryId)
            .then().log().all().assertThat().statusCode(200);//deletion code for team is 200

    }
    @Then("User logged in to Bookit api as team lead role")
    public void user_logged_in_to_Bookit_api_as_team_lead_role() {
        accessToken = BookItApiUtil.getAccessToken(Environment.LEADER_EMAIL,Environment.LEADER_PASSWORD);
        System.out.println("LEADER_EMAIL = " + Environment.LEADER_EMAIL);
        System.out.println("LEADER_PASSWORD = " + Environment.LEADER_PASSWORD);
        System.out.println("accessToken = " + accessToken);
    }

    @Then("User sends GET request to {string} with:")
    public void user_sends_GET_request_to_with(String endPoint, Map<String,String> queryParams) {
        response = given().accept(ContentType.JSON).log().all()
                .and().header("Authorization",accessToken)
                .and().queryParams(queryParams)
                .when().get(Environment.BASE_URL + endPoint);

    }

    @Then("available rooms in response should match UI results")
    public void available_rooms_in_response_should_match_UI_results() {
        JsonPath json = response.jsonPath();
        apiAvailableRooms = json.getList("name");
        System.out.println("roomsList = " + apiAvailableRooms);
        System.out.println("UI Rooms List = "+UIStepDefs.uiAvailableRooms);
        assertThat(apiAvailableRooms,equalTo(UIStepDefs.uiAvailableRooms));
    }

    @And("available rooms in database should match UI and API results")
    public void availableRoomsInDatabaseShouldMatchUIAndAPIResults() {
        String query = "select room.name from room inner join cluster on room.cluster_id = cluster.id where cluster.name='light-side'";
        List<String> dbAvailableRooms = new ArrayList<>();
        DBUtils.getColumnData(query, "name").forEach(name->dbAvailableRooms.add(name.toString()));
        System.out.println("dbAvailableRooms = " + dbAvailableRooms);
        assertThat(dbAvailableRooms,equalTo(apiAvailableRooms));
        assertThat(dbAvailableRooms,equalTo(UIStepDefs.uiAvailableRooms));
        assertThat(dbAvailableRooms,allOf(equalTo(apiAvailableRooms),equalTo(UIStepDefs.uiAvailableRooms)));

    }
    private void deleteTeamBeforeAdd(String teamName){
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .when().get(Environment.BASE_URL+"/api/teams");

        if (response.body().asString().contains(teamName))   {
            List<Map<String,Object>> teams = response.jsonPath().getList("");
            for(Map<String,Object> team:teams){
                if (team.get("name").equals(teamName)) {
                    System.out.println("Team with same name exists");
                    System.out.println(team.get("name")+"\t"+team.get("id"));
                    newEntryId = (int) team.get("id");
                    System.out.println("Team ID = "+teamName);
                    userDeletesPreviouslyCreatedTeam();
                    break;
                }
            }
        }
    }
}
