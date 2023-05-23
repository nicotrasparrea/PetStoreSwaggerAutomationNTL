package stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.JsonUtils;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class PetImplementation {

    private static final String DATA_PATH = "src/test/resources/data/pet/";
    private static final String BODY_ADD_FILEPATH = "bodyAddPetRequest.json";
    private static final String FIND_BY_STATUS_ENDPOINT = "findByStatus";
    private String jsonId;

    Response validationResponse;
    Response addResponse;

    @Before("@petSuite")
    public void before() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet/";
    }

    @And("we validate the response is {int} for pet")
    public void validateResposeCode(int responseCode) {
        if (validationResponse == null) {
            validationResponse = addResponse;
        }
        assertEquals("The response code is incorrect",
                responseCode, validationResponse.statusCode());
    }

    @Given("we send the post request that adds a pet with name {string}")
    public void addPetWithName(String petName) throws IOException {
        // Add given petName to json
        // With org.json
//        String jsonString = JsonUtils.readFileAsString(DATA_PATH + fileName);
//        JSONObject jsonObject = new JSONObject(jsonString);
//        jsonObject.put("name", petName);

        // With jackson-databind
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(JsonUtils.readFileAsString(DATA_PATH + BODY_ADD_FILEPATH));
        ((ObjectNode) jsonObject).put("name", petName);

        addResponse = given().log().all()
                .contentType(ContentType.JSON).body(jsonObject.toString()).post();
    }

    @And("we validate the body contains key name")
    public void bodyContainsId() {
        validationResponse.then().body("$", hasKey("id"));
    }

    @Then("we validate the body response contains the pet name {string}")
    public void validateBodyName(String expectedPetName) {
        JsonPath jsonPathPets = new JsonPath(validationResponse.body().asString());
        String jsonPet = jsonPathPets.getString("name");

        assertEquals("The value of the name field does not match",
                expectedPetName, jsonPet);
    }

    @When("we send the put request that updates pets with new name {string}")
    public void weSendThePutRequestThatUpdatesPetsWithNewName(String updateName) throws IOException {
        // Add given petName to response json
        String jsonString = validationResponse.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(jsonString);
        ((ObjectNode) jsonObject).put("name", updateName);

        validationResponse = given().log().all()
                .contentType(ContentType.JSON).body(jsonObject.toString()).put();
    }

    @Given("we send the post request that adds a pet with name {string} and status {string}")
    public void weSendThePostRequestThatAddsAPetWithNameAndStatus(String petName, String petStatus) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(JsonUtils.readFileAsString(DATA_PATH + BODY_ADD_FILEPATH));
        ((ObjectNode) jsonObject).put("name", petName).put("status", petStatus);

        addResponse = given().log().all().contentType(ContentType.JSON)
                .body(jsonObject.toString()).post();
    }

    @When("we send the get request that returns the pets by status {string}")
    public void weSendTheGetRequestThatReturnsThePetsByStatus(String petStatus) {
        validationResponse = given().log().all()
                .param("status", petStatus)
                .get(FIND_BY_STATUS_ENDPOINT);
    }

    @Then("we validate the body response contains objects with status {string}")
    public void weValidateTheBodyResponseContainsObjectsWithStatus(String petStatus) {
        // Validar usando assertTrue y recorriendo la lista, o de esta forma?
        validationResponse.then().log().all()
                .body("status", everyItem(equalTo(petStatus)));
    }

    @Then("we validate the body response contains the pet name {string} in the list")
    public void weValidateTheBodyResponseContainsThePetNameInTheList(String petName) {
        // Validar usando assertTrue y recorriendo la lista, o de esta forma?
        validationResponse.then().log().all()
                .body("name", hasItem(petName));
    }

    @When("we send the get request that returns the pet filtered by ID")
    public void getById() {
        JsonPath jsonPathPets = new JsonPath(addResponse.body().asString());
        jsonId = jsonPathPets.getString("id");
        validationResponse = given().log().all()
                .get(jsonId);
    }

    @Then("we validate the body response contains the expected ID")
    public void containsId() {
        JsonPath jsonPathPets = new JsonPath(validationResponse.body().asString());
        String jsonActualId = jsonPathPets.getString("id");

        assertEquals("ERROR: Pet IDs dont match",
                jsonId, jsonActualId);
    }

    @When("we send the delete request that deletes a pet by an ID")
    public void weSendTheDeleteRequestThatDeletesAPetByAnID() {
        JsonPath jsonPathPets = new JsonPath(addResponse.body().asString());
        jsonId = jsonPathPets.getString("id");
        validationResponse = given().log().all()
                .delete(jsonId);
    }

    @Then("we validate the body response contains the same ID")
    public void weValidateTheBodyResponseContainsTheSameID() {
        JsonPath jsonPathPets = new JsonPath(validationResponse.body().asString());
        String actualId = jsonPathPets.getString("message");
        assertEquals("ERROR: Pet IDs dont match",
                jsonId, actualId);
    }

    @After("@petSuite and not @delete")
    public void after() {
        JsonPath jsonPathPets = new JsonPath(addResponse.body().asString());
        String jsonIdCreate = jsonPathPets.getString("id");
        given().log().all().delete(jsonIdCreate);
    }
}
