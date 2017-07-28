package edu.monash.fit3027.teleporter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import edu.monash.fit3027.teleporter.models.Job;
import edu.monash.fit3027.teleporter.models.User;

public class UserNavigation extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private TextView deliveryStatus;
    private TextView driverName;
    private TextView recipientNameTextView;
    private TextView itemTextView;
    private ImageButton callButton;
    private String jobId;
    private Job currentJob;
    private String phoneNo;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    //Take LatLng to set markers for on map for delivery pickup and dropoff locations
    double StartLat;
    double StartLng;
    double EndLat;
    double EndLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_navigation);

        //Connect firebase elements
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");

        //Connect UI Elements
        deliveryStatus = (TextView) findViewById(R.id.deliveryStatusTextView);
        driverName = (TextView) findViewById(R.id.textView3);
        recipientNameTextView = (TextView) findViewById(R.id.recipientNameTextView2);
        itemTextView = (TextView) findViewById(R.id.itemNameTextView2);
        callButton = (ImageButton) findViewById(R.id.callButton2);
        callButton.setOnClickListener(this);

        //Get the job details from the previous page
        Intent intent = getIntent();
        currentJob = intent.getParcelableExtra("Job");
        jobId = currentJob.getJobId();
        String jobDelivery = currentJob.getM_sDeliveryType();
        String item = currentJob.getM_sItemName();
        String recipient = currentJob.getM_sRecipientName();
        final String teleporterId = currentJob.getTeleporterId();



        //Set the status of the job on the text views
        deliveryStatus.setText(jobDelivery);
        recipientNameTextView.setText(recipient);
        itemTextView.setText(item);
        if(teleporterId.equals("")){
            //If no one has taken up the job the driver name and option to call are removed
            driverName.setVisibility(View.INVISIBLE);
            callButton.setVisibility(View.INVISIBLE);
        }else{
            //Get teleporter phone number and name
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot users: dataSnapshot.getChildren()){
                        User currentTeleporter = users.getValue(User.class);
                        String Uid = currentTeleporter.getUserId();
                        if(teleporterId.equals(Uid)){
                            String teleporterName = currentTeleporter.getFirstName();
                            driverName.setText(teleporterName);
                            phoneNo = currentTeleporter.getPhoneNo();


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



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

        //Set dropoff and pickup locations on the map
        StartLat = currentJob.getM_sStartLat();
        StartLng = currentJob.getM_sStartLng();
        EndLat = currentJob.getM_sEndLat();
        EndLng = currentJob.getM_sEndLng();

        LatLng pickupLocation = new LatLng(StartLat,StartLng);
        mMap.addMarker(new MarkerOptions().position(pickupLocation));

        LatLng dropoffLocation = new LatLng(EndLat,EndLng);
        mMap.addMarker(new MarkerOptions().position(dropoffLocation));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 11f));
    }


    @Override
    public void onClick(View view) {
        if(view == callButton){
            //Call the teleporter currently on the job
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String formatString = "tel:" + phoneNo;
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

        }

    }
}
