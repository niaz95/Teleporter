package edu.monash.fit3027.teleporter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.monash.fit3027.teleporter.models.Job;

/**
 * Created by niaz on 2/5/17.
 */

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private FirebaseAuth firebaseAuth;
    private Button m_cDeliveryRequest;
    private FirebaseDatabase mDatabase;
    private ListView mJobsListView;
    private DatabaseReference myRef;
    private TextView welcomeText;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView logoutTextView;
    private String userId;
    List<Job> jobList;
    private FirebaseUser currentuser;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        //Connect to firebase database and auth
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("jobs");
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        userId = currentuser.getUid();

        //Connect to UI elements

        mJobsListView = (ListView) findViewById(R.id.jobsListView);
        jobList = new ArrayList<>();
        logoutTextView = (TextView) findViewById(R.id.logoutTextView);
        m_cDeliveryRequest = (Button) findViewById(R.id.deliveryRequestButton);
        m_cDeliveryRequest.setOnClickListener(this);
        welcomeText = (TextView) findViewById(R.id.welcomeTextView);
        mJobsListView.setOnItemClickListener(this);
        logoutTextView.setOnClickListener(this);

        if(currentuser != null) {
            //Sets the display name of the user on the page
            String displayName = "Welcome, "+currentuser.getDisplayName();
            welcomeText.setText(displayName);
        }else{
            //If the user is not logged in, direct them back to the main page
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                jobList.clear();    //Clears the previous list and refreshes it each time the page is opened

                for(DataSnapshot jobsSnapshot: dataSnapshot.getChildren()){


                    Job job = jobsSnapshot.getValue(Job.class);
                    //Gets only the jobs specific to the user from the snapshot
                   if(userId.equals(job.getUserId())){jobList.add(job);}
                }

                UserJobsList adapter = new UserJobsList(UserHomeActivity.this,jobList);
                //Sets the list view with the jobs posted by the user
                mJobsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == m_cDeliveryRequest){
            //Go to postjob page
            startActivity(new Intent(this, PostJobActivity.class));
        }
        if(view == logoutTextView){
            //log out user
            firebaseAuth.signOut();
            Toast.makeText(this,"You have been logged out",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Show details of the job selected
        Intent newIntent = new Intent(this,UserNavigation.class);
        newIntent.putExtra("Job",jobList.get(position));
        startActivity(newIntent);

    }
}
