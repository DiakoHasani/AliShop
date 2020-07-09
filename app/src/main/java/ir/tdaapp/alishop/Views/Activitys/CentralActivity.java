package ir.tdaapp.alishop.Views.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import ir.tdaapp.alishop.R;
import ir.tdaapp.alishop.Views.DataBase.Tbl_User;
import ir.tdaapp.alishop.Views.Fragments.AddFragment;
import ir.tdaapp.alishop.Views.Fragments.EditProductFragment;
import ir.tdaapp.alishop.Views.Fragments.HomeFragment;
import ir.tdaapp.alishop.Views.Fragments.SellFragment;
import ir.tdaapp.alishop.Views.Fragments.SendFragment;
import ir.tdaapp.alishop.Views.Utility.BottomBarItems;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class CentralActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    HomeFragment homeFragment;
    SellFragment sellFragment;
    AddFragment addFragment;
    SendFragment sendFragment;
    FrameLayout frame_Product, frame_Sell, frame_Add, frame_Transfer;
    BottomBarItems SelectedFragment;
    BottomNavigationView bottomBar;
    private Tbl_User tbl_user;

    public Tbl_User getTbl_user() {
        return tbl_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        FindItem();
        Implements();

        SetPage(BottomBarItems.Products);
    }

    void FindItem() {
        frame_Product = findViewById(R.id.Frame_Product);
        frame_Sell = findViewById(R.id.Frame_Sell);
        frame_Add = findViewById(R.id.Frame_Add);
        frame_Transfer = findViewById(R.id.Frame_Transfer);
        bottomBar = findViewById(R.id.bottomBar);
    }

    void Implements() {
        bottomBar.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        tbl_user = new Tbl_User(getApplicationContext());
    }

    //در اینجا کاربر با زدن آیتم های باتوم بار صفحه مربوط به آن نمایش داده می شود
    void SetPage(BottomBarItems item) {
        switch (item) {
            case Products:

                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.Frame_Product, homeFragment, HomeFragment.TAG).commit();
                }
                ShowFrameLayout(BottomBarItems.Products);

                if (SelectedFragment != null)
                    HideFrameLayout(SelectedFragment);

                SelectedFragment = BottomBarItems.Products;

                break;
            case Sell:

                if (sellFragment == null) {
                    sellFragment = new SellFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.Frame_Sell, sellFragment, SellFragment.TAG).commit();
                }
                ShowFrameLayout(BottomBarItems.Sell);

                if (SelectedFragment != null)
                    HideFrameLayout(SelectedFragment);

                SelectedFragment = BottomBarItems.Sell;

                break;
            case Add:

                if (addFragment == null) {
                    addFragment = new AddFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.Frame_Add, addFragment, AddFragment.TAG).commit();
                }
                ShowFrameLayout(BottomBarItems.Add);

                if (SelectedFragment != null)
                    HideFrameLayout(SelectedFragment);

                SelectedFragment = BottomBarItems.Add;

                break;
            case Send:

                if (sendFragment == null) {
                    sendFragment = new SendFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.Frame_Transfer, sendFragment, SendFragment.TAG).commit();
                }
                ShowFrameLayout(BottomBarItems.Send);

                if (SelectedFragment != null)
                    HideFrameLayout(SelectedFragment);

                SelectedFragment = BottomBarItems.Send;

                break;
        }
    }

    //در اینجا فریم لایوت مربوطه نمایش داده می شود
    void ShowFrameLayout(BottomBarItems item) {
        switch (item) {
            case Products:
                frame_Product.setVisibility(View.VISIBLE);
                break;
            case Sell:
                frame_Sell.setVisibility(View.VISIBLE);
                break;
            case Add:
                frame_Add.setVisibility(View.VISIBLE);
                break;
            case Send:
                frame_Transfer.setVisibility(View.VISIBLE);
                break;
        }
    }

    //در اینجا فریم لایوت مربوطه مخفی می شود
    void HideFrameLayout(BottomBarItems item) {
        switch (item) {
            case Products:
                frame_Product.setVisibility(View.INVISIBLE);
                break;
            case Sell:
                frame_Sell.setVisibility(View.INVISIBLE);
                break;
            case Add:
                frame_Add.setVisibility(View.INVISIBLE);
                break;
            case Send:
                frame_Transfer.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.BottomBar_Product:
                if (SelectedFragment != BottomBarItems.Products) {
                    SetPage(BottomBarItems.Products);
                }
                break;
            case R.id.BottomBar_Sell:
                if (SelectedFragment != BottomBarItems.Sell) {
                    SetPage(BottomBarItems.Sell);
                }
                break;
            case R.id.BottomBar_Add:
                if (SelectedFragment != BottomBarItems.Add) {
                    SetPage(BottomBarItems.Add);
                }
                break;
            case R.id.BottomBar_Send:
                if (SelectedFragment != BottomBarItems.Send) {
                    SetPage(BottomBarItems.Send);
                }
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        if (SelectedFragment != null) {

            if (SelectedFragment == BottomBarItems.Products) {

                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Product);

                if (fragmentInFrame instanceof HomeFragment) {

                    HomeFragment f=((HomeFragment)fragmentInFrame);

                    if (f.getDrawer().isDrawerOpen(GravityCompat.START)){
                        f.getDrawer().closeDrawers();
                    }else{
                        super.onBackPressed();
                    }


                } else {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragmentInFrame).commit();
                }

            } else if (SelectedFragment == BottomBarItems.Sell) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Sell);

                if (fragmentInFrame instanceof SellFragment) {
                    bottomBar.setSelectedItemId(R.id.BottomBar_Product);
                }else {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragmentInFrame).commit();
                }
            } else if (SelectedFragment == BottomBarItems.Add) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Add);

                if (fragmentInFrame instanceof AddFragment) {
                    bottomBar.setSelectedItemId(R.id.BottomBar_Product);
                }else {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragmentInFrame).commit();
                }
            } else if (SelectedFragment == BottomBarItems.Send) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Transfer);

                if (fragmentInFrame instanceof SendFragment) {
                    bottomBar.setSelectedItemId(R.id.BottomBar_Product);
                }else {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragmentInFrame).commit();
                }
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 || requestCode == 2)
            getSupportFragmentManager().findFragmentByTag(AddFragment.TAG).onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 || requestCode == 4)
            getSupportFragmentManager().findFragmentByTag(EditProductFragment.TAG).onActivityResult(requestCode, resultCode, data);
    }

    public void ReloadAllRecycler(){

        if (homeFragment!=null){
            homeFragment.GetProducts();
        }
        if (sellFragment!=null){
            sellFragment.GetProducts();
        }

        if (sendFragment!=null){
            sendFragment.ShowPage();
        }

    }

}
