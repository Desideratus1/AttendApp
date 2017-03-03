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

public class AdministratorDeleteClass extends AppCompatActivity {
    Button deleteClass;
    TextView deleteClassText;
    EditText className;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_class_layout);

        deleteClass = (Button) findViewById(R.id.delete_class);
        deleteClassText = (TextView) findViewById(R.id.delete_class_text);
        className = (EditText) findViewById(R.id.class_name);

        deleteClassText.setVisibility(View.INVISIBLE);

        deleteClass.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteClass(view);
                    }
                });
    }

    private void deleteClass(View view) {
        String cl = className.getText().toString();
        /*
        TODO: SEND THIS STUFF TO THE PI
            WHAT WE SEND:
                STRING: USERNAME
            WHAT WE GET: BOOLEAN SAYING IF IT WAS SUCCESSFUL
         */
    }
}
