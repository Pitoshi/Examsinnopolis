package tests;

import data.EmployeeData;
import helper.ConfProperties;
import helper.EmployeeApiHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import model.CreateEmployeeRequest;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class JSONSTests {

    private static EmployeeApiHelper apiHelper;
    private static String token;
    private static int companyId;
    private static final String COMPANY_NAME = "JSON-schema компания " + LocalDateTime.now();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = ConfProperties.getProperty("base_url");
        apiHelper = new EmployeeApiHelper();
        token = apiHelper.getToken();
        companyId = apiHelper.createCompanyID(token, COMPANY_NAME, "тест Лена");
    }

    @Test
    @DisplayName("JSON схема - получить список сотрудников")
    public void validateJSONSchemaForGetEmployees() {
        apiHelper.createEmployee(token, companyId,
                EmployeeData.firstName,
                EmployeeData.lastName,
                EmployeeData.middleName,
                EmployeeData.email,
                EmployeeData.url,
                EmployeeData.phone,
                EmployeeData.birthdate,
                EmployeeData.isActive);

        when()
                .get("/employee?company={id}", companyId)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("employees-schema.json"));
    }

    @Test
    @DisplayName("JSON схема - получить сотрудника по ID")
    public void validateJSONSchemaForGetEmployeeById() {
        int employeeId = apiHelper.createEmployee(token, companyId,
                EmployeeData.firstName,
                EmployeeData.lastName,
                EmployeeData.middleName,
                EmployeeData.email,
                EmployeeData.url,
                EmployeeData.phone,
                EmployeeData.birthdate,
                EmployeeData.isActive).id();

        when()
                .get("/employee/{id}", employeeId)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("employee-schema.json"));
    }

    @Test
    @DisplayName("JSON схема - создать сотрудника")
    public void validateJSONSchemaForCreateEmployee() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                EmployeeData.firstName,
                EmployeeData.lastName,
                EmployeeData.middleName,
                companyId,
                EmployeeData.email,
                EmployeeData.url,
                EmployeeData.phone,
                EmployeeData.birthdate,
                EmployeeData.isActive
        );

        given()
                .contentType(ContentType.JSON)
                .header("x-client-token", token)
                .body(request)
                .when()
                .post("/employee")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("create-employee-response-schema.json"));
    }

    @Test
    @DisplayName("JSON схема - обновить сотрудника")
    public void validateJSONSchemaForUpdateEmployee() {
        int employeeId = apiHelper.createEmployee(token, companyId,
                EmployeeData.firstName,
                EmployeeData.lastName,
                EmployeeData.middleName,
                EmployeeData.email,
                EmployeeData.url,
                EmployeeData.phone,
                EmployeeData.birthdate,
                EmployeeData.isActive).id();

        CreateEmployeeRequest updateRequest = new CreateEmployeeRequest(
                "UpdatedFirstName",
                "UpdatedLastName",
                EmployeeData.middleName,
                companyId,
                EmployeeData.email,
                EmployeeData.url,
                EmployeeData.phone,
                EmployeeData.birthdate,
                EmployeeData.isActive
        );

        given()
                .contentType(ContentType.JSON)
                .header("x-client-token", token)
                .body(updateRequest)
                .when()
                .patch("/employee/{id}", employeeId)
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("update-employee-response-schema.json"));
    }

    @Test
    @DisplayName("JSON схема - ошибка при получении несуществующего сотрудника")
    public void validateJSONSchemaForNonExistentEmployee() {
        int nonExistentEmployeeId = 99999;

        when()
                .get("/employee/{id}", nonExistentEmployeeId)
                .then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("error-response-schema.json"));
    }

    @AfterAll
    public static void tearDown() {
        apiHelper.deleteCompany(token, companyId);
    }
}