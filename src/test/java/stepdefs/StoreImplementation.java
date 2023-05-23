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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreImplementation {

    private static final String DATA_PATH = "src/test/resources/data/store/";
    private static final String BODY_ADD_FILEPATH = "bodyAddOrderRequest.json";
    private static final String ORDER_ENDPOINT = "order/";
    private static final String INVENTORY_ENDPOINT = "inventory";
    private String currentDate;
    private String jsonId;

    Response validationResponse;
    Response addResponse;

    @Given("we send the post request that adds an order with pet ID {long}")
    public void addOrderPetId(long petId) throws IOException {
        // Pagina no añade fecha de pedido automaticamente
        LocalDate now = LocalDate.now();
        currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(JsonUtils.readFileAsString(DATA_PATH + BODY_ADD_FILEPATH));
        ((ObjectNode) jsonObject).put("petId", petId)
                .put("shipDate", currentDate);

        addResponse = given().log().all().contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post(ORDER_ENDPOINT);
        SharedFields.setResponse(addResponse);
    }

    @And("we validate the response is {int} for store")
    public void validateResponseCode(int responseCode) {
        if (validationResponse == null) {
            validationResponse = addResponse;
        }
        assertEquals("The response code is incorrect",
                responseCode, validationResponse.statusCode());
    }

    @And("we validate the body contains a ship date with same date as current")
    public void weValidateTheBodyContainsAShipDateWithSameDateAsCurrent() {
        JsonPath jsonPathOrder = new JsonPath(validationResponse.body().asString());
        String shipDate = jsonPathOrder.getString("shipDate");

        assertTrue("ERROR: Expected date: " + currentDate + " isnt present in ship date: " + shipDate,
                shipDate.contains(currentDate));
    }

    @Then("we validate the body response contains the pet ID {long}")
    public void weValidateTheBodyResponseContainsThePetID(long petId) {
        JsonPath jsonPathOrder = new JsonPath(validationResponse.body().asString());
        long actualPetId = jsonPathOrder.getLong("petId");

        assertEquals("ERROR: Pet IDs dont match",
                petId, actualPetId);
    }

    @When("we send the get request that returns an order by ID")
    public void weSendTheGetRequestThatReturnsAnOrderByID() {
        JsonPath jsonPathOrder = new JsonPath(addResponse.body().asString());
        jsonId = jsonPathOrder.getString("id");
        validationResponse = given().log().all()
                .get(ORDER_ENDPOINT + jsonId);
    }

    @Then("we validate the body response contains the order ID")
    public void weValidateTheBodyResponseContainsTheOrderID() {
        JsonPath jsonPathOrder = new JsonPath(addResponse.body().asString());
        String jsonIdActual = jsonPathOrder.getString("id");

        assertEquals("ERROR: Order IDs dont match",
                jsonId, jsonIdActual);
    }

    @When("we send the delete request that deletes an order by an ID")
    public void weSendTheDeleteRequestThatDeletesAnOrderByAnID() {
        JsonPath jsonPathOrder = new JsonPath(addResponse.body().asString());
        jsonId = jsonPathOrder.getString("id");
        validationResponse = given().log().all()
                .delete(ORDER_ENDPOINT + jsonId);
    }

    @Given("we send the get request that returns a list of status")
    public void weSendTheGetRequestThatReturnsAListOfStatus() {
        validationResponse = given().log().all()
                .get(INVENTORY_ENDPOINT);
    }

    @Then("we validate the response body contains the status {string}")
    public void weValidateTheResponseBodyContainsTheStatus(String status) {
        String responseBody = validationResponse.body().asString();
        assertTrue("ERROR: Response does not contain the status: " + status,
                responseBody.contains(status));
    }
}
