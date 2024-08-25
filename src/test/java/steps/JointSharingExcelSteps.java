package steps;

import api.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumberConfig.WireMockServerManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.GetNameSharingRequest.AccountName;
import model.GetNameSharingRequest.AccountResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExcelReader;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JointSharingExcelSteps {

    private static final Logger logger = LoggerFactory.getLogger(JointSharingSteps.class);
    private static final String BASE_URI = "http://localhost:8081/verifyAccount";

    private Response response;

    private AccountResponse accountResponse;
    private List<Map<String, String>> excelData;
    private final List<String> errors = new ArrayList<>();

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

    @Given("data is loaded from excel {string} and sheet {string}")
    public void loadDataFromExcel(String filePath, String sheetName) {
        var path = Paths.get(filePath);
        excelData = ExcelReader.getDataFromExcel(path, sheetName);
        logger.info("Data loaded from Excel: {} rows", excelData.size());
    }

    @Then("perform account verification")
    public void performAccountVerification() {
        for (int rowIndex = 0; rowIndex < excelData.size(); rowIndex++) {
            Map<String, String> data = excelData.get(rowIndex);

            verifyAllAccountDetails(data, rowIndex + 1);
        }

        if (!errors.isEmpty()) {
            errors.forEach(logger::error);
            fail("There were errors in account verification:\n" + String.join("\n", errors));
        }
    }

    @Step("Verify all account details for account {accountNumber1}")
    private void verifyAllAccountDetails(Map<String, String> data, int rowIndex) {
        String accountNumber1 = data.get("accountNumber1");

        sendAccountVerificationRequest(accountNumber1);

        verifyJointCustomerNumber(data.get("jointCustomerNumber"), rowIndex);
        verifyAccountNumber(data.get("accountNumber"), rowIndex);
        verifyFormattedAccountNumber(data.get("formattedAccountNumber"), rowIndex);
        verifyCustomerDetails(data, rowIndex);
    }

    @Step("Send a name sharing request for account: {accountNumber1}")
    @Description("This step sends a request to verify the account information based on the account number.")
    private void sendAccountVerificationRequest(String accountNumber1) {
        try {
            response = given()
                    .baseUri(BASE_URI)
                    .header("Content-Type", "application/json")
                    .when()
                    .get("/%s".formatted(accountNumber1))
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            logger.info("Request sent successfully for account: {}", accountNumber1);

            ObjectMapper mapper = new ObjectMapper();
             accountResponse = mapper.readValue(response.getBody().asString(), AccountResponse.class);
        } catch (Exception e) {
            logger.error("Failed to send request or map the response for account: {}", accountNumber1, e);
            fail("Failed to map the response to AccountResponse model.");
        }

        attachResponseBody(response.getBody().asString());
    }

    private void verifyJointCustomerNumber(String expectedJointCustomerNumber, int rowIndex) {
        String actualJointCustomerNumber = accountResponse.customerNumber();
        if (!expectedJointCustomerNumber.equals(actualJointCustomerNumber)) {
            errors.add(String.format("Row %d: Joint customer number mismatch. Expected: %s, Actual: %s",
                    rowIndex, expectedJointCustomerNumber, actualJointCustomerNumber));
        } else {
            logger.info("Verified joint customer number: expected = {}, actual = {}", expectedJointCustomerNumber, actualJointCustomerNumber);
        }
    }

    private void verifyAccountNumber(String expectedAccountNumber, int rowIndex) {
        String actualAccountNumber = accountResponse.account().accountNumber();
        if (!expectedAccountNumber.equals(actualAccountNumber)) {
            errors.add(String.format("Row %d: Account number mismatch. Expected: %s, Actual: %s",
                    rowIndex, expectedAccountNumber, actualAccountNumber));
        } else {
            logger.info("Verified account number: expected = {}, actual = {}", expectedAccountNumber, actualAccountNumber);
        }
    }

    private void verifyFormattedAccountNumber(String expectedFormattedAccountNumber, int rowIndex) {
        String actualFormattedAccountNumber = accountResponse.account().accountNumberFormatted();
        if (!expectedFormattedAccountNumber.equals(actualFormattedAccountNumber)) {
            errors.add(String.format("Row %d: Formatted account number mismatch. Expected: %s, Actual: %s",
                    rowIndex, expectedFormattedAccountNumber, actualFormattedAccountNumber));
        } else {
            logger.info("Verified formatted account number: expected = {}, actual = {}", expectedFormattedAccountNumber, actualFormattedAccountNumber);
        }
    }

    private void verifyCustomerDetails(Map<String, String> data, int rowIndex) {
        for (int i = 1; i <= 4; i++) {
            String expectedCustomerNumber = data.get("customerNumber" + i);
            String expectedAccountName = data.get("accountName" + i);
//         Check excel for empty or null, if found will exit the loop
            if (expectedCustomerNumber == null || expectedCustomerNumber.isEmpty() ||
                    expectedAccountName == null || expectedAccountName.isEmpty()) {
                continue;
            }

            var accountNameMap = accountResponse.accountNames().stream()
                    .collect(Collectors.toMap(AccountName::customerNumber, AccountName::accountName));

            String actualAccountName = accountNameMap.get(expectedCustomerNumber);

            if (actualAccountName == null) {
                errors.add(String.format("Row %d: Expected customer number %s not found. Available customer numbers: %s",
                        rowIndex, expectedCustomerNumber, accountNameMap.keySet()));
            } else if (!expectedAccountName.equals(actualAccountName)) {
                errors.add(String.format("Row %d: Mismatch for customer number %s: Expected account name %s but found %s",
                        rowIndex, expectedCustomerNumber, expectedAccountName, actualAccountName));
            } else {
                logger.info("Verified customer number {} has name {}", expectedCustomerNumber, expectedAccountName);
            }
        }
    }

    @Attachment(value = "Response JSON", type = "application/json")
    public String attachResponseBody(String responseBody) {
        return responseBody;
    }
}
