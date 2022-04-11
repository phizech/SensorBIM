package com.example.sensorBIM.HttpBody;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordBody {

    public final long userId;

    public final String oldPassword;

    public final String newPassword;

    public final String repeatedPassword;

}
