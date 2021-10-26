package com.smart_learn.presenter.user.activities.authentication.helpers;

import com.smart_learn.presenter.user.activities.authentication.helpers.BasicForm;
import com.smart_learn.presenter.user.activities.authentication.helpers.LoginForm;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm extends BasicForm {

    public final static int MAX_PROFILE_LENGTH = 20;

    // TODO: make a regex for emails (email is verified by firebase but check it before also)
    //public final static Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public final static Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE);

    // TODO: make a regex for password
    // at least one letter and one number
    //public final static Pattern PASSWORD_REGEX_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]$", Pattern.CASE_INSENSITIVE);
    public final static Pattern PASSWORD_REGEX_PATTERN = Pattern.compile("^(.+)$", Pattern.CASE_INSENSITIVE);

    private String profile;
    private String retypedEmail;
    private String retypedPassword;

    public RegisterForm(String profile, String email, String retypedEmail, String password, String retypedPassword) {
        super(email, password);
        this.profile = profile == null? null : profile.trim();
        this.retypedEmail = retypedEmail == null? null : retypedEmail.trim();
        this.retypedPassword = retypedPassword == null? null : retypedPassword.trim();
    }

    public void updateFromLoginForm(LoginForm loginForm){
        if(loginForm != null){
            email = loginForm.getEmail();
            password = loginForm.getPassword();
        }
    }
}
