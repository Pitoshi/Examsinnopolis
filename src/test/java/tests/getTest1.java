package tests;

import data.CompanyData;
import data.EmployeeData;
import helper.ConfProperties;
import helper.EmployeeApiHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class getTest1 {

    private static EmployeeApiHelper APIHelper;
    private static String token;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = ConfProperties.getProperty("base_url");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        APIHelper = new EmployeeApiHelper();
        token = APIHelper.getToken();
    }

    @Test
    @Tag("Позитивные")
    @DisplayName("Контракт - получаем список сотрудников для пустой компании")
    public void getEmployeesFromEmptyCompany() {
        int companyId = APIHelper.createCompanyID(token, CompanyData.name, CompanyData.description);

        given()
                .when()
                .get("/employee?company={id}", companyId)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body(equalTo("[]"));

        APIHelper.deleteCompany(token, companyId);
    }

    @Test
    @Tag("Позитивные")
    @DisplayName("Контракт - получаем список сотрудников для непустой компании")
    public void getEmployeesFromCompany() {
        int companyId = APIHelper.createCompanyID(token, CompanyData.name, CompanyData.description);
        APIHelper.createEmployee(token, companyId, EmployeeData.firstName, EmployeeData.lastName,
                EmployeeData.middleName, EmployeeData.email, EmployeeData.url, EmployeeData.phone, EmployeeData.birthdate, EmployeeData.isActive);

        given()
                .when()
                .get("/employee?company={id}", companyId)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("[0].id", greaterThanOrEqualTo(1));

        APIHelper.deleteCompany(token, companyId);
    }

    @Test
    @Tag("Негативные")
    @DisplayName("Контракт - получаем список сотрудников - ID компании текст")
    public void getEmployeesFromCompanyWithTextID() {
        given()
                .when()
                .get("/employee?company={id}", "Компания")
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"), new Object[]{})
    }
}