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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Users,ExpensesViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users_list);

        userNotExist();
        setRecyclerView();
    }

    private void setRecyclerView()
    {
        firebaseDatabase   =    FirebaseDatabase.getInstance();
        if(firebaseDatabase==null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }

        databaseReference   = FirebaseDatabase.getInstance().getReference().child("Admin");
        recyclerView        =  findViewById(R.id.chat_user_list_recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

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


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, ExpensesViewHolder>(
                Users.class,
                R.layout.chat_user_list_layout,
                ExpensesViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final ExpensesViewHolder viewHolder, final Users model, final int position) {


                if(model.getUserid().toString().equalsIgnoreCase(mAuth.getCurrentUser().getUid()))
                    viewHolder.itemLayout.setVisibility(View.GONE);
                else
                    viewHolder.itemLayout.setVisibility(View.VISIBLE);
                viewHolder.setName(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setOnline(model.getOnline());


                Log.i("tag", "online status" + model.getOnline());
                Log.i("tag", "name" + model.getName());
                Log.i("tag", "phone" + model.getPhone());
                Log.i("tag", "userid" + model.getUserid());
                Log.i("tag", "url" + model.getImgurl());


                if (model.getImgurl() != null) {
                    if (model.getImgurl() != null) {
                        if (model.getImgurl() != null && model.getImgurl().length() > 0) {
                            Picasso.with(MainActivity.this).load(model.getImgurl()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(viewHolder.imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(MainActivity.this).load(model.getImgurl()).into(viewHolder.imageView);
                                        }
                                    });
                        }
                    }

                }
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("phone", model.getPhone());
                        intent.putExtra("userid", model.getUserid());
                        startActivity(intent);

                    }
                });

            }


        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }



    public static class ExpensesViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        ImageView imageView;
        LinearLayout itemLayout;
        public ExpensesViewHolder(View itemView) {
            super(itemView);
            mview       =   itemView;
            imageView   =   mview.findViewById(R.id.pic);
            itemLayout  =   mview.findViewById(R.id.item_layout);
        }

        public void setName(String name)
        {
            TextView amountTextViw =  mview.findViewById(R.id.user_list_name);
            amountTextViw.setText(""+name);
        }

        public void setPhone(String phone)
        {
            TextView amountTextViw =   mview.findViewById(R.id.user_list_email);
            amountTextViw.setText(""+phone);
        }

        public void setImage(String image)
        {

        }
        public void setOnline(boolean status)
        {
            ImageView imageView = mview.findViewById(R.id.online_status);
            if(status)
            {
                imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView.setVisibility(View.GONE);
            }
        }

    }
}
