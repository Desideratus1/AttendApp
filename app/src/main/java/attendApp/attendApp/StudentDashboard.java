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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

public class StudentDashboard extends AppCompatActivity {

    Button submitAttendance;
    TextView attendanceResponse;
    String username;
    String response = "Unknown failure";
	ProgressBar bar;
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
		bar = (ProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);
        submitAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        submitAttendance();
                    }
                });
    }

	/**
	 * Function to attempt to submit attendance
	 */
	private void submitAttendance() {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bar.setVisibility(View.VISIBLE);
						attendanceResponse.setText("");
					}
				});
				double lat = 0;
				double lon = 0;
				try {
					comm = new RaspberryPiCommunication();
					url = new URL("http://freegeoip.net/json");
					URLConnection urlconn = url.openConnection();
					urlconn.setConnectTimeout(3000);
					urlconn.connect();
					Scanner scanner = new Scanner(urlconn.getInputStream());
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
					e.printStackTrace();
					response = "Something has failed with the GPS. Restart your wifi";
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							attendanceResponse.setText(response);
						}
					});
					return;
				}
                try {
                    Boolean b = comm.sendDataToRaspberryPi(
							new String[] {"2", username, Double.toString(lat), Double.toString(lon) });
                    if(!b) {
                        response = "Data could not be sent";
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								attendanceResponse.setText(response);
							}
						});
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
					response = split[1];
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							attendanceResponse.setText(response);
						}
					});
                    return;
                } catch (Exception e) {
					response = "Networking errors; Unable to connect to server";
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							attendanceResponse.setText(response);
						}
					});
                }
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bar.setVisibility(View.INVISIBLE);
						attendanceResponse.setText(response);
					}
				});
            }
        });
		networkThread.start();
    }
}

