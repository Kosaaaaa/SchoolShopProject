package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public final static String BASE_LOG_TAG = "SHOP_PROJECT";
    final static String LOGGED_USER_ID = "logged_uid";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void log_info(String msg) {
        Log.i(BASE_LOG_TAG, msg);
    }

    public void switchActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout_user) {
            log_info("menu_logout_user");
            userLogout();
        } else {
            throw new IllegalStateException("Unexpected value: " + id);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ApplySharedPref")
    private void userLogout() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(LOGGED_USER_ID, -1);
        editor.commit();
        switchActivity(LoginUserActivity.class);
    }

}
