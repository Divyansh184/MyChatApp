package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class specificchat extends AppCompatActivity {

    EditText mgetmesssage;
    ImageButton msendmessagebutton;
    CardView msendermessageCardView;

    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mimageviewofspecifixuser;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;

    String mrecievername, sendername, mrecieveruid, msenderuid;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom, recieverroom;

    ImageButton mbackbuttonofspeificchat;

    RecyclerView mmessagerecyclerview;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);

        mgetmesssage = findViewById(R.id.getmessage);
        msendermessageCardView = findViewById(R.id.cardviewofsendmessage);
        msendmessagebutton = findViewById(R.id.imageviewsendmessage);
        mtoolbarofspecificchat = findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser = findViewById(R.id.nameofspecificuser);
        mimageviewofspecifixuser = findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspeificchat = findViewById(R.id.backbuttonofspecificchat);

        messagesArrayList = new ArrayList<>();
        mmessagerecyclerview = findViewById(R.id.recyclerviewofspecific);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(specificchat.this, messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);

        intent = getIntent();

        setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(specificchat.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mrecieveruid = getIntent().getStringExtra("receiveduid");
        mrecievername = getIntent().getStringExtra("name");
        senderroom = msenderuid + mrecieveruid;
        recieverroom = mrecieveruid + msenderuid;

        mnameofspecificuser.setText(mrecievername);
        String uri = intent.getStringExtra("imageuri");
        if (uri != null && !uri.isEmpty()) {
            Picasso.get().load(uri).into(mimageviewofspecifixuser);
        } else {
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
        }

        loadMessages();

        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredmessage = mgetmesssage.getText().toString();
                if (enteredmessage.isEmpty()) {
                    Toast.makeText(specificchat.this, "Enter Message First", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage();
                }
            }
        });

        mbackbuttonofspeificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(specificchat.this, chatActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void sendMessage() {
        Date date = new Date();
        currenttime = simpleDateFormat.format(calendar.getTime());
        Messages messages = new Messages(enteredmessage, firebaseAuth.getUid(), date.getTime(), currenttime);

        firebaseDatabase.getReference().child("chats")
                .child(senderroom)
                .child("messages")
                .push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseDatabase.getReference()
                                .child("chats")
                                .child(recieverroom)
                                .child("messages")
                                .push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Optionally handle success.
                                    }
                                });
                    }
                });

        mgetmesssage.setText(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
    }
}
