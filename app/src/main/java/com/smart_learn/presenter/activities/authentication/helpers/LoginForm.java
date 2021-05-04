package com.smart_learn.presenter.activities.authentication.helpers;

import android.text.TextUtils;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.presenter.helpers.ApplicationController;

public class LoginForm extends BasicForm {

    public LoginForm(String email, String password) {
        super(email, password);
    }

    public ResponseInfo goodLoginCredentials(){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            ResponseInfo emailStatus = super.goodEmail(email);
            ResponseInfo passwordStatus = super.goodPassword(password);
            if(!emailStatus.isOk()){
                return emailStatus;
            }
            if(!passwordStatus.isOk()){
                return passwordStatus;
            }
            return new ResponseInfo(true, "");
        }
        return new ResponseInfo(false, ApplicationController.getInstance().getString(R.string.empty_credentials));
    }
}
