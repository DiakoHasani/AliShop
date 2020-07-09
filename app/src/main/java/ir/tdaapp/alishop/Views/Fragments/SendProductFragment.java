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

public class SendProductFragment extends Fragment implements IBase, View.OnClickListener {

    public final static String TAG = "SendProductFragment";

    TextView lbl_Title;
    EditText txt_Count;
    RadioButton rdo_Store1, rdo_Store2;
    Button btn_Cancel, btn_Send;
    RelativeLayout background;
    int Id = -1;
    GetJsonObjectVolley getJsonObjectVolley;
    ShimmerFrameLayout Loading;
    PostJsonObjectVolley postJsonObjectVolley;
    int Fee = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_product_fragment, container, false);

        FindItem(view);
        Implements();
        GetItem();

        return view;
    }

    void FindItem(View view) {
        lbl_Title = view.findViewById(R.id.lbl_Title);
        txt_Count = view.findViewById(R.id.txt_Count);
        rdo_Store1 = view.findViewById(R.id.rdo_Store1);
        rdo_Store2 = view.findViewById(R.id.rdo_Store2);
        btn_Cancel = view.findViewById(R.id.btn_Cancel);
        btn_Send = view.findViewById(R.id.btn_Send);
        background = view.findViewById(R.id.background);
        Loading = view.findViewById(R.id.Loading);
    }

    void Implements() {
        background.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
        btn_Send.setOnClickListener(this);
    }

    void GetItem() {

        Loading.startShimmerAnimation();
        Id = getArguments().getInt("Id");

        getJsonObjectVolley = new GetJsonObjectVolley(Api + "Product/ProductById?id=" + Id, resault -> {

            Loading.stopShimmerAnimation();

            if (resault.getResault() == ResaultCode.Success) {

                try {

                    JSONObject object = resault.getObject();

                    lbl_Title.setText(object.getString("ProductName"));
                    Fee = object.getInt("Fee");

                    if (getArguments().getInt("Place") == 1) {
                        rdo_Store1.setEnabled(false);
                        rdo_Store2.setChecked(true);
                    } else {
                        rdo_Store2.setEnabled(false);
                        rdo_Store1.setChecked(true);
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

    void SetItem() {

        JSONObject object = new JSONObject();
        try {

            Loading.startShimmerAnimation();

            int Place;

            if (rdo_Store1.isChecked()) {
                Place = 1;
            } else {
                Place = 2;
            }

            int Place2;
            if (Place == 1)
                Place2 = 2;
            else
                Place2 = 1;

            object.put("Id", Id);
            object.put("Count", Integer.valueOf(txt_Count.getText().toString()));
            object.put("FkPlaceDestination", Place);
            object.put("FkPlace", Place2);
            object.put("Fee", Fee);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postJsonObjectVolley = new PostJsonObjectVolley(Api + "Product/TransporProduct", object, resault -> {

            Loading.stopShimmerAnimation();

            if (resault.getResault() == ResaultCode.Success) {

                try {

                    Toast.makeText(getContext(), resault.getObject().getString("Titel"), Toast.LENGTH_SHORT).show();

                    if (resault.getObject().getBoolean("Status")) {
                        ((CentralActivity) getActivity()).ReloadAllRecycler();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.background:
            case R.id.btn_Cancel:
                getActivity().onBackPressed();
                break;
            case R.id.btn_Send:
                if (Validation.Required(txt_Count, getResources().getString(R.string.ThisEditTextIsRequired))) {
                    SetItem();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getJsonObjectVolley.Cancel(TAG, getContext());

        if (postJsonObjectVolley != null)
            postJsonObjectVolley.Cancel(TAG, getContext());
    }
}
