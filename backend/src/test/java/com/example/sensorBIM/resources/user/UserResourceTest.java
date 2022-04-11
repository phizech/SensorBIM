package com.example.sensorBIM.resources.user;

import com.example.sensorBIM.HttpBody.ResetPasswordBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Enums.UserRole;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.resources.User.UserResource;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserResourceTest {

    @Autowired
    private UserResource userResource;

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = setBasicValues();
        user.setUsername("addnewuser");
        user.setEmail("thisisanewemail@gmail.com");
        Response<User> response = userResource.addUser(user).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        Assert.assertEquals("user.savingSuccess", response.getMessage());
    }

    @Test
    public void testAddUserWithInvalidEmail() {
        String email = userService.findUsers().get(0).getEmail();
        User user = setBasicValues();
        user.setUsername("addneinvalidwuser");
        user.setEmail(email);
        Response<User> response = userResource.addUser(user).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
        Assert.assertEquals("user.email.alreadyExists", response.getMessage());
    }

    @Test
    public void testDeleteUser() {
        User user = userService.findUserByUsername("wanda");
        Assert.assertNotNull(user);
        userResource.getUserById(user.getId());
        userResource.deleteUser(user);
        Assert.assertFalse(userResource.getAllUsers().getBody().contains(user));
    }

    @Test
    public void testUpdateUser() {
        User user = userService.findUserByUsername("peterparker");
        Assert.assertNotNull(user);
        String oldName = user.getFirstName();
        user.setFirstName("New Peter");
        User saved = Objects.requireNonNull(userResource.updateUser(user).getBody()).getBody();
        Assert.assertNotNull(user);
        Assert.assertNotEquals(oldName, saved.getFirstName());
    }

    @Test
    public void testGetUserById() {
        int numberOfUsers = userResource.getAllUsers().getBody().size();
        for (int i = 1; i <= numberOfUsers; i++) {
            Assert.assertNotNull(userResource.getUserById((long) i));
        }
    }

    @Test
    public void testAllUsers() {
        List<User> users = userResource.getAllUsers().getBody();
        Assert.assertNotNull(users);
        Assert.assertFalse(users.isEmpty());
        Assert.assertEquals(userService.findUsers().size(), users.size());
    }

    @Test
    public void testUpdatePassword() {
        User user = userService.findUserByUsername("hulk");
        String newPassword = "updatingThePassword!";
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), "password", newPassword, newPassword);
        Assert.assertEquals(resetPasswordBody.newPassword, resetPasswordBody.repeatedPassword);
        Response<Object> response = userResource.resetPassword(resetPasswordBody).getBody();
        assert response != null;
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        User saved = userService.findUserByUsername("hulk");
        Assert.assertNotEquals(saved.getPassword(), resetPasswordBody.oldPassword);
    }

    @Test
    public void testUpdatePasswordWithWrongOldPassword() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), "somePassword", "newPassword", "newPassword");
        Response<Object> response = userResource.resetPassword(resetPasswordBody).getBody();
        assert response != null;
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testUpdatePasswordWithWrongRepeatedPassword() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), user.getPassword(), "newPassword", "wrongRepeatedPassword");
        Response<Object> response = userResource.resetPassword(resetPasswordBody).getBody();
        assert response != null;
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
    }

    @Test
    public void testUpdatePasswordWithMatchingPasswords() {
        User user = userService.findUserByUsername("hulk");
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(user.getId(), user.getPassword(), user.getPassword(), user.getPassword());
        Response<Object> response = userResource.resetPassword(resetPasswordBody).getBody();
        assert response != null;
        Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
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
