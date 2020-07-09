package ir.tdaapp.alishop.Views.Utility;

import android.content.Context;

import ir.tdaapp.alishop.Views.Adapters.DBAdapter;

public class LazySingleton {

    private DBAdapter dbAdapter;
    private Context context;

    public DBAdapter getDbAdapter() {
        return dbAdapter;
    }

    public LazySingleton(Context context) {
        this.context = context;
        dbAdapter = new DBAdapter(context);
    }

    private static LazySingleton sInstance;

    public static LazySingleton INSTANCE(Context c) {

        if (sInstance == null) {
            sInstance = new LazySingleton(c);
        }
        return sInstance;
    }
}
