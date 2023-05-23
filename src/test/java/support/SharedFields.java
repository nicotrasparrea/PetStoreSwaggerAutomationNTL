package support;

import io.restassured.response.Response;

public class SharedFields {
    private static Response sharedResponse;
    private static String sharedUsername;

    public static Response getResponse(){
        return sharedResponse;
    }

    public static void setResponse(Response response){
        sharedResponse = response;
    }

    public static String getUsername(){
        return sharedUsername;
    }

    public static void setResponse(String username){
        sharedUsername = username;
    }
}
