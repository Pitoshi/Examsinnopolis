package data;

import com.github.javafaker.Faker;

public class EmployeeData {

    public static Faker faker = new Faker();
    public static String firstName = faker.name().firstName();
    public static String lastName = faker.name().lastName();
    public static String middleName = "";
    public static String email = "api-db.test@test.com";
    public static String url = "https://test.com";
    public static String phone = "+75551112233";
    public static String birthdate = "1990-08-15";
    public static boolean isActive = true;

}
