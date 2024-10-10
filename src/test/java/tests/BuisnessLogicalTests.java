package tests;

import data.CompanyData;
import data.EmployeeData;
import helper.ConfProperties;
import helper.DBHelper;
import helper.EmployeeApiHelper;
import io.restassured.RestAssured;
import model.EmployeeResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuisnessLogicalTests {

    private static EmployeeApiHelper apiHelper;
    private static DBHelper dbHelper;
    private static String token;
    public static int companyId;
    private static Connection connection;

    @BeforeAll
    public static void setUp() throws SQLException {
        RestAssured.baseURI = ConfProperties.getProperty("base_url");
        apiHelper = new EmployeeApiHelper();
        token = apiHelper.getToken();
        companyId = apiHelper.createCompanyID(token, CompanyData.name, CompanyData.description);
        dbHelper = new DBHelper();
        connection = dbHelper.connectionToDB();
    }

    @Test
    public void newEmployeeCanBeAdded() {
        EmployeeResponse newEmployee = apiHelper.createEmployee(token, companyId,
                EmployeeData.firstName, EmployeeData.lastName,
                EmployeeData.middleName, EmployeeData.email,
                EmployeeData.url, EmployeeData.phone,
                EmployeeData.birthdate, EmployeeData.isActive);

        System.out.println("В компании: " + companyId + " Создан сотрудник: " + newEmployee);

        assertTrue(newEmployee.id() > 0, "ID нового сотрудника должно быть больше 0");
        assertEquals(EmployeeData.firstName, newEmployee.firstName(), "Имя сотрудника не совпадает");
        assertEquals(EmployeeData.lastName, newEmployee.lastName(), "Фамилия сотрудника не совпадает");
        assertEquals(companyId, newEmployee.companyId(), "ID компании не совпадает");
        assertEquals(EmployeeData.middleName, newEmployee.middleName(), "Отчество сотрудника не совпадает");
        assertEquals(EmployeeData.email, newEmployee.email(), "Email сотрудника не совпадает");
        assertEquals(EmployeeData.url, newEmployee.url(), "URL сотрудника не совпадает");
        assertEquals(EmployeeData.phone, newEmployee.phone(), "Телефон сотрудника не совпадает");
        assertEquals(EmployeeData.birthdate, newEmployee.birthdate(), "Дата рождения сотрудника не совпадает");
        assertEquals(EmployeeData.isActive, newEmployee.isActive(), "Статус активности сотрудника не совпадает");
    }

    @Test
    public void validatingDBNewEmployeeCanBeAdded() throws SQLException {
        EmployeeResponse newEmployee = apiHelper.createEmployee(token, companyId,
                EmployeeData.firstName, EmployeeData.lastName,
                EmployeeData.middleName, EmployeeData.email,
                EmployeeData.url, EmployeeData.phone,
                EmployeeData.birthdate, EmployeeData.isActive);

        System.out.println("В компании: " + companyId + " Создан сотрудник: " + newEmployee);

        ResultSet resultSet = dbHelper.getEmployeeByID(connection, newEmployee.id());

        assertTrue(resultSet.next(), "Сотрудник не найден в базе данных");
        assertEquals(newEmployee.id(), resultSet.getInt(1), "ID сотрудника в базе данных не совпадает");
        assertTrue(resultSet.getBoolean(2), "Статус активности сотрудника в базе данных неверен");
        assertEquals(EmployeeData.firstName, resultSet.getString(5), "Имя сотрудника в базе данных не совпадает");
        assertEquals(EmployeeData.lastName, resultSet.getString(6), "Фамилия сотрудника в базе данных не совпадает");
        assertEquals(EmployeeData.middleName, resultSet.getString(7), "Отчество сотрудника в базе данных не совпадает");
        assertEquals(EmployeeData.phone, resultSet.getString(8), "Телефон сотрудника в базе данных не совпадает");
        assertEquals(EmployeeData.email, resultSet.getString(9), "Email сотрудника в базе данных не совпадает");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        apiHelper.deleteCompany(token, companyId);
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
