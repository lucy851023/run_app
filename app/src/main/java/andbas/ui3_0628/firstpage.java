package andbas.ui3_0628;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class firstpage extends AppCompatActivity {
    private TextInputEditText et_name;
    private Button bt_sure;
    private View.OnClickListener mOnClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        et_name = findViewById(R.id.et_name);
        bt_sure = findViewById(R.id.btn_sure);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_name.getText().toString().isEmpty()){
                    Toast.makeText(firstpage.this,"Empty.Please enter your nickname",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(firstpage.this,ChooseSexActivity.class);
                    String name = et_name.getText().toString();
                    intent.putExtra("name",name);
                    startActivity(intent);
                    finish();
                }

            }
        };

    }

    @Override
    protected void onStart() {

        super.onStart();
        bt_sure.setOnClickListener(mOnClickListener);
    }
}
