package ir.tdaapp.alishop.Views.DataBase;

import android.content.Context;
import android.database.Cursor;

import ir.tdaapp.alishop.Views.Utility.LazySingleton;
import ir.tdaapp.alishop.Views.ViewModels.VM_User;

public class Tbl_User {

    Context context;

    public Tbl_User(Context context) {
        this.context = context;
    }

    //در اینجا چک می شود که کاربر قبلا لاگین کرده است
    public boolean HasAccount() {
        Cursor cursor = LazySingleton.INSTANCE(context).getDbAdapter().ExecuteQ("select * from Tbl_User");
        return cursor.getCount() > 0;
    }

    public boolean Add(VM_User vm_user){
        try{
            LazySingleton.INSTANCE(context).getDbAdapter().ExecuteQ("delete from Tbl_User");
            String query="insert into Tbl_User (Id,UserName,Place,PlaceName,Kind) values (1,'"+vm_user.getUserName()+"',"+vm_user.getFkPlace()+",'"+vm_user.getPalceName()+"',"+vm_user.getKind()+")";
            LazySingleton.INSTANCE(context).getDbAdapter().ExecuteQ(query);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public int GetPlaceUser(){
        if (HasAccount()){

            Cursor q=LazySingleton.INSTANCE(context).getDbAdapter().ExecuteQ("select Place from Tbl_User limit 1");
            int place=q.getInt(0);

            return place;

        }else {
            return 0;
        }
    }

    public String GetUserName(){
        if (HasAccount()){

            Cursor q=LazySingleton.INSTANCE(context).getDbAdapter().ExecuteQ("select UserName from Tbl_User limit 1");
            String userName=q.getString(0);

            return userName;

        }else {
            return "";
        }
    }

}
