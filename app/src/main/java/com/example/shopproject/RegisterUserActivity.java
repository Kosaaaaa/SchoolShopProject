package com.example.shopproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopproject.models.UserModel;

public class RegisterUserActivity extends BaseActivity {

    EditText emailEditText;
    EditText passwordEditText;
    EditText password2EditText;
    EditText nameEditText;
    EditText cityEditText;
    EditText zipCodeEditText;
    EditText address1EditText;
    EditText address2EditText;

    EditText[] requiredFields;

    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailEditText = findViewById(R.id.register_user_email);
        passwordEditText = findViewById(R.id.register_user_password);
        password2EditText = findViewById(R.id.register_user_password2);
        nameEditText = findViewById(R.id.register_user_name);
        cityEditText = findViewById(R.id.register_user_city);
        zipCodeEditText = findViewById(R.id.register_user_zip_code);
        address1EditText = findViewById(R.id.register_user_address_1);
        address2EditText = findViewById(R.id.register_user_address_2);
        registerBtn = findViewById(R.id.register_user_register_btn);

        requiredFields = new EditText[]{
                emailEditText, passwordEditText, password2EditText,
                nameEditText, cityEditText, zipCodeEditText, address1EditText};

        registerBtn.setOnClickListener(this::registerUser);
    }

    private boolean checkRequiredFields() {
        boolean isValid = true;
        for (EditText editText : requiredFields) {
            if (TextUtils.isEmpty(editText.getText())) {
                editText.setError("This field is required");
                isValid = false;
            }
        }
        return isValid;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void registerUser(View v) {
        boolean isValid = checkRequiredFields();
        String password = passwordEditText.getText().toString();
        String password2 = password2EditText.getText().toString();

        if (!password.equals(password2)) {
            password2EditText.setError("Repeat Password don't match");
            isValid = false;
        }
        String email = emailEditText.getText().toString();

        if (!isValidEmail(email)) {
            emailEditText.setError("Email is not valid.");
            isValid = false;
        }


        String name = nameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String zipCode = zipCodeEditText.getText().toString();
        String address1 = address1EditText.getText().toString();
        String address2 = address2EditText.getText().toString();
        if (!isValid) {
            return;
        }
        password = DbConnector.sha256String(password);
        UserModel userModel = new UserModel(email, password, name, city, zipCode, address1, address2);
        DbConnector dbConnector = new DbConnector(v.getContext());

        Pair<Integer, String> result = dbConnector.registerUser(userModel);

        if (result.first > 0) {
            Toast.makeText(v.getContext(), result.second, Toast.LENGTH_LONG).show();
            switchActivity(LoginUserActivity.class);
        } else {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Register User Error")
                    .setMessage(result.second)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}