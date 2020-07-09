package ir.tdaapp.alishop.Views.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Adapters.DeatailItemAdapter;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_DetailItem;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonArrayVolley;

public class DetailProductFragment extends Fragment implements IBase {

    public final static String TAG="DetailProductFragment";

    ImageView img;
    RecyclerView Recycler;
    Toolbar toolBar;
    GetJsonArrayVolley getJsonArrayVolley;
    int Id=-1;
    DeatailItemAdapter deatailItemAdapter;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_product_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();

        GetProuductItem();

        return view;
    }

    void FindItem(View view) {
        img = view.findViewById(R.id.img);
        Recycler = view.findViewById(R.id.Recycler);
        toolBar = view.findViewById(R.id.toolBar);
    }

    void Implements(){
        layoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.DetailProduct));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);
        ((CentralActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        toolBar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        setHasOptionsMenu(true);
    }

    void GetProuductItem(){

        Id=getArguments().getInt("Id");

        getJsonArrayVolley=new GetJsonArrayVolley(Api+"Product/AllProuductItem?id="+Id,resault -> {

            if (resault.getResault()== ResaultCode.Success){

                SetRecyclerItems(resault.getJsonArray());

            }else if (resault.getResault() == ResaultCode.NetworkError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
            } else if (resault.getResault() == ResaultCode.TimeoutError) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
            }

        });

    }

    void SetRecyclerItems(JSONArray array){

        if (array.length()>0){

            try {
                JSONObject object=array.getJSONObject(0);

                Glide.with(getContext())
                        .load(ApiImage+object.getString("TblProductImage"))
                        .placeholder(R.drawable.ic_priority_high)
                        .error(R.drawable.ic_image_product)
                        .into(img);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        List<VM_DetailItem> vals=new ArrayList<>();

        for (int i=0;i<array.length();i++){

            try {

                JSONObject object=array.getJSONObject(i);

                VM_DetailItem item=new VM_DetailItem();

                item.setCount(object.getInt("Count"));
                item.setDescription(object.getString("Description"));
                item.setFee(object.getInt("Fee"));
                item.setId(object.getInt("Id"));
                item.setPlace(object.getInt("FkPlace"));
                item.setTitle(object.getString("TblProductProductName"));

                String[] d = object.getString("DateInsert").split("T");
                item.setDate(d[0]);

                vals.add(item);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        deatailItemAdapter=new DeatailItemAdapter(getContext(),vals);

        Recycler.setAdapter(deatailItemAdapter);
        Recycler.setLayoutManager(layoutManager);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getJsonArrayVolley.Cancel(TAG,getContext());
    }
}
