package edu.monash.fit3027.teleporter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.monash.fit3027.teleporter.R;
import edu.monash.fit3027.teleporter.models.Job;

/**
 * Created by niaz on 19/5/17.
 */

public class PostJobActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText m_cPickupLocationEditText;
    private EditText m_cDropoffLocationEditText;
    private EditText m_cRecipientNameTextEdit;
    private EditText m_cItemTextEdit;
    private Spinner m_cDeliveryTypeSpinner;
    private Button m_cSubmitButton;
    private RadioGroup m_cItemTypes;
    private RadioButton m_cItemType;
    private RadioGroup m_cFoodTypes;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    String userID;
    int checkedRadioButton;

    //
    private Integer THRESHOLD = 2;
    private DelayAutoCompleteTextView geo_autocomplete;
    private ImageView geo_autocomplete_clear;
    private DelayAutoCompleteTextView geo_autocomplete2;
    private ImageView geo_autocomplete_clear2;
    //


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        firebaseAuth = FirebaseAuth.getInstance();
        //Jobs are save in a separate node to users in the database
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");
        progressDialog = new ProgressDialog(this);

        //Checks if user is logged in, if not, it redirects the user to the main page
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userID = user.getUid();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    toastMessage("You need to be signed in to view this page");
                }

            }
        };
        //Connecting input fields from the view to the controller
        //m_cPickupLocationEditText = (EditText) findViewById(R.id.pickupLocationEditText);
        m_cPickupLocationEditText = (EditText) findViewById(R.id.geo_autocomplete);
        m_cDropoffLocationEditText = (EditText) findViewById(R.id.geo_autocomplete2);
        m_cRecipientNameTextEdit = (EditText) findViewById(R.id.recipientNameTextEdit);
        m_cItemTextEdit = (EditText) findViewById(R.id.itemTextEdit);
        m_cDeliveryTypeSpinner = (Spinner) findViewById(R.id.deliveryTypeSpinner);
        m_cItemTypes = (RadioGroup) findViewById(R.id.radioGroup);
        m_cFoodTypes = (RadioGroup) findViewById(R.id.foodRadioGroup);
        m_cSubmitButton = (Button) findViewById(R.id.SubmitButton2);
        m_cSubmitButton.setOnClickListener(this);

        //Address autocomplete implementation

        geo_autocomplete_clear = (ImageView) findViewById(R.id.geo_autocomplete_clear);

        geo_autocomplete = (DelayAutoCompleteTextView) findViewById(R.id.geo_autocomplete);
        geo_autocomplete.setThreshold(THRESHOLD);
        geo_autocomplete.setAdapter(new GeoAutoCompleteAdapter(this)); // 'this' is Activity instance

        geo_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                geo_autocomplete.setText(result.getAddress());
            }
        });

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    geo_autocomplete_clear.setVisibility(View.VISIBLE);
                } else {
                    geo_autocomplete_clear.setVisibility(View.GONE);
                }
            }
        });

        geo_autocomplete_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                geo_autocomplete.setText("");
            }
        });


        //--------------------------------------

        geo_autocomplete_clear2 = (ImageView) findViewById(R.id.geo_autocomplete_clear2);

        geo_autocomplete2 = (DelayAutoCompleteTextView) findViewById(R.id.geo_autocomplete2);
        geo_autocomplete2.setThreshold(THRESHOLD);
        geo_autocomplete2.setAdapter(new GeoAutoCompleteAdapter(this)); // 'this' is Activity instance

        geo_autocomplete2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                geo_autocomplete2.setText(result.getAddress());
            }
        });

        geo_autocomplete2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    geo_autocomplete_clear2.setVisibility(View.VISIBLE);
                } else {
                    geo_autocomplete_clear2.setVisibility(View.GONE);
                }
            }
        });

        geo_autocomplete_clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                geo_autocomplete2.setText("");
            }
        });

        //-----------------------

        m_cFoodTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                checkedRadioButton = checkedId;


            }
        });

        m_cItemTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                m_cFoodTypes.clearCheck();
                checkedRadioButton = checkedId;


            }
        });


        //Set up spinner for delivery types
        List<String> deliveryTypes = new ArrayList<>();
        deliveryTypes.add("2 hour delivery: 7AM - 9PM (Order before 7PM)");
        deliveryTypes.add("4 hour delivery: 7AM - 5PM (Order before 1PM)");
        deliveryTypes.add("Same Day delivery: 7AM - 5PM (Order before 12PM)");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deliveryTypes);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_cDeliveryTypeSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onResume() {
        super.onResume();
        if (mAuthListener != null) {
            firebaseAuth.addAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View view) {

        if (view == m_cSubmitButton) {
            //Take in values form all the input fields
            final String pickupaddress = m_cPickupLocationEditText.getText().toString();
            final String dropoffaddress = m_cDropoffLocationEditText.getText().toString();
            final String recipientname = m_cRecipientNameTextEdit.getText().toString();
            final String itemname = m_cItemTextEdit.getText().toString();
            final String deliverytype = m_cDeliveryTypeSpinner.getSelectedItem().toString();
            double startLat, startLng, endLat, endLng;
            String itemtype;

            try {
                //Set item type
                m_cItemType = (RadioButton) findViewById(checkedRadioButton);
                itemtype = m_cItemType.getText().toString();
            } catch (Exception e) {
                toastMessage("Choose type of item");


                if (pickupaddress.length() < 1) {
                    //pickup location is empty
                    toastMessage("Please enter pickup address");

                }
                return;
            }
            if (dropoffaddress.length() < 1) {
                //dropoff location is empty
                toastMessage("Please enter dropoff address");
                return;
            }
            if (recipientname.length() < 1) {
                //recipient name is empty
                toastMessage("Please enter recipient name");
                return;
            }
            if (itemname.length() < 1) {
                //item name is empty
                toastMessage("Please enter item name");
                return;
            }
            if (deliverytype.length() < 1) {
                //delivery type is not selected
                toastMessage("Please select the type of delivery");
                return;
            }
            //convert pickup and dropoff locations to longitude and latitude values to store in Fire base
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocationName(pickupaddress, 1);
                Address add = addresses.get(0);
                //String locality = add.getLocality();

                startLat = add.getLatitude();
                startLng = add.getLongitude();
            } catch (Exception e) {
                toastMessage("Pickup Address is invalid");
                return;
            }

            try {
                //Check if addresses are valid with the geocoder
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocationName(dropoffaddress, 1);
                Address add = addresses.get(0);


                endLat = add.getLatitude();
                endLng = add.getLongitude();
            } catch (Exception e) {
                toastMessage("Pickup Address is invalid");
                return;
            }

            progressDialog.setMessage("Posting Delivery Request Please Wait...");
            progressDialog.show();

            //If food item is selected change the text to show that it is food
            if (itemtype.equals("Hot") || itemtype.equals("Cold")) {
                itemtype = itemtype + " Food";
            }
            if (itemtype.equals("Neither")) {
                itemtype = "Food";
            }

            String teleporterID = "";   //set initial teleporter ID as null, will be changed when a teleporter takes up this job
            String jobId = databaseReference.push().getKey();
            Job newjob = new Job(jobId, userID, startLat, startLng, endLat, endLng, recipientname, itemname, itemtype, deliverytype, teleporterID);

            databaseReference.child(jobId).setValue(newjob);

            finish();

        }

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
