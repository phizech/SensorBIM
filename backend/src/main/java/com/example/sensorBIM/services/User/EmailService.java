package com.example.sensorBIM.services.User;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * if a user forgot their email, an email is sent to them with a new password
     * if sending the email was not successful, an error message occurs
     *
     * @param user    the user which forgot the password
     * @param subject the subject of the email
     * @param message the message to send to the user
     * @return a response; whether it was a successful or not. if there was a failure, a error message is sent as well
     */
    public Response<Object> sendEmail(User user, String subject, String message) {
        var msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject(subject);
        msg.setText(message);
        try {
            this.javaMailSender.send(msg);
        } catch (MailException e) {
            return new Response<>(ResponseStatus.FAILURE, "user.email.sendEmail.Failure", null);
        }
        return new Response<>(ResponseStatus.SUCCESS, "user.email.sendEmail.Success", null);
    }
}
