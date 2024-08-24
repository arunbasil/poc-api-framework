package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import com.fasterxml.jackson.databind.JsonNode;
import api.ApiClient;
import cucumberConfig.WireMockServerManager;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.qameta.allure.Attachment;
import io.restassured.response.Response;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Step("Send a name sharing request for account: {accountNumber1}")
    @Description("This step sends a request to verify the account information based on the account number.")
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

        // Attach the response body to the Allure report
        attachResponseBody(response.getBody().asString());
    }

    @Step("Verify the joint customer number corresponds to: {expectedJointCustomerNumber}")
    @Description("This step verifies that the joint customer number in the response matches the expected value.")
    @Then("^the joint customer number corresponds to \"([^\"]*)\"$")
    public void verifyJointCustomerNumber(String expectedJointCustomerNumber) {
        String actualJointCustomerNumber = response.jsonPath().getString("customerNumber");
        assertEquals(expectedJointCustomerNumber, actualJointCustomerNumber, "Joint customer number did not match!");
    }

    @Step("Verify the account number corresponds to: {expectedAccountNumber}")
    @Description("This step verifies that the account number in the response matches the expected value.")
    @And("^the account number corresponds to \"([^\"]*)\"$")
    public void verifyAccountNumber(String expectedAccountNumber) {
        String actualAccountNumber = response.jsonPath().getString("account.accountNumber");
        assertEquals(expectedAccountNumber, actualAccountNumber, "Account number did not match!");
    }

    @Step("Verify the formatted account number corresponds to: {expectedFormattedAccountNumber}")
    @Description("This step verifies that the formatted account number in the response matches the expected value.")
    @And("^the formatted account number corresponds to \"([^\"]*)\"$")
    public void verifyFormattedAccountNumber(String expectedFormattedAccountNumber) {
        String actualFormattedAccountNumber = response.jsonPath().getString("account.accountNumberFormatted");
        assertEquals(expectedFormattedAccountNumber, actualFormattedAccountNumber, "Formatted account number did not match!");
    }

    @Step("Verify that the following customer details are correct")
    @Description("This step verifies that the customer details in the response match the expected values.")
    @Then("the following customer details are correct:")
    public void verifyCustomerDetails(DataTable customerDetails) {
        List<Map<String, String>> detailsList = customerDetails.asMaps(String.class, String.class);
        List<JsonNode> accountNames = response.jsonPath().getList("accountNames", JsonNode.class);

        int detailsCount = detailsList.size();
        int accountNamesCount = accountNames.size();

        for (int i = 0, j = 0; i < detailsCount && j < accountNamesCount; i++) {
            Map<String, String> expectedDetails = detailsList.get(i);

            // Safely get and trim the expected values, handle potential nulls
            String expectedCustomerNumber = Objects.requireNonNullElse(expectedDetails.get("customerNumber"), "").trim();
            String expectedAccountName = Objects.requireNonNullElse(expectedDetails.get("accountName"), "").trim();

            // Skip if either customerNumber or accountName is empty in the expected details
            if (expectedCustomerNumber.isEmpty() || expectedAccountName.isEmpty()) {
                continue;
            }

            // Safely get and trim the actual values from JSON
            String actualCustomerNumber = accountNames.get(j).get("customerNumber").asText().trim();
            String actualAccountName = accountNames.get(j).get("accountName").asText().trim();

            // Perform the assertions
            assertEquals(expectedCustomerNumber, actualCustomerNumber, "Customer number did not match at index " + j);
            assertEquals(expectedAccountName, actualAccountName, "Account name did not match at index " + j);

            j++; // Increment only if we actually checked an account name
        }

        // Handle the case where there is a size mismatch between non-empty expected details and actual account names
        long nonEmptyDetailsCount = detailsList.stream().filter(detail ->
                !Objects.requireNonNullElse(detail.get("customerNumber"), "").isEmpty() &&
                        !Objects.requireNonNullElse(detail.get("accountName"), "").isEmpty()).count();

        assertEquals(nonEmptyDetailsCount, accountNamesCount,
                "Mismatch in number of customer details: Expected " + nonEmptyDetailsCount + " but found " + accountNamesCount);
    }

    @Step("Verify the customer {customerNumber} has the name {expectedAccountName}")
    @Description("This step verifies that the specific customer number corresponds to the expected account name.")
    @And("the customer {string} should have the name {string}")
    public void verifyCustomerName(String customerNumber, String expectedAccountName) {
        // Assuming the response contains a list of customer details, we would loop through and find the right customer
        List<Map<String, String>> accountNames = response.jsonPath().getList("accountNames");
        for (Map<String, String> accountName : accountNames) {
            if (accountName.get("customerNumber").equals(customerNumber)) {
                assertEquals(expectedAccountName, accountName.get("accountName"));
                return;
            }
        }
        fail("Customer number " + customerNumber + " not found in the response");
    }

    @Attachment(value = "Response JSON", type = "application/json")
    public String attachResponseBody(String responseBody) {
        return responseBody;
    }
}
