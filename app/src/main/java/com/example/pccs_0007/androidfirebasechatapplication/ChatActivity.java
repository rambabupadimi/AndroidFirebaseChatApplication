package com.example.pccs_0007.androidfirebasechatapplication;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EmojiEditText message;
    private ImageView send;
    private DatabaseReference databaseReference;
    private String senderId,receiverId;
    private FirebaseAuth mAuth;
    List<ChatMessage> chatMessageList;

    ChatAdapter chatAdapter;
    ImageView emojiButton;
    EmojiPopup emojiPopup;
    ViewGroup rootView;
    TextView onlineStatus;
    LinearLayout chatDateTimeLayout;
    TextView chatDateTimeTextView;
    private String TAG ="chatactivity";
    Toolbar chatToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialiseIDs();
        initialiseObjects();
        initialiseToolbar();
        initialiseRecyclerView();
        initialiseClickListeners();
        setUpEmojiPopup();


    }

    private void initialiseRecyclerView()
    {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        databaseReference.keepSynced(true);
        chatAdapter = new ChatAdapter(chatMessageList, senderId);
        chatRecyclerView = findViewById(R.id.chat_recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(null);
        chatRecyclerView.setAdapter(chatAdapter);


    }

    private void initialiseClickListeners()
    {

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(senderId);
                chatMessage.setReceiverId(receiverId);
                chatMessage.setMessage(message.getText().toString());
                chatMessage.setSent_time(getCurrentDateAndTime());
                chatMessage.setSenderId_receiverId(senderId+"_"+receiverId);
                databaseReference.push().setValue(chatMessage);
                databaseReference.keepSynced(true);


                message.setText("");
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });



        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                if (chatMessage.getSenderId_receiverId().toString().equalsIgnoreCase(senderId + "_" + receiverId)
                        || chatMessage.getSenderId_receiverId().toString().equalsIgnoreCase(receiverId + "_" + senderId)) {
                    chatMessageList.add(chatMessage);

                    chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                    chatAdapter.notifyItemInserted(chatMessageList.size() - 1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        if (Build.VERSION.SDK_INT >= 11) {
            chatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        chatRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    chatRecyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, 100);
                    }
                }
            });

        }

        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                emojiPopup.toggle();
            }
        });
    }

    private void initialiseToolbar()
    {
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initialiseIDs()
    {
        onlineStatus        = findViewById(R.id.chat_toolbar_online);
        chatToolbar         =   findViewById(R.id.chat_toolbar);
        chatRecyclerView    = findViewById(R.id.chat_recylerview);
        message             = findViewById(R.id.emojiEditText);
        send                = findViewById(R.id.send);
        mAuth               = FirebaseAuth.getInstance();
        emojiButton         =  findViewById(R.id.main_activity_emoji);
        emojiButton.setColorFilter(ContextCompat.getColor(this, R.color.emoji_icons), PorterDuff.Mode.SRC_IN);
        rootView            =  findViewById(R.id.main_activity_root_view);
        chatDateTimeLayout   = findViewById(R.id.date_and_time_chat_layout);
        chatDateTimeTextView =  findViewById(R.id.date_and_time_bubble);
        chatDateTimeLayout.setEnabled(false);
        chatDateTimeLayout.setClickable(false);

    }

    private void initialiseObjects()
    {
        chatMessageList     = new ArrayList<>();
        try {
            senderId = mAuth.getCurrentUser().getUid();
            receiverId = getIntent().getStringExtra("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    public String getCurrentDateAndTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }
        super.onStop();

    }


    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override
                    public void onEmojiBackspaceClicked(final View v) {
                        if (emojiPopup != null) {
                            emojiPopup.dismiss();
                        }

                        Log.d(TAG, "Clicked on Backspace");
                    }
                })
                .setOnEmojiClickedListener(new OnEmojiClickedListener() {
                    @Override
                    public void onEmojiClicked(final Emoji emoji) {
                        Log.d(TAG, "Clicked on emoji");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emojiButton.setImageResource(R.drawable.ic_keyboard);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(final int keyBoardHeight) {
                        Log.d(TAG, "Opened soft keyboard");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emojiButton.setImageResource(R.drawable.emoji_ios_category_people);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        Log.d(TAG, "Closed soft keyboard");
                        if (emojiPopup != null) {
                            emojiPopup.dismiss();
                        }

                    }
                })
                .build(message);
    }




    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {

        //   LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chat-message-received"));
        super.onResume();


    }




}




