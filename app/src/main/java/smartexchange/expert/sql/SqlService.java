package smartexchange.expert.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.model.Banknote;
import smartexchange.expert.model.Exchange;
import smartexchange.expert.model.Region;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;


public class SqlService {

    public static void clearAll(Context context, String table) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        sqLiteDatabase.delete(table, null, null);
        sqLiteDatabase.close();
    }

    public static void insertExchangeList(Context context, List<Exchange> exchangeList) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        for (Exchange exchange : exchangeList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlStructure.SqlData.exchange_name, exchange.getName());
            contentValues.put(SqlStructure.SqlData.exchange_value, exchange.getValue());
            contentValues.put(SqlStructure.SqlData.exchange_multiplier, exchange.getMultiplier());
            contentValues.put(SqlStructure.SqlData.convertor_favorite, exchange.getConvertorFavorite());
            contentValues.put(SqlStructure.SqlData.calculator_favorite, exchange.getCalculatorFavorite());
            sqLiteDatabase.insert(SqlStructure.SqlData.CURRENCY, null, contentValues);
        }
        sqLiteDatabase.close();
    }

    public static void insertBanknotes(Context context, String exchangeName, List<Banknote> banknoteList) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        for (Banknote banknote : banknoteList) {
            ContentValues cv = new ContentValues();
            cv.put(SqlStructure.SqlData.banknote_exchange_name, exchangeName);
            cv.put(SqlStructure.SqlData.banknote_name, banknote.getName());
            cv.put(SqlStructure.SqlData.banknote_image, banknote.getImage());
            database.insert(SqlStructure.SqlData.BANKNOTE, null, cv);
        }
        database.close();
    }

    public static List<Banknote> getBanknoteList(Context context, String exchangeName) {
        List<Banknote> banknoteList = new ArrayList<>();
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(SqlStructure.SqlData.BANKNOTE, null,
                SqlStructure.SqlData.banknote_exchange_name + " = ?",
                new String[] {exchangeName}, null, null, null);
        while (cursor.moveToNext()) {
            int image = cursor.getInt(cursor.getColumnIndex(SqlStructure.SqlData.banknote_image));
            String name = cursor.getString(cursor.getColumnIndex(SqlStructure.SqlData.banknote_name));
            Banknote banknote = new Banknote(name, image);
            banknoteList.add(banknote);
        }
        cursor.close();
        database.close();
        return banknoteList;
    }

    public static void insertExchange(Context context, Exchange exchange) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlStructure.SqlData.exchange_name, exchange.getName());
        contentValues.put(SqlStructure.SqlData.exchange_value, exchange.getValue());
        contentValues.put(SqlStructure.SqlData.exchange_multiplier, exchange.getMultiplier());
        contentValues.put(SqlStructure.SqlData.convertor_favorite, exchange.getConvertorFavorite());
        contentValues.put(SqlStructure.SqlData.calculator_favorite, exchange.getCalculatorFavorite());
        sqLiteDatabase.insert(SqlStructure.SqlData.CURRENCY, null, contentValues);
        sqLiteDatabase.close();
    }

    public static void updateExchangeList(Context context, List<Exchange> exchangeList) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        for (Exchange exchange : exchangeList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlStructure.SqlData.exchange_name, exchange.getName());
            contentValues.put(SqlStructure.SqlData.exchange_value, exchange.getValue());
            contentValues.put(SqlStructure.SqlData.exchange_multiplier, exchange.getMultiplier());
            contentValues.put(SqlStructure.SqlData.convertor_favorite, exchange.getConvertorFavorite());
            contentValues.put(SqlStructure.SqlData.calculator_favorite, exchange.getCalculatorFavorite());
            sqLiteDatabase.update(SqlStructure.SqlData.CURRENCY, contentValues,
                    SqlStructure.SqlData._ID + " = ?",
                    new String[]{String.valueOf(exchange.getId())});
        }
        sqLiteDatabase.close();
    }

    public static void updateExchange(Context context, Exchange exchange) {
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlStructure.SqlData.exchange_name, exchange.getName());
        contentValues.put(SqlStructure.SqlData.exchange_value, exchange.getValue());
        contentValues.put(SqlStructure.SqlData.exchange_multiplier, exchange.getMultiplier());
        contentValues.put(SqlStructure.SqlData.convertor_favorite, exchange.getConvertorFavorite());
        contentValues.put(SqlStructure.SqlData.calculator_favorite, exchange.getCalculatorFavorite());
        sqLiteDatabase.update(SqlStructure.SqlData.CURRENCY, contentValues,
                SqlStructure.SqlData._ID + " = ?",
                new String[]{String.valueOf(exchange.getId())});
        sqLiteDatabase.close();
    }

    public static List<Region> getExchangeList(Context context, String selection, String[] args) {
        List<Region> regionList = new ArrayList<>();
        SqlHelper sqlHelper = new SqlHelper(context);
        SQLiteDatabase sqLiteDatabase = sqlHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(SqlStructure.SqlData.CURRENCY, null,
                selection, args, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SqlStructure.SqlData._ID));
            String name = cursor.getString(cursor.getColumnIndex(SqlStructure.SqlData.exchange_name));
            float value = cursor.getFloat(cursor.getColumnIndex(SqlStructure.SqlData.exchange_value));
            int multiplier = cursor.getInt(cursor.getColumnIndex(SqlStructure.SqlData.exchange_multiplier));
            String calculatorFavorite = cursor.getString(cursor.getColumnIndex(SqlStructure.SqlData.calculator_favorite));
            String converterFavorite = cursor.getString(cursor.getColumnIndex(SqlStructure.SqlData.convertor_favorite));
            Exchange exchange = new Exchange(id, name, value, multiplier, calculatorFavorite, converterFavorite);
            Region region = new Region();
            region.setExchange(exchange);
            String[] data = setRegionName(context, exchange);
            region.setName(data[0]);
            if (data[1] != null) {
                region.setValue(Float.parseFloat(data[1]));
            }
            region.setSymbol(data[2]);
            region.setFlag(Constants.EXCHANGE_MAP.get(exchange.getName()));
            regionList.add(region);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            regionList.removeIf((Region region) ->
                    region.getExchange().getName().equals(Constants.XAU) ||
                            region.getExchange().getName().equals(Constants.XDR));
        }
        else {
            Iterator<Region> regionIterator = regionList.iterator();
            while (regionIterator.hasNext()) {
                Region region = regionIterator.next();
                if (region.getExchange().getName().equals(Constants.XAU) ||
                        region.getExchange().getName().equals(Constants.XDR)) {
                    regionIterator.remove();
                }
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        if (regionList.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                regionList.sort(Comparator.comparing(Region::getName));
            }
            else {
                Collections.sort(regionList, (r1, r2) -> r1.getName().compareTo(r2.getName()));
            }
        }
        return regionList;
    }

    private static String[] setRegionName(Context context, Exchange exchange) {
        String[] datas = new String[3];
        datas[1] = String.valueOf(Utils.computeValueCurrency(exchange.getValue(), exchange.getMultiplier()));
        switch (exchange.getName()) {
            case Constants.RON:
                datas[0] = context.getResources().getString(R.string.region_ro);
                datas[2] = "LEU";
                break;
            case Constants.EUR:
                datas[0] = context.getResources().getString(R.string.region_ue);
                datas[2] = "€";
                break;
            case Constants.USD:
                datas[0] = context.getResources().getString(R.string.region_sua);
                datas[2] = "$";
                break;
            case Constants.AED:
                datas[0] = context.getResources().getString(R.string.region_eau);
                datas[2] = "د.إ";
                break;
            case Constants.AUD:
                datas[0] = context.getResources().getString(R.string.region_au);
                datas[2] = "$";
                break;
            case Constants.BGN:
                datas[0] = context.getResources().getString(R.string.region_bul);
                datas[2] = "лв";
                break;
            case Constants.BRL:
                datas[0] = context.getResources().getString(R.string.region_bra);
                datas[2] = "R$";
                break;
            case Constants.CAD:
                datas[0] = context.getResources().getString(R.string.region_can);
                datas[2] = "$";
                break;
            case Constants.CHF:
                datas[0] = context.getResources().getString(R.string.region_swi);
                datas[2] = "fr.";
                break;
            case Constants.CNY:
                datas[0] = context.getResources().getString(R.string.region_chi);
                datas[2] = "¥";
                break;
            case Constants.CZK:
                datas[0] = context.getResources().getString(R.string.region_ceh);
                datas[2] = "Kč";
                break;
            case Constants.DKK:
                datas[0] = context.getResources().getString(R.string.region_den);
                datas[2] = "kr.";
                break;
            case Constants.EGP:
                datas[0] = context.getResources().getString(R.string.region_eg);
                datas[2] = "E£";
                break;
            case Constants.GBP:
                datas[0] = context.getResources().getString(R.string.region_uk);
                datas[2] = "£";
                break;
            case Constants.HRK:
                datas[0] = context.getResources().getString(R.string.region_cro);
                datas[2] = "kn";
                break;
            case Constants.HUF:
                datas[0] = context.getResources().getString(R.string.region_un);
                datas[2] = "Ft";
                break;
            case Constants.INR:
                datas[0] = context.getResources().getString(R.string.region_in);
                datas[2] = "₹";
                break;
            case Constants.JPY:
                datas[0] = context.getResources().getString(R.string.region_ja);
                datas[2] = "¥";
                break;
            case Constants.KRW:
                datas[0] = context.getResources().getString(R.string.region_cor);
                datas[2] = "₩";
                break;
            case Constants.MDL:
                datas[0] = context.getResources().getString(R.string.region_mold);
                datas[2] = "MDL";
                break;
            case Constants.MXN:
                datas[0] = context.getResources().getString(R.string.region_mex);
                datas[2] = "$";
                break;
            case Constants.NOK:
                datas[0] = context.getResources().getString(R.string.region_nor);
                datas[2] = "kr";
                break;
            case Constants.NZD:
                datas[0] = context.getResources().getString(R.string.region_nzl);
                datas[2] = "$";
                break;
            case Constants.PLN:
                datas[0] = context.getResources().getString(R.string.region_pol);
                datas[2] = "zł";
                break;
            case Constants.RSD:
                datas[0] = context.getResources().getString(R.string.region_ser);
                datas[2] = "РСД";
                break;
            case Constants.RUB:
                datas[0] = context.getResources().getString(R.string.region_ru);
                datas[2] = "руб";
                break;
            case Constants.SEK:
                datas[0] = context.getResources().getString(R.string.region_sw);
                datas[2] = "kr";
                break;
            case Constants.THB:
                datas[0] = context.getResources().getString(R.string.region_th);
                datas[2] = "฿";
                break;
            case Constants.TRY:
                datas[0] = context.getResources().getString(R.string.region_tu);
                datas[2] = "₺";
                break;
            case Constants.UAH:
                datas[0] = context.getResources().getString(R.string.region_uc);
                datas[2] = "₴";
                break;
//            case Constants.XAU:
//                datas[0] = "Philadelphia Gold and Silver Index";
//                datas[2] = "XAU";
//                break;
//            case Constants.XDR:
//                datas[0] = "Fondul monetar international";
//                datas[2] = "SDR";
//                break;
            case Constants.ZAR:
                datas[0] = context.getResources().getString(R.string.region_af);
                datas[2] = "R";
                break;
        }
        return datas;
    }
}
