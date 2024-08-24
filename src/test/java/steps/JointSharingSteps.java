package steps;

import api.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumberConfig.WireMockServerManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.GetNameSharingRequest.AccountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JointSharingSteps {

    private static final Logger logger = LoggerFactory.getLogger(JointSharingSteps.class);

    private Response response;
    private static ApiClient apiClient;
    private AccountResponse accountResponse;

    @BeforeAll
    public static void setup() {
        logger.info("Starting WireMock server and setting up mock responses.");
        WireMockServerManager.startWireMockServer();
        WireMockServerManager.setupMockResponses();
    }

    @AfterAll
    public static void teardown() {
        logger.info("Stopping WireMock server.");
        WireMockServerManager.stopWireMockServer();
    }

    @Step("Send a name sharing request for account: {accountNumber1}")
    @Description("This step sends a request to verify the account information based on the account number.")
    @Given("^a name sharing request is made for account \"([^\"]*)\"$")
    public void sendAccountVerificationRequest(String accountNumber1) {
        try {
            response = given()
                    .baseUri("http://localhost:8081/verifyAccount")
                    .header("Content-Type", "application/json")
                    .when()
                    .get("/%s".formatted(accountNumber1))
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            logger.info("Request sent successfully for account: {}", accountNumber1);

            // Map the response to the AccountResponse model
            ObjectMapper mapper = new ObjectMapper();
            accountResponse = mapper.readValue(response.getBody().asString(), AccountResponse.class);

        } catch (Exception e) {
            logger.error("Failed to send request or map the response for account: {}", accountNumber1, e);
            fail("Failed to map the response to AccountResponse model.");
        }

        // Attach the response body to the Allure report
        attachResponseBody(response.getBody().asString());
    }

    @Step("Verify the joint customer number corresponds to: {expectedJointCustomerNumber}")
    @Description("This step verifies that the joint customer number in the response matches the expected value.")
    @Then("^the joint customer number corresponds to \"([^\"]*)\"$")
    public void verifyJointCustomerNumber(String expectedJointCustomerNumber) {
        String actualJointCustomerNumber = accountResponse.customerNumber();
        assertEquals(expectedJointCustomerNumber, actualJointCustomerNumber, "Joint customer number did not match!");
        logger.info("Verified joint customer number: expected = {}, actual = {}", expectedJointCustomerNumber, actualJointCustomerNumber);
    }

    @Step("Verify the account number corresponds to: {expectedAccountNumber}")
    @Description("This step verifies that the account number in the response matches the expected value.")
    @And("^the account number corresponds to \"([^\"]*)\"$")
    public void verifyAccountNumber(String expectedAccountNumber) {
        String actualAccountNumber = accountResponse.account().accountNumber();
        assertEquals(expectedAccountNumber, actualAccountNumber, "Account number did not match!");
        logger.info("Verified account number: expected = {}, actual = {}", expectedAccountNumber, actualAccountNumber);
    }

    @Step("Verify the formatted account number corresponds to: {expectedFormattedAccountNumber}")
    @Description("This step verifies that the formatted account number in the response matches the expected value.")
    @And("^the formatted account number corresponds to \"([^\"]*)\"$")
    public void verifyFormattedAccountNumber(String expectedFormattedAccountNumber) {
        String actualFormattedAccountNumber = accountResponse.account().accountNumberFormatted();
        assertEquals(expectedFormattedAccountNumber, actualFormattedAccountNumber, "Formatted account number did not match!");
        logger.info("Verified formatted account number: expected = {}, actual = {}", expectedFormattedAccountNumber, actualFormattedAccountNumber);
    }

    @Step("Verify that the following customer details are correct")
    @Description("This step verifies that the customer details in the response match the expected values.")
    @Then("the following customer details are correct:")
    public void verifyCustomerDetails(DataTable customerDetails) {
        List<Map<String, String>> detailsList = customerDetails.asMaps(String.class, String.class);
        var accountNames = accountResponse.accountNames();

        int detailsCount = detailsList.size();
        int accountNamesCount = accountNames.size();

        for (int i = 0, j = 0; i < detailsCount && j < accountNamesCount; i++) {
            Map<String, String> expectedDetails = detailsList.get(i);

            String expectedCustomerNumber = Objects.requireNonNullElse(expectedDetails.get("customerNumber"), "").trim();
            String expectedAccountName = Objects.requireNonNullElse(expectedDetails.get("accountName"), "").trim();

            if (expectedCustomerNumber.isEmpty() || expectedAccountName.isEmpty()) {
                logger.warn("Skipping empty customer details at index {}", i);
                continue;
            }

            String actualCustomerNumber = accountNames.get(j).customerNumber().trim();
            String actualAccountName = accountNames.get(j).accountName().trim();

            assertEquals(expectedCustomerNumber, actualCustomerNumber, "Customer number did not match at index " + j);
            assertEquals(expectedAccountName, actualAccountName, "Account name did not match at index " + j);

            logger.info("Verified customer details at index {}: customer number = {}, account name = {}", j, actualCustomerNumber, actualAccountName);

            j++;
        }

        long nonEmptyDetailsCount = detailsList.stream().filter(detail ->
                !Objects.requireNonNullElse(detail.get("customerNumber"), "").isEmpty() &&
                        !Objects.requireNonNullElse(detail.get("accountName"), "").isEmpty()).count();

        assertEquals(nonEmptyDetailsCount, accountNamesCount,
                "Mismatch in number of customer details: Expected " + nonEmptyDetailsCount + " but found " + accountNamesCount);
        logger.info("Verified total number of customer details.");
    }

    @Step("Verify the customer {customerNumber} has the name {expectedAccountName}")
    @Description("This step verifies that the specific customer number corresponds to the expected account name.")
    @And("the customer {string} should have the name {string}")
    public void verifyCustomerName(String customerNumber, String expectedAccountName) {
        var accountNames = accountResponse.accountNames();
        for (var accountName : accountNames) {
            if (accountName.customerNumber().equals(customerNumber)) {
                assertEquals(expectedAccountName, accountName.accountName());
                logger.info("Verified customer number {} has name {}", customerNumber, expectedAccountName);
                return;
            }
        }
        logger.error("Customer number {} not found in the response", customerNumber);
        fail("Customer number " + customerNumber + " not found in the response");
    }

    @Attachment(value = "Response JSON", type = "application/json")
    public String attachResponseBody(String responseBody) {
        return responseBody;
    }
}
