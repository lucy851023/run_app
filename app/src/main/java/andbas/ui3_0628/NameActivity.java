package andbas.ui3_0628;


import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mName;
    private Button mSaveBtn;

    private DatabaseReference mNameDatabase;
    private FirebaseUser mCurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        mToolbar = findViewById(R.id.name_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Name");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();

        mNameDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        String name_value = getIntent().getStringExtra("name_value");
        mName = findViewById(R.id.til_name);
        mName.getEditText().setText(name_value);
        mSaveBtn = findViewById(R.id.btn_name_save);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getEditText().getText().toString();
                mNameDatabase.child("name").setValue(name);
            }
        });
    }
}
