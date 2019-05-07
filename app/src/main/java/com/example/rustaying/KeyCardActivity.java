/************************
 Authors:
 Mathew Varghese
 Shilp Shah
 *************************/

package com.example.rustaying;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class KeyCardActivity extends AppCompatActivity {

    private static final String TAG = "KeyCardActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userID;

    private Button genKeyBtn;
    private TextView codeStr;

    private Guest g = new Guest();


    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_card);

        genKeyBtn = (Button) findViewById(R.id.genKeyBtn);
        codeStr =  (TextView) findViewById(R.id.keyCodeStr);


        Intent i = getIntent(); //gets intent from Home Activity
        Bundle b = i.getBundleExtra("keyInfo");
        String keyCodeStr = b.getString("keycode");
        boolean checked = b.getBoolean("checkedIn");


        if(checked==false) //arent checked in so you cant click button
        {
            genKeyBtn.setEnabled(false);
            Toast.makeText(KeyCardActivity.this, "Please check in to get key code", Toast.LENGTH_SHORT).show();
        }else { //you are checked in
            //genKeyBtn.setEnabled(true);
            if (keyCodeStr.equals("-1")) { //key yet to be set
                codeStr.setText("");
                genKeyBtn.setEnabled(true);
            } else { //key already exists
                codeStr.setText(keyCodeStr);
                genKeyBtn.setEnabled(false);
            }
        }






        //set up Bottom Navigation Bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_account:
                        Intent account = new Intent(KeyCardActivity.this,ProfileActivity.class);
                        startActivity(account);
                        finish();
                        break;
                    case R.id.navigation_home:
                        Intent home = new Intent(KeyCardActivity.this,HomeActivity.class);
                        startActivity(home);
                        finish();
                        break;
                    case R.id.navigation_services:
                        Intent service = new Intent(KeyCardActivity.this, ServicesActivity.class);
                        startActivity(service);
                        finish();
                        break;
                }
                return false;
            }
        });


        //setup firebase stuff
        mAuth = FirebaseAuth.getInstance(); //mAuth
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference(); //dbRef
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        //generate button
        genKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> list = new HashMap<>();


                final String code = getRandomNumberString();
                list.put("keyCode", code);


                myRef.child("Guest").child(userID).updateChildren(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            codeStr.setText(code);
                            Toast.makeText(KeyCardActivity.this, "Info updated", Toast.LENGTH_SHORT).show();

                        }else{
                            startActivity(new Intent(KeyCardActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
                genKeyBtn.setEnabled(false);
            }
        });



    }
}
