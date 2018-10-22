package andbas.ui3_0628;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class AchievementFragment extends Fragment {

    private RecyclerView achievement_list;
    private View mMainView;
    private String user_id;
    private DatabaseReference mAchievementDatabase,mMedalDatabase;
    private FirebaseRecyclerAdapter<AchievementItem,AchievementFragment.AchievementViewHolder> mAdapter;


    public AchievementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_achievement, container, false);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mMedalDatabase = FirebaseDatabase.getInstance().getReference("Medal");

        mAchievementDatabase = FirebaseDatabase.getInstance().getReference("Achievements").child(user_id);

        achievement_list = mMainView.findViewById(R.id.achievement_list);
        achievement_list.setHasFixedSize(true);
        achievement_list.setLayoutManager(new GridLayoutManager(getContext(),3));
        achievement_list.addItemDecoration(new SpaceItemDecoration(10));

        Query query = mAchievementDatabase.limitToLast(50);
        FirebaseRecyclerOptions<AchievementItem> options =
                new FirebaseRecyclerOptions.Builder<AchievementItem>()
                        .setQuery(query, AchievementItem.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<AchievementItem, AchievementViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AchievementViewHolder holder, int position, @NonNull final AchievementItem model) {
                String medal_key = getRef(position).getKey();
                mMedalDatabase.child(medal_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String medal_image = dataSnapshot.child("image").getValue().toString();
                        holder.setImage(medal_image,model.own);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.cell_layout,viewGroup,false);

                return new AchievementViewHolder(view);
            }
        };

        achievement_list.setAdapter(mAdapter);
        return mMainView;
    }

    public void onStart(){
        super.onStart();
        mAdapter.startListening();
    }

    public void onStop(){
        super.onStop();
        mAdapter.startListening();
    }


    public static class AchievementViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String url,boolean own) {
            ImageView item_image = mView.findViewById(R.id.iv_cell);
            Picasso.get().load(url).into(item_image);
            if(own){
                item_image.setVisibility(View.VISIBLE);
            }else {
                item_image.setVisibility(View.INVISIBLE);
            }
        }




    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %3==0) {
                outRect.left = 0;
            }
        }

    }


}
