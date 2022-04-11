package com.example.sensorBIM.resources.User;

import com.example.sensorBIM.HttpBody.ResetPasswordBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserResource {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUserByID(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Response<User>> addUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Response<User>> updateUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<User> deleteUser(@RequestBody User user) {
        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Response<Object>> resetPassword(@RequestBody ResetPasswordBody resetPasswordBody) {
        return new ResponseEntity<>(userService.updatePassword(resetPasswordBody), HttpStatus.OK);
    }
}
