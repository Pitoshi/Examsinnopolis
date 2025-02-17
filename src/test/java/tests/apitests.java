package tests;

import data.CompanyData;
import data.EmployeeData;
import data.EmployeePatchData;
import helper.ConfProperties;
import helper.EmployeeApiHelper;
import io.restassured.RestAssured;
import model.EmployeeResponse;
import model.PatchEmployeeRequest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class apitests {

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
        // Предполагаем, что этот ID не существует

        // Ожидаем статус 404 Not Found
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - редактируем сотрудника - некорректный формат email")
    public void patchEmployeeWithInvalidEmail() {
        String invalidEmail = "invalid-email-format";
        var invalidEmailRequest = new PatchEmployeeRequest(EmployeePatchData.lastName,                invalidEmail, EmployeePatchData.url, EmployeePatchData.phone, EmployeePatchData.isActive);

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