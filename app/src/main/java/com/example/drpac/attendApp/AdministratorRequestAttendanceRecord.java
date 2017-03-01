package com.example.drpac.attendApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorRequestAttendanceRecord extends AppCompatActivity {
    Button requestRecord;
    TextView requestRecordText;
    EditText className;
    EditText email;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_class_layout);

        requestRecord = (Button) findViewById(R.id.request_record);
        requestRecordText = (TextView) findViewById(R.id.request_record_text);
        className = (EditText) findViewById(R.id.class_name);
        email = (EditText) findViewById(R.id.request_record_email);
        requestRecordText.setVisibility(View.INVISIBLE);

        requestRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        requestRecord(view);
                    }
                });
    }

    private void requestRecord(View view) {
        String cl = className.getText().toString();
        String em = email.getText().toString();
        /*
        TODO: SEND THIS STUFF TO THE PI
            WHAT WE SEND:
                STRING: USERNAME
                STRING: CLASS NAME
            WHAT WE GET: BOOLEAN SAYING IF IT WAS SUCCESSFUL
        Here's what i'm thinking. Just ask them for an email address, and send them the .csv file
         */
    }
}
