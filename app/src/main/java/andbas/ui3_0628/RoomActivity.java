package andbas.ui3_0628;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RoomActivity extends AppCompatActivity {
    private RecyclerView mPlayerList;
    private DatabaseReference mPlayerDatabase;
    private Button mExitBtn;
    private FirebaseUser mCurrentUser;
    private FirebaseRecyclerAdapter<Players,RoomActivity.PlayerViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mPlayerDatabase = FirebaseDatabase.getInstance().getReference().child("Room").child("room1");
        mPlayerDatabase.keepSynced(true);

        mPlayerList = findViewById(R.id.player_recView);
        mPlayerList.setHasFixedSize(true);
        mPlayerList.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Room").child("room1")
                .limitToLast(50);

        FirebaseRecyclerOptions<Players> options =
                new FirebaseRecyclerOptions.Builder<Players>()
                        .setQuery(query, Players.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Players, PlayerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PlayerViewHolder holder, int position, @NonNull Players model) {

                holder.setEmail(model.email);
                holder.setReady(model.ready);
            }

            @NonNull
            @Override
            public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.player_single_layout,viewGroup,false);
                return new PlayerViewHolder(view);
            }
        };

        mPlayerList.setAdapter(mAdapter);

        mExitBtn = findViewById(R.id.button3);
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_uid = mCurrentUser.getUid();
                DatabaseReference playerRef = FirebaseDatabase.getInstance().
                                                getReference().child("Room").
                                                 child("room1").child(current_uid);
                playerRef.removeValue();

                Intent exitIntent = new Intent(RoomActivity.this,AccountActivity.class);
                startActivity(exitIntent);
                finish();

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEmail(String email){
            TextView userNameView = (TextView)mView.findViewById(R.id.player_single_email);
            userNameView.setText(email);
        }

        public void setReady(String ready){
            TextView userStatusView = (TextView)mView.findViewById(R.id.player_single_ready);
            userStatusView.setText(ready);
        }
    }
}
