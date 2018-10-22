package andbas.ui3_0628;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class BackpacksFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<BackpackItem,BackpacksFragment.BackpackViewHolder> mAdapter;
    private DatabaseReference mBackpackDatabase,mWeaponDatabase;
    private View mMainView;
    private String user_id;



    public BackpacksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_backpacks, container, false);
        recyclerView = mMainView.findViewById(R.id.backpack_list);
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mBackpackDatabase = FirebaseDatabase.getInstance().getReference("Backpacks").child(user_id);
        mWeaponDatabase = FirebaseDatabase.getInstance().getReference("Weapon");


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = mBackpackDatabase.limitToLast(50);

        FirebaseRecyclerOptions<BackpackItem> options =
                new FirebaseRecyclerOptions.Builder<BackpackItem>()
                        .setQuery(query, BackpackItem.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<BackpackItem, BackpackViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BackpackViewHolder holder, int position, @NonNull BackpackItem model) {

                holder.setCount(model.count);

                String weapon_key = getRef(position).getKey();
                mWeaponDatabase.child(weapon_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String weapon_image = dataSnapshot.child("image").getValue().toString();
                        String weapon_name = dataSnapshot.child("name").getValue().toString();

                        holder.setImage(weapon_image);
                        holder.setName(weapon_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public BackpackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.backpack_item_layout,viewGroup,false);
                return new BackpackViewHolder(view);
            }
        };

        recyclerView.setAdapter(mAdapter);

        return mMainView;
    }

    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    public void onStop() {

        super.onStop();
        mAdapter.stopListening();
    }


    public static class BackpackViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BackpackViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String url){
            ImageView item_image = mView.findViewById(R.id.backpack_item_image);
            Picasso.get().load(url).into(item_image);
        }

        public void setName(String name){
            TextView item_name = mView.findViewById(R.id.backpack_item_name);
            item_name.setText(name);
        }

        public void setCount(int count){
            TextView item_count = mView.findViewById(R.id.backpack_item_count);
            item_count.setText(String.valueOf(count));

        }



    }

}
