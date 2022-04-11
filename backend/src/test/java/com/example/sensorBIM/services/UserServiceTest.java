package com.example.sensorBIM.services;

import com.example.sensorBIM.HttpBody.ResetPasswordBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Enums.UserRole;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private static long NUMBER_OF_ALL_USERS;
    @Autowired
    private UserService userService;

    @Before
    public void init() {
        NUMBER_OF_ALL_USERS = userService.findUsers().size();
    }

    @Test
    public void testSaveUser() {
        User user = setBasicValues();
        user.setUsername("jondoe1669");
        user.setEmail("sensorbimuibk@gmail.com");
        Response<User> respones = userService.saveUser(user);
        Assert.assertEquals(ResponseStatus.SUCCESS, respones.getResponseStatus());
        NUMBER_OF_ALL_USERS++;
    }

    @Test
    public void testSaveUserWithDuplicateUsername() {
        String username = "hulk";
        User alreadyExists = userService.findUserByUsername(username);
        Assert.assertNotNull(alreadyExists);
        User user = setBasicValues();
        user.setUsername(username);
        user.setEmail("sensorbimuibk@gmail.com");
        Response<User> respones = userService.saveUser(user);
        Assert.assertEquals(ResponseStatus.FAILURE, respones.getResponseStatus());
    }

    @Test
    public void testSaveUserWithDuplicateEmail() {
        String email = "steverogers@somemail.com";
        User alreadyExists = userService.findUserByEmail(email);
        Assert.assertNotNull(alreadyExists);
        User user = setBasicValues();
        user.setUsername("testUsername");
        user.setEmail(email);
        Response<User> respones = userService.saveUser(user);
        Assert.assertEquals(ResponseStatus.FAILURE, respones.getResponseStatus());
    }

    @Test
    public void testSaveUserWithInvalidEmail() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserRole(UserRole.MEMBER);
        user.setUsername("somerandomusername");
        user.setEmail("someinvalidemail");
        user.setPassword("");
        Response<User> response = userService.saveUser(user);
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
        Assert.assertEquals("user.email.sendEmail.Failure", response.getMessage());
    }

    @Test
    public void testDeleteUser() {
        User user = userService.findUserByUsername("tonystark");
        userService.deleteUser(user);
        Assert.assertFalse(userService.findUsers().contains(user));
        NUMBER_OF_ALL_USERS--;
    }

    @Test
    public void testUpdateUser() {
        User user = userService.findUserByUsername("steverogers");
        String oldFirstName = user.getFirstName();
        String oldLastName = user.getLastName();
        String oldEmail = user.getEmail();
        user.setFirstName("Steven");
        user.setLastName("Roger");
        user.setEmail("newemail");
        Response<User> response = userService.updateUser(user);
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        Assert.assertNotEquals(oldEmail, user.getEmail());
        Assert.assertNotEquals(oldFirstName, user.getFirstName());
        Assert.assertNotEquals(oldLastName, user.getLastName());
    }

    @Test
    public void testResetPassword() {
        Response<Object> response = userService.resetPassword("sensorbimuibk@gmail.com");
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
    }

    @Test
    public void testResetPasswordWithInvalidEmail() {
        Response<Object> response = userService.resetPassword("somerandomemail");
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testUpdatePassword() {
        User user = userService.findUserByUsername("tonystark");
        String newPassword = "newPassword";
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), "password", newPassword, newPassword);
        Response<Object> response = userService.updatePassword(resetPasswordBody);
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        Assert.assertEquals("user.password.savingSuccess", response.getMessage());
    }

    @Test
    public void testUpdatePasswordWithWrongOldPassword() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), "somePassword", "newPassword", "newPassword");
        Response<Object> response = userService.updatePassword(resetPasswordBody);
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testUpdatePasswordWithWrongRepeatedPassword() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), user.getPassword(), "newPassword", "wrongRepeatedPassword");
        Response<Object> response = userService.updatePassword(resetPasswordBody);
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testUpdatePasswordWithMatchingPasswords() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), user.getPassword(), user.getPassword(), user.getPassword());
        Response<Object> response = userService.updatePassword(resetPasswordBody);
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testGetAllUser() {
        Assert.assertEquals(NUMBER_OF_ALL_USERS, userService.findUsers().size());
    }

    @Test
    public void testGetUserByEmail() {
        String email = "steverogers@somemail.com";
        User user = userService.findUserByEmail(email);
        Assert.assertNotNull(user);
    }

    @Test
    public void testGetUserByUsername() {
        String username = "steverogers";
        User user = userService.findUserByUsername(username);
        Assert.assertNotNull(user);
    }

    private User setBasicValues() {
        User user = new User();
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserRole(UserRole.MEMBER);
        return user;
    }

}
