package ir.tdaapp.alishop.Views.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.DataBase.Tbl_User;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_User;
import ir.tdaapp.li_utility.Codes.Replace;
import ir.tdaapp.li_utility.Codes.Validation;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IBase {

    TextInputEditText txt_UserName, txt_Password;
    CardView btn_Login;
    GetJsonObjectVolley getJsonObjectVolley;
    String TAG = "LoginActivity";
    Tbl_User tbl_user;
    ShimmerFrameLayout Loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FindItem();
        Implements();
    }

    void FindItem() {
        txt_UserName = findViewById(R.id.txt_UserName);
        txt_Password = findViewById(R.id.txt_Password);
        btn_Login = findViewById(R.id.btn_Login);
        Loading = findViewById(R.id.Loading);
    }

    void Implements() {
        tbl_user = new Tbl_User(getApplicationContext());
        btn_Login.setOnClickListener(this);
    }

    //در اینجا ولید بودن ادیت تکست ها چک می شود
    public boolean CheckValidation() {

        try {
            boolean resault = true;

            //در اینجا ولیدیشن ادیت تکست نام کاربری چک می شود
            if (!Validation.Required(txt_UserName, getResources().getString(R.string.Input_Your_UserName))) {
                resault = false;
            }

            //در اینجا ولیدیشن ادیت تکست پسوورد چک می شود
            if (!Validation.Required(txt_Password, getResources().getString(R.string.Input_Your_Password))) {
                resault = false;
            }

            return resault;

        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Login:
                AddUser();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getJsonObjectVolley.Cancel(TAG, getApplicationContext());
    }

    void AddUser() {
        setLoading(true);
        if (CheckValidation()) {

            getJsonObjectVolley = new GetJsonObjectVolley(Api + "User?userName=" + Replace.Number_fn_To_en(txt_UserName.getText().toString()) + "&Password=" + Replace.Number_fn_To_en(txt_Password.getText().toString()), resault -> {

                if (resault.getResault() == ResaultCode.Success) {

                    try {
                        JSONObject object = resault.getObject();

                        if (object.getInt("FkPlace") != 0) {

                            if (object.getBoolean("Isactive")) {
                                VM_User vm_user = new VM_User();
                                vm_user.setUserName(Replace.Number_fn_To_en(txt_UserName.getText().toString()));
                                vm_user.setFkPlace(object.getInt("FkPlace"));
                                vm_user.setKind(object.getInt("Kind"));
                                vm_user.setPalceName(object.getString("PalceName"));

                                if (tbl_user.Add(vm_user)) {
                                    startActivity(new Intent(getApplicationContext(), CentralActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.YourAccountIsDisable), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, getResources().getString(R.string.NoSuchUserFound), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                    }

                } else if (resault.getResault() == ResaultCode.NetworkError) {
                    Toast.makeText(this, getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                }

                setLoading(false);

            });

        } else {
            setLoading(false);
            Toast.makeText(this, getResources().getString(R.string.Please_be_Careful_in_Entering_Information), Toast.LENGTH_SHORT).show();
        }
    }

    void setLoading(boolean lo) {

        if (lo) {
            btn_Login.setEnabled(false);
            Loading.startShimmerAnimation();
        } else {
            btn_Login.setEnabled(true);
            Loading.stopShimmerAnimation();
        }

    }
}
