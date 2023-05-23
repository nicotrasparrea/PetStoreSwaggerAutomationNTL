package stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import support.SharedFields;
import utils.JsonUtils;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;

public class UserImplementation {
    private static final String DATA_PATH = "src/test/resources/data/user/";
    private static final String BODY_ADD_FILEPATH = "bodyAddUserRequest.json";
    private static final String CREATE_ARRAY_ENDPOINT = "createWithArray";
    private String createdUsername;

    private Response validationResponse;
    private Response addResponse;

    @Given("we send the post request that adds a user with username {string}")
    public void weSendThePostRequestThatAddsAUserWithName(String username) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(JsonUtils.readFileAsString(DATA_PATH + BODY_ADD_FILEPATH));
        ((ObjectNode) jsonObject).put("username", username);

        addResponse = given().log().all().contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post();
        createdUsername = username;
        SharedFields.setResponse(createdUsername);
    }

    @And("we validate the response is {int} for user")
    public void validateResponseCode(int responseCode) {
        if (validationResponse == null) {
            validationResponse = addResponse;
        }
        assertEquals("The response code is incorrect",
                responseCode, validationResponse.statusCode());
    }

    @Then("we validate the body response contains the field {string}")
    public void weValidateTheBodyResponseContainsTheField(String field) {
        validationResponse.then().body("$", hasKey(field));
    }

    @Then("we validate the body response contains the username {string}")
    public void weValidateTheBodyResponseContainsTheUsername(String username) {
        getUserByUsername(username);
        JsonPath jsonPathUser = new JsonPath(validationResponse.body().asString());
        String actualUsername = jsonPathUser.getString("username");

        assertEquals("ERROR: Usernames do not match",
                username, actualUsername);
    }

    @When("we send the put request that updates users with new name {string}")
    public void weSendThePutRequestThatUpdatesUsersWithNewName(String updateUsername) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(JsonUtils.readFileAsString(DATA_PATH + BODY_ADD_FILEPATH));
        ((ObjectNode) jsonObject).put("username", updateUsername);

        validationResponse = given().log().all().contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .put(createdUsername);
        createdUsername = updateUsername;
        SharedFields.setResponse(createdUsername);
    }

    @When("we send the get request that returns the user filtered by username {string}")
    public void getUserByUsername(String username) {
        validationResponse = given().log().all()
                .get(username);
    }

    @Then("we validate the body response contains the user with expected ID")
    public void weValidateTheBodyResponseContainsTheUserWithID() {
        JsonPath jsonPathAddUser = new JsonPath(addResponse.body().asString());
        long expectedId = jsonPathAddUser.getLong("message");

        JsonPath jsonPathGetUser = new JsonPath(validationResponse.body().asString());
        long actualId = jsonPathGetUser.getLong("id");

        assertEquals("ERROR: User IDs do not match",
                expectedId, actualId);
    }

    @When("we send the delete request that deletes a  user by username {string}")
    public void weSendTheDeleteRequestThatDeletesAUserByUsername(String username) {
        validationResponse = given().log().all()
                .delete(username);
    }

    @Then("we validate the delete body response contains the username {string}")
    public void weValidateTheDeleteBodyResponseContainsTheUsername(String username) {
        JsonPath jsonPathUser = new JsonPath(validationResponse.body().asString());
        String actualUsername = jsonPathUser.getString("message");

        assertEquals("ERROR: Usernames do not match",
                username, actualUsername);
    }
}
