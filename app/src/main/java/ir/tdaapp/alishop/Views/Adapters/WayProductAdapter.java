package ir.tdaapp.alishop.Views.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Utility.WayProductClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_WayClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_WayProduct;

public class WayProductAdapter extends RecyclerView.Adapter<WayProductAdapter.MyViewHolder> {

    List<VM_WayProduct> vals = new ArrayList<>();
    Context context;
    WayProductClicked wayProductClicked;

    public WayProductAdapter(List<VM_WayProduct> vals, Context context) {
        this.vals = vals;
        this.context = context;
    }

    public void setWayProductClicked(WayProductClicked wayProductClicked) {
        this.wayProductClicked = wayProductClicked;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_way_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.lbl_Title.setText(vals.get(position).getProductName());
        holder.lbl_Count.setText(context.getResources().getString(R.string.Count2)+" "+vals.get(position).getCount());
        holder.lbl_Price.setText(context.getResources().getString(R.string.Fee2)+" "+vals.get(position).getFee());

        Glide.with(context)
                .load("//")
                .error(R.drawable.ic_image_product)
                .into(holder.img);

        holder.Layout.setOnClickListener(view -> {

            VM_WayClicked v = new VM_WayClicked();
            v.setCount(vals.get(position).getCount());
            v.setFee(vals.get(position).getFee());
            v.setId(vals.get(position).getId());
            v.setProductId(vals.get(position).getProductId());
            v.setTitle(vals.get(position).getProductName());

            wayProductClicked.WayClicked(v);
        });

    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_Title, lbl_Price, lbl_Count;
        ImageView img;
        CardView Layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            FindItem(itemView);

        }

        void FindItem(View view){
            lbl_Title=view.findViewById(R.id.lbl_Title);
            lbl_Price=view.findViewById(R.id.lbl_Price);
            lbl_Count=view.findViewById(R.id.lbl_Count);
            img=view.findViewById(R.id.img);
            Layout=view.findViewById(R.id.Layout);
        }

    }

}
