package edu.monash.fit3027.teleporter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.monash.fit3027.teleporter.models.User;

/**
 * Created by niaz on 2/5/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText m_cEmailEditText;
    private EditText m_cPasswordEditText;
    private Button m_cLoginButton;
    private TextView m_cSignUpTextView;
    private DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userEmail;


    private ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Setup firebase elements
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");

        //Check if user is logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //Display message if user is logged in
                    userEmail = user.getEmail();
                    toastMessage("Welcome "+user.getDisplayName());

                    //Check if user is registered as a Driver or Customer
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            showData(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        //Connect input fields to the class
        m_cEmailEditText = (EditText) findViewById(R.id.firstNameEditText);
        m_cPasswordEditText = (EditText) findViewById(R.id.passwordEditTextIn);
        m_cLoginButton = (Button) findViewById(R.id.loginButton);
        m_cSignUpTextView = (TextView) findViewById(R.id.signUpTextView);
        m_cLoginButton.setOnClickListener(this);
        m_cSignUpTextView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);



    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onResume(){
        super.onResume();
        if(mAuthListener != null){
            firebaseAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void checkUserLogin(){
        //Check email and password with Firebase database
        String email = m_cEmailEditText.getText().toString();
        String password = m_cPasswordEditText.getText().toString();

        //Check if email is entered
        if(email.length() < 3){
            toastMessage("Please enter email");
            return;
        }

        //Check if password is entered
        if(password.length() < 3){
            toastMessage("Please enter password");
            return;
        }
        //Show porgress dialog
        progressDialog.setMessage("Signing In Please Wait..");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){


                }else{
                    System.out.println("Sign-in Failed:"+task.getException().getMessage());
                    Toast.makeText(getApplicationContext(),"Your username and password did not match, Please try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //Method tos how toast messages
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot users: dataSnapshot.getChildren()){
            //Check if  current user is a teleporter or not
            User currentuser = users.getValue(User.class);
            if(currentuser.getEmail().equals(userEmail)){
                if(currentuser.getIsTeleporter() == 1){
                    startActivity(new Intent(getApplicationContext(), TeleporterHomeActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                }
            }
        }
    }
    @Override
    public void onClick(View view) {
        if(view == m_cSignUpTextView){
            //Go to register activity
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        }else if(view == m_cLoginButton){
            //Check input fields to log in user
            checkUserLogin();
        }

    }
}
