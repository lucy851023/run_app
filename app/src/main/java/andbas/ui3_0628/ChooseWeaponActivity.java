package andbas.ui3_0628;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class ChooseWeaponActivity extends AppCompatActivity {
    private Button bt_weapon_kind;
    private String[] weapon_names = {"金色流星鎚","侏羅紀魚類的骨頭","太陽神之箭","樵夫的鐵斧頭","開山刀",
            "風魔手裏劍","樵夫的金斧頭","柯爾特M2000型手槍","廚餘蘋果核","路邊的小石子","窮酸的寶劍"};
    private boolean[] checkItems;
    private ArrayList<Integer> mPlayerItems = new ArrayList<>();
    private int[] images = {R.drawable.baton,R.drawable.bone,R.drawable.bow,
            R.drawable.chopper,R.drawable.dagger,R.drawable.dart,
            R.drawable.golden_axe,R.drawable.gun,R.drawable.kernal,
            R.drawable.rock,R.drawable.sword};
    private List<Integer> images_chosen = new ArrayList<>();
    private List<String> weapon_name_chosen = new ArrayList<>();
    private List<Integer> weapon_count_chosen = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WeaponRecyclerAdapter adapter;

    private Dialog mDialog;
    private NumberPicker mNumberPicker;
    private Button bt_choose_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_weapon);
        bt_weapon_kind = findViewById(R.id.bt_weapon_kind);
        checkItems = new boolean[weapon_names.length];
        recyclerView = findViewById(R.id.choose_weapon_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mDialog = new Dialog(this);
        bt_choose_count = findViewById(R.id.bt_choose_count);
        bt_choose_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseWeaponActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                builder.setTitle("武器數量");
                View view = inflater.inflate(R.layout.number_selector_dialog_layout,null);
                mNumberPicker = view.findViewById(R.id.number_picker);
                mNumberPicker.setMinValue(0);
                mNumberPicker.setMaxValue(10);
                mNumberPicker.setValue(1);
                mNumberPicker.setWrapSelectorWheel(false);
                mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        /*mNumberPicker = findViewById(R.id.number_picker);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(10);
        mNumberPicker.setValue(1);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //得到选择结果
            }
        });*/




        bt_weapon_kind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChooseWeaponActivity.this);
                mBuilder.setTitle("請選擇武器種類(五種)");
                mBuilder.setMultiChoiceItems(weapon_names, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            if(!mPlayerItems.contains(which)){
                                mPlayerItems.add(which);
                            }
                        }else if (mPlayerItems.contains(which)){
                            mPlayerItems.remove(which);
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choose_items = "";
                        for(int i=0;i< mPlayerItems.size();i++){
                            choose_items = choose_items + weapon_names[mPlayerItems.get(i)] + ",";
                        }

                        images_chosen.clear();
                        weapon_name_chosen.clear();
                        for(int j=0;j < checkItems.length;j++){
                            if(checkItems[j]){
                                images_chosen.add(images[j]);
                                weapon_name_chosen.add(weapon_names[j]);
                                weapon_count_chosen.add(0);
                            }
                        }

                        adapter = new WeaponRecyclerAdapter(ListToArray(images_chosen),ListToArray(weapon_count_chosen));
                        recyclerView.setAdapter(adapter);

                        Toast.makeText(ChooseWeaponActivity.this,choose_items,Toast.LENGTH_SHORT).show();
                    }
                });

                mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0; i<checkItems.length;i++){
                            checkItems[i] = false;
                            mPlayerItems.clear();
                        }
                    }
                });

                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();
            }
        });
    }

    public int[] ListToArray(List<Integer> list){
        int[] array = new int[list.size()];
        for(int i=0;i<list.size();i++){
            array[i] = list.get(i);
        }
        return array;
    }




}
