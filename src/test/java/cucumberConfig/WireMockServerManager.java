package cucumberConfig;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.get;


public class WireMockServerManager {
    private static WireMockServer wireMockServer;

    public static void startWireMockServer() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
            wireMockServer.start();
            WireMock.configureFor("localhost", 8081);
        }
    }

    public static void setupMockResponses() {
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/verifyAccount"))
                .withRequestBody(WireMock.containing("001"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\", \"name\":\"Arun Basil\"}")
                        .withStatus(200))
        );

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/verifyAccount"))
                .withRequestBody(WireMock.containing("002"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\", \"name\":\"Ann Basil\"}")
                        .withStatus(200))
        );

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/verifyAccount"))
                .withRequestBody(WireMock.containing("003"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"error\", \"message\":\"Bad Request for Account ID 003\"}")
                        .withStatus(400))
        );

        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/verifyAccount"))
                .withRequestBody(WireMock.containing("004"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"error\", \"message\":\"Internal Server Error for Account ID 004\"}")
                        .withStatus(500))
        );

        WireMock.stubFor(get(urlMatching("/verifyAccount/eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTE0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"customerNumber\": 987652,"
                                + "\"account\": {"
                                + "    \"id\": \"eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTE0\","
                                + "    \"accountNumber\": \"020512000867932\","
                                + "    \"accountNumberFormatted\": \"02-0512-000867-932\""
                                + "},"
                                + "\"accountNames\": ["
                                + "    {\"customerNumber\": 111113, \"accountName\": \"Alice Smith\"},"
                                + "    {\"customerNumber\": 222223, \"accountName\": \"Bob Smith\"},"
                                + "    {\"customerNumber\": 333334, \"accountName\": \"Carol Smith\"}"
                                + "]"
                                + "}")));

        WireMock.stubFor(get(urlMatching("/verifyAccount/eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"customerNumber\": 987653,"
                                + "\"account\": {"
                                + "    \"id\": \"eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEz\","
                                + "    \"accountNumber\": \"020512000867931\","
                                + "    \"accountNumberFormatted\": \"02-0512-000867-931\""
                                + "},"
                                + "\"accountNames\": ["
                                + "    {\"customerNumber\": 111112, \"accountName\": \"Arun Basil\"},"
                                + "    {\"customerNumber\": 222221, \"accountName\": \"Ann Basil\"}"
                                + "]"
                                + "}")));

        WireMock.stubFor(get(urlMatching("/verifyAccount/eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"customerNumber\": 987654,"
                                + "\"account\": {"
                                + "    \"id\": \"eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEy\","
                                + "    \"accountNumber\": \"020512000867933\","
                                + "    \"accountNumberFormatted\": \"02-0512-000867-933\""
                                + "},"
                                + "\"accountNames\": ["
                                + "    {\"customerNumber\": 111111, \"accountName\": \"John Smith\"},"
                                + "    {\"customerNumber\": 222222, \"accountName\": \"Jane Smith\"},"
                                + "    {\"customerNumber\": 333333, \"accountName\": \"James Smith\"},"
                                + "    {\"customerNumber\": 444444, \"accountName\": \"Joe Smith\"}"
                                + "]"
                                + "}")));
    }

    public static void stopWireMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
