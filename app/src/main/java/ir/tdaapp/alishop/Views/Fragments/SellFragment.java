package ir.tdaapp.alishop.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
import ir.tdaapp.alishop.Views.Adapters.ProductAdapter;
import ir.tdaapp.alishop.Views.Adapters.SellProductAdapter;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.Utility.ProductClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;

public class SellFragment extends Fragment implements IBase, CompoundButton.OnCheckedChangeListener
, ProductClicked {

    public static final String TAG = "SellFragment";

    Toolbar toolBar;
    RecyclerView Recycler;
    LinearLayoutManager layoutManager;
    CardView btn_Reload;
    ShimmerFrameLayout Loading;
    GetJsonArrayVolley getJsonArrayVolley;
    SellProductAdapter sellProductAdapter;
    CheckBox chk_Store1, chk_Store2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sell_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();

        chk_Store2.setChecked(true);

        GetProducts();

        return view;
    }

    void FindItem(View view) {
        toolBar = view.findViewById(R.id.toolBar);
        Recycler = view.findViewById(R.id.Recycler);
        btn_Reload = view.findViewById(R.id.btn_Reload);
        Loading = view.findViewById(R.id.Loading);
        chk_Store1 = view.findViewById(R.id.chk_Store1);
        chk_Store2 = view.findViewById(R.id.chk_Store2);
    }

    void Implements() {
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        Loading.startShimmerAnimation();

        chk_Store1.setOnCheckedChangeListener(this::onCheckedChanged);
        chk_Store2.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.Sell));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);

        setHasOptionsMenu(true);
    }

    public void GetProducts() {
        Loading.setVisibility(View.VISIBLE);
        btn_Reload.setVisibility(View.GONE);
        Recycler.setVisibility(View.GONE);


        int Place = 0;
        if (chk_Store1.isChecked() && chk_Store2.isChecked()) {
            Place = 0;
        } else if (!chk_Store1.isChecked() && !chk_Store2.isChecked()) {
            Place = 0;
        } else if (chk_Store1.isChecked() && !chk_Store2.isChecked()) {
            Place = 1;
        } else if (!chk_Store1.isChecked() && chk_Store2.isChecked()) {
            Place = 2;
        }

        getJsonArrayVolley = new GetJsonArrayVolley(Api + "Product/AllProuduct?palce=" + Place, resault -> {

            Loading.setVisibility(View.GONE);
            Recycler.setVisibility(View.VISIBLE);

            if (resault.getResault() == ResaultCode.Success) {

                SetItemsRecycler(resault.getJsonArray());

            } else if (resault.getResault() == ResaultCode.NetworkError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);

            } else if (resault.getResault() == ResaultCode.TimeoutError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);

            } else {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);

            }

        });

    }

    void SetItemsRecycler(JSONArray array) {

        List<VM_Product> vals = new ArrayList<>();

        try {

            for (int i = 0; i < array.length(); i++) {

                try {

                    JSONObject object = array.getJSONObject(i);
                    VM_Product vm_product = new VM_Product();

                    vm_product.setId(object.getInt("TblProductId"));
                    vm_product.setProductName(object.getString("TblProductProductName"));
                    vm_product.setImage(ApiImage + object.getString("TblProductImage"));
                    vm_product.setPlace(object.getInt("FkPlace"));
                    vm_product.setTotal(object.getInt("Count"));

                    vals.add(vm_product);

                } catch (Exception e) {

                }
            }

            sellProductAdapter = new SellProductAdapter(getContext(), vals);
            Recycler.setAdapter(sellProductAdapter);
            Recycler.setLayoutManager(layoutManager);
            sellProductAdapter.setProductClicked(this::Click);

        } catch (Exception e) {
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getJsonArrayVolley.Cancel(TAG, getContext());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        GetProducts();
    }

    @Override
    public void Click(int Id) {

        Bundle bundle=new Bundle();
        bundle.putInt("Id",Id);
        SellProductFragment sellProductFragment=new SellProductFragment();
        sellProductFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Sell,sellProductFragment).commit();
    }
}
