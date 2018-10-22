package andbas.ui3_0628;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private int[] images;


    public RecyclerAdapter(int[] images){
        this.images = images;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout,viewGroup,false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return  imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int i) {
        int image_id = images[i];
        viewHolder.iv_cell.setImageResource(image_id);

    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_cell;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_cell = itemView.findViewById(R.id.iv_cell);
        }
    }
}
