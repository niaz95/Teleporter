package edu.monash.fit3027.teleporter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    private Button m_cLoginButton;
    private Button m_cRegisterButton;
    private Button m_cAboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link all the buttons
        m_cLoginButton = (Button) findViewById(R.id.loginButton);
        m_cRegisterButton = (Button) findViewById(R.id.registerButton);
        m_cRegisterButton.setOnClickListener(this);
        m_cLoginButton.setOnClickListener(this);
        m_cAboutButton = (Button) findViewById(R.id.aboutButton);
        m_cAboutButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == m_cRegisterButton){
            //Go to register activity
            startActivity(new Intent(MainActivity.this,RegisterActivity.class));
        }else if(view == m_cLoginButton){
            //Go to login activity
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }else if(view == m_cAboutButton){
            //Got to About page
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
        }

    }
}
