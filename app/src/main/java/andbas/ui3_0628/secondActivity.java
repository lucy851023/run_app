package andbas.ui3_0628;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class secondActivity extends Activity implements View.OnClickListener{
    private Button bt_explore,bt_kill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        bt_explore = findViewById(R.id.bt_explore);
        bt_kill = findViewById(R.id.bt_kill);

        bt_explore.setOnClickListener(this);
        bt_kill.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Intent intent = new Intent();

        if(i == R.id.bt_explore){
            intent.setClass(secondActivity.this,MainActivity.class);

        }else{
            intent.setClass(secondActivity.this,DoubleModeActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
