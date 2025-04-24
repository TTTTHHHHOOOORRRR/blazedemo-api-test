package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String flightsHtml = flightsPage.asString();
        Assert.assertTrue(flightsHtml.contains("Flights from Boston to London"));
        System.out.println("Flight list retrieved.");

        // 3. Extract a flight ID using regex (safe method)
        Pattern flightIdPattern = Pattern.compile("flight=(\\d+)");
        Matcher matcher = flightIdPattern.matcher(flightsHtml);

        String flightId = null;
        if (matcher.find()) {
            flightId = matcher.group(1);
            System.out.println("Choosing flight ID: " + flightId);
        } else {
            Assert.fail("No flight ID found in the response.");
        }

        // 4. Load the purchase page with the selected flight
        Response purchasePage = given()
                .queryParam("flight", flightId)
                .when()
                .get("/purchase.php");

        Assert.assertTrue(purchasePage.asString().contains("Your flight from"));
        System.out.println("Purchase page loaded.");

        // 5. Submit passenger details and confirm booking
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
