package andbas.ui3_0628;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeaponRecyclerAdapter extends RecyclerView.Adapter<WeaponRecyclerAdapter.WeaponViewHolder>{

    private int[] images;
    private int[] counts;


    public WeaponRecyclerAdapter(int[] images, int[] counts) {
        this.images = images;
        this.counts = counts;
    }

    @NonNull
    @Override
    public WeaponViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_weapon_layout,viewGroup,false);
        WeaponViewHolder ViewHolder = new WeaponViewHolder(view);
        return  ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeaponViewHolder weaponViewHolder, int i) {
        int image_id = images[i];
        int count_id = counts[i];
        weaponViewHolder.iv_weapon_image.setImageResource(image_id);
        weaponViewHolder.tv_weapon_count.setText(String.valueOf(count_id));
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class WeaponViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_weapon_image;
        TextView tv_weapon_count;

        public WeaponViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_weapon_image = itemView.findViewById(R.id.single_weapon_img);
            tv_weapon_count = itemView.findViewById(R.id.single_weapon_count);
        }
    }
}
