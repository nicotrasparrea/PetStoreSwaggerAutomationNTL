package support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

public class Hooks {

    private static final String ORDER_ENDPOINT = "order/";

    @Before("@petSuite")
    public void beforePet() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet/";
    }

    @Before("@storeSuite")
    public void beforeStore() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/store/";
    }

    @Before("@userSuite")
    public void beforeUser() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/user/";
    }

    @After("@petSuite and not @delete")
    public void afterPet() {
        JsonPath jsonPathPets = new JsonPath(SharedFields.getResponse().body().asString());
        String jsonIdCreate = jsonPathPets.getString("id");
        given().log().all().delete(jsonIdCreate);
    }

    @After("@storeSuite and not @deleteOrderIdOK and not @findInventoriesStatus")
    public void afterStore() {
        JsonPath jsonPathOrder = new JsonPath(SharedFields.getResponse().body().asString());
        String jsonIdCreate = jsonPathOrder.getString("id");
        given().log().all().delete(ORDER_ENDPOINT + jsonIdCreate);
    }

    @After("@userSuite and not @deleteUser")
    public void after() {
        given().log().all().delete(SharedFields.getUsername());
    }
}
