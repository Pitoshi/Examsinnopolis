package helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.AuthRequest;
import model.CreateCompanyRequest;
import model.CreateEmployeeRequest;
import model.EmployeeResponse;

import static io.restassured.RestAssured.given;
public class EmployeeApiHelper {

    public String getToken() {
        String username = ConfProperties.getProperty("username");
        String password = ConfProperties.getProperty("password");
        AuthRequest authRequest = new AuthRequest(username, password);

        return
                given()
                        .body(authRequest)
                        .contentType("application/json")
                        .header("accept", "application/json")
                        .when()
                        .post("/auth/login")
                        .jsonPath()
                        .getString("userToken");

    }

    public int createCompanyID(String token, String name, String description) {
        CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest(name, description);

        return
                given()
                        .body(createCompanyRequest)
                        .contentType(ContentType.JSON)
                        .header("x-client-token", token)
                        .when()
                        .post("/company")
                        .jsonPath()
                        .getInt("id");
    }

    public EmployeeResponse createEmployee(String token, int companyId, String firstName, String lastName, String middleName, String email,
                                           String url, String phone, String birthDate, boolean isActive) {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(firstName, lastName, middleName, companyId, email,
                url, phone, birthDate, isActive);
        return
                given()
                        .body(createEmployeeRequest)
                        .contentType("application/json")
                        .header("x-client-token", token)
                        .when()
                        .post("/employee")
                        .body().as(EmployeeResponse.class);
    }

    public void deleteCompany(String token, int companyId) {
        given()
                .basePath("company/delete/" + companyId)
                .header("x-client-token", token)
                .contentType("application/json")
                .when()
                .get();
    }


}
