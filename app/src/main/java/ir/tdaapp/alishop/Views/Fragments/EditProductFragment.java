package ir.tdaapp.alishop.Views.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.Activitys.CentralActivity;
import ir.tdaapp.alishop.Views.Utility.FileManger;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.li_image.ImagesCodes.CompressImage;
import ir.tdaapp.li_image.ImagesCodes.GetByCamera;
import ir.tdaapp.li_image.ImagesCodes.GetByGalery;
import ir.tdaapp.li_image.ImagesCodes.SaveImageToMob;
import ir.tdaapp.li_utility.Codes.Validation;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;
import ir.tdaapp.li_volley.Volleys.PostJsonObjectVolley;

public class EditProductFragment extends Fragment implements IBase, View.OnClickListener, TextWatcher {

    public static final String TAG = "EditProductFragment";
    Toolbar toolBar;
    EditText txt_ProductName, txt_Count, txt_Fee, txt_Date, txt_Description;
    ImageView img;
    CardView btn_Edit;
    GetJsonObjectVolley getJsonObjectVolley;
    ProgressBar progressBar;
    int Id = -1;
    FileManger _FileManger;
    PostJsonObjectVolley postJsonObjectVolley;
    GetByGalery getByGalery;
    GetByCamera getByCamera;
    CompressImage compressImage;
    DatePickerDialog.OnDateSetListener date;
    RadioButton rdo_Store1, rdo_Store2;
    EditText txt_Sum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_product_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();
        GetProduct();

