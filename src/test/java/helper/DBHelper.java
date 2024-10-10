package helper;

import java.sql.*;

public class DBHelper {

    public Connection connectionToDB() throws SQLException {
        String connectionString = ConfProperties.getProperty("connectionString");
        String DBUsername = ConfProperties.getProperty("DBUsername");
        String DBPassword = ConfProperties.getProperty("DBPassword");

        return DriverManager.getConnection(connectionString, DBUsername, DBPassword);
    }

    public ResultSet getEmployeeByID(Connection connection, int companyId) throws SQLException {
        String SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employee WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_BY_ID);
        preparedStatement.setInt(1, companyId);

        return preparedStatement.executeQuery();
    }

    public Connection connectToDB() {
        return null;
    }
}
