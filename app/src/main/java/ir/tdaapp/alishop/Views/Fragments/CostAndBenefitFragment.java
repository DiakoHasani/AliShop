package ir.tdaapp.alishop.Views.Fragments;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product_Spinner;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;

public class CostAndBenefitFragment extends Fragment implements IBase, View.OnClickListener {

    public final static String TAG = "CostAndBenefitFragment";

    Toolbar toolBar;
    TextView lbl_AllProductIn, lbl_AllProductTransportIn, lbl_AllProductOut, lbl_AllProductBack, lbl_ProductSoldCount;
    TextView lbl_ProductSoldPrice, lbl_ProductBoughtCount, lbl_ProductBoughtPrice, lbl_Profit;
    CardView btn_Search;
    DatePickerDialog.OnDateSetListener F_date, T_date;
    EditText txt_FromDate, txt_ToDate;
    SearchableSpinner spinner_search;
    ShimmerFrameLayout Loading;
    GetJsonArrayVolley getProductSpinnerVolley;
    GetJsonObjectVolley getReportVolley;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cost_and_benefit_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();
        SetSpinnerProductName();

        return view;
    }

    void FindItem(View view) {
        toolBar = view.findViewById(R.id.toolBar);
        lbl_AllProductIn = view.findViewById(R.id.lbl_AllProductIn);
        lbl_AllProductTransportIn = view.findViewById(R.id.lbl_AllProductTransportIn);
        lbl_AllProductOut = view.findViewById(R.id.lbl_AllProductOut);
        lbl_AllProductBack = view.findViewById(R.id.lbl_AllProductBack);
        lbl_ProductSoldCount = view.findViewById(R.id.lbl_ProductSoldCount);
        lbl_ProductSoldPrice = view.findViewById(R.id.lbl_ProductSoldPrice);
        lbl_ProductBoughtCount = view.findViewById(R.id.lbl_ProductBoughtCount);
        lbl_ProductBoughtPrice = view.findViewById(R.id.lbl_ProductBoughtPrice);
        lbl_Profit = view.findViewById(R.id.lbl_Profit);
        btn_Search = view.findViewById(R.id.btn_Search);
        txt_FromDate = view.findViewById(R.id.txt_FromDate);
        txt_ToDate = view.findViewById(R.id.txt_ToDate);
        spinner_search = view.findViewById(R.id.spinner_search);
        Loading = view.findViewById(R.id.Loading);
    }

    void Implements() {
        btn_Search.setOnClickListener(this::onClick);

        txt_FromDate.setOnFocusChangeListener((view, b) -> {
            if (b) {
                ShowDatePicker(F_date);
            }
        });

        txt_ToDate.setOnFocusChangeListener((view, b) -> {
            if (b) {
                ShowDatePicker(T_date);
            }
        });

        F_date = (datePicker, i, i1, i2) -> {
            String d = i + "/" + i1 + "/" + i2;
            txt_FromDate.setText(d);
        };

        T_date = (datePicker, i, i1, i2) -> {
            String d = i + "/" + i1 + "/" + i2;
            txt_ToDate.setText(d);
        };
    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.CostAndBenefit));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);
        ((CentralActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        toolBar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Search:
                GetReport();
                break;
        }
    }

    void ShowDatePicker(DatePickerDialog.OnDateSetListener date) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, year, month, day);
        datePickerDialog.show();
    }

    //در اینجا مقادیر اسپینر جستجو گرفته می شود
    void SetSpinnerProductName() {

        Loading.startShimmerAnimation();

        getProductSpinnerVolley = new GetJsonArrayVolley(Api + "Product/AllProductDp", resault -> {

            if (resault.getResault() == ResaultCode.Success) {

                List<VM_Product_Spinner> products = new ArrayList<>();
                VM_Product_Spinner v0 = new VM_Product_Spinner(0, getContext().getResources().getString(R.string.SearchByName));
                products.add(v0);

                JSONArray array = resault.getJsonArray();

                for (int i = 0; i < array.length(); i++) {

                    try {

                        JSONObject object = array.getJSONObject(i);
                        VM_Product_Spinner v = new VM_Product_Spinner();
                        v.setId(object.getInt("Id"));
                        v.setTitle(object.getString("ProductName"));

                        products.add(v);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                        R.layout.spinner_layout, products);

                spinner_search.setAdapter(arrayAdapter);

                GetReport();
            }

        });

    }

    void GetReport() {

        Loading.startShimmerAnimation();
        String FromDate = txt_FromDate.getText().toString();
        String ToDate = txt_ToDate.getText().toString();
        int ProductId = ((VM_Product_Spinner) spinner_search.getSelectedItem()).getId();

        String Url = Api + "Order/Report";

        if (ProductId!=0){
            Url+="?id="+ProductId;
        }

        if (!FromDate.equalsIgnoreCase("") && !ToDate.equalsIgnoreCase("")) {
            if (ProductId!=0){
                Url += "&from=" + FromDate + "&to=" + ToDate;
            }else{
                Url+="?from=" + FromDate + "&to=" + ToDate;
            }

        }

        getReportVolley=new GetJsonObjectVolley(Url,resault -> {

            Loading.stopShimmerAnimation();

            if (resault.getResault() == ResaultCode.Success) {
                SetDetail(resault.getObject());
            } else if (resault.getResault() == ResaultCode.NetworkError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            } else if (resault.getResault() == ResaultCode.TimeoutError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
            }

        });

    }

    void SetDetail(JSONObject object){

        try {
            lbl_AllProductIn.setText(object.getString("AllProductIn"));
            lbl_AllProductTransportIn.setText(object.getString("AllProductTransportIn"));
            lbl_AllProductOut.setText(object.getString("AllProductOut"));
            lbl_AllProductBack.setText(object.getString("AllProductBack"));
            lbl_ProductSoldCount.setText(object.getString("ProductSoldCount"));
            lbl_ProductSoldPrice.setText(object.getString("ProductSoldPrice"));
            lbl_ProductBoughtCount.setText(object.getString("ProductBoughtCount"));
            lbl_ProductBoughtPrice.setText(object.getString("ProductBoughtPrice"));
            lbl_Profit.setText(object.getString("Profit"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getProductSpinnerVolley != null)
            getProductSpinnerVolley.Cancel(TAG, getContext());

        if (getReportVolley != null)
            getReportVolley.Cancel(TAG, getContext());
    }
}
