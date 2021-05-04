package com.smart_learn.presenter.activities.authentication.helpers;

import android.text.TextUtils;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.presenter.helpers.ApplicationController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm extends BasicForm {

    private String profile;
    private String retypedPassword;

    public RegisterForm(String profile, String email, String password, String retypedPassword) {
        super(email, password);
        this.profile = profile == null? null : profile.trim();
        this.retypedPassword = retypedPassword == null? null : retypedPassword.trim();
    }

    public ResponseInfo goodRegisterCredentials(){
        if(!TextUtils.isEmpty(profile) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(retypedPassword)){
            ResponseInfo emailStatus = super.goodEmail(email);
            ResponseInfo passwordStatus = super.goodPassword(password);
            if(!emailStatus.isOk()){
                return emailStatus;
            }
            if(!passwordStatus.isOk()){
                return passwordStatus;
            }
            if(!password.equals(retypedPassword)){
                return new ResponseInfo(false, ApplicationController.getInstance().getString(R.string.passwords_not_matching));
            }
            return new ResponseInfo(true, "");
        }
        return new ResponseInfo(false, ApplicationController.getInstance().getString(R.string.empty_credentials));
    }

    public void updateFromLoginForm(LoginForm loginForm){
        if(loginForm != null){
            email = loginForm.getEmail();
            password = loginForm.getPassword();
        }
    }
}
