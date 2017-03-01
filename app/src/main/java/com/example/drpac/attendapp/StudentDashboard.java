package com.example.drpac.attendApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentDashboard extends AppCompatActivity {

    Button submitAttendance;
    TextView success;
    TextView fail;
    private LocationManager locManager;
    private LocationListener locListener;
    Location loc;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_layout);

        submitAttendance = (Button) findViewById((R.id.submit_attendance));
        success = (TextView) findViewById(R.id.attendance_success);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                        }
                        , 10);
            }
            return;
        }

        success.setVisibility(View.INVISIBLE);
        fail = (TextView) findViewById(R.id.attendance_fail);
        fail.setVisibility(View.INVISIBLE);

        submitAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        locManager.requestLocationUpdates("gps", 5000, 0, locListener); //This is handled
                        //Set both the success text and fail text to invisible
                        success.setVisibility(View.INVISIBLE);
                        fail.setVisibility(View.INVISIBLE);
                        if(submitAttendance("admin")) {
                            /*
                            TODO: replace "admin" with the full name of their account.
                             */
                            //If their full name exists, mark them as here and give the "SUCCESS" text
                            success.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.INVISIBLE);
                        }
                        else {
                            //Otherwise they failed but...
                            //TODO: Maybe tell them why it failed? Too far away?
                            fail.setVisibility(View.VISIBLE);
                            success.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    submitAttendance("admin");
        }
    }

    //Return true if successfully submitted
    private boolean submitAttendance(String username) {
        /*
        TODO: Replace this with data sent to and from the Raspberry Pi
        WHAT IS BEING SENT:
            Full name
        WHAT IS BEING RECIEVED:
            Boolean stating whether or not the attendance for that name has succeeded
         */

        return (username.equals("admin"));
    }
}

