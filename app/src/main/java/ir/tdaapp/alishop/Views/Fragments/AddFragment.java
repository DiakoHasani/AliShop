package ir.tdaapp.alishop.Views.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import ir.tdaapp.li_volley.Volleys.PostJsonObjectVolley;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener, IBase, TextWatcher {

    public static final String TAG = "AddFragment";
    Toolbar toolBar;
    GetByGalery getByGalery;
    GetByCamera getByCamera;
    ImageView img;
    CompressImage compressImage;
    EditText txt_ProductName, txt_Count, txt_Fee, txt_Date, txt_Description;
    CardView btn_Add;
    FileManger _FileManger;
    PostJsonObjectVolley postJsonObjectVolley;
    DatePickerDialog.OnDateSetListener date;
    RadioButton rdo_Store1, rdo_Store2;
    EditText txt_Sum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_fragment, container, false);

        FindItem(view);
        Implements();
        SetToolBar();
        rdo_Store1.setChecked(true);

        return view;
    }

    void FindItem(View view) {
        toolBar = view.findViewById(R.id.toolBar);
        img = view.findViewById(R.id.img);
        txt_ProductName = view.findViewById(R.id.txt_ProductName);
        txt_Count = view.findViewById(R.id.txt_Count);
        txt_Fee = view.findViewById(R.id.txt_Fee);
        btn_Add = view.findViewById(R.id.btn_Add);
        txt_Date = view.findViewById(R.id.txt_Date);
        txt_Description = view.findViewById(R.id.txt_Description);
        rdo_Store1 = view.findViewById(R.id.rdo_Store1);
        rdo_Store2 = view.findViewById(R.id.rdo_Store2);
        txt_Sum = view.findViewById(R.id.txt_Sum);
    }

    void Implements() {
        img.setOnClickListener(this::onClick);
        btn_Add.setOnClickListener(this::onClick);
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
        toolBar.setTitle(getContext().getResources().getString(R.string.Add));
        ((CentralActivity) getActivity()).setSupportActionBar(toolBar);

        setHasOptionsMenu(true);
    }

    void SelectImage() {

        final CharSequence[] options = {getResources().getString(R.string.Camera),
                getResources().getString(R.string.Gallery),
                getResources().getString(R.string.Cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getContext().getResources().getString(R.string.YourPicture) + "</font>")));

        builder.setItems(options, (dialogInterface, i) -> {

            if (options[i].equals(getResources().getString(R.string.Camera))) {

                getByCamera = new GetByCamera(getActivity(), 1, image -> {
                    img.setImageBitmap(compressImage.Compress(image));
                });

            } else if (options[i].equals(getResources().getString(R.string.Gallery))) {

                getByGalery = new GetByGalery(getActivity(), 2, image -> {
                    img.setImageBitmap(compressImage.Compress(image));
                });

            } else {
                dialogInterface.dismiss();
            }

        });
        builder.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:
                SelectImage();
                break;
            case R.id.btn_Add:
                UploadImageToServer();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                getByCamera.Continue();
            }
            if (requestCode == 2) {
                getByGalery.Continue(data);
            }
        }

    }

    void UploadImageToServer() {

        if (IsValid()) {

            btn_Add.setEnabled(false);
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

        if (!Validation.Required(txt_Date, getResources().getString(R.string.ThisEditTextIsRequired))) {
            res = false;
        }

        return res;
    }

    void AddProduct(String ImageName, ProgressDialog progressBar) {

        JSONObject object = new JSONObject();

        try {

            object.put("ProductName", txt_ProductName.getText().toString());
            object.put("Description", txt_Description.getText().toString());
            object.put("DateInsert", txt_Date.getText().toString());
            object.put("Count", txt_Count.getText().toString());
            object.put("Fee", txt_Fee.getText().toString());
            object.put("Image", ImageName);

            if (rdo_Store1.isChecked()) {
                object.put("FkPlace", 1);
            } else {
                object.put("FkPlace", 2);
            }

            postJsonObjectVolley = new PostJsonObjectVolley(Api + "Product", object, resault -> {

                btn_Add.setEnabled(true);
                progressBar.dismiss();

                if (resault.getResault() == ResaultCode.Success) {

                    try {

                        try {
                            ((CentralActivity) getActivity()).ReloadAllRecycler();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        postJsonObjectVolley.Cancel(TAG, getActivity());
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
