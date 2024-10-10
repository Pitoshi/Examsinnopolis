package model;

public record CreateEmployeeRequest(String firstName, String lastName, String middleName, int companyId,
                                    String email, String url, String phone, String birthdate, boolean isActive) {

    public void setEmail(String ignoredInvalidEmail) {
    }
}