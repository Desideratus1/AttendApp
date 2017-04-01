

package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    Button createButton;
    EditText usernameField;
    EditText passwordField;
    EditText passwordFieldCheck;
    EditText fullNameField;
    CheckBox isTeacher;
    TextView registerFailed;
    RaspberryPiCommunication comm;
    boolean flag = false;
    String response = "Unknown failure";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        createButton = (Button) findViewById(R.id.createAccount);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        passwordFieldCheck = (EditText) findViewById(R.id.passwordCheck);
        isTeacher = (CheckBox) findViewById(R.id.isTeacher);
        fullNameField = (EditText) findViewById(R.id.fullName);
        registerFailed = (TextView) findViewById(R.id.registerText);

        createButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        register(view);
                    }
                });
    }

    /**
     * Function to determine whether or not the passwords to make the accountmatch.
     * @param view
     * @return if both passwords match, true. Else, false
     */
    private boolean passwordsMatch(View view) {
        //We shouldn't let them create the account if they can't enter the password twice
        String password = passwordField.getText().toString();
        String passwordCheck = passwordFieldCheck.getText().toString();

        return (password.equals(passwordCheck));
    }

    /**
     * Function that checks if the passwords match. If they do, create the account and go back to the login screen
     * @param view
     */
    private void register(View view) {
        if (!passwordsMatch(view)) {
            registerFailed.setText("Passwords do not match");
            return;
        }
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    comm = new RaspberryPiCommunication();
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();
                    String fullName = fullNameField.getText().toString();
                    int isT;
                    if (isTeacher.isChecked()) isT = 1; //If they are a teacher this is one
                    else isT = 0; //Otherwise they're a student, 0
                    Boolean b = comm.sendDataToRaspberryPi(
                            new String[] { "1", username, password, fullName, Integer.toString(isT) });
                    if (!b) {
                        response = "Data could not be sent";
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);
                    if (code > 99) { //100+ is an error
                        response = split[1];
                        return;
                    }
                    flag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Networking errors; Unable to connect to server";
                }
            }
        });
        networkThread.start();
		try {
			networkThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if (flag) startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        else registerFailed.setText(response);
    }
}

