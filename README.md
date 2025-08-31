# Credit Application

In order to run the application, first maven dependencies should be installed with the following command:

./mvnw clean install

Then, application can be started with the following command:

./mvnw spring-boot:run

APIs can be tested using postman collection and environment files in  src/main/resources as follows:

1. User can register to the system using "Register User" with his/her info.
2. User should log in using "Login User" with his/her credentials
3. To use Loan APIs, user should have a corresponding customer in the system. That customer can be registered by an admin user. So, admin user should login with his/her credentials using "Login User (Admin)".
4. Admin user should call "Create Customer" with his/her admin token.
5. User having customer role can call "Create Loan" with his/her token retrieved from login user.
6. After creating loan, user can "List Loans", "List Loan Installments" and "Pay Loan".

User APIs

1. A user can call "Get Current User" after "Register User" and "Login User" to retrieve his info.
2. Admin role user can call "List Users" to list all users and "Create Admin User" to create another admin user.