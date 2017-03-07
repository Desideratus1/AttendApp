package attendApp.attendApp;

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
    TextView response;
    private LocationManager locManager;
    private LocationListener locListener;
    Location loc;
    RaspberryPiCommunication comm = new RaspberryPiCommunication();
    String username;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_layout);

        submitAttendance = (Button) findViewById((R.id.submit_attendance));
        response = (TextView) findViewById(R.id.attendance_success);
        username = getIntent().getStringExtra("USERNAME");
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

        submitAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        locManager.requestLocationUpdates("gps", 5000, 0, locListener); //This is handled
                        if(submitAttendance()) {
                            /*
                            TODO: replace "admin" with the full name of their account.
                             */
                            //If their full name exists, mark them as here and give the "SUCCESS" text
                            response.setText("Attendance Recieved");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    submitAttendance();
        }
    }

    //Return true if successfully submitted
    private boolean submitAttendance() {

        Boolean b = comm.sendDataToRaspberryPi("2&" + username);
        if(!b) {
            response.setText("Data could not be sent");
            return false;
        }

        String[] split = comm.getDataFromRaspberryPi();
        int code = Integer.parseInt(split[0]);
        if(code > 99) { //100+ is an error
            response.setText(split[1]);
            return false;
        }
        return true;
        /*
        TODO: Replace this with data sent to and from the Raspberry Pi
        WHAT IS BEING SENT:
            Username
        WHAT IS BEING RECIEVED:
            Boolean stating whether or not the attendance for that name has succeeded
         */
    }
}
