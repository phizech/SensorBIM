package com.example.sensorBIM.services.User;

import com.example.sensorBIM.HttpBody.NewUserRequestBody;
import com.example.sensorBIM.HttpBody.ResetPasswordBody;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Enums.UserRole;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.repository.UserRepository;
import com.example.sensorBIM.services.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * saves the user.
     * if the admin is creating the user, an email is sent to them with a password
     * if the user is creating their own account, no email is sent
     *
     * @param user the new user
     * @return returns whether saving the user was successful, including the user and an error message if an error occurred
     */
    public Response<User> saveUser(User user) {
        if (userRepository.getByUsername(user.getUsername()) != null) {
            return new Response<>(ResponseStatus.FAILURE, "user.username.taken", null);
        } else if (userRepository.getUserByEmail(user.getEmail()) != null) {
            return new Response<>(ResponseStatus.FAILURE, "user.email.alreadyExists", null);
        }
        if (user.passwordMatches("")) {
            String generatedPassword = this.generatePassword();
            user.setPassword(generatedPassword);
            String message = "Hallo " + user.getFirstName() + "!\n\n" +
                    "Ein Account wurde erfolgreich für Sie erstellt." + "\n" +
                    "Ihr username lautet " + user.getUsername() + " und ihr Passwort\n" +
                    generatedPassword + "\n\n" +
                    "Sie können ihre Daten unter " + Constants.link + " ändern.";
            Response emailResponse = emailService.sendEmail(user, "Account für SensorBIM", message);
            if (emailResponse.getResponseStatus().equals(ResponseStatus.FAILURE)) {
                return emailResponse;
            }
        }
        User saved = userRepository.save(user);
        if (saved != null) {
            return new Response(ResponseStatus.SUCCESS, "user.savingSuccess", userRepository.save(user));
        }
        return new Response(ResponseStatus.FAILURE, "user.savingFailed", saved);
    }

    /**
     * update a user. this is only allowed, if the username and email are not already taken
     *
     * @param user to update
     * @return returns whether saving the user was successful, including the user and an error message if an error occurred
     */
    public Response<User> updateUser(User user) {
        if (userRepository.getByUsername(user.getUsername()) != null) {
            if (userRepository.getByUsername(user.getUsername()).getId() != user.getId()) {
                return new Response<>(ResponseStatus.FAILURE, "user.username.taken", null);
            }
        } else if (userRepository.getUserByEmail(user.getEmail()) != null) {
            if (userRepository.getByUsername(user.getEmail()).getId() != user.getId()) {
                return new Response<>(ResponseStatus.FAILURE, "user.eamil.alreadyExists", null);
            }
        }
        User saved = userRepository.save(user);
        if (saved != null) {
            return new Response<>(ResponseStatus.SUCCESS, "user.updateSuccess", user);
        }
        return new Response<>(ResponseStatus.FAILURE, "user.updateFailure", user);
    }

    /**
     * update the password of a user. only allowed if the new and repeated password match and the old
     * password is correct
     *
     * @param resetPasswordBody the body containing the old, new and repeated password
     * @return a success or error message
     */
    public Response<Object> updatePassword(ResetPasswordBody resetPasswordBody) {
        User user = findUserByID(resetPasswordBody.userId);
        if (!user.passwordMatches(resetPasswordBody.oldPassword)) {
            return new Response<>(ResponseStatus.FAILURE, "user.password.OldInvalid", null);
        } else if (user.passwordMatches(resetPasswordBody.newPassword)) {
            return new Response<>(ResponseStatus.FAILURE, "user.password.OldNewMatch", null);
        } else if (!resetPasswordBody.newPassword.equals(resetPasswordBody.repeatedPassword)) {
            return new Response<>(ResponseStatus.FAILURE, "user.password.NewRepeatedNotMatching", null);
        }
        user.setPassword(resetPasswordBody.newPassword);
        User saved = userRepository.save(user);
        if (saved == null) {
            return new Response<>(ResponseStatus.FAILURE, "user.password.savingError", null);
        }
        return new Response<>(ResponseStatus.SUCCESS, "user.password.savingSuccess", null);
    }


    /**
     * Sends a new email to a user who forgot his email
     *
     * @param email the email address of thee user
     * @return a success or error message
     */
    public Response<Object> resetPassword(String email) {
        User user = this.findUserByEmail(email);
        if (user != null) {
            String newPassword = generatePassword();
            user.setPassword(newPassword);
            this.saveUser(user);
            String message = "Hallo " + user.getUsername() + "!\nDein Neues Passwort lautet \n" + newPassword + "\nBitte diese Email löschen und das Passwort ändern!";
            return emailService.sendEmail(user, "Neues Passwort", message);
        }
        return new Response<>(ResponseStatus.FAILURE, "user.email.invalid", null);
    }

    /**
     * generate a random password. this method is used if the admin creates a user or if the user forgot their password
     *
     * @return the new password
     */
    private String generatePassword() {
        // code from https://www.baeldung.com/java-generate-secure-password
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "error";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User findUserByID(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    public User findUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public Response<User> createUser(NewUserRequestBody newUser) {
        if ((!Objects.equals(newUser.getPassword(), newUser.getRepeatedPassword())) && newUser.getRepeatedPassword() != null) {
            return new Response<>(ResponseStatus.FAILURE, "user.password.createRepeatedWrong", null);
        }
        User user = new User(newUser.getFirstName(), newUser.getLastName(), newUser.getUsername(), newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setUserRole(UserRole.MEMBER);
        user.setBuildings(new HashSet<>());
        return saveUser(user);
    }
}
