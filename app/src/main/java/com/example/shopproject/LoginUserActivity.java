package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginUserActivity extends BaseActivity {

    Button loginBtn;
    EditText emailEditText;
    EditText passwordEditText;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        loginBtn = findViewById(R.id.login_user_login_btn);
        emailEditText = findViewById(R.id.login_user_email);
        passwordEditText = findViewById(R.id.login_user_password);
        registerBtn = findViewById(R.id.login_user_register_btn);
        registerBtn.setOnClickListener(view -> startActivity(new Intent(view.getContext(), RegisterUserActivity.class)));

        loginBtn.setOnClickListener(this::loginUser);
    }

    private void loginUser(View v) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        DbConnector dbConnector = new DbConnector(v.getContext());

        Pair<Integer, String> result = dbConnector.loginUser(email, password);
        log_info("result " + result);
        if (result.first > 0) {
            onSuccess(v, result);
        } else {
            onFail(v, result.second);
        }
    }

    @SuppressLint("ApplySharedPref")
    private void onSuccess(View v, Pair<Integer, String> result) {
        log_info("logged in: #" + result.first);
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra(LOGGED_USER_ID, result.first);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(LOGGED_USER_ID, result.first);
        editor.commit();
        startActivity(intent);
    }

    private void onFail(View v, String msg) {
        passwordEditText.setError(msg);
        Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}