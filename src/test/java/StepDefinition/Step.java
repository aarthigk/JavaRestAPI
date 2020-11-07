package StepDefinition;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
//step definition
public class Step {
	private static final String USER_ID = "229b40c3-e1da-4997-ba0c-3ed73fcbcd52";
	private static final String USERNAME = "aarthi";
	private static final String PASSWORD = "Test4test!";
	private static final String BASE_URL = "https://bookstore.toolsqa.com";

	private static String token;
	private static Response response;
	private static String jsonString;
	private static String bookId;


	@Given("I am an authorized user")
	public void iAmAnAuthorizedUser() {

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Content-Type", "application/json");
		response = request.body("{ \"userName\":\"" + USERNAME + "\", \"password\":\"" + PASSWORD + "\"}")
				.post("/Account/v1/GenerateToken");

		String jsonString = response.asString();
		token = JsonPath.from(jsonString).get("token");

	}

	@Given("A list of books are available")
	public void listOfBooksAreAvailable() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		response = request.get("/BookStore/v1/Books");

		jsonString = response.asString();
		List<Map<String, String>> books = JsonPath.from(jsonString).get("books");
		Assert.assertTrue(books.size() > 0);

		bookId = books.get(0).get("isbn");	   
	}

	@When("I add a book to my reading list")
	public void addBookInList() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", "Bearer " + token)
		.header("Content-Type", "application/json");

		response = request.body("{ \"userId\": \"" + USER_ID + "\", " +
				"\"collectionOfIsbns\": [ { \"isbn\": \"" + bookId + "\" } ]}")
				.post("/BookStore/v1/Books");
	}

	@Then("the book is added")
	public void the_book_is_added() {
	    // Write code here that turns the phrase above into concrete actions
		Assert.assertEquals(201, response.getStatusCode());
	}


	@When("I remove a book from my reading list")
	public void removeBookFromList() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Authorization", "Bearer " + token)
		.header("Content-Type", "application/json");

		response = request.body("{ \"isbn\": \"" + bookId + "\", \"userId\": \"" + USER_ID + "\"}")
				.delete("/BookStore/v1/Book");


	}

	@Then("the book is removed")
	public void bookIsRemoved() {
		Assert.assertEquals(204, response.getStatusCode());

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Authorization", "Bearer " + token)
		.header("Content-Type", "application/json");

		response = request.get("/Account/v1/User/" + USER_ID);
		Assert.assertEquals(200, response.getStatusCode());

		jsonString = response.asString();
		List<Map<String, String>> booksOfUser = JsonPath.from(jsonString).get("books");
		Assert.assertEquals(0, booksOfUser.size());
	}
}