package ir.tdaapp.alishop.Views.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.ViewModels.VM_DetailItem;

public class DeatailItemAdapter extends RecyclerView.Adapter<DeatailItemAdapter.MyViewHolder> {

    Context context;
    List<VM_DetailItem> vals;

    public DeatailItemAdapter(Context context, List<VM_DetailItem> vals) {
        this.context = context;
        this.vals = vals;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_deatail_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {

            holder.lbl_Title.setText(vals.get(position).getTitle());
            holder.lbl_Count.setText(context.getResources().getString(R.string.Count2) + vals.get(position).getCount());
            holder.lbl_Date.setText(vals.get(position).getDate());
            holder.lbl_Description.setText(vals.get(position).getDescription());
            holder.lbl_Fee.setText(context.getResources().getString(R.string.Fee2) + vals.get(position).getFee());
            holder.lbl_Sum.setText(context.getResources().getString(R.string.Sum2)+" "+vals.get(position).getCount()*vals.get(position).getFee());

            if (vals.get(position).getPlace() == 1) {
                holder.lbl_Place.setText(context.getResources().getString(R.string.Store1));
            } else {
                holder.lbl_Place.setText(context.getResources().getString(R.string.Store2));
            }

        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_Title, lbl_Place, lbl_Count, lbl_Date, lbl_Fee, lbl_Description,lbl_Sum;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            FindItem(itemView);

        }

        void FindItem(View view) {
            lbl_Title = view.findViewById(R.id.lbl_Title);
            lbl_Place = view.findViewById(R.id.lbl_Place);
            lbl_Count = view.findViewById(R.id.lbl_Count);
            lbl_Date = view.findViewById(R.id.lbl_Date);
            lbl_Fee = view.findViewById(R.id.lbl_Fee);
            lbl_Description = view.findViewById(R.id.lbl_Description);
            lbl_Sum = view.findViewById(R.id.lbl_Sum);
        }
    }
}
