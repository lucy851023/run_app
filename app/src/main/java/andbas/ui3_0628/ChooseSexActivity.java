package andbas.ui3_0628;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseSexActivity extends AppCompatActivity implements View.OnClickListener{

    private CircleImageView iv_male;
    private CircleImageView iv_female;
    private String name;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private static final String[] weapon_id = {"baton","bone","bow","chopper","dagger",
                                               "dart","golden_axe","gun","kernel","rock"};
    private static final String[] medal_id = {"black","red","transparent","yellow"};
    private DatabaseReference mBackpackDatabase, mAchievementDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sex);

        iv_male = findViewById(R.id.iv_male);
        iv_female = findViewById(R.id.iv_female);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mBackpackDatabase = FirebaseDatabase.getInstance().getReference("Backpacks").child(uid);
        mAchievementDatabase = FirebaseDatabase.getInstance().getReference("Achievements").child(uid);

        iv_male.setOnClickListener(this);
        iv_female.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        String sex;

        if(i == R.id.iv_male){
            sex = "male";

        }else{
            sex = "female";
        }

        MyUsers user = new MyUsers(name,sex,0,0);
        mUserDatabase.setValue(user);

        for(int j=0; j<weapon_id.length;j++){
            mBackpackDatabase.child(weapon_id[j]).child("count").setValue(0);
        }

        for(int k=0; k<medal_id.length;k++){
            mAchievementDatabase.child(medal_id[k]).child("own").setValue(false);
        }


        Intent secondPage = new Intent(ChooseSexActivity.this,secondActivity.class);
        startActivity(secondPage);
        finish();
    }
}
