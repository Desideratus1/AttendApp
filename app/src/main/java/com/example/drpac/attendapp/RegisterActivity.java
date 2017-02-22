package com.example.drpac.attendapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    Button createButton;
    EditText usernameField;
    EditText passwordField;
    EditText passwordFieldCheck;
    EditText fullNameField;
    CheckBox isTeacher;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        createButton = (Button) findViewById(R.id.createAccount);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        passwordFieldCheck = (EditText) findViewById(R.id.passwordCheck);
        isTeacher = (CheckBox) findViewById(R.id.isTeacher);
        fullNameField = (EditText) findViewById(R.id.fullName);

        createButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        register(view);
                    }
                });
    }

    private boolean passwordsMatch(View view) {
        //We shouldn't let them create the account if they can't enter the password twice
        String password = passwordField.getText().toString();
        String passwordCheck = passwordFieldCheck.getText().toString();
        return (password.equals(passwordCheck));
    }

    private void register(View view) {
        if (passwordsMatch(view)) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            //TODO: Data to and from Raspberry Pi, send them back to the login screen if they succeeded
        }
    }
}

