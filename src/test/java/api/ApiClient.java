package api;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response sendRequest(String endpoint, String body) {
        return given()
                .baseUri(baseUrl)
                .log().headers()
                .contentType("application/json")
                .body(body)
                .when()
                .post(endpoint);
    }
}