package ir.tdaapp.alishop.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_WayClicked;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;

public class ConditionProductFragment extends Fragment implements IBase, View.OnClickListener {

    public final static String TAG = "ConditionProductFragment";

    TextView lbl_Title, lbl_Fee, lbl_Count;
    Button btn_Comback, btn_Accept;
    GetJsonObjectVolley sendWayVolley;
    ShimmerFrameLayout Loading;
    RelativeLayout background;

    int Id, ProductId,Count;
    double Fee;
    String Title;

    public ConditionProductFragment(VM_WayClicked v) {
        this.Id = v.getId();
        this.ProductId = v.getProductId();
        this.Count=v.getCount();
        this.Fee=v.getFee();
        this.Title=v.getTitle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.condition_product_fragment, container, false);

        FindItem(view);
        Implements();
        GetItem();

        return view;
    }

    void Implements() {
        background.setOnClickListener(this);
        btn_Comback.setOnClickListener(this);
        btn_Accept.setOnClickListener(this);
    }

    void FindItem(View view) {

        lbl_Title = view.findViewById(R.id.lbl_Title);
        lbl_Fee = view.findViewById(R.id.lbl_Fee);
        lbl_Count = view.findViewById(R.id.lbl_Count);
        btn_Comback = view.findViewById(R.id.btn_Comback);
        btn_Accept = view.findViewById(R.id.btn_Accept);
        Loading = view.findViewById(R.id.Loading);
        background = view.findViewById(R.id.background);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sendWayVolley!=null)
            sendWayVolley.Cancel(TAG,getContext());
    }

    void GetItem() {

        lbl_Title.setText(Title);
        lbl_Count.setText(getContext().getResources().getString(R.string.Count2) + " " + Count);
        lbl_Fee.setText(getContext().getResources().getString(R.string.Fee2) + " " + Fee);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.background:
                getActivity().onBackPressed();
                break;
            case R.id.btn_Accept:
                SendWay(false);
                break;
            case R.id.btn_Comback:
                SendWay(true);
                break;
        }
    }

    void SendWay(boolean back) {

        Loading.startShimmerAnimation();
        btn_Accept.setEnabled(false);
        btn_Comback.setEnabled(false);

        String Url = Api + "Product/ReceiveProduct?id="+Id+"&back="+back;

        sendWayVolley=new GetJsonObjectVolley(Url,resault -> {

            Loading.stopShimmerAnimation();
            btn_Accept.setEnabled(true);
            btn_Comback.setEnabled(true);

            if (resault.getResault() == ResaultCode.Success) {

                JSONObject object=resault.getObject();

                try {

                    Toast.makeText(getContext(), object.getString("Titel"), Toast.LENGTH_SHORT).show();

                    if (object.getBoolean("Status")){
                        ((CentralActivity)getActivity()).ReloadAllRecycler();
                        getActivity().onBackPressed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resault.getResault() == ResaultCode.NetworkError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            } else if (resault.getResault() == ResaultCode.TimeoutError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
            }

        });
    }

}
