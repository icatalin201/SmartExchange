package smartexchange.expert.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by icatalin on 11.02.2018.
 */

public class SqlHelper extends SQLiteOpenHelper{

    private static final String TAG = "SQL_HELPER";
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "exchange.db";

    private static final String SQL_CREATE_BANKNOTE =
            "CREATE TABLE " + SqlStructure.SqlData.BANKNOTE + " (" +
                    SqlStructure.SqlData._ID + " INTEGER PRIMARY KEY," +
                    SqlStructure.SqlData.banknote_exchange_name + " TEXT," +
                    SqlStructure.SqlData.banknote_name + " TEXT," +
                    SqlStructure.SqlData.banknote_image + " INTEGER)";

    private static final String SQL_CREATE_EXCHANGE =
            "CREATE TABLE " + SqlStructure.SqlData.CURRENCY + " (" +
                    SqlStructure.SqlData._ID + " INTEGER PRIMARY KEY," +
                    SqlStructure.SqlData.exchange_name + " TEXT," +
                    SqlStructure.SqlData.exchange_value + " REAL," +
                    SqlStructure.SqlData.calculator_favorite + " TEXT," +
                    SqlStructure.SqlData.convertor_favorite + " TEXT," +
                    SqlStructure.SqlData.exchange_multiplier + " INTEGER)";

    private static final String SQL_DELETE_BANKNOTE =
            "DROP TABLE IF EXISTS " + SqlStructure.SqlData.BANKNOTE;

    private static final String SQL_DELETE_EXCHANGE =
            "DROP TABLE IF EXISTS " + SqlStructure.SqlData.CURRENCY;

    SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_EXCHANGE);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_EXCHANGE);
        sqLiteDatabase.execSQL(SQL_DELETE_BANKNOTE);
        Log.i(TAG, "onUpgrade");
        onCreate(sqLiteDatabase);
    }
}
