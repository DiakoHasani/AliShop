package ir.tdaapp.alishop.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Adapters.ProductAdapter;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.Utility.PlusProductClicked;
import ir.tdaapp.alishop.Views.Utility.ProductClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;

public class HomeFragment extends Fragment implements IBase, View.OnClickListener, PlusProductClicked,
        CompoundButton.OnCheckedChangeListener, ProductClicked, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "HomeFragment";

    Toolbar toolBar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    ShimmerFrameLayout Loading;
    ProductAdapter productAdapter;
    GetJsonArrayVolley getJsonArrayVolley;
    CardView btn_Reload;
    RecyclerView Recycler;
    LinearLayoutManager layoutManager;
    CheckBox chk_Store1, chk_Store2;
    NavigationView nav_View;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();

        chk_Store1.setChecked(true);

        GetProducts();

        return view;
    }

    void FindItem(View view) {
        toolBar = view.findViewById(R.id.toolBar);
        Loading = view.findViewById(R.id.Loading);
        drawer = view.findViewById(R.id.drawer);
        btn_Reload = view.findViewById(R.id.btn_Reload);
        Recycler = view.findViewById(R.id.Recycler);
        chk_Store1 = view.findViewById(R.id.chk_Store1);
        chk_Store2 = view.findViewById(R.id.chk_Store2);
        nav_View = view.findViewById(R.id.nav_View);
    }

    void Implements() {
        toggle = new ActionBarDrawerToggle(getActivity(), drawer, R.string.Open, R.string.Close);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        btn_Reload.setOnClickListener(this::onClick);

        chk_Store1.setOnCheckedChangeListener(this::onCheckedChanged);
        chk_Store2.setOnCheckedChangeListener(this::onCheckedChanged);

        nav_View.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.Products));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);
        ((CentralActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void GetProducts() {
        Recycler.setVisibility(View.GONE);
        Loading.setVisibility(View.VISIBLE);
        Loading.startShimmerAnimation();
        btn_Reload.setVisibility(View.GONE);

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

            Loading.stopShimmerAnimation();
            Loading.setVisibility(View.GONE);
            Recycler.setVisibility(View.VISIBLE);

            if (resault.getResault() == ResaultCode.Success) {

                SetItemsRecyclr(resault.getJsonArray());

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

    void SetItemsRecyclr(JSONArray array) {

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

            productAdapter = new ProductAdapter(getContext(), vals);
            Recycler.setAdapter(productAdapter);
            Recycler.setLayoutManager(layoutManager);
            productAdapter.setPlusProductClicked(this);
            productAdapter.setProductClicked(this::Click);

        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Reload:
                GetProducts();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnClick(int Id) {

        Bundle bundle = new Bundle();
        bundle.putInt("Id", Id);
        EditProductFragment editProductFragment = new EditProductFragment();
        editProductFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Product, editProductFragment, EditProductFragment.TAG).commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        GetProducts();
    }

    @Override
    public void Click(int Id) {
        Bundle bundle = new Bundle();
        bundle.putInt("Id", Id);

        DetailProductFragment detailProductFragment=new DetailProductFragment();
        detailProductFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Product, detailProductFragment, DetailProductFragment.TAG).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_report:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.Frame_Product,new ProductReportFragment()).commit();
                break;
            case R.id.nav_CostAndBenefit:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.Frame_Product,new CostAndBenefitFragment()).commit();
                break;
        }

        drawer.closeDrawers();

        return true;
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }
}
