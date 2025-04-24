package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BlazeDemoTest {

    @Test
    public void flightBookingFlow() {
        RestAssured.baseURI = "https://blazedemo.com";

        // 1. Load landing page
        Response landing = given().when().get("/index.php");
        Assert.assertEquals(landing.getStatusCode(), 200);
        System.out.println("Landing page loaded.");

        // 2. Search for flights from Boston to London
        Response flightsPage = given()
                .formParam("fromPort", "Boston")
                .formParam("toPort", "London")
                .when()
                .post("/reserve.php");
        Assert.assertTrue(flightsPage.asString().contains("Flights from Boston to London"));
        System.out.println("Flight list retrieved.");

        // 3. Extract lowest cost flight
        String responseBody = flightsPage.asString();
        String cheapestFlightId = responseBody.split("Choose This Flight")[0].split("flight=")[1].split("\"")[0];
        System.out.println("Choosing flight ID: " + cheapestFlightId);

        // 4. Select that flight (GET request to purchase page)
        Response purchasePage = given()
                .queryParam("flight", cheapestFlightId)
                .when()
                .get("/purchase.php");
        Assert.assertTrue(purchasePage.asString().contains("Your flight from"));
        System.out.println("Purchase page loaded.");

        // 5. Submit passenger details and purchase
        Response confirmationPage = given()
                .formParam("inputName", "John Doe")
                .formParam("address", "123 Main St")
                .formParam("city", "New York")
                .formParam("state", "NY")
                .formParam("zipCode", "10001")
                .formParam("cardType", "Visa")
                .formParam("creditCardNumber", "4111111111111111")
                .formParam("creditCardMonth", "12")
                .formParam("creditCardYear", "2025")
                .formParam("nameOnCard", "John Doe")
                .formParam("rememberMe", "on")
                .when()
                .post("/confirmation.php");

        Assert.assertTrue(confirmationPage.asString().contains("Thank you for your purchase today!"));
        System.out.println("Booking confirmed.");
    }
}
