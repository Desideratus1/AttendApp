package com.example.drpac.attendapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AdministratorDashboard extends AppCompatActivity {

    Button startAttendance;
    Button requestNewClass;
    Button deleteClass;
    Button getAttendanceRecord;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_dashboard_layout);

        startAttendance = (Button) findViewById(R.id.startAttendance);
        requestNewClass = (Button) findViewById(R.id.requestClass);
        deleteClass = (Button) findViewById(R.id.deleteClass);
        getAttendanceRecord = (Button) findViewById(R.id.getAttendanceRecords);

        startAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        beginAttendancePeriod(view);
                    }
                });
        requestNewClass.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        createNewClass(view);
                    }
                });
        deleteClass.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteClass(view);
                    }
                });
        getAttendanceRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        getAttendanceRecord(view);
                    }
                });
    }

    private void beginAttendancePeriod(View view) {
        startActivity(new Intent(AdministratorDashboard.this, AdministratorAttendancePeriod.class));
    }

    private void createNewClass(View view) {
        startActivity(new Intent(AdministratorDashboard.this, AdministratorNewClass.class));
    }

    private void deleteClass(View view) {
        startActivity(new Intent(AdministratorDashboard.this, AdministratorDeleteClass.class));
    }

    private void getAttendanceRecord(View view) {
        startActivity(new Intent(AdministratorDashboard.this, AdministratorAttendancePeriod.class));
    }
}

