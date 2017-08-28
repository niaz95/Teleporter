package edu.monash.fit3027.teleporter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextClock;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.monash.fit3027.teleporter.models.Job;

public class TeleporterHomeActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseDatabase mDatabase;
    private ListView ActiveJobsList;
    LocationManager locationManager;
    private DatabaseReference myRef;
    private NavigationView mNavigationView;
    private FirebaseAuth firebaseAuth;
    private TeleporterJobsList adapter;
    List<Job> jobList;

    //Take LatLng to set markers for on map for delivery pickup and dropoff locations
    double StartLat;
    double StartLng;
    double EndLat;
    double EndLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_teleporter);

        //Set up Firebase elements
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("jobs");
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        String userEmail = currentuser.getEmail();
        String userName = currentuser.getDisplayName();


        //set up Jobs list
        ActiveJobsList = (ListView) findViewById(R.id.activeJobsList);
        jobList = new ArrayList<>();

        ActiveJobsList.setOnItemClickListener(this);
        ActiveJobsList.setOnItemLongClickListener(this);


        //set up Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //homeDrawerLayout
        mNavigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mNavigationView.setNavigationItemSelectedListener(this);
        mToggle.syncState();
        //Set up drawer header with user name and email
        View headerView = mNavigationView.getHeaderView(0);
        TextView nav_userEmail = (TextView) headerView.findViewById(R.id.displayEmailTextView);
        TextView nav_userName = (TextView) headerView.findViewById(R.id.displayNameTextView);
        nav_userName.setText(userName);
        nav_userEmail.setText(userEmail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Check if network provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get the longitude and latitude of current location
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //Create a latLng object for the values
                    LatLng latLng = new LatLng(latitude, longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ",";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f));
                    } catch (IOException e) {
                        System.out.print("Error: " + e.getMessage());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get the longitude and latitude of current location
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //Create a latLng object for the values
                    LatLng latLng = new LatLng(latitude, longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ",";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                    } catch(IOException e) {
                        System.out.print("Error: "+e.getMessage());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

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


        //Setup a default marker at Monash Caulfield
        mMap = googleMap;
        LatLng monash = new LatLng(-37.876823,145.045837);
        //mMap.addMarker(new MarkerOptions().position(monash));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(monash, 12f));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Set up drawer toggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clear current list of available jobs
                jobList.clear();
                for(DataSnapshot jobsSnapshot: dataSnapshot.getChildren()){
                    //Get available jobs from firebase database
                    Job job = jobsSnapshot.getValue(Job.class);
                    String teleportId = job.getTeleporterId();
                    if(teleportId.equals("")){
                        jobList.add(job);
                    }


                }

                adapter = new TeleporterJobsList(TeleporterHomeActivity.this,jobList);
                ActiveJobsList.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Navigation item clicks handled here
        int id = item.getItemId();
        //Toast.makeText(this,"This is "+id,Toast.LENGTH_SHORT).show();

        if(id == R.id.new_job){
            startActivity(new Intent(getApplicationContext(),PostJobActivity.class));
        }else if(id == R.id.nav_logout){
            //Add functionality to log out user
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Clear markers from map
        mMap.clear();
        //Set markers for pickup and dropoff locations on the map
        Job currentjob = jobList.get(position);
        StartLat = currentjob.getM_sStartLat();
        StartLng = currentjob.getM_sStartLng();
        EndLat = currentjob.getM_sEndLat();
        EndLng = currentjob.getM_sEndLng();

        LatLng pickupLocation = new LatLng(StartLat,StartLng);
        mMap.addMarker(new MarkerOptions().position(pickupLocation));

        LatLng dropoffLocation = new LatLng(EndLat,EndLng);
        mMap.addMarker(new MarkerOptions().position(dropoffLocation));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 11f));









    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //Shows details of selected job on a new activity

        Intent newIntent = new Intent(this,TeleporterViewJobActivity.class);
        newIntent.putExtra("Job",jobList.get(position));
        //Toast.makeText(this,jobList.get(position).getM_sItemName(),Toast.LENGTH_SHORT).show();
        startActivity(newIntent);
        return true;
    }
}