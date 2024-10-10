package tests;

import data.CompanyData;
import data.EmployeeData;
import data.EmployeePatchData;
import helper.ConfProperties;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.EmployeeResponse;
import model.PatchEmployeeRequest;
import org.junit.jupiter.api.*;
import helper.EmployeeApiHelper;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class APItests {

    private static EmployeeApiHelper APIHelper;
    private static String token;
    public static int companyIdGeneral;
    public static int employeeIdGeneral;
    private static PatchEmployeeRequest patchEmployeeRequest;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = ConfProperties.getProperty("base_url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        APIHelper = new EmployeeApiHelper();
        token = APIHelper.getToken();

        companyIdGeneral = APIHelper.createCompanyID(token, CompanyData.name, CompanyData.description);
        EmployeeResponse employeeResponse = APIHelper.createEmployee(token, companyIdGeneral, EmployeeData.firstName, EmployeeData.lastName,
                EmployeeData.middleName, EmployeeData.email, EmployeeData.url, EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);
        employeeIdGeneral = employeeResponse.id();

        patchEmployeeRequest = new PatchEmployeeRequest(EmployeePatchData.lastName,
                EmployeePatchData.email, EmployeePatchData.url, EmployeePatchData.phone, EmployeePatchData.isActive);
    }

    @Test
    @Tag("Позитивные")
    @DisplayName("Контракт - редактируем сотрудника")
    public void patchEmployee() {
        given()
                .body(patchEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .header("x-client-token", token)
                .when()
                .patch("employee/{employeeId}", employeeIdGeneral)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(employeeIdGeneral));
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - редактируем сотрудника - авторизация обязательна")
    public void patchEmployeeWithoutAuthorization() {
        PatchEmployeeRequest patchEmployeeRequest = new PatchEmployeeRequest(EmployeePatchData.lastName,
                EmployeePatchData.email, EmployeePatchData.url, EmployeePatchData.phone, EmployeePatchData.isActive);

        given()
                .body(patchEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .when()
                .patch("employee/{employeeId}", employeeIdGeneral)
                .then()
                .statusCode(401);
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - редактируем сотрудника - несуществующий ID")
    public void patchEmployeeWithNonExistentId() {
        int nonExistentId = 99999; // Предполагаем, что этот ID не существует

        final ValidatableResponse accept = given()
                .body(patchEmployeeRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .header("x-client-token", token)
                .when()
                .patch("employee/{employeeId}", nonExistentId)
                .then()
                .statusCode(404);// Ожидаем статус 404 Not Found
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - редактируем сотрудника - некорректный формат email")
    public void patchEmployeeWithInvalidEmail() {
        String invalidEmail = "invalid-email-format";
        PatchEmployeeRequest invalidEmailRequest = new PatchEmployeeRequest(EmployeePatchData.lastName,                invalidEmail, EmployeePatchData.url, EmployeePatchData.phone, EmployeePatchData.isActive);

        given()
                .body(invalidEmailRequest)
                .contentType("application/json")
                .header("accept", "application/json")
                .header("x-client-token", token)
                .when()
                .patch("employee/{employeeId}", employeeIdGeneral)
                .then()
                .statusCode(400); // Ожидаем статус 400 Bad Request
    }

    @AfterAll
    public static void tearDown() {
        APIHelper.deleteCompany(token, companyIdGeneral);
    }
}