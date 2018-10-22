package andbas.ui3_0628;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private ImageButton btOK;
    private TextView mName,tvLevel;
    private ProgressBar pb_exp;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ValueEventListener valueEventListener , backpackListener,achievementListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mBackpackDatabase, mAchievementDatabase;
    private static final String[] weapon_id = {"baton","bone","bow","chopper","dagger",
            "dart","golden_axe","gun","kernel","rock"};
    private static final String[] medal_id = {"black","red","transparent","yellow"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //printKeyHash();

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }else{
                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                    String uid = mCurrentUser.getUid();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    mUserDatabase.keepSynced(true);
                    mUserDatabase.addValueEventListener(valueEventListener);

                    mBackpackDatabase = FirebaseDatabase.getInstance().getReference("Backpacks").child(uid);
                    mBackpackDatabase.keepSynced(true);
                    mBackpackDatabase.addListenerForSingleValueEvent(backpackListener);
                    mAchievementDatabase = FirebaseDatabase.getInstance().getReference("Achievements").child(uid);
                    mAchievementDatabase.keepSynced(true);
                    mAchievementDatabase.addListenerForSingleValueEvent(achievementListener);


                }

            }
        };



        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String sex = dataSnapshot.child("sex").getValue().toString();
                    int level = dataSnapshot.child("level").getValue(Integer.class);
                    int exp = dataSnapshot.child("exp").getValue(Integer.class);
                    mName.setText(name);
                    tvLevel.setText("Lv." + level);
                    pb_exp.setProgress(exp);

                    if(sex.equals("male")){
                        btOK.setImageResource(R.drawable.male_img);
                    }else{
                        btOK.setImageResource(R.drawable.female_img);
                    }

                }else{
                    Intent nameIntent = new Intent(MainActivity.this,firstpage.class);
                    startActivity(nameIntent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        backpackListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    for(int j=0; j<weapon_id.length;j++){
                        mBackpackDatabase.child(weapon_id[j]).child("count").setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        achievementListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    for(int j=0; j<medal_id.length;j++){
                        mAchievementDatabase.child(medal_id[j]).child("own").setValue(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



        mName = findViewById(R.id.tvIdNickname);
        tvLevel = findViewById(R.id.tv_level);
        pb_exp = findViewById(R.id.pb_exp);
        btOK = findViewById(R.id.bt_ok);



        mViewPager = findViewById(R.id.tabPager);
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewPager);




        btOK.setOnClickListener(btOKListener);

        drawerLayout= findViewById(R.id.drawerLayout);
        navigationView= findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(GravityCompat.START);

                int id = menuItem.getItemId();

                if(id == R.id.record){
                    Intent record_intent = new Intent(MainActivity.this,RecordsActivity.class);
                    startActivity(record_intent);
                    return true;
                }
                else if(id == R.id.killWay){
                    //Toast.makeText(MainActivity.this,"打怪模式",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,DoubleModeActivity.class);
                    startActivity(i);
                    return true;
                }else if(id == R.id.action_setting){
                    Toast.makeText(MainActivity.this,"設定",Toast.LENGTH_SHORT).show();
                    Intent setting_intent = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(setting_intent);
                    return true;
                }
                return false;
            }
        });


    }

    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);


    }

    private void printKeyHash(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "andbas.ui3_0628",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private View.OnClickListener btOKListener  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,MapsActivity.class);
            startActivity(intent);
        }
    };
    public class PlaceholderFragment{}
}
