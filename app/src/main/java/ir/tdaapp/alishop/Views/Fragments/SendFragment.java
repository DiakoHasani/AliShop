package ir.tdaapp.alishop.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import ir.tdaapp.alishop.Views.Adapters.SellProductAdapter;
import ir.tdaapp.alishop.Views.Adapters.WayProductAdapter;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.Utility.ProductClicked;
import ir.tdaapp.alishop.Views.Utility.WayProductClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_Product;
import ir.tdaapp.alishop.Views.ViewModels.VM_WayClicked;
import ir.tdaapp.alishop.Views.ViewModels.VM_WayProduct;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;

public class SendFragment extends Fragment implements IBase, ProductClicked, CompoundButton.OnCheckedChangeListener, WayProductClicked,View.OnClickListener {

    public static final String TAG = "SendFragment";
    RecyclerView Recycler_Product, Recycler_Way_Product;
    LinearLayout Recyclers;
    CardView btn_Reload, Card_Recycler1, Card_Recycler2;
    ShimmerFrameLayout Loading;
    LinearLayoutManager layoutManager_Products, layoutManager_Way;
    GetJsonArrayVolley GetWays, GetProducts;
    RadioButton chk_Store1, chk_Store2;
    SellProductAdapter ProductAdapter;
    WayProductAdapter wayProductAdapter;
    Toolbar toolBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();

        int Place = ((CentralActivity) getActivity()).getTbl_user().GetPlaceUser();
        if (Place == 1)
            chk_Store1.setChecked(true);
        else if (Place == 2)
            chk_Store2.setChecked(true);

        ShowPage();

        return view;
    }

    void FindItem(View view) {
        Recycler_Product = view.findViewById(R.id.Recycler_Product);
        Recycler_Way_Product = view.findViewById(R.id.Recycler_Way_Product);
        Recyclers = view.findViewById(R.id.Recyclers);
        btn_Reload = view.findViewById(R.id.btn_Reload);
        Loading = view.findViewById(R.id.Loading);
        chk_Store1 = view.findViewById(R.id.chk_Store1);
        chk_Store2 = view.findViewById(R.id.chk_Store2);
        toolBar = view.findViewById(R.id.toolBar);
        Card_Recycler1 = view.findViewById(R.id.Card_Recycler1);
        Card_Recycler2 = view.findViewById(R.id.Card_Recycler2);
    }

    void Implements() {
        layoutManager_Products = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        layoutManager_Way = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        Loading.startShimmerAnimation();

        chk_Store1.setOnCheckedChangeListener(this::onCheckedChanged);
        chk_Store2.setOnCheckedChangeListener(this::onCheckedChanged);
        btn_Reload.setOnClickListener(this);
    }

    public void ShowPage() {

        if (chk_Store1.isChecked()) {

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    2
            );

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    0
            );


            Card_Recycler1.setLayoutParams(param1);
            Card_Recycler2.setLayoutParams(param2);

            GetProduct();

        } else {

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    0
            );

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    2
            );


            Card_Recycler1.setLayoutParams(param1);
            Card_Recycler2.setLayoutParams(param2);

            GetWay();

        }

    }

    void GetProduct() {

        btn_Reload.setVisibility(View.GONE);
        Recyclers.setVisibility(View.GONE);
        Loading.setVisibility(View.VISIBLE);

        int Place;
        if (chk_Store1.isChecked())
            Place = 1;
        else
            Place = 2;

        GetProducts = new GetJsonArrayVolley(Api + "Product/AllProuduct?palce=" + Place, resault -> {

            Loading.setVisibility(View.GONE);
            Recyclers.setVisibility(View.VISIBLE);

            if (resault.getResault() == ResaultCode.Success) {

                SetItemsRecycler_Product(resault.getJsonArray());

            } else if (resault.getResault() == ResaultCode.NetworkError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);

            } else if (resault.getResault() == ResaultCode.TimeoutError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);
            } else {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);
            }

        });
    }

    void SetItemsRecycler_Product(JSONArray array) {

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

                    if (object.getInt("Count") > 0)
                        vals.add(vm_product);

                } catch (Exception e) {

                }
            }

            ProductAdapter = new SellProductAdapter(getContext(), vals);
            Recycler_Product.setAdapter(ProductAdapter);
            Recycler_Product.setLayoutManager(layoutManager_Products);
            ProductAdapter.setProductClicked(this::Click);

        } catch (Exception e) {
        }

    }

    void GetWay() {

        btn_Reload.setVisibility(View.GONE);
        Recyclers.setVisibility(View.GONE);
        Loading.setVisibility(View.VISIBLE);


        int Place;
        if (chk_Store1.isChecked())
            Place = 1;
        else
            Place = 2;

        GetWays = new GetJsonArrayVolley(Api + "Order/AllInWay?id=" + Place, resault -> {

            Loading.setVisibility(View.GONE);
            Recyclers.setVisibility(View.VISIBLE);

            if (resault.getResault() == ResaultCode.Success) {

                try {

                    List<VM_WayProduct> vals = new ArrayList<>();
                    JSONArray array = resault.getJsonArray();

                    for (int i = 0; i < array.length(); i++) {

                        try {

                            JSONObject object = array.getJSONObject(i);
                            VM_WayProduct model = new VM_WayProduct();

                            model.setProductName(object.getString("TblProductProductName"));
                            model.setCount(object.getInt("Count"));
                            model.setFee(object.getInt("Fee"));
                            model.setProductId(object.getInt("TblProductId"));
                            model.setId(object.getInt("Id"));

                            vals.add(model);
                        } catch (Exception e) {
                        }

                    }

                    wayProductAdapter = new WayProductAdapter(vals, getContext());
                    Recycler_Way_Product.setAdapter(wayProductAdapter);
                    Recycler_Way_Product.setLayoutManager(layoutManager_Way);
                    wayProductAdapter.setWayProductClicked(this::WayClicked);

                } catch (Exception e) {
                }

            } else if (resault.getResault() == ResaultCode.NetworkError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);
            } else if (resault.getResault() == ResaultCode.TimeoutError) {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);
            } else {

                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                btn_Reload.setVisibility(View.VISIBLE);
                Recyclers.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (GetWays != null) {
            GetWays.Cancel(TAG, getContext());
        }

        if (GetProducts != null) {
            GetProducts.Cancel(TAG, getContext());
        }
    }

    @Override
    public void Click(int Id) {

        Bundle bundle = new Bundle();
        bundle.putInt("Id", Id);
        bundle.putInt("Place", chk_Store1.isChecked() ? 1 : 2);
        SendProductFragment sendProductFragment = new SendProductFragment();
        sendProductFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Transfer, sendProductFragment).commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.chk_Store1:
            case R.id.chk_Store2:
                ShowPage();
                break;
        }
    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.Send));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);

        setHasOptionsMenu(true);
    }

    @Override
    public void WayClicked(VM_WayClicked v) {

        getActivity().getSupportFragmentManager()
                .beginTransaction().add(R.id.Frame_Transfer, new ConditionProductFragment(v)).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_Reload:
                ShowPage();
                break;
        }
    }
}
