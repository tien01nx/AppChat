package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Adapter.MessageAdapter;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    TextView username;
    ImageView imageView;


    FirebaseUser fUser;
    DatabaseReference reference;
    Intent intent;

    String userid;

    RecyclerView recyclerView;
    EditText msg_editText;
    ImageButton sendBtn;

    //Message Adapter
    MessageAdapter messageAdapter;
    List<Chat> mChat;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        //Widgets
        imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.usernamey);
        msg_editText = findViewById(R.id.text_send);
        sendBtn = findViewById(R.id.btn_send);

        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


//        lay du lieu tu trang UserAdapter
        intent = getIntent();
        userid = intent.getStringExtra("userid");


        fUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher);

                } else {
                    Glide.with(MessageActivity.this).
                            load(user.getImageURL()).into(imageView);
                }

                // lay du lieu tin nhan ve
                readMessages(fUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msg_editText.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), userid, msg);

                } else {
                    Toast.makeText(MessageActivity.this, "vui long soan tin nhan", Toast.LENGTH_SHORT).show();
                }
                msg_editText.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", msg);
        reference.child("Chats").push().setValue(hashMap);


        // Thêm người dùng vào đoạn trò chuyện: Trò chuyện mới nhất với các liên hệ
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(fUser.getUid())
                .child(userid);


        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readMessages(final String myid, final String userid, final String imageurl) {

        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {

                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    private void checkStatus(String status){
        // lay du lieu tronng FirebaseDatabase qua Myusers va lay theo getid
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fUser.getUid());

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("offline");
    }



}