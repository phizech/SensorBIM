package com.example.sensorBIM.JWT;

import com.example.sensorBIM.HttpBody.AuthRequest;
import com.example.sensorBIM.HttpBody.NewUserRequestBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class JWTService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(authRequest.getUsername()));
        response.put("user", userService.findUserByUsername(authRequest.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<Response<Object>> resetPassword(@PathVariable("email") String email) {
        return new ResponseEntity<>(userService.resetPassword(email), HttpStatus.OK);
    }

    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public ResponseEntity<Response<User>> createAccount(@RequestBody NewUserRequestBody user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

}
