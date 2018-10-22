package andbas.ui3_0628;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameRoomActivity extends AppCompatActivity {

    private RecyclerView mPlayerList;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mPlayerDatabase;
    private FirebaseRecyclerAdapter<MyPlayers,GameRoomActivity.MyPlayerViewHolder> mAdapter;
    private DatabaseReference mUSerDatabase;
    private String roomID;
    private String roomType;
    private Button btn_ready;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        Intent intent = getIntent();
        roomType = intent.getStringExtra("room_type");
        roomID = intent.getStringExtra("room_id");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mPlayerDatabase = FirebaseDatabase.getInstance().getReference().child("Room").child(roomType).child(roomID);
        mPlayerDatabase.keepSynced(true);
        mUSerDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUSerDatabase.keepSynced(true);

        mLayoutManager = new GridLayoutManager(this,2);

        mPlayerList = findViewById(R.id.player_list);
        mPlayerList.setHasFixedSize(true);
        mPlayerList.setLayoutManager(mLayoutManager);
        mPlayerList.addItemDecoration(new SpaceItemDecoration(10));
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Room").child(roomType).child(roomID)
                .limitToLast(50);

        FirebaseRecyclerOptions<MyPlayers> options =
                new FirebaseRecyclerOptions.Builder<MyPlayers>()
                        .setQuery(query, MyPlayers.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<MyPlayers,MyPlayerViewHolder>(options){

            @NonNull
            @Override
            public MyPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_player_layout,viewGroup,false);
                return new MyPlayerViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final MyPlayerViewHolder holder, int position, @NonNull MyPlayers model) {

                holder.setReady(model.ready);
                String list_user_id = getRef(position).getKey();
                mUSerDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String user_name = dataSnapshot.child("name").getValue().toString();
                        String user_sex = dataSnapshot.child("sex").getValue().toString();

                        holder.setName(user_name);
                        holder.setSex(user_sex);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        mPlayerList.setAdapter(mAdapter);

        btn_ready = findViewById(R.id.btn_ready);

    }

    @Override
    protected void onStart(){
        super.onStart();

        mAdapter.startListening();
        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*String uid = mCurrentUser.getUid();
                mPlayerDatabase.child(uid).child("ready").setValue("yes");
                btn_ready.setEnabled(false);*/
                Intent choose_weapon_intent = new Intent(GameRoomActivity.this,ChooseWeaponActivity.class);
                startActivity(choose_weapon_intent);
                //finish();



            }
        });
    }
    @Override
    protected void onStop(){
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_BACK){
            String uid = mCurrentUser.getUid();
            mPlayerDatabase.child(uid).removeValue();
            finish();
        }

        return super.onKeyDown(keyCode,event);
    }

    public static class MyPlayerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyPlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSex(String sex){
            CircleImageView playerImageView = mView.findViewById(R.id.player_image);
            if(sex.equals("male")){
                playerImageView.setImageResource(R.drawable.male_img);
            }else{
                playerImageView.setImageResource(R.drawable.female_img);
            }

        }

        public void setName(String name){
            TextView playerName = mView.findViewById(R.id.tv_player_name);
            playerName.setText(name);
        }

        public void setReady(String ready){
            CircleImageView readyImageView = mView.findViewById(R.id.ready_image);
            if(ready.equals("yes")){
                readyImageView.setVisibility(View.VISIBLE);
            }else{
                readyImageView.setVisibility(View.INVISIBLE);
            }
        }


    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %2==0) {
                outRect.left = 0;
            }
        }

    }

}
