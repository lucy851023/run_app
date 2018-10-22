package andbas.ui3_0628;


import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private Toolbar mToolbar;
    private RadioGroup group_sex;
    private RadioButton rb;
    private Button bt_save;
    private TextInputLayout til_name;


    private View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.setting_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("設定");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group_sex = findViewById(R.id.sex_group);
        til_name = findViewById(R.id.til_name);
        bt_save = findViewById(R.id.bt_save_change);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sex = dataSnapshot.child("sex").getValue().toString();
                String nickname = dataSnapshot.child("name").getValue().toString();

                if(sex.equals("male")){
                    group_sex.check(R.id.rb_male);
                }else{
                    group_sex.check(R.id.rb_female);
                }

                til_name.getEditText().setText(nickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radio_id = group_sex.getCheckedRadioButtonId();
                if(radio_id == R.id.rb_male){
                    mUserDatabase.child("sex").setValue("male");
                }else{
                    mUserDatabase.child("sex").setValue("female");
                }

                String user_name = til_name.getEditText().getText().toString();
                mUserDatabase.child("name").setValue(user_name);

                Toast.makeText(SettingsActivity.this,"已儲存",Toast.LENGTH_SHORT).show();
            }
        };
    }


    protected void onStart() {

        super.onStart();
        bt_save.setOnClickListener(mOnClickListener);
    }
}
