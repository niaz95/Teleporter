package edu.monash.fit3027.teleporter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import edu.monash.fit3027.teleporter.models.Job;
import edu.monash.fit3027.teleporter.models.User;

public class TeleporterNavigation extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private ImageButton callButton;
    private ImageButton messageButton;
    private TextView clientName;
    private TextView recipientName;
    private Button progressButton;
    private Button navigateButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference jobsRef;
    private DatabaseReference usersRef;
    private FirebaseDatabase mDatabase;
    private GoogleMap mMap;
    private String clientUId;
    private String jobId;
    private Job currentJob;
    private String clientNo;
    private String deliveryStatus;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleporter_navigation);
        //Connect firebase elements
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        usersRef = mDatabase.getReference("users");
        jobsRef = mDatabase.getReference("jobs");
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        userId = currentuser.getUid();
        //Get job object from previous activity
        Intent intent = getIntent();
        currentJob = intent.getParcelableExtra("Job");
        clientUId = currentJob.getUserId();
        jobId = currentJob.getJobId();



        deliveryStatus = "Accepted";
        //Update the job with the teleporter Id to show that the job has been accepted
        Job updatedjob = new Job(currentJob.getJobId(),currentJob.getUserId(),currentJob.getM_sStartLat(),currentJob.getM_sStartLng(),currentJob.getM_sEndLat(),currentJob.getM_sEndLng(),currentJob.getM_sRecipientName(),currentJob.getM_sItemName(),currentJob.getM_sItemType(),deliveryStatus,userId);

        jobsRef.child(jobId).setValue(updatedjob);


        //Connect UI elements
        callButton = (ImageButton) findViewById(R.id.callButton);
        messageButton = (ImageButton) findViewById(R.id.messageButton);
        clientName = (TextView) findViewById(R.id.clientNameTextView);
        recipientName = (TextView) findViewById(R.id.recipientTextView);
        progressButton = (Button) findViewById(R.id.progressButton);
        navigateButton = (Button) findViewById(R.id.navigateButton);
        progressButton.setOnClickListener(this);
        navigateButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        callButton.setOnClickListener(this);

        String recipientText = "Deliver to " + currentJob.getM_sRecipientName();
        recipientName.setText(recipientText);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng showAddress;
        // Add a marker in Sydney and move the camera
        String currentProgress = progressButton.getText().toString();
        if(currentProgress.equals("Picked Up")){
            showAddress = new LatLng(currentJob.getM_sEndLat(),currentJob.getM_sEndLng());
        }else{
            showAddress = new LatLng(currentJob.getM_sStartLat(),currentJob.getM_sStartLng());
        }
        mMap.addMarker(new MarkerOptions().position(showAddress).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(showAddress, 12f));
    }

    @Override
    public void onStart() {
        super.onStart();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User clientuser = userSnapshot.getValue(User.class);

                    if (clientUId.equals(clientuser.getUserId())) {
                        String client = clientuser.getFirstName();
                        clientName.setText(client);
                        clientNo = clientuser.getPhoneNo();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == navigateButton) {
            //Navigate button functionality
            String currentProgress = progressButton.getText().toString();
            double lat = currentJob.getCurrentLat();
            double lng = currentJob.getCurrentLng();

            if (currentProgress.equals("Picked Up")) {
                lat = currentJob.getM_sStartLat();
                lng = currentJob.getM_sStartLng();
            } else if (currentProgress.equals("Delivered")) {
                lat = currentJob.getM_sEndLat();
                lng = currentJob.getM_sEndLng();
            }

            String format = "geo:0,0?q=" + lat + "," + lng + "Melbourne";
            Uri uri = Uri.parse(format);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mapIntent);
        }
        if (view == progressButton) {
            //progress button functionality
            String getProgress = progressButton.getText().toString();
            if (getProgress.equals("Picked Up")) {
                progressButton.setText("Delivered");
                int orangeColor = R.color.colorOrange;
                progressButton.setBackgroundResource(orangeColor);

                deliveryStatus = "Picked Up";
                //Update the job with the teleporter Id to show that the job has been accepted
                Job updatedjob = new Job(currentJob.getJobId(),currentJob.getUserId(),currentJob.getM_sStartLat(),currentJob.getM_sStartLng(),currentJob.getM_sEndLat(),currentJob.getM_sEndLng(),currentJob.getM_sRecipientName(),currentJob.getM_sItemName(),currentJob.getM_sItemType(),deliveryStatus,userId);

                jobsRef.child(jobId).setValue(updatedjob);
            } else if (getProgress.equals("Delivered")) {
                progressButton.setText("Picked Up");
                int blueColor = R.color.colorBlue;
                progressButton.setBackgroundResource(blueColor);

                deliveryStatus = "Completed";
                //Update the job with the teleporter Id to show that the job has been accepted
                Job updatedjob = new Job(currentJob.getJobId(),currentJob.getUserId(),currentJob.getM_sStartLat(),currentJob.getM_sStartLng(),currentJob.getM_sEndLat(),currentJob.getM_sEndLng(),currentJob.getM_sRecipientName(),currentJob.getM_sItemName(),currentJob.getM_sItemType(),deliveryStatus,userId);

                jobsRef.child(jobId).setValue(updatedjob);

                //Show alert dialog stating that the delivery is complete
                AlertDialog alertDialog = new AlertDialog.Builder(TeleporterNavigation.this).create();
                alertDialog.setTitle("Delivery Complete!");
                alertDialog.setMessage("Congratulations! You have completed the delivery! Click OK to go back and take up more deliveries.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
                startActivity(new Intent(getApplicationContext(), TeleporterHomeActivity.class));




            } else if (view == callButton) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String formatString = "tel:" + clientNo;
                    callIntent.setData(Uri.parse(formatString));
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }catch(Exception e){
                    System.out.print("Error: "+e.getMessage());
                    Toast.makeText(this,"Could not make phone call",Toast.LENGTH_SHORT).show();
                }
                if(view == messageButton){
                    //Send message funcionality
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String message = "Hello I am a Teleporter currently taking on your delivery";
                        smsManager.sendTextMessage(clientNo, null, message, null, null);
                        Toast.makeText(this, "Message Sent! Check messages", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        Toast.makeText(this,"Could not send message",Toast.LENGTH_SHORT).show();
                        System.out.print("Error: "+e.getMessage());
                    }
                }

            }
        }

    }
}
