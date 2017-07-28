package edu.monash.fit3027.teleporter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.monash.fit3027.teleporter.models.User;

/**
 * Created by niaz on 2/5/17.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{



    private EditText m_cFirstNameInputText;
    private EditText m_cLastNameInputText;
    private EditText m_cPhonenoInputText;
    private EditText m_cEmailInputText;
    private EditText m_cPasswordInputText;
    private EditText m_cRetypeInputText;
    private Button m_cSubmitButton;
    private Button m_cCancelButton;
    private RadioButton m_cTeleporterRadioButton;
    private boolean userRegistered = false;
    private String userEmail;

    private String firstname;
    private String lastname;
    private String number;
    private String email;
    private String password;
    private String retypepassword;
    private int isTeleporter;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set up firebase elements
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (userRegistered) {
                        //Create new user and input into firebase database
                        FirebaseUser newuser = firebaseAuth.getCurrentUser();
                        String userId = newuser.getUid();
                        User user = new User(userId, firstname, lastname, number, email, password, isTeleporter);
                        //database and auth are asynchronous
                        //when authentication state changes access database, only do it when registering
                        //onAuthstartechange user is logged in, user has just logged in now, user has just registered


                        usersRef.child(userId).setValue(user);
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(firstname).build();
                        newuser.updateProfile(profileUpdate);
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    userEmail = user.getEmail();

                    usersRef.addValueEventListener(new ValueEventListener() {
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

        progressDialog = new ProgressDialog(this);
        //Link UI elements to the class
        m_cTeleporterRadioButton = (RadioButton) findViewById(R.id.teleporterRadioButton);
        m_cFirstNameInputText = (EditText) findViewById(R.id.firstNameEditText);
        m_cLastNameInputText = (EditText) findViewById(R.id.lastNameEditText);
        m_cPhonenoInputText = (EditText) findViewById(R.id.phoneNoEditText);
        m_cEmailInputText = (EditText) findViewById(R.id.emailEditTextIn);
        m_cPasswordInputText = (EditText) findViewById(R.id.passwordEditTextIn);
        m_cRetypeInputText = (EditText) findViewById(R.id.retypeEditText);
        m_cSubmitButton = (Button) findViewById(R.id.submitButton);
        m_cCancelButton = (Button) findViewById(R.id.cancelButton);
        m_cSubmitButton.setOnClickListener(this);
        m_cCancelButton.setOnClickListener(this);
        m_cTeleporterRadioButton.setOnClickListener(this);



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


    private void registerUser(){
        //Get values from the input fields
        firstname = m_cFirstNameInputText.getText().toString();
        lastname = m_cLastNameInputText.getText().toString();
        number = m_cPhonenoInputText.getText().toString();
        email = m_cEmailInputText.getText().toString();
        password = m_cPasswordInputText.getText().toString();
        retypepassword = m_cRetypeInputText.getText().toString();


        if(m_cTeleporterRadioButton.isChecked()){
            //Get value form isTeleporter radio buttin
            isTeleporter = 1;
        }else{
            isTeleporter = 0;
        }

        if(firstname.length() <1){
            //firstname is empty
            toastMessage("Please enter first name");
            return;
        }
        if(lastname.length() <1){
            //lastname is empty
            toastMessage("Please enter last name");
            return;
        }
        if(number.length() <1){
            //address is empty
            toastMessage("Please enter phone number");
        }
        //Assumption: Email and password should both be over 3 characters at least
        if(email.length() < 3){
            //email is empty
            toastMessage("Please enter an email");
            return;
        }
        if(password.length() < 3){
            //password is empty
            toastMessage("Please enter a password");
            return;
        }
        if(!password.equals(retypepassword)){
            //password and confirmed password do not match
            toastMessage("Password and confirm password do not match");
            return;
        }

            progressDialog.setMessage("Registering User Please Wait..");
            progressDialog.show();

            userRegistered = true;

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

                        //user is successfully registered
                        toastMessage("Registration successful");

                    } else {
                        //user registration failed
                        toastMessage("There is already an account with this email. Please login with your password");

                    }
                }
            });




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
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View view) {
        if(view == m_cCancelButton){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        }else if(view == m_cSubmitButton){
            registerUser();
        }else if(view == m_cTeleporterRadioButton){
            if(m_cTeleporterRadioButton.isChecked()){
                //confirm if the user wants to be a teleporter driver by showing an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to be a Teleporter Driver? ");
                builder.setIcon(R.drawable.teleport_logo);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        m_cTeleporterRadioButton.setChecked(false);

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }else{
                m_cTeleporterRadioButton.setChecked(false);
            }
        }

    }
}
