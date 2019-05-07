/************************
 Authors:
 Shilp Shah
 Mathew Varghese
 *************************/

package com.example.rustaying;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.StringTokenizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class newViewRooms extends AppCompatActivity {

    private static final String TAG = "newViewRooms";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ResInfo resInfo;

    int year0Tok;
    int month0Tok;
    int day0Tok;
    LocalDate inDateTok;

    int year1Tok;
    int month1Tok;
    int day1Tok;
    LocalDate outDateTok;

    RecyclerView recyclerView;
    newRoomAdapter adapter;

    ArrayList<Room> roomList = new ArrayList<>();
    String[] receivedRoomTypes = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_view_rooms);

        //GET DATA from ReservationActivity
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("resInfo");

        //get values from bundle
        receivedRoomTypes = b.getStringArray("roomTypes");

        int inDay = b.getInt("inDay");
        int inMonth = b.getInt("inMonth");
        int inYear = b.getInt("inYear");

        int outDay = b.getInt("outDay");
        int outMonth = b.getInt("outMonth");
        int outYear = b.getInt("outYear");

        //log the values
        Log.i(TAG, "onCreate: " + inDay);
        Log.i(TAG, "onCreate: " + inMonth);
        Log.i(TAG, "onCreate: " + inYear);

        Log.i(TAG, "onCreate: " + outDay);
        Log.i(TAG, "onCreate: " + outMonth);
        Log.i(TAG, "onCreate: " + outYear);

        //create Date objects based off of passed strings
        LocalDate inDate = parseDate(inYear, inMonth, inDay);
        LocalDate outDate = parseDate(outYear, outMonth, outDay);

        //create ResInfo Object from passed information
        resInfo = new ResInfo(inDate, outDate, receivedRoomTypes);

        //create recycleview
        createRecycleView();


        //fire base connection
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed In");
                }else{

                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };

        myRef.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private LocalDate parseDate(int year, int month, int date)
    {
        return LocalDate.of(year, month, date);
    }

    //use adapter to create recycle view
    private void createRecycleView(){
        Log.d(TAG, "createRecycleView: Started view");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        newRoomAdapter adapter = new newRoomAdapter(this,roomList, resInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()){

            //creates Room object from data from firebase
            Log.d(TAG, "showData: " + data.getValue(Room.class).getRoomType());

            Room room = new Room(); // create new object

            room.setRoomId(data.getValue(Room.class).getRoomId());
            room.setRoomType(data.getValue(Room.class).getRoomType());
            room.setIsAvailable(data.getValue(Room.class).getIsAvailable());
            room.setCheckInDate(data.getValue(Room.class).getCheckInDate());
            room.setCheckOutDate(data.getValue(Room.class).getCheckOutDate());


            //Create LocalDates objects of the dates for comparison
            StringTokenizer st0 = new StringTokenizer(room.getCheckInDate(),"-");
            //Log.d(TAG, "showData: " + room.getCheckInDate());
            //Log.d(TAG, "showData1: " + st0.nextToken());
            //Log.d(TAG, "showData1: " + st0.nextToken());
            //Log.d(TAG, "showData1: " + st0.nextToken());

            if(st0.hasMoreTokens())
            {
                year0Tok = Integer.parseInt(st0.nextToken());
                month0Tok = Integer.parseInt(st0.nextToken());
                day0Tok = Integer.parseInt(st0.nextToken());
                inDateTok = parseDate(year0Tok, month0Tok, day0Tok);
            }

            StringTokenizer st1 = new StringTokenizer(room.getCheckOutDate(),"-");

            if(st1.hasMoreTokens())
            {
                year1Tok = Integer.parseInt(st1.nextToken());
                month1Tok = Integer.parseInt(st1.nextToken());
                day1Tok = Integer.parseInt(st1.nextToken());
                outDateTok = parseDate(year1Tok, month1Tok, day1Tok);
            }

            //Check if ResInfo dates collide with existing checkIn and checkOut dates of the room
            if(room.getCheckInDate().equals("") || room.getCheckInDate() == null)
            {
                //No reservation dates
            }
            else if(room.getCheckOutDate().equals("") || room.getCheckOutDate() == null)
            {
                //No reservation dates
            }
            else if(resInfo.getCheckIn().compareTo(inDateTok) == 0)
            {
                //If the new checkIn date is the same as existing checkIn date, conflict
                continue;
            }
            else if(resInfo.getCheckOut().compareTo(outDateTok) == 0)
            {
                //The the checkOut dates are equal, conflict
                continue;
            }
            else if(resInfo.getCheckIn().compareTo(outDateTok) > 0)
            {
                //If new reservation checkIn date is greater than existing checkOut dates, no conflict
            }
            else if(resInfo.getCheckOut().compareTo(inDateTok) < 0)
            {
                //If the new reservation checkOut date is less than existing checkIn date, no conflict
            }
            else if(resInfo.getCheckIn().compareTo(inDateTok) > 0 && resInfo.getCheckIn().compareTo(outDateTok) < 0)
            {
                //If new reservation checkIn date is after the existing checkIn date and before checkOut date, conflict
                continue;
            }
            else if(resInfo.getCheckOut().compareTo(inDateTok) > 0 && resInfo.getCheckOut().compareTo(outDateTok) < 0)
            {
                //If new reservation checkOut date is after checkIn date but before checkOut date, conflict
                continue;
            }



            if(receivedRoomTypes[0] == null && room.getRoomType().equals("Single"))
            {
                continue;
            }
            else if (receivedRoomTypes[1] == null && room.getRoomType().equals("Double"))
            {
                continue;
            }
            else if(receivedRoomTypes[2] == null && room.getRoomType().equals("Queen"))
            {
                continue;
            }
            else if(receivedRoomTypes[3] == null && room.getRoomType().equals("King"))
            {
                continue;
            }


            //add object to array list
            roomList.add(new Room(room.getRoomId(),room.getRoomType(),room.getIsAvailable()));

            //Log.d(TAG, "showData: Room ID: " + room.getRoomId());
            //Log.d(TAG, "showData: Room Type: " + room.getRoomType());
            //Log.d(TAG, "showData: Availability: " + room.getIsAvailable());
            //Log.d(TAG, "showData: Array List: " + roomList );

            //add array list to recycle view
            createRecycleView();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}