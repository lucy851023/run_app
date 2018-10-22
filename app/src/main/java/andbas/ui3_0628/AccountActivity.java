package andbas.ui3_0628;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    private Button mLogoutBtn;
    private Button mEnterBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;


    private DatabaseReference mPlayerDatabase;
    private Button mEnterRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(AccountActivity.this,LoginActivity.class));
                    finish();
                }

            }
        };

        mLogoutBtn = findViewById(R.id.logoutBtn);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "offline";
                mUserDatabase.child("status").setValue(status);
                FirebaseAuth.getInstance().signOut();

            }
        });

        mEnterBtn = findViewById(R.id.bt_enter);
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent enterIntent = new Intent(AccountActivity.this,UsersActivity.class);
                startActivity(enterIntent);
            }
        });

        mEnterRoom = findViewById(R.id.bt_enter_room);
        mEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerDatabase = FirebaseDatabase.getInstance().
                        getReference().child("Room").child("room1").child(mCurrentUser.getUid());
                HashMap<String,String> playerMap = new HashMap<>();
                playerMap.put("email",mCurrentUser.getEmail());
                playerMap.put("ready","no");
                mPlayerDatabase.setValue(playerMap);

                Intent i = new Intent(AccountActivity.this,RoomActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);

    }
}
