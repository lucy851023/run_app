package andbas.ui3_0628;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DoubleModeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_two_people,btn_three_people,btn_four_people;
    private DatabaseReference mRoomDatabase;
    private DatabaseReference mPlayerDatabase;
    private ValueEventListener mValueEventListener;
    private FirebaseUser mCurrentUser;
    private String roomID;
    private String[] roomType = {"two","three","four"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_mode);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mRoomDatabase = FirebaseDatabase.getInstance().getReference().child("Room");



        btn_two_people = findViewById(R.id.btn_two_people);
        btn_three_people = findViewById(R.id.btn_three_people);
        btn_four_people = findViewById(R.id.btn_four_people);


    }

    @Override
    protected void onStart() {

        super.onStart();
        btn_two_people.setOnClickListener(this);
        btn_three_people.setOnClickListener(this);
        btn_four_people.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i==R.id.btn_two_people){
            enterGameRoom(2);

        }else if(i==R.id.btn_three_people){
            enterGameRoom(3);

        }else{
            enterGameRoom(4);
        }


    }

    private void enterGameRoom(int number){

        final int num = number-2;

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFull = true;
                String user_id = mCurrentUser.getUid();


                if(dataSnapshot.hasChildren()){

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        if(snapshot.getChildrenCount() < num+2){
                            isFull = false;
                            roomID = snapshot.getKey();
                            Toast.makeText(DoubleModeActivity.this,"Have room. ID is"+roomID ,Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    if(isFull){
                        roomID = mRoomDatabase.child(roomType[num]).push().getKey();
                        Toast.makeText(DoubleModeActivity.this,"Room is full. ID is " + roomID ,Toast.LENGTH_SHORT).show();

                    }
                }else{

                    roomID = mRoomDatabase.child(roomType[num]).push().getKey();
                    Toast.makeText(DoubleModeActivity.this,"No room. ID is " +roomID,Toast.LENGTH_SHORT).show();

                }

                mPlayerDatabase = mRoomDatabase.child(roomType[num]).child(roomID).child(user_id);
                MyPlayers players = new MyPlayers("no");
                mPlayerDatabase.setValue(players);
                Intent intent = new Intent();
                intent.setClass(DoubleModeActivity.this,GameRoomActivity.class);
                intent.putExtra("room_type",roomType[num]);
                intent.putExtra("room_id",roomID);
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

       mRoomDatabase.child(roomType[num]).addListenerForSingleValueEvent(mValueEventListener);

    }
}
