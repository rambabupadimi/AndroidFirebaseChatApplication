package com.example.pccs_0007.androidfirebasechatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    List<Users> usersList =new ArrayList<>();
    int count=0;
    TextView toolbarHeading;
    ChatUserListAdapter chatUserListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users_list);
        initialiseIDs();
        userNotExist();
        setRecyclerView();
    }

    private void initialiseIDs()
    {
        toolbarHeading = findViewById(R.id.toolbar_heading);

    }

    private void setRecyclerView()
    {
        firebaseDatabase   =    FirebaseDatabase.getInstance();
        if(firebaseDatabase==null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }


        databaseReference   = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                         count++;
                        Users users = dataSnapshot1.getValue(Users.class);
                        if(users.getUserid().toString().equalsIgnoreCase(mAuth.getCurrentUser().getUid()))
                        {
                            Log.i("tag","use not "+users.getName());
                        }
                        else
                        {

                            usersList.add(users);
                            if(count==dataSnapshot.getChildrenCount())
                            {


                                chatUserListAdapter = new ChatUserListAdapter(MainActivity.this,usersList);
                                recyclerView        =  findViewById(R.id.chat_user_list_recylerview);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setAdapter(chatUserListAdapter);

                            }
                            Log.i("tag","use yes "+users.getName());
                        }

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void userNotExist()
    {
        mAuth   =   FirebaseAuth.getInstance();
        mAuthListener   =   new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() ==null)
                {
                    Intent intent = new Intent(MainActivity.this,SignupOrLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null)
                            {
                                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                    Users users = dataSnapshot1.getValue(Users.class);

                                    if(users.getUserid().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                        toolbarHeading.setText(users.getName());
                                        return;
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);


    }


}
