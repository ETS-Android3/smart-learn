package com.smart_learn.presenter.activities.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.databinding.ActivityAuthenticationBinding;
import com.smart_learn.presenter.activities.main.MainActivity;
import com.smart_learn.presenter.helpers.LoadingDialog;

public class AuthenticationActivity extends AppCompatActivity {

    private DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthenticationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarAuthenticationActivity);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        dismissLoadingDialog();
        super.onStop();
    }

    /** Used for resetting toolbar title, from fragments. */
    public void resetToolbar(String title){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(title);
        }
    }

    public void showLoadingDialog(String tag, String message){
        loadingDialog = new LoadingDialog(message);
        loadingDialog.show(getSupportFragmentManager(), tag);
    }

    public void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void goToMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}