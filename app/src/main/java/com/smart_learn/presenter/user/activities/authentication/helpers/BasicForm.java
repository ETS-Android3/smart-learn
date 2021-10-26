package com.smart_learn.presenter.user.activities.authentication.helpers;

import lombok.Getter;
import lombok.Setter;

public abstract class BasicForm {

    public final static int MAX_EMAIL_LENGTH = 100;
    public final static int MIN_PASSWORD_LENGTH = 8;
    public final static int MAX_PASSWORD_LENGTH = 50;

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

}
