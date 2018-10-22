package andbas.ui3_0628;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import java.util.Objects;


public class RecordsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private RecyclerView mRecordList;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mRecordDatabase;
    private FirebaseRecyclerAdapter<Records,RecordsActivity.RecordViewHolder> mAdapter;
    private FirebaseUser mCurrentUser;
    private ValueEventListener valueEventListener;
    private TextView tv_total_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mRecordDatabase = FirebaseDatabase.getInstance().getReference().child("Records").child(uid);

        mToolbar = findViewById(R.id.record_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("拓荒紀錄");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_total_length = findViewById(R.id.tv_total_length);




        mLayoutManager = new LinearLayoutManager(this);

        mRecordList = findViewById(R.id.record_list);
        mRecordList.setHasFixedSize(true);
        mRecordList.setLayoutManager(mLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
        mRecordList.addItemDecoration(divider);




        Query query = mRecordDatabase.limitToLast(50);

        FirebaseRecyclerOptions<Records> options =
                new FirebaseRecyclerOptions.Builder<Records>()
                        .setQuery(query, Records.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Records, RecordViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecordViewHolder holder, int position, @NonNull Records model) {
                holder.setDate(model.date);
                holder.setTime(model.time);
                holder.setLength(Float.toString(model.length));
            }

            @NonNull
            @Override
            public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_record_layout,viewGroup,false);
                return new RecordViewHolder(view);

            }
        };

        mRecordList.setAdapter(mAdapter);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                float total_length = 0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    total_length = total_length + snapshot.child("length").getValue(Float.class);
                }

               tv_total_length.setText(total_length + "KM");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



    }

    @Override
    protected void onStart() {

        super.onStart();
        mAdapter.startListening();
        mRecordDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAdapter.stopListening();
        mRecordDatabase.removeEventListener(valueEventListener);
    }


    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date){
            TextView runDate = mView.findViewById(R.id.single_record_date);
            runDate.setText(date);

        }

        public void setTime(String time){
            TextView runTime = mView.findViewById(R.id.single_record_time);
            runTime.setText(time);
        }

        public void setLength(String length){
            TextView runLength = mView.findViewById(R.id.single_record_length);
            runLength.setText(length);
        }


    }
}
