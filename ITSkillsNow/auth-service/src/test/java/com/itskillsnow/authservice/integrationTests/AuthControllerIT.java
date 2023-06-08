package com.itskillsnow.authservice.integrationTests;

import com.itskillsnow.authservice.dto.AddUserDto;
import com.itskillsnow.authservice.dto.AuthRequest;
import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.dto.request.AddRoleDto;
import com.itskillsnow.authservice.dto.request.DeleteUserDto;
import com.itskillsnow.authservice.dto.request.LoginWithMultiFactorDto;
import com.itskillsnow.authservice.repository.OTPCodeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private OTPCodeRepository otpCodeRepository;


    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/auth");
    }

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @AfterEach
    public void tearDown() {
        // Clean up databases
        otpCodeRepository.deleteAll();
    }

    @Test
    void testAddNewUser() {
        // Create the request body
        AddUserDto addUserDto = new AddUserDto();
        addUserDto.setFullName("John Doe1");
        addUserDto.setUsername("johnDoe1");
        addUserDto.setEmail("johndoe1@example.com");
        addUserDto.setPassword("password1");

        // Create the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity
        HttpEntity<AddUserDto> entity = new HttpEntity<>(addUserDto, headers);



        // Make the HTTP request
        ResponseEntity<String> response = restTemplate.exchange(baseUrl.concat("/register") ,
                HttpMethod.POST, entity, String.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user added to the system", response.getBody());
    }

    @Test
    void testAddNewUser_WithExistingUsernameOrEmail() {
        // Create the request body
        AddUserDto addUserDto = new AddUserDto();
        addUserDto.setFullName("John Doe2");
        addUserDto.setUsername("johnDoe2");
        addUserDto.setEmail("johndoe2@example.com");
        addUserDto.setPassword("password2");

        // Create the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity
        HttpEntity<AddUserDto> entity = new HttpEntity<>(addUserDto, headers);

        // Make the HTTP request to add the user
        ResponseEntity<String> response = restTemplate.exchange(baseUrl.concat("/register") ,
                HttpMethod.POST, entity, String.class);

        // Verify that the response indicates that the user was added successfully
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user added to the system", response.getBody());

        // Make the HTTP request again with the same email and username
        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl.concat("/register") ,
                HttpMethod.POST, entity, String.class);

        // Verify that the response indicates that the email and username are already taken
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Email or username is already taken!", response2.getBody());
    }

    @Test
    void testLogin() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        // make the login request
        ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(baseUrl + "/login", authRequest, AuthResponse.class);

        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // assert that the response body contains an access token and a refresh token
        AuthResponse authResponse = responseEntity.getBody();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());
        assertNotNull(authResponse.getRoles());
    }

    @Test
    void testLoginWithWrongPassword() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe4");
        user.setUsername("johnDoe4");
        user.setEmail("johndoe4@example.com");
        user.setPassword("password4");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request with wrong password
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe4");
        authRequest.setPassword("wrongPassword");

        // make the login request
        try {
            restTemplate.postForObject(baseUrl + "/login", authRequest, AuthResponse.class);
        } catch (HttpClientErrorException ex) {
            // assert that the response status code is 403 Forbidden
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    @Test
    void testValidateToken() {
        // Create a new user
        AddUserDto userDto = new AddUserDto();
        userDto.setFullName("John Doe5");
        userDto.setUsername("johnDoe5");
        userDto.setEmail("johndoe5@example.com");
        userDto.setPassword("password5");
        String createUserUrl = baseUrl + "/register";
        ResponseEntity<String> response = restTemplate.postForEntity(createUserUrl, userDto, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Login with the new user to get the authentication token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe5");
        authRequest.setPassword("password5");
        String loginUrl = baseUrl + "/login";
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(loginUrl, authRequest, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Extract the authentication token from the login response
        String token = Objects.requireNonNull(loginResponse.getBody()).getAccessToken();

        // Call the validateToken endpoint with the authentication token
        String validateTokenUrl = baseUrl + "/validate?token=" + token;
        ResponseEntity<String> validateTokenResponse = restTemplate.getForEntity(validateTokenUrl, String.class);
        assertEquals(HttpStatus.OK, validateTokenResponse.getStatusCode());
        assertEquals("Token is valid", validateTokenResponse.getBody());
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        // Create an invalid token
        String invalidToken = "invalid_token";

        // Create the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the HTTP request
        try {
            restTemplate.exchange(baseUrl.concat("/validate?token=")
                            .concat(invalidToken),
                    HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException ex) {
            // assert that the response status code is 403 Forbidden
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    @Test
    void testRefreshToken() {
        // create a user and login to get the auth token and refresh token
        AddUserDto userDto = new AddUserDto();
        userDto.setUsername("johnDoe6");
        userDto.setPassword("password123");
        userDto.setEmail("johndoe6@example.com");
        userDto.setFullName("John Doe");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/register", userDto, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(userDto.getUsername());
        authRequest.setPassword(userDto.getPassword());

        ResponseEntity<AuthResponse> authResponseEntity = restTemplate.postForEntity(baseUrl + "/login", authRequest, AuthResponse.class);
        assertEquals(HttpStatus.OK, authResponseEntity.getStatusCode());

        AuthResponse authResponse = authResponseEntity.getBody();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());

        // use the refresh token to get a new auth token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authResponse.getRefreshToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<AuthResponse> refreshTokenResponseEntity = restTemplate.exchange(baseUrl + "/refresh/" + authResponse.getRefreshToken(), HttpMethod.POST, entity, AuthResponse.class);
        assertEquals(HttpStatus.OK, refreshTokenResponseEntity.getStatusCode());

        AuthResponse newAuthResponse = refreshTokenResponseEntity.getBody();
        assertNotNull(newAuthResponse);
        assertNotNull(newAuthResponse.getAccessToken());
        assertNotNull(newAuthResponse.getRefreshToken());
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        // Create an invalid refresh token
        String invalidToken = "invalidRefreshToken";

        // Create the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Make the HTTP request
        try {
            restTemplate.exchange(
                    baseUrl + "/refresh/" + invalidToken,
                    HttpMethod.POST,
                    entity,
                    AuthResponse.class
            );
        } catch (HttpClientErrorException ex) {
            // assert that the response status code is 403 Forbidden
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    @Test
    void testDeleteMe() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        restTemplate.postForObject(baseUrl + "/login", authRequest, AuthResponse.class);


        // make the request
        DeleteUserDto deleteUserDto = new DeleteUserDto(authRequest.getUsername(), authRequest.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeleteUserDto> requestEntity = new HttpEntity<>(deleteUserDto, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(baseUrl.concat("/").concat("deleteMe"),
                HttpMethod.POST, requestEntity, Boolean.class);


        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // assert that the response body is true
        assertEquals(Boolean.TRUE, responseEntity.getBody());

    }

    @Test
    void testAddRole() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

         restTemplate.postForEntity(baseUrl + "/login", authRequest, AuthResponse.class);

        AddRoleDto addRoleDto = new AddRoleDto("ADMIN", user.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AddRoleDto> requestEntity = new HttpEntity<>(addRoleDto, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(baseUrl.concat("/").concat("addRole"),
                HttpMethod.POST, requestEntity, Boolean.class);


        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // assert that the response body is true
        assertEquals(Boolean.TRUE, responseEntity.getBody());
    }


    @Test
    void testAddRole_roleAlreadyExistsReturnsFalse() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        restTemplate.postForEntity(baseUrl + "/login", authRequest, AuthResponse.class);

        AddRoleDto addRoleDto = new AddRoleDto("USER", user.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AddRoleDto> requestEntity = new HttpEntity<>(addRoleDto, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(baseUrl.concat("/").concat("addRole"),
                HttpMethod.POST, requestEntity, Boolean.class);


        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // assert that the response body is true
        assertEquals(Boolean.FALSE, responseEntity.getBody());
    }


    @Test
    void testLoginWithMultiFactor() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        // make the login with multi_factor request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/login-multiFactor",
                authRequest, String.class);

        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Code sent to your email for multi factor authentication", responseEntity.getBody());
    }


    @Test
    void testLoginWithMultiFactor_withWrongUsernameAndPassword() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("wrongUsername");
        authRequest.setPassword("wrongPassword");

        HttpClientErrorException.Forbidden response = Assertions.assertThrows(
                HttpClientErrorException.Forbidden.class, () ->
                        restTemplate.postForEntity(baseUrl + "/login-multiFactor",
                                authRequest, String.class)
        );

        // Verify
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void testCheckMultiFactor() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        // make the login with multi_factor request
        restTemplate.postForEntity(baseUrl + "/login-multiFactor",
                authRequest, String.class);

        String code = otpCodeRepository.findAll().get(0).getOtpCode();

        LoginWithMultiFactorDto request = new LoginWithMultiFactorDto("johnDoe3", code);

        //check the code
        ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(baseUrl + "/check-multiFactor",
                request, AuthResponse.class);

        // assert that the response status code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AuthResponse authResponse = responseEntity.getBody();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());
        assertNotNull(authResponse.getRoles());
    }


    @Test
    void testCheckMultiFactor_withWrongCode() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        // make the login with multi_factor request
        restTemplate.postForEntity(baseUrl + "/login-multiFactor",
                authRequest, String.class);


        LoginWithMultiFactorDto request = new LoginWithMultiFactorDto("johnDoe3", "123456");

        //check the code
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.postForEntity(baseUrl + "/check-multiFactor",
                                request, AuthResponse.class)
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid code.", response.getResponseBodyAsString());
    }


    @Test
    void testCheckMultiFactor_withWrongCode_tryThreeTimes_returnBadRequest() {
        // create a new user to test the login endpoint
        AddUserDto user = new AddUserDto();
        user.setFullName("John Doe3");
        user.setUsername("johnDoe3");
        user.setEmail("johndoe3@example.com");
        user.setPassword("password3");

        // register the user
        restTemplate.postForObject(baseUrl + "/register", user, String.class);

        // create the authentication request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("johnDoe3");
        authRequest.setPassword("password3");

        // make the login with multi_factor request
        restTemplate.postForEntity(baseUrl + "/login-multiFactor",
                authRequest, String.class);


        LoginWithMultiFactorDto request = new LoginWithMultiFactorDto("johnDoe3", "123456");

        //request 3 times
        Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.postForEntity(baseUrl + "/check-multiFactor",
                                request, AuthResponse.class)
        );
        Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.postForEntity(baseUrl + "/check-multiFactor",
                                request, AuthResponse.class)
        );

        // the last attempt
        HttpClientErrorException.TooManyRequests response = Assertions.assertThrows(
                HttpClientErrorException.TooManyRequests.class, () ->
                        restTemplate.postForEntity(baseUrl + "/check-multiFactor",
                                request, AuthResponse.class)
        );

        // Verify
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("Too many login attempts. Please try again later.", response.getResponseBodyAsString());
    }

}
