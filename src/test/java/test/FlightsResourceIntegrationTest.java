package test;

import io.restassured.*;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import org.apache.catalina.LifecycleException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.utils.EmbeddedTomcat;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.hamcrest.core.IsEqual.equalTo;

public class FlightsResourceIntegrationTest {

    private static String HOST = "http://localhost";
    private static final int PORT = 8084;
    private static final String CONTEXT = "/SundeFlyBack";

    private static EmbeddedTomcat tomcat;
    private static String securityToken;
    
    @BeforeClass
    public static void setUpBeforeAll() throws ServletException, LifecycleException, MalformedURLException {
        tomcat = new EmbeddedTomcat();
        tomcat.start(PORT, CONTEXT);
        RestAssured.baseURI = HOST;
        RestAssured.port = PORT;
        RestAssured.basePath = CONTEXT;
        RestAssured.defaultParser = Parser.JSON;
    }

    private void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        System.out.println(json);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/api/login")
                .then()
                .extract().path("token");
        System.out.println("Token: " + securityToken);
    }

    private static void logOut() {
        securityToken = null;
    }

    @Test
    public void testGetTwoFlightsFromAdminREST_ShouldReturnTwoFlights() {
        login("admin", "password123");
        given()
                .header("Authorization", "Bearer " + securityToken)
                .queryParam("page_number", 1).queryParam("page_count", 2)
                .when().get("/api/flights")
                .then()
                .statusCode(200)
                .body("airline", equalTo("Sunde Fly"))
                .body("flights.size()", equalTo(2))
                .body("flights[0].flightNumber", equalTo(5))
                .body("flights[0].destination", equalTo("TXL"))
                .body("flights[1].date", equalTo("2017-07-29"));
    }

    @Test
    public void serverIsRunning() {
        given().when().get("api/flights/Berlin/Copenhagen/2017-07-26/2").then().statusCode(200);
    }
    
    //Exception Testing
    @Test
    public void nonexistantPageExceptionTest() {
        given().
                when().get("api/bingotrolden").
                then().
                body("error.code", equalTo(404));
    }

    @AfterClass
    public static void after() throws ServletException, IOException, LifecycleException, IOException {
        logOut();
        tomcat.stop();
    }

}