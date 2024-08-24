package steps;

import api.ApiClient;
import builder.POCAccountVerificationRequestBuilder;
import cucumberConfig.WireMockServerManager;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.POCAccountVerificationRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class RequestAPISteps {

    private static ApiClient apiClient;
    private Response response;

    @BeforeAll
    public static void setup() {
        WireMockServerManager.startWireMockServer();
        WireMockServerManager.setupMockResponses();
//        String apiUrl = System.getProperty("api.url", "http://localhost:8081");
//        apiClient = new ApiClient(apiUrl);
    }

    @AfterAll
    public static void teardown() {
        WireMockServerManager.stopWireMockServer();
    }

    @When("a matching request is sent with {string}")
    public void aMatchingRequestIsSentWith(String accountID) {
//        POCAccountVerificationRequest request = POCAccountVerificationRequestBuilder
//                .buildPOCAccountVerificationRequest(accountID);
        var request = POCAccountVerificationRequest.of(accountID);

        try {
            response = given()
                    .contentType("application/json")
                    .body(request)
                    .when()
                    .post("http://localhost:8081/verifyAccount");
        } catch (Exception e) {
            System.err.println("Exception during API request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertNotNull(response, "Response is null, the request might not have been sent correctly.");
        assertThat(response.getStatusCode(), equalTo(statusCode));
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedValue) {
        assertNotNull(response, "Response is null, the request might not have been sent correctly.");
//
//        // Parse the response body as JSON
//        JsonPath jsonPath = response.jsonPath();

//        // Extract the 'name' field from the JSON response
//        String actualName = jsonPath.getString("name");
////
////        // Assert that the actual name matches the expected value
////        assertThat(actualName, equalTo(expectedValue));
//        // Parse the response body as a string
//        String responseBody = response.getBody().asString();
//
//        // Check if the response contains the expected value
////        assertThat(responseBody, containsString(expectedValue));
//        assertThat("Response body does not match the expected value.", actualName, equalTo(expectedValue));
// Extract the status code and response body once
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        if (statusCode == 200) {
            // Handle successful response
            String actualName = JsonPath.from(responseBody).getString("name");

            // Assert that the actual name matches the expected value
            assertThat("The actual name does not match the expected value.", actualName, equalTo(expectedValue));

        } else if (statusCode == 400 || statusCode == 500) {
            // Handle error response
            String expectedErrorMessage = (statusCode == 400) ?
                    "Bad Request for Account ID" : "Internal Server Error for Account ID";

            // Assert that the response body contains the expected error message
            assertThat("Unexpected response body for error status code.", responseBody, containsString(expectedErrorMessage));

        } else {
            // Handle unexpected status codes
            fail("Unexpected status code: " + statusCode);
        }


    }
}
