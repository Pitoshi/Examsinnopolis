### TO DO
Split tests to Contract and Business
Extract Helper class for Business tests class
Add negative tests to Contract tests
Add negative tests to Business tests

### HELPER (do private methods first in Tests class)
1. Auth - returns token OR returns all response body and we extract token then using getters - ONLY TOKEN
2. Create a company - returns company id OR all response body (including company id) - COMPANY ID
3. Create an employee - ? It's something that I need to test! Should it be in a test class? Yes, let's put it here as well,
because we will use it multiple times for other tests. It will be created as a separate test as well! Returns all response body!

### CONTRACT TESTS for EMPLOYEE module:

Pre-conditions - Before All 
1. Authenticate - and get token
2. Create a company - so I can use it's id in my tests!

#### POSITIVE
1. Create employee -
- Checks:
  - status code 201 Created 
  - AND has ID > 0 
  - AND response body has all needed fields with values which I specified - NO, IT'S CHECKED IN BUSINESS CASES! 
  - AND response headers have Content-type = application-json charset utf-8 ? 
- Pre-condition: 1) authenticated (helper) - get token 2) create a company (helper) - get company id
- Post-condition: Remove company (soft delete)

2. GetEmployees -
New company without employees
- Checks:
  - status code 200 OK
  - AND response body is a json object?
  - AND response body is equal to [];?
- Pre-condition: 1) create a company (helper)
- Post-condition: Remove company (soft delete)

3. GetEmployees -
New company with 2 or more employees
- Checks:
  - status code 200 OK 
  - AND response body is a json object?
  - AND response body has 2 or more objects   - how to check it?
  - AND first object has expected keys/properties - employee.id, employee.first_name, employee.last_name, etc. NO, IT'S BUSINESS CASE 
- Pre-condition: 1) create an employee to the company (helper) - 2 times?
- Post-condition: Remove company (soft delete)

4. GetEmployee by ID
- Checks:
  - status code 200 OK 
  - AND response body is a json object?
  - AND employee id exists and > 0 (request by id created). 
- Pre-conditions: 1) create an employee to the company (helper, get it's id)

5. Change Employee
- Checks:
  - status code 200 OK
  - AND response body is a json object?
  - AND all fields has new value
- Should we change all fields at once? Or one by one in 5 different tests?? I would do all at once
- Body:
  {
    "lastName": "string",
    "email": "string",
    "url": "string",
    "phone": "string",
    "isActive": true
  }
- Pre-conditions: 1) create an employee to the company (helper, get it's id)

#### NEGATIVE
1. Create employee - without authentication - 401 Unauthenticated
2. Create employee without firstName field - 400 Bad request - Message: "firstName field is required"
3. Create employee without lastName field - 400 Bad request - Message: "lastName field is required"
4. Create employee without company field - 400 Bad request - Message: "company field is required"
5. Create employee with first name empty - 400 Bad request
6. Create employee with last name empty - 400 Bad request
7. Create employee with email empty - 400 Bad request
8. Create employee with first name = ' ' - 400 Bad request
9. Create employee with last name = ' ' - 400 Bad request
10. Create employee with id field - 400 Bad request
11. Create employee with email = ' ' - 400 Bad request
11. Create employee with email in a wrong format - no @ - 400 Bad request
12. Create employee - put email in a wrong format - no . after @ - 400 Bad request
13. Create employee - in a wrong company (company id doesn't exist) - 400 Bad request
14. Get list of employees for a company - company id = 'abc' - 400 Bad request ? Error 'Value must be a number' ?
15. Get list of employees for a company - company id is empty - 400 Bad request ?
16. Get list of employees for non-existing company? Pre-conditions: get list of companies - get the latest id, use it + 1000
17. Get user by id - use id that doesn't exist? How to get it?? Use number 1? 404 Not found
18. Patch employee - without authentication - 401
19. Patch employee - put empty email - 400 Bad request 
20. Patch employee - put email in a wrong format - no @ - 400 Bad request
21. Patch employee - put email in a wrong format - no . after @ - 400 Bad request
22. Patch employee - put email in a wrong format - 1 symbol before @ - 400 Bad request
23. Patch employee - by ID that doesn't exist

### BUSINESS TESTS for EMPLOYEE module:

#### POSITIVE
1. Create employee - check that data are returned correctly - will not work in this case, it returns only id..
2. Create employee - check it appeared in the DB (when create it via API, we receive his ID, then select it from the DB using this ID)
2. Patch employee - check that all data are correct in the response
3. Patch employee, Get employee by ID - check that updated data are returned

#### NEGATIVE
1. Create employee, and then create employee with the same email - 400 Bad request? Message = user with this email already exists ? NO, NO VALIDATION
2. Patch employee that doesn't exist - 404 Not found?
3. Delete company and then try to get it's employee by ID - 404 Not found?
4. Delete employee and then try to delete him again - 404 Not found? THERE IS NO ENDPOINT FOR DELETING EMPLOYEE
