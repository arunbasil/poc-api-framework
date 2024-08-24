package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import com.fasterxml.jackson.databind.JsonNode;
import api.ApiClient;
import builder.POCAccountVerificationRequestBuilder;
import cucumberConfig.WireMockServerManager;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JointSharingSteps {
    private Response response;
    private static ApiClient apiClient;

    @BeforeAll
    public static void setup() {
        WireMockServerManager.startWireMockServer();
        WireMockServerManager.setupMockResponses();

    }

    @AfterAll
    public static void teardown() {
        WireMockServerManager.stopWireMockServer();
    }

    @Given("^a name sharing request is made for account \"([^\"]*)\"$")
    public void sendAccountVerificationRequest(String accountNumber1) {
        response = given()
                .baseUri("http://localhost:8081/verifyAccount")
                .header("Content-Type", "application/json")
                .when()
                .get("/%s".formatted(accountNumber1))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Then("^the joint customer number corresponds to \"([^\"]*)\"$")
    public void verifyJointCustomerNumber(String expectedJointCustomerNumber) {
        String actualJointCustomerNumber = response.jsonPath().getString("customerNumber");
        assertEquals(expectedJointCustomerNumber, actualJointCustomerNumber, "Joint customer number did not match!");
    }

    @And("^the account number corresponds to \"([^\"]*)\"$")
    public void verifyAccountNumber(String expectedAccountNumber) {
        String actualAccountNumber = response.jsonPath().getString("account.accountNumber");
        assertEquals(expectedAccountNumber, actualAccountNumber, "Account number did not match!");
    }

    @And("^the formatted account number corresponds to \"([^\"]*)\"$")
    public void verifyFormattedAccountNumber(String expectedFormattedAccountNumber) {
        String actualFormattedAccountNumber = response.jsonPath().getString("account.accountNumberFormatted");
        assertEquals(expectedFormattedAccountNumber, actualFormattedAccountNumber, "Formatted account number did not match!");
    }

    @Then("^the following customer details are correct:$")
    public void verifyCustomerDetails(DataTable customerDetails) {
        List<Map<String, String>> detailsList = customerDetails.asMaps(String.class, String.class);
        List<JsonNode> accountNames = response.jsonPath().getList("accountNames", JsonNode.class);

        assertEquals(detailsList.size(), accountNames.size(), "Mismatch in number of customer details");

        for (int i = 0; i < detailsList.size(); i++) {
            Map<String, String> expectedDetails = detailsList.get(i);
            String expectedCustomerNumber = expectedDetails.get("customerNumber");
            String expectedAccountName = expectedDetails.get("accountName");

            String actualCustomerNumber = accountNames.get(i).get("customerNumber").asText();
            String actualAccountName = accountNames.get(i).get("accountName").asText();

            assertEquals(expectedCustomerNumber, actualCustomerNumber, "Customer number did not match at index " + i);
            assertEquals(expectedAccountName, actualAccountName, "Account name did not match at index " + i);
        }
    }
}
