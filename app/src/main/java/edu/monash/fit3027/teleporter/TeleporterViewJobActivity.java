package edu.monash.fit3027.teleporter;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import edu.monash.fit3027.teleporter.models.Job;

public class TeleporterViewJobActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mCurrentRecipient;
    private TextView mPickupLocation;
    private TextView mDropoffLocation;
    private TextView mItemType;
    private TextView mItem;
    private TextView mDeliveryType;
    private Button mAcceptJobButton;
    private Button mCancelButton;
    private Job currentJob;
    private Geocoder geocoder;
    private List<Address> addresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleporter_view_job);

        geocoder = new Geocoder(this, Locale.getDefault());



        //Connect UI elements to class
        mCurrentRecipient = (TextView) findViewById(R.id.currentRecipientTextView);
        mPickupLocation = (TextView) findViewById(R.id.currentPickupAddtextView);
        mDropoffLocation = (TextView) findViewById(R.id.currenctDropoffAddtextView);
        mItemType = (TextView) findViewById(R.id.currentItemTypeTextView);
        mItem = (TextView) findViewById(R.id.currentItemTextView);
        mDeliveryType = (TextView) findViewById(R.id.currentDeliveryTypeTextView);
        mAcceptJobButton = (Button) findViewById(R.id.acceptButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mAcceptJobButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        Intent intent = getIntent();
        currentJob = intent.getParcelableExtra("Job");

        String userId = currentJob.getUserId();
        String recipientName = String.valueOf(currentJob.getM_sRecipientName());
        String deliveryType = currentJob.getM_sDeliveryType();
        String itemName = currentJob.getM_sItemName();
        String itemType = currentJob.getM_sItemType();

        //Formatting informaton to be displayed
        if(deliveryType.equals("2 hour delivery: 7AM - 9PM (Order before 7PM)")){
            deliveryType="2 hour delivery";
        }else if(deliveryType.equals("4 hour delivery: 7AM - 5PM (Order before 1PM)")){
            deliveryType="4 hour delivery";
        }else if(deliveryType.equals("Same Day delivery: 7AM - 5PM (Order before 12PM)")){
            deliveryType="Same Day Delivery";
        }

        if(itemType.equals("Cold")){
            itemType="Cold Food";
        }else if(itemType.equals("Hot")){
            itemType="Hot Food";
        }else if(itemType.equals("Neither")){
            itemType="Food";
        }

        try {
            addresses = geocoder.getFromLocation(currentJob.getM_sStartLat(), currentJob.getM_sStartLng(), 1);
            String startaddress = addresses.get(0).getAddressLine(0);
            mPickupLocation.setText(startaddress);

            addresses = geocoder.getFromLocation(currentJob.getM_sEndLat(), currentJob.getM_sEndLng(), 1);
            String endaddress = addresses.get(0).getAddressLine(0);
            mDropoffLocation.setText(endaddress);

        }catch(Exception e){
            System.out.print("Error: "+e.getMessage());
        }



        mCurrentRecipient.setText(recipientName);
        mItemType.setText(itemType);
        mItem.setText(itemName);
        mDeliveryType.setText(deliveryType);




    }

    @Override
    public void onClick(View view) {
        if(view == mAcceptJobButton){
            Intent newIntent = new Intent(this,TeleporterNavigation.class);
            newIntent.putExtra("Job",currentJob);
            startActivity(newIntent);


        }else if(view == mCancelButton){
            finish();
            //startActivity(new Intent(this,TeleporterHomeActivity.class));
        }

    }
}
