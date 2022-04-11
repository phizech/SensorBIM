package com.example.sensorBIM.HttpBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewUserRequestBody {

    private String username;

    private String lastName;

    private String firstName;

    private String email;

    private String password;

    private String repeatedPassword;
}
