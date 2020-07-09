package ir.tdaapp.alishop.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import ir.tdaapp.li_utility.Codes.Validation;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;
import ir.tdaapp.li_volley.Volleys.PostJsonObjectVolley;

public class SellProductFragment extends Fragment implements IBase, View.OnClickListener {

    public final static String TAG = "SellProductFragment";

    TextView lbl_Title,lbl_Total;
    EditText txt_Fee, txt_Count;
    RadioButton rdo_Store1, rdo_Store2;
    Button btn_Cancel, btn_Sell;
    int Id = -1;
    GetJsonObjectVolley getJsonObjectVolley;
    RelativeLayout background;
    PostJsonObjectVolley postJsonObjectVolley;
    ShimmerFrameLayout Loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sell_product_fragment, container, false);

        FindItem(view);
        Implements();

        SetItem();

        return view;
    }

    void FindItem(View view) {
        lbl_Title = view.findViewById(R.id.lbl_Title);
        txt_Fee = view.findViewById(R.id.txt_Fee);
        txt_Count = view.findViewById(R.id.txt_Count);
        rdo_Store1 = view.findViewById(R.id.rdo_Store1);
        rdo_Store2 = view.findViewById(R.id.rdo_Store2);
        btn_Cancel = view.findViewById(R.id.btn_Cancel);
        btn_Sell = view.findViewById(R.id.btn_Sell);
        background = view.findViewById(R.id.background);
        Loading = view.findViewById(R.id.Loading);
        lbl_Total = view.findViewById(R.id.lbl_Total);
    }

    void Implements() {
        background.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
        btn_Sell.setOnClickListener(this);
    }

    void SetItem() {

        Loading.startShimmerAnimation();

        Id = getArguments().getInt("Id");

        getJsonObjectVolley = new GetJsonObjectVolley(Api + "Product/ProductById?id=" + Id, resault -> {

            Loading.stopShimmerAnimation();

            if (resault.getResault() == ResaultCode.Success) {

                try {

                    JSONObject object = resault.getObject();

                    lbl_Title.setText(object.getString("ProductName"));
                    lbl_Total.setText(getContext().getResources().getString(R.string.Total)+" "+object.getString("Count"));

                    if (object.getInt("FkPlace") == 1) {
                        rdo_Store2.setEnabled(false);
                        rdo_Store1.setChecked(true);
                    } else {
                        rdo_Store1.setEnabled(false);
                        rdo_Store2.setChecked(true);
                    }

                } catch (Exception e) {

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

    void SellProduct() {

        boolean valid = true;

        if (!Validation.Required(txt_Fee, getContext().getResources().getString(R.string.ThisEditTextIsRequired))) {
            valid = false;
        }

        if (!Validation.Required(txt_Count, getContext().getResources().getString(R.string.ThisEditTextIsRequired))) {
            valid = false;
        }

        if (valid) {

            try {

                Loading.startShimmerAnimation();
                btn_Sell.setEnabled(false);

                int Place = rdo_Store1.isChecked() ? 1 : 2;

                JSONObject object = new JSONObject();
                object.put("Id", Id);
                object.put("FkPlace", Place);
                object.put("Fee", txt_Fee.getText());
                object.put("Count", txt_Count.getText());

                postJsonObjectVolley = new PostJsonObjectVolley(Api + "Product/SellProduct", object, resault -> {

                    Loading.stopShimmerAnimation();
                    btn_Sell.setEnabled(true);

                    if (resault.getResault() == ResaultCode.Success) {

                        JSONObject obj = resault.getObject();

                        try {

                            Toast.makeText(getContext(), obj.getString("Titel"), Toast.LENGTH_SHORT).show();

                            if (obj.getBoolean("Status")) {
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

            } catch (Exception e) {

            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Sell:
                SellProduct();
                break;
            case R.id.background:
            case R.id.btn_Cancel:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getJsonObjectVolley.Cancel(TAG, getContext());

        if (postJsonObjectVolley != null) {
            postJsonObjectVolley.Cancel(TAG, getContext());
        }
    }
}
