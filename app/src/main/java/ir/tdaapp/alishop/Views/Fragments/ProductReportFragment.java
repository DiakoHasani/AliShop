package ir.tdaapp.alishop.Views.Fragments;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Adapters.ReportAdapter;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product_Spinner;
import ir.tdaapp.alishop.Views.ViewModels.VM_Report;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;

public class ProductReportFragment extends Fragment implements IBase, View.OnClickListener {

    public final static String TAG = "ProductReportFragment";

    Toolbar mToolbar;
    SearchableSpinner spinner_search;
    GetJsonArrayVolley getProductSpinnerVolley;
    ShimmerFrameLayout Loading;
    RecyclerView Recycler;
    CardView btn_Search, btn_Reload;
    EditText txt_FromDate, txt_ToDate;
    DatePickerDialog.OnDateSetListener F_date, T_date;
    GetJsonArrayVolley getProductsVolley;
    ReportAdapter reportAdapter;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_report_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();
        SetSpinnerProductName();


        return view;
    }

    void FindItem(View view) {
        mToolbar = view.findViewById(R.id.mToolbar);
        spinner_search = view.findViewById(R.id.spinner_search);
        Loading = view.findViewById(R.id.Loading);
        Recycler = view.findViewById(R.id.Recycler);
        btn_Search = view.findViewById(R.id.btn_Search);
        txt_FromDate = view.findViewById(R.id.txt_FromDate);
        txt_ToDate = view.findViewById(R.id.txt_ToDate);
        btn_Reload = view.findViewById(R.id.btn_Reload);
    }

    void Implements() {

        layoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);

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

        btn_Reload.setOnClickListener(this::onClick);
        btn_Search.setOnClickListener(this::onClick);

    }

    //در اینجا مقادیر اسپینر جستجو گرفته می شود
    void SetSpinnerProductName() {

        Loading.startShimmerAnimation();
        btn_Reload.setVisibility(View.GONE);

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

                GetProduts();

            }

        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getProductSpinnerVolley != null)
            getProductSpinnerVolley.Cancel(TAG, getContext());

        if (getProductsVolley != null)
            getProductsVolley.Cancel(TAG, getContext());

    }

    void SetToolBar() {

        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        mToolbar.setTitle(getContext().getResources().getString(R.string.Report));
        ((CentralActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CentralActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        mToolbar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        setHasOptionsMenu(true);
    }

    void GetProduts() {

        Loading.startShimmerAnimation();

        int ProductId = ((VM_Product_Spinner) spinner_search.getSelectedItem()).getId();
        String FromDate = txt_FromDate.getText().toString();
        String ToDate = txt_ToDate.getText().toString();

        String Url = "";

        if (!FromDate.equalsIgnoreCase("") && !ToDate.equalsIgnoreCase("")) {
            Url = Api + "Order/" + ProductId + "?from=" + FromDate + "&to=" + ToDate;
        } else {
            Url = Api + "Order/" + ProductId;
        }

        getProductsVolley = new GetJsonArrayVolley(Url, resault -> {

            Loading.stopShimmerAnimation();
            if (resault.getResault() == ResaultCode.Success) {
                SetItemRecycler(resault.getJsonArray());
            } else if (resault.getResault() == ResaultCode.NetworkError) {
                btn_Reload.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            } else if (resault.getResault() == ResaultCode.TimeoutError) {
                btn_Reload.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
            } else {
                btn_Reload.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
            }

        });

    }

    void SetItemRecycler(JSONArray array) {

        List<VM_Report> vals = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {

                JSONObject object=array.getJSONObject(i);
                VM_Report vm_report=new VM_Report();

                vm_report.setId(object.getInt("Id"));
                vm_report.setProductId(object.getInt("TblProductId"));
                vm_report.setCount(object.getInt("Count"));
                vm_report.setFee(object.getDouble("Fee"));
                vm_report.setProductName(object.getString("TblProductProductName"));

                String[] d = object.getString("OrderLogDateLog").split("T");
                vm_report.setDate(d[0]);

                vals.add(vm_report);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        reportAdapter=new ReportAdapter(vals,getContext());
        Recycler.setAdapter(reportAdapter);
        Recycler.setLayoutManager(layoutManager);
    }

    void ShowDatePicker(DatePickerDialog.OnDateSetListener date) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Reload:
                SetSpinnerProductName();
                break;
            case R.id.btn_Search:
                GetProduts();
                break;
        }
    }
}
