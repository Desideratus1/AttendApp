package attendApp.attendApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class StudentDashboard extends AppCompatActivity {

    Button submitAttendance;
    TextView attendanceResponse;
    String username;
    String response = "Unknown failure";
    boolean success = false;
	RaspberryPiCommunication comm;
	URL url;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_layout);

        submitAttendance = (Button) findViewById((R.id.submit_attendance));
        attendanceResponse = (TextView) findViewById(R.id.attendance_success);
        username = getIntent().getStringExtra("USERNAME");
        submitAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        submitAttendance();
                        if(success) {
                            attendanceResponse.setText("Attendance Recieved");
                        } else {
							attendanceResponse.setText("Sorry, you haven't set GPS permissions.");
						}
                    }
                });
    }

    //Return true if successfully submitted
    private void submitAttendance() {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
				double lat = 0;
				double lon = 0;
				try {
					comm = new RaspberryPiCommunication();
					url = new URL("http://freegeoip.net/json");
					Scanner scanner = new Scanner(url.openStream());
					String str = "";
					while(scanner.hasNextLine()) {
						str = str + scanner.nextLine();
					}
					scanner.close();
					String[] split = str.split(",");
					for(String string : split) {
						if(string.contains("latitude")) {
							String[] moresplit = string.split(":");
							lat = Double.parseDouble(moresplit[1]);
						}
						if(string.contains("longitude")) {
							String[] moresplit = string.split(":");
							lon = Double.parseDouble(moresplit[1]);
						}
					}
					if(lat == 0 || lon == 0) throw new Exception("Failure");
				} catch (Exception e) {
					response = "Something has failed with the GPS. There is nothing we can do.";
					return;
				}
                try {
                    Boolean b = comm.sendDataToRaspberryPi("2&" + username + "&" + lat + "&" + lon);
					comm.end();
                    if(!b) {
                        response = "Data could not be sent";
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
					Log.d("We have reached thd1111", "Why are we frozen?");

                    int code = Integer.parseInt(split[0]);
                    if(code > 99) { //100+ is an error
                        response = split[1];
						Log.d("We have reached the end", "Why are we frozen?");
                    }
                    success = true;
                    return;
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
    }
}

