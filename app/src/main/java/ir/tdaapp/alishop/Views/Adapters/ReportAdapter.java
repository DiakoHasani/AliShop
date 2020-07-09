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
import ir.tdaapp.alishop.Views.ViewModels.VM_Report;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    List<VM_Report> vals;
    Context context;

    public ReportAdapter(List<VM_Report> vals, Context context) {
        this.vals = vals;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_report, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.lbl_Title.setText(vals.get(position).getProductName());
        holder.lbl_Count.setText(context.getResources().getString(R.string.Count2) + " " + vals.get(position).getCount());
        holder.lbl_Fee.setText(context.getResources().getString(R.string.Fee2) + " " + vals.get(position).getFee());
        holder.lbl_Date.setText(context.getResources().getString(R.string.Date2) + " " + vals.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_Title, lbl_Fee, lbl_Count, lbl_Date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            FindItem(itemView);
        }

        void FindItem(View view) {
            lbl_Title = view.findViewById(R.id.lbl_Title);
            lbl_Fee = view.findViewById(R.id.lbl_Fee);
            lbl_Count = view.findViewById(R.id.lbl_Count);
            lbl_Date = view.findViewById(R.id.lbl_Date);
        }

    }

}
