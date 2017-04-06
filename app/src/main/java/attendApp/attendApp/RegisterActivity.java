

package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button createButton;
    EditText usernameField;
    EditText passwordField;
    EditText passwordFieldCheck;
    EditText fullNameField;
    CheckBox isTeacher;
    TextView registerFailed;
	ProgressBar bar;
    RaspberryPiCommunication comm;

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
		bar = (ProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);

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
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bar.setVisibility(View.VISIBLE);
						registerFailed.setText("");
					}
				});
                try {
                    comm = new RaspberryPiCommunication();
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();
                    String fullName = fullNameField.getText().toString();

					if(username.equals("") || password.equals("") || fullName.equals("")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								registerFailed.setText("All fields are required");
							}
						});
						return;
					}

					if(username.trim().length() == 0 || password.trim().length() == 0 || fullName.trim().length() == 0) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								registerFailed.setText(new String("Your username/password/name are illegal"));
							}
						});
						return;
					}

                    int isT;
                    if (isTeacher.isChecked()) isT = 1; //If they are a teacher this is one
                    else isT = 0; //Otherwise they're a student, 0
                    Boolean b = comm.sendDataToRaspberryPi(
                            new String[] { "1", username, password, fullName, Integer.toString(isT) });
                    if (!b) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								registerFailed.setText(new String("Data could not be sent"));
							}
						});
                        return;
                    }

                    final String[] split = comm.getDataFromRaspberryPi();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							registerFailed.setText(split[1]);
						}
					});

					startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							registerFailed.setText(new String("Unable to connect to server"));
						}
					});
                }
            }
        });
            networkThread.start();
    }
}

