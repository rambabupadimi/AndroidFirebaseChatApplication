package com.example.pccs_0007.androidfirebasechatapplication;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Pattern;

public class SignupOrLogin extends AppCompatActivity {

    Button signup,siginin,signupadmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);
        signup  = (Button)findViewById(R.id.signup);
        siginin =   (Button)findViewById(R.id.signin);

        TextView middle = (TextView)findViewById(R.id.main_note);


        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                Log.i("tag","possible email"+possibleEmail);

            }
        }


        signupadmin =   (Button)findViewById(R.id.signup_admin);
        signupadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupOrLogin.this,Register.class);
                intent.putExtra("usertype","admin");
                startActivity(intent);
            }
        });

        siginin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupOrLogin.this,LoginActivity.class);
                startActivity(intent);

            }
        });
    }
}
