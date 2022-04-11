package com.example.sensorBIM.services;

import com.example.sensorBIM.HttpBody.AuthRequest;
import com.example.sensorBIM.HttpBody.NewUserRequestBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.JWT.CustomUserDetailsService;
import com.example.sensorBIM.JWT.JWTService;
import com.example.sensorBIM.JWT.JwtUtil;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testRegisterUser() {
        NewUserRequestBody newUser = new NewUserRequestBody();
        newUser.setFirstName("Jean");
        newUser.setLastName("Doe");
        newUser.setUsername("jeamdoe");
        newUser.setPassword("password");
        newUser.setRepeatedPassword("password");
        newUser.setEmail("newtestemail@gmail.com");
        Response<User> response = jwtService.createAccount(newUser).getBody();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
    }

    @Test
    public void testGetToken() {
        User user = userService.findUserByUsername("steverogers");
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(user.getUsername());
        authRequest.setPassword("password");
        Map<String, Object> response = jwtService.generateToken(authRequest).getBody();
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());
        Assert.assertNotNull(response.get("token"));
        String token = response.get("token").toString();
        Assert.assertNotNull(response.get("user"));
        Assert.assertNotNull(jwtUtil.extractUsername(token));
        Assert.assertEquals(user.getUsername(), jwtUtil.extractUsername(token));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        Assert.assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    public void testGetTokenWithInvalidCredentials() {
        User user = userService.findUserByUsername("steverogers");
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(user.getUsername());
        authRequest.setPassword("this is a wrong password");
        Map<String, Object> response = jwtService.generateToken(authRequest).getBody();
        Assert.assertNull(response);
    }

    @Test
    public void testResetPasswordWithValidEmail() {
        Response<Object> response = jwtService.resetPassword(userService.findUserByUsername("sensorBIM").getEmail()).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals("user.email.sendEmail.Success", response.getMessage());
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void testResetPasswordWithInvalidEmail() {
        Response<Object> response = jwtService.resetPassword("thisisaninvalidEmail@somemail.com").getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals("user.email.invalid", response.getMessage());
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
        Assert.assertNull(response.getBody());
    }

}
