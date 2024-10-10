package tests;

import data.CompanyData;
import data.EmployeeData;
import helper.ConfProperties;
import helper.DBHelper;
import io.restassured.RestAssured;
import model.CreateEmployeeRequest;
import model.EmployeeResponse;
import org.junit.jupiter.api.*;
import helper.EmployeeApiHelper;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateEmployeeNegativePositiveTests {

    private static EmployeeApiHelper APIHelper;
    private static String token;
    public static int companyIdGeneral;
    public static int employeeIdGeneral;
    private static Connection connection;

    @BeforeAll
    public static void setUp() throws SQLException {
        RestAssured.baseURI = ConfProperties.getProperty("base_url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        APIHelper = new EmployeeApiHelper();
        token = APIHelper.getToken();

        companyIdGeneral = APIHelper.createCompanyID(token, CompanyData.name, CompanyData.description);
        EmployeeResponse employeeResponse = APIHelper.createEmployee(token, companyIdGeneral, EmployeeData.firstName,
                EmployeeData.lastName, EmployeeData.middleName, EmployeeData.email, EmployeeData.url,
                EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);
        employeeIdGeneral = employeeResponse.id();

        helper.DBHelper DBHelper = new DBHelper();
        connection = DBHelper.connectionToDB();
    }

    @Test
    @Tag("Позитивные")
    @DisplayName("Контракт - создаем сотрудника в новой компании")
    public void employeeCanBeCreated() {
        int companyId = APIHelper.createCompanyID(token, CompanyData.name, CompanyData.description);
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(EmployeeData.firstName,
                EmployeeData.lastName, EmployeeData.middleName, companyId, EmployeeData.email,
                EmployeeData.url, EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);

        given()
                .body(createEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .header("x-client-token", token)
                .when()
                .post("/employee")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", greaterThanOrEqualTo(1));

        APIHelper.deleteCompany(token, companyId);
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - создаем сотрудника - авторизация обязательна")
    public void employeeCanNotBeCreatedWithoutAuthorization() {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(EmployeeData.firstName,
                EmployeeData.lastName, EmployeeData.middleName, companyIdGeneral, EmployeeData.email,
                EmployeeData.url, EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);

        given()
                .body(createEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .when()
                .post("/employee")
                .then()
                .statusCode(401);
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - создаем сотрудника - поле firstName обязательное")
    public void employeeCanNotBeCreatedWithoutName() {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(null,
                EmployeeData.lastName, EmployeeData.middleName, companyIdGeneral, EmployeeData.email,
                EmployeeData.url, EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);

        given()
                .body(createEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .header("x-client-token", token)
                .when()
                .post("/employee")
                .then()
                .statusCode(400) // Ожидаем ошибку 400 для обязательного поля
                .body("error", equalTo("firstName is required")); // Предполагаемое сообщение об ошибке
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        APIHelper.deleteCompany(token, companyIdGeneral); // Удаляем созданную компанию после тестов
    }
}