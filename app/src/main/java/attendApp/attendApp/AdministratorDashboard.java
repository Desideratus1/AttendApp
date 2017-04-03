package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdministratorDashboard extends AppCompatActivity {

    Button startAttendance;
    Button requestNewClass;
    Button deleteClass;
    Button getAttendanceRecord;
    String username;
    String password;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_dashboard_layout);

        username = getIntent().getStringExtra("USERNAME");
        password = getIntent().getStringExtra("PASSWORD");
        startAttendance = (Button) findViewById(R.id.startAttendance);
        requestNewClass = (Button) findViewById(R.id.requestClass);
        deleteClass = (Button) findViewById(R.id.delete_class);
        getAttendanceRecord = (Button) findViewById(R.id.getAttendanceRecords);

        //Whenever a certain button is clicked, do the function
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

    //All these 4 functions do is tell us where to go when a button is pressed.
    private void beginAttendancePeriod(View view) {
        Intent k = new Intent(AdministratorDashboard.this, AdministratorAttendancePeriod.class);
        k.putExtra("USERNAME", username);
        k.putExtra("PASSWORD", password);
        startActivity(k);
    }

    private void createNewClass(View view) {
        Intent k = new Intent(AdministratorDashboard.this, AdministratorNewClass.class);
        k.putExtra("USERNAME", username);
        k.putExtra("PASSWORD", password);
        startActivity(k);
    }

    private void deleteClass(View view) {
        Intent k = new Intent(AdministratorDashboard.this, AdministratorDeleteClass.class);
        k.putExtra("USERNAME", username);
        k.putExtra("PASSWORD", password);
        startActivity(k);
    }

    private void getAttendanceRecord(View view) {
        Intent k = new Intent(AdministratorDashboard.this, AdministratorRequestAttendanceRecord.class);
        k.putExtra("USERNAME", username);
        k.putExtra("PASSWORD", password);
        startActivity(k);
    }
}

