package andbas.ui3_0628;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FinishActivity extends AppCompatActivity {

    private TextView run_length;
    private TextView run_time;
    private TextView totalLength;
    private TextView tvDate;
    private ImageButton ib_home;
    private static final String TAG = FinishActivity.class.getSimpleName();
    private static final String FILE_NAME = "MyFile";
    //private ImageView ivMap;
    //private Button bt_image;
    private static final String[] weapon_id = {"baton","bone","bow","chopper","dagger",
            "dart","golden_axe","gun","kernel","rock"};
    private int[] current_weapon_count;

    private int[] images = {R.drawable.baton,R.drawable.bone,R.drawable.bow,
            R.drawable.chopper,R.drawable.dagger,R.drawable.dart,
            R.drawable.golden_axe,R.drawable.gun,R.drawable.kernal,
            R.drawable.rock,R.drawable.sword};
    private int[] counts = {0,0,0,0,0,0,0,0,0,0,0};
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WeaponRecyclerAdapter adapter;

    private float currentLength;
    private DatabaseReference mRecordDatabase,mBackpackDatabase;
    private FirebaseUser mCurrentUser;
    private ValueEventListener valueEventListener ,backpackListener;
    private float len;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        run_length = findViewById(R.id.tvRunLength);
        run_time = findViewById(R.id.tvRunTime);
        //ivMap = findViewById(R.id.ivMap);
        //bt_image = findViewById(R.id.bt_image);
        totalLength = findViewById(R.id.tvTotalLength);
        tvDate = findViewById(R.id.tvDate);
        ib_home = findViewById(R.id.home);



        //currentLength = 0;

        Intent intent = getIntent();
        len = intent.getFloatExtra("run_length",0.f);
        String time = intent.getStringExtra("run_time");
        int box_count = intent.getIntExtra("box_num",0);

        for(int j=0;j<box_count;j++){
            Random r = new Random();
            int i = r.nextInt(counts.length);
            counts[i]++;
        }
        //float total = len + currentLength;
        Date da = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mRecordDatabase = FirebaseDatabase.getInstance().getReference().
                child("Records").child(uid);

        mBackpackDatabase = FirebaseDatabase.getInstance().getReference("Backpacks").child(uid);


        Records records = new Records(time,formatter.format(da),len);
        String record_id = mRecordDatabase.push().getKey();
        mRecordDatabase.child(record_id).setValue(records);

        recyclerView = findViewById(R.id.finish_weapon_list);
        layoutManager = new GridLayoutManager(this,3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WeaponRecyclerAdapter(images,counts);
        recyclerView.setAdapter(adapter);


        run_length.setText(len + "KM");
        run_time.setText(time);

        tvDate.setText(formatter.format(da));

        /*bt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivMap.setImageBitmap(loadBitmap(FinishActivity.this,FILE_NAME));
            }
        });*/

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                float total_length = 0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    total_length = total_length + snapshot.child("length").getValue(Float.class);
                }

                totalLength.setText(total_length + "KM");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //current_weapon_count = new int[counts.length];

        backpackListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int k = 0;

                for(DataSnapshot weapon_snapshot:dataSnapshot.getChildren()){
                    String weapon_key = weapon_snapshot.getKey();
                    int previous_count = weapon_snapshot.child("count").getValue(Integer.class);
                    int total_count = previous_count + counts[k];
                    mBackpackDatabase.child(weapon_key).child("count").setValue(total_count);
                    k++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ib_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(FinishActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });


    }

    protected void onStart() {

        super.onStart();

        mRecordDatabase.addListenerForSingleValueEvent(valueEventListener);
        mBackpackDatabase.addListenerForSingleValueEvent(backpackListener);

        /*for(int j=0;j<counts.length;j++){
            int total_count = counts[j] + current_weapon_count[j];
            mBackpackDatabase.child(weapon_id[j]).child("count").setValue(total_count);
        }*/


    }

    public static Bitmap loadBitmap(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }
}
