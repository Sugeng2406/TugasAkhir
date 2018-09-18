package com.chatln.chat_ln;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private Button button, signout;
    private List<String> listString;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        String uid = null;

        if (auth.getCurrentUser() != null){
            uid = auth.getCurrentUser().getUid();

            listString = new ArrayList<>();

            listView = findViewById(R.id.listView);
            editText = findViewById(R.id.editText);
            button = findViewById(R.id.button);
            signout = findViewById(R.id.signout);

            final ArrayAdapter arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, listString);
            listView.setAdapter(arrayAdapter);

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            final DatabaseReference usersRef = database.getReference("users");
            final DatabaseReference uidRef = usersRef.child(uid);
            final DatabaseReference message = uidRef.child("message");

            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(true)
                            .setMessage("Are you sure to logout?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    auth.signOut();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    finish();
                                }
                            }).show();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    message.push().child("task").setValue(editText.getText().toString());
                }
            });

            message.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    listString.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        listString.add((String) ds.child("task").getValue());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }


    }
}
