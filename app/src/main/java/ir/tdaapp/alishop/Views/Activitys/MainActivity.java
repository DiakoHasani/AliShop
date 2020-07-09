package ir.tdaapp.alishop.Views.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.DataBase.Tbl_User;
import ir.tdaapp.alishop.Views.Utility.IBase;
import ir.tdaapp.alishop.Views.ViewModels.VM_User;
import ir.tdaapp.li_utility.Codes.Replace;
import ir.tdaapp.li_volley.Enum.ResaultCode;
import ir.tdaapp.li_volley.Volleys.GetJsonObjectVolley;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IBase {

    public static final String TAG = "MainActivity";

    LinearLayout layout;
    Handler handlerShowLayout, handlerGoToNextPage;
    Tbl_User tbl_user;
    GetJsonObjectVolley getJsonObjectVolley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);

        tbl_user = new Tbl_User(getApplicationContext());

        ShowAnim();
    }

    void ShowAnim() {

        handlerShowLayout = new Handler();
        handlerGoToNextPage = new Handler();

        handlerShowLayout.postDelayed(() -> {
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            layout.startAnimation(aniFade);
            layout.setVisibility(View.VISIBLE);
            CheckIsActive();
        }, 800);
    }

    void CheckIsActive() {

        if (tbl_user.HasAccount()) {

            getJsonObjectVolley = new GetJsonObjectVolley(Api + "User/CheckUser?userName=" + tbl_user.GetUserName(), resault -> {

                if (resault.getResault() == ResaultCode.Success) {

                    try {
                        JSONObject object = resault.getObject();

                        if (object.getBoolean("Isactive")) {
                            startActivity(new Intent(getApplicationContext(), CentralActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.YourAccountIsDisable), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                    }

                } else if (resault.getResault() == ResaultCode.NetworkError) {
                    Toast.makeText(this, getResources().getString(R.string.please_Checked_Your_Internet_Connection), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.Operation_error_occurred), Toast.LENGTH_SHORT).show();
                }

            });

        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getJsonObjectVolley != null)
            getJsonObjectVolley.Cancel(TAG, getApplicationContext());

    }
}