        return view;
    }

    void FindItem(View view) {
        toolBar = view.findViewById(R.id.toolBar);
        txt_ProductName = view.findViewById(R.id.txt_ProductName);
        txt_Count = view.findViewById(R.id.txt_Count);
        txt_Fee = view.findViewById(R.id.txt_Fee);
        img = view.findViewById(R.id.img);
        btn_Edit = view.findViewById(R.id.btn_Edit);
        progressBar = view.findViewById(R.id.progressBar);
        txt_Date = view.findViewById(R.id.txt_Date);
        txt_Description = view.findViewById(R.id.txt_Description);
        rdo_Store1 = view.findViewById(R.id.rdo_Store1);
        rdo_Store2 = view.findViewById(R.id.rdo_Store2);
        txt_Sum = view.findViewById(R.id.txt_Sum);
    }

    void Implements() {
        img.setOnClickListener(this);
        btn_Edit.setOnClickListener(this);
        compressImage = new CompressImage(320, 420, 70, getActivity());

        txt_Date.setOnFocusChangeListener((view, b) -> {
            if (b) {
                ShowDatePicker();
            }
        });

        date = (datePicker, i, i1, i2) -> {
            String d = i + "/" + i1 + "/" + i2;
            txt_Date.setText(d);
        };

        txt_Count.addTextChangedListener(this);
        txt_Fee.addTextChangedListener(this);

    }

    void SetToolBar() {

        toolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolBar.setTitle(getContext().getResources().getString(R.string.EditProduct));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);
        ((CentralActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        toolBar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        setHasOptionsMenu(true);
    }

    void GetProduct() {

        btn_Edit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Id = getArguments().getInt("Id");

        getJsonObjectVolley = new GetJsonObjectVolley(Api + "Product/ProductById?id=" + Id, resault -> {

            progressBar.setVisibility(View.INVISIBLE);

            try {

                if (resault.getResault() == ResaultCode.Success) {

                    btn_Edit.setEnabled(true);

                    JSONObject object = resault.getObject();

                    txt_ProductName.setText(object.getString("ProductName"));
                    txt_Count.setText(object.getString("Count"));
                    txt_Fee.setText(object.getString("Fee"));
                    txt_Description.setText(object.getString("Description"));


                    String[] d = object.getString("DateInsert").split("T");
                    txt_Date.setText(d[0]);

                    if (object.getInt("FkPlace") == 1) {
                        rdo_Store1.setChecked(true);
                    } else {
                        rdo_Store2.setChecked(true);
                    }

                    Glide.with(this).asBitmap().load(ApiImage + object.getString("Image"))
                            .error(R.drawable.ic_image_product)
                            .placeholder(R.drawable.ic_priority_high)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    img.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                } else if (resault.getResault() == ResaultCode.NetworkError) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                } else if (resault.getResault() == ResaultCode.TimeoutError) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getJsonObjectVolley.Cancel(TAG, getContext());

        if (postJsonObjectVolley != null)
            postJsonObjectVolley.Cancel(TAG, getContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:
                SelectImage();
                break;
            case R.id.btn_Edit:
                UploadImageToServer();
                break;
        }
    }

    void UploadImageToServer() {

        if (IsValid()) {

            btn_Edit.setEnabled(false);
            //در اینجا progressbar لودینگ نمایش داده می شود
            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setTitle((Html.fromHtml("<font color='#FF7F27'>در حال ارسال</font>")));
            progress.setMessage((Html.fromHtml("<font color='#FF7F27'>لطفا منتظر بمانید</font>")));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            new Thread(() -> {

                try {
                    String Url = Api + "Product/PostFile";
                    _FileManger = new FileManger(Url);

                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                    AddProduct(_FileManger.uploadFile(SaveImageToMob.SaveImageCamera("temp.jpg", bitmap)).replace("\"", ""), progress);
                } catch (Exception e) {
                    AddProduct("NoImage", progress);
                }

            }).start();

        }

    }


    void AddProduct(String ImageName, ProgressDialog progressBar) {

        JSONObject object = new JSONObject();

        try {

            object.put("ProductName", txt_ProductName.getText().toString());
            object.put("Count", txt_Count.getText().toString());
            object.put("Fee", txt_Fee.getText().toString());
            object.put("Image", ImageName);
            object.put("FkPlace", ((CentralActivity) getActivity()).getTbl_user().GetPlaceUser());
            object.put("Id", Id);
            object.put("Description", txt_Description.getText().toString());
            object.put("DateInsert", txt_Date.getText().toString());

            if (rdo_Store1.isChecked()) {
                object.put("FkPlace", 1);
            } else {
                object.put("FkPlace", 2);
            }

            postJsonObjectVolley = new PostJsonObjectVolley(Api + "Product", object, resault -> {

                btn_Edit.setEnabled(true);
                progressBar.dismiss();

                if (resault.getResault() == ResaultCode.Success) {

                    try {

                        try {
                            ((CentralActivity)getActivity()).ReloadAllRecycler();
                        } catch (Exception ex) {
                        }

                        Toast.makeText(getActivity(), resault.getObject().getString("Titel"), Toast.LENGTH_SHORT).show();
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    boolean IsValid() {
        boolean res = true;

        if (!Validation.Required(txt_ProductName, getResources().getString(R.string.ThisEditTextIsRequired))) {
            res = false;
        }

        if (!Validation.Required(txt_Count, getResources().getString(R.string.ThisEditTextIsRequired))) {
            res = false;
        }

        if (!Validation.Required(txt_Fee, getResources().getString(R.string.ThisEditTextIsRequired))) {
            res = false;
        }

        return res;
    }

    void SelectImage() {

        final CharSequence[] options = {getResources().getString(R.string.Camera),
                getResources().getString(R.string.Gallery),
                getResources().getString(R.string.Cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getContext().getResources().getString(R.string.YourPicture) + "</font>")));

        builder.setItems(options, (dialogInterface, i) -> {

            if (options[i].equals(getResources().getString(R.string.Camera))) {

                getByCamera = new GetByCamera(getActivity(), 3, image -> {
                    img.setImageBitmap(compressImage.Compress(image));
                });

            } else if (options[i].equals(getResources().getString(R.string.Gallery))) {

                getByGalery = new GetByGalery(getActivity(), 4, image -> {
                    img.setImageBitmap(compressImage.Compress(image));
                });

            } else {
                dialogInterface.dismiss();
            }

        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3) {
                getByCamera.Continue();
            }
            if (requestCode == 4) {
                getByGalery.Continue(data);
            }
        }

    }

    void ShowDatePicker() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        double Price =0;
        double count = 0;

        if (!txt_Fee.getText().toString().equalsIgnoreCase("")){
            Price=Double.valueOf(txt_Fee.getText().toString());
        }

        if (!txt_Count.getText().toString().equalsIgnoreCase("")){
            count=Double.valueOf(txt_Count.getText().toString());
        }

        txt_Sum.setText(count * Price + "");
    }
}
