package com.smart_learn.presenter.activities.authentication.helpers;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.presenter.helpers.ApplicationController;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

public abstract class BasicForm {

    private final static int MIN_PASSWORD_LENGTH = 6;
    private final static Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^(.+)@(.+)$");

    @Getter
    @Setter
    protected String email;
    @Getter
    @Setter
    protected String password;

    protected BasicForm(String email, String password) {
        this.email = email == null? null : email.trim();
        this.password = password == null? null : password.trim();
    }

    protected ResponseInfo goodEmail(String email){
        if(!EMAIL_REGEX_PATTERN.matcher(email).matches()){
            return new ResponseInfo(false, ApplicationController.getInstance().getString(R.string.email_not_valid));
        }
        return new ResponseInfo(true, "");
    }

    protected ResponseInfo goodPassword(String password){
        if(password.length() <= MIN_PASSWORD_LENGTH){
            return new ResponseInfo(false, ApplicationController.getInstance().getString(R.string.password_too_short));
        }
        return new ResponseInfo(true, "");
    }


}
