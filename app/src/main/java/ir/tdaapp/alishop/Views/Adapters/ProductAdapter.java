package ir.tdaapp.alishop.Views.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Utility.PlusProductClicked;
import ir.tdaapp.alishop.Views.Utility.ProductClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    List<VM_Product> vals;
    private PlusProductClicked plusProductClicked;
    private ProductClicked productClicked;

    public void setPlusProductClicked(PlusProductClicked plusProductClicked) {
        this.plusProductClicked = plusProductClicked;
    }

    public void setProductClicked(ProductClicked productClicked) {
        this.productClicked = productClicked;
    }

    public ProductAdapter(Context context, List<VM_Product> vals) {
        this.context = context;
        this.vals = vals;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_product,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(vals.get(position).getProductName());

        holder.lbl_Total.setText(context.getResources().getString(R.string.Total)+" "+vals.get(position).getTotal());

        if (vals.get(position).getPlace()==1){
            holder.lbl_StoreTitle.setText(context.getResources().getString(R.string.Store1));
        }else{
            holder.lbl_StoreTitle.setText(context.getResources().getString(R.string.Store2));
        }

        Glide.with(context)
                .load(vals.get(position).getImage())
                .placeholder(R.drawable.ic_priority_high)
                .error(R.drawable.ic_image_product)
                .into(holder.img);

        holder.plus.setOnClickListener(view -> {
            plusProductClicked.OnClick(vals.get(position).getId());
        });

        holder.layout.setOnClickListener(view -> {
            productClicked.Click(vals.get(position).getId());
        });

    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView layout;
        ImageView img,plus;
        TextView title,lbl_StoreTitle,lbl_Total;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            FindItem(itemView);

            layout.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite));
            title.setTextColor(context.getResources().getColor(R.color.colorBlack));

        }

        void FindItem(View itemView){
            layout=itemView.findViewById(R.id.layout);
            img=itemView.findViewById(R.id.img);
            plus=itemView.findViewById(R.id.plus);
            title=itemView.findViewById(R.id.title);
            lbl_StoreTitle=itemView.findViewById(R.id.lbl_StoreTitle);
            lbl_Total=itemView.findViewById(R.id.lbl_Total);
        }
    }

}
