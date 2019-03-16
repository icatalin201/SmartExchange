package smartexchange.expert.util;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.model.Banknote;
import smartexchange.expert.model.Exchange;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.sql.SqlStructure;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static void launchJob(Context context, Class<?> cls) {
        ComponentName serviceComponent = new ComponentName(context, cls);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        long millis = 1000 * 60 * 60 * 3;
        builder.setMinimumLatency(millis);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) jobScheduler.schedule(builder.build());
    }

    public static void stopJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) jobScheduler.cancelAll();
    }

    public static void customAnimation(Context context, View view, int duration, int anim) {
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static void changeFragmentWithStack(Context context, Fragment fragment) {
        Fragment f = ((AppCompatActivity) context).getSupportFragmentManager()
                .findFragmentById(R.id.FragmentContainer);
        FragmentManager fragmentManager = ((AppCompatActivity) context)
                .getSupportFragmentManager();
        fragmentManager.beginTransaction().hide(f)
                .add(R.id.FragmentContainer, fragment).addToBackStack(null).commit();
    }

    public static void changeFragment(Context context, Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context)
                .getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, fragment).commit();
    }

    public static void putInSharedPreferences(String pref_name, Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getFromSharedPreferences(String pref_name, Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return sharedPreferences.getString(key, "no_value");
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null;
    }

    private static float convert(float sourceValue, float sourceExchange, int multiplier) {
        return sourceValue * sourceExchange / (float) multiplier;
    }

    private static float convert (int multiplier, float value, float exchange) {
        return value * multiplier / exchange;
    }

    public static float computeValueCurrency(float value, int multiplier) {
        return multiplier / value;
    }

    public static float computeValueCurrency(Region sourceRegion, Region currentRegion) {
        float toRon = convert(sourceRegion.getValue(), sourceRegion.getExchange().getValue(),
                sourceRegion.getExchange().getMultiplier());
        return convert(currentRegion.getExchange().getMultiplier(),
                toRon, currentRegion.getExchange().getValue());
    }

    public static Date stringToDate(String date, String pattern) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.parse(date);
    }

    public static String dateToString(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static void parseStringXml(Context context, String result) {
        List<Exchange> exchangeList = new ArrayList<>();
        String firstTime = Utils.getFromSharedPreferences(Constants.FIRST_TIME, context, "first");
        String date = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(result));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    switch (name) {
                        case "Rate":
                            String currency = parser.getAttributeValue(null,
                                    "currency");
                            String multiplier = parser.getAttributeValue(null,
                                    "multiplier");
                            if (parser.next() == XmlPullParser.TEXT) {
                                String value = parser.getText();
                                Exchange exchange = new Exchange();
                                exchange.setName(currency);
                                exchange.setValue(Float.parseFloat(value));
                                if (multiplier == null) {
                                    multiplier = "1";
                                }
                                if (firstTime.equals("no_value") || !firstTime.equals("no")) {
                                    if (currency.equals(Constants.EUR) ||
                                            currency.equals(Constants.GBP) ||
                                            currency.equals(Constants.USD) ||
                                            currency.equals(Constants.CHF) ||
                                            currency.equals(Constants.CAD)) {
                                        exchange.setCalculatorFavorite("1");
                                    } else {
                                        exchange.setCalculatorFavorite("0");
                                    }
                                    if (currency.equals(Constants.EUR)) {
                                        exchange.setConvertorFavorite("1");
                                    } else {
                                        exchange.setConvertorFavorite("0");
                                    }
                                }
                                exchange.setMultiplier(Integer.parseInt(multiplier));
                                exchangeList.add(exchange);
                            }
                            break;
                        case "PublishingDate":
                            date = parser.next() == XmlPullParser.TEXT ? parser.getText() : null;
                            break;
                    }
                }
                eventType = parser.next();
            }
            if (exchangeList.size() > 0) {
                Exchange exchange = new Exchange();
                exchange.setMultiplier(1);
                exchange.setValue(1);
                exchange.setName(Constants.RON);
                if (firstTime.equals("no_value") || !firstTime.equals("no")) {
                    exchange.setConvertorFavorite("1");
                    exchange.setCalculatorFavorite("1");
                }
                exchangeList.add(exchange);
                if (firstTime.equals("no_value") || !firstTime.equals("no")) {
                    SqlService.clearAll(context, SqlStructure.SqlData.CURRENCY);
//                    SqlService.clearAll(context, SqlStructure.SqlData.BANKNOTE);
                    SqlService.insertExchangeList(context, exchangeList);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        exchangeList.forEach(a -> makeBanknoteList(context, a.getName()));
//                    }
//                    else {
//                        for (Exchange e : exchangeList) {
//                            makeBanknoteList(context, e.getName());
//                        }
//                    }
                    Utils.putInSharedPreferences(Constants.FIRST_TIME, context, "first", "no");
                    Utils.putInSharedPreferences(Constants.AUTO_UPDATE, context, "auto", "1");
                }
                else {
                    SqlService.updateExchangeList(context, exchangeList);
                }
            }
            if (date != null) {
                Utils.putInSharedPreferences(Constants.DATE_PREF, context, Constants.date_key, date);
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLocale(Context context) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language, context);
    }

    public static String getLanguage(Context context) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getString(langPref, "");
    }

    public static void changeLang(String lang, Context context) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang, context);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        ((AppCompatActivity) context).getBaseContext().getResources()
                .updateConfiguration(config, ((AppCompatActivity) context)
                        .getBaseContext().getResources().getDisplayMetrics());

    }

    private static void saveLocale(String lang, Context context) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

//    private static void makeBanknoteList(Context context, String exchangeName) {
//        List<Banknote> banknoteList = new ArrayList<>();
//        switch (exchangeName) {
//            case Constants.RON:
//                banknoteList.add(new Banknote("1 LEU", R.drawable.a1_leu_romania_2005_a));
//                banknoteList.add(new Banknote("1 LEU", R.drawable.a1_leu_romania_2005_b));
//                banknoteList.add(new Banknote("5 LEI", R.drawable.a5_lei_romania_2005_a));
//                banknoteList.add(new Banknote("5 LEI", R.drawable.a5_lei_romania_2005_b));
//                banknoteList.add(new Banknote("10 LEI", R.drawable.a10_lei_romania_2008_a));
//                banknoteList.add(new Banknote("10 LEI", R.drawable.a10_lei_romania_2008_b));
//                banknoteList.add(new Banknote("50 LEI", R.drawable.a50_lei_romania_2005_a));
//                banknoteList.add(new Banknote("50 LEI", R.drawable.a50_lei_romania_2005_b));
//                banknoteList.add(new Banknote("100 LEI", R.drawable.a100_lei_romania_2005_a));
//                banknoteList.add(new Banknote("100 LEI", R.drawable.a100_lei_romania_2005_b));
//                banknoteList.add(new Banknote("200 LEI", R.drawable.a200_lei_romania_2006_a));
//                banknoteList.add(new Banknote("200 LEI", R.drawable.a200_lei_romania_2006_b));
//                banknoteList.add(new Banknote("500 LEI", R.drawable.a500_lei_romania_2005_a));
//                banknoteList.add(new Banknote("500 LEI", R.drawable.a500_lei_romania_2005_b));
//                break;
//            case Constants.EUR:
//                banknoteList.add(new Banknote("5 €", R.drawable.eur_5_obverse));
//                banknoteList.add(new Banknote("5 €", R.drawable.eur_5_reverse));
//                banknoteList.add(new Banknote("10 €", R.drawable.eur_10_obverse));
//                banknoteList.add(new Banknote("10 €", R.drawable.eur_10_reverse));
//                banknoteList.add(new Banknote("20 €", R.drawable.eur_20_obverse));
//                banknoteList.add(new Banknote("20 €", R.drawable.eur_20_reverse));
//                banknoteList.add(new Banknote("50 €", R.drawable.eur_50_obverse));
//                banknoteList.add(new Banknote("50 €", R.drawable.eur_50_reverse));
//                banknoteList.add(new Banknote("100 €", R.drawable.eur_100_obverse));
//                banknoteList.add(new Banknote("100 €", R.drawable.eur_100_reverse));
//                banknoteList.add(new Banknote("200 €", R.drawable.eur_200_obverse));
//                banknoteList.add(new Banknote("200 €", R.drawable.eur_200_reverse));
//                banknoteList.add(new Banknote("500 €", R.drawable.eur_500_obverse));
//                banknoteList.add(new Banknote("500 €", R.drawable.eur_500_reverse));
//                break;
//            case Constants.USD:
//                banknoteList.add(new Banknote("1 $", R.drawable.usd_1_front));
//                banknoteList.add(new Banknote("1 $", R.drawable.usd_1_back));
//                banknoteList.add(new Banknote("2 $", R.drawable.usd_2_front));
//                banknoteList.add(new Banknote("2 $", R.drawable.usd_2_back));
//                banknoteList.add(new Banknote("5 $", R.drawable.usd_5_front));
//                banknoteList.add(new Banknote("5 $", R.drawable.usd_5_back));
//                banknoteList.add(new Banknote("10 $", R.drawable.usd_10_front));
//                banknoteList.add(new Banknote("10 $", R.drawable.usd_10_back));
//                banknoteList.add(new Banknote("20 $", R.drawable.usd_20_front));
//                banknoteList.add(new Banknote("20 $", R.drawable.usd_20_back));
//                banknoteList.add(new Banknote("50 $", R.drawable.usd_50_front));
//                banknoteList.add(new Banknote("50 $", R.drawable.usd_50_back));
//                banknoteList.add(new Banknote("100 $", R.drawable.usd_100_front));
//                banknoteList.add(new Banknote("100 $", R.drawable.usd_100_back));
//                break;
//            case Constants.AED:
//                banknoteList.add(new Banknote("5 د.إ", R.drawable.united_arab_emirates_cba_5_dirhams_f));
//                banknoteList.add(new Banknote("5 د.إ", R.drawable.united_arab_emirates_cba_5_dirhams_r));
//                banknoteList.add(new Banknote("10 د.إ", R.drawable.united_arab_emirates_cba_10_dirhams_f));
//                banknoteList.add(new Banknote("10 د.إ", R.drawable.united_arab_emirates_cba_10_dirhams_r));
//                banknoteList.add(new Banknote("20 د.إ", R.drawable.united_arab_emirates_cba_20_dirhams_f));
//                banknoteList.add(new Banknote("20 د.إ", R.drawable.united_arab_emirates_cba_20_dirhams_r));
//                banknoteList.add(new Banknote("50 د.إ", R.drawable.united_arab_emirates_cba_50_dirhams_f));
//                banknoteList.add(new Banknote("50 د.إ", R.drawable.united_arab_emirates_cba_50_dirhams_r));
//                banknoteList.add(new Banknote("200 د.إ", R.drawable.united_arab_emirates_cba_200_dirhams_f));
//                banknoteList.add(new Banknote("200 د.إ", R.drawable.united_arab_emirates_cba_200_dirhams_r));
//                banknoteList.add(new Banknote("500 د.إ", R.drawable.united_arab_emirates_cba_500_dirhams_f));
//                banknoteList.add(new Banknote("500 د.إ", R.drawable.united_arab_emirates_cba_500_dirhams_r));
//                banknoteList.add(new Banknote("1000 د.إ", R.drawable.united_arab_emirates_cba_1000_dirhams_f));
//                banknoteList.add(new Banknote("1000 د.إ", R.drawable.united_arab_emirates_cba_1000_dirhams_r));
//                break;
//            case Constants.AUD:
//                break;
//            case Constants.BGN:
//                break;
//            case Constants.BRL:
//                banknoteList.add(new Banknote("2 R$", R.drawable.brazil_bcb_2_reais_f));
//                banknoteList.add(new Banknote("2 R$", R.drawable.brazil_bcb_2_reais_r));
//                banknoteList.add(new Banknote("5 R$", R.drawable.brazil_bcb_5_reais_f));
//                banknoteList.add(new Banknote("5 R$", R.drawable.brazil_bcb_5_reais_r));
//                banknoteList.add(new Banknote("10 R$", R.drawable.brazil_bcb_10_reais_f));
//                banknoteList.add(new Banknote("10 R$", R.drawable.brazil_bcb_10_reais_r));
//                banknoteList.add(new Banknote("20 R$", R.drawable.brazil_bcb_20_reais_f));
//                banknoteList.add(new Banknote("20 R$", R.drawable.brazil_bcb_20_reais_r));
//                banknoteList.add(new Banknote("50 R$", R.drawable.brazil_bcb_50_reais_f));
//                banknoteList.add(new Banknote("50 R$", R.drawable.brazil_bcb_50_reais_r));
//                banknoteList.add(new Banknote("100 R$", R.drawable.brazil_bcb_100_reais_f));
//                banknoteList.add(new Banknote("100 R$", R.drawable.brazil_bcb_100_reais_r));
//                break;
//            case Constants.CAD:
//                break;
//            case Constants.CHF:
//                break;
//            case Constants.CNY:
//                break;
//            case Constants.CZK:
//                break;
//            case Constants.DKK:
//                break;
//            case Constants.EGP:
//                break;
//            case Constants.GBP:
//                break;
//            case Constants.HRK:
//                break;
//            case Constants.HUF:
//                break;
//            case Constants.INR:
//                break;
//            case Constants.JPY:
//                break;
//            case Constants.KRW:
//                break;
//            case Constants.MDL:
//                break;
//            case Constants.MXN:
//                break;
//            case Constants.NOK:
//                banknoteList.add(new Banknote("50 kr", R.drawable.norway_nb_50_kroner_f));
//                banknoteList.add(new Banknote("50 kr", R.drawable.norway_nb_50_kroner_r));
//                banknoteList.add(new Banknote("100 kr", R.drawable.norway_nb_100_kroner_f));
//                banknoteList.add(new Banknote("100 kr", R.drawable.norway_nb_100_kroner_r));
//                banknoteList.add(new Banknote("200 kr", R.drawable.norway_nb_200_kroner_f));
//                banknoteList.add(new Banknote("200 kr", R.drawable.norway_nb_200_kroner_r));
//                banknoteList.add(new Banknote("500 kr", R.drawable.norway_nb_500_kroner_f));
//                banknoteList.add(new Banknote("500 kr", R.drawable.norway_nb_500_kroner_r));
//                banknoteList.add(new Banknote("1000 kr", R.drawable.norway_nb_1000_kroner_f));
//                banknoteList.add(new Banknote("1000 kr", R.drawable.norway_nb_1000_kroner_r));
//                break;
//            case Constants.NZD:
//                break;
//            case Constants.PLN:
//                break;
//            case Constants.RSD:
//                break;
//            case Constants.RUB:
//                break;
//            case Constants.SEK:
//                banknoteList.add(new Banknote("20 kr", R.drawable.sweden_sr_20_kronor_f));
//                banknoteList.add(new Banknote("20 kr", R.drawable.sweden_sr_20_kronor_r));
//                banknoteList.add(new Banknote("50 kr", R.drawable.sweden_sr_50_kronor_f));
//                banknoteList.add(new Banknote("50 kr", R.drawable.sweden_sr_50_kronor_r));
//                banknoteList.add(new Banknote("100 kr", R.drawable.sweden_sr_100_kronor_f));
//                banknoteList.add(new Banknote("100 kr", R.drawable.sweden_sr_100_kronor_r));
//                banknoteList.add(new Banknote("200 kr", R.drawable.sweden_sr_200_kronor_f));
//                banknoteList.add(new Banknote("200 kr", R.drawable.sweden_sr_200_kronor_r));
//                banknoteList.add(new Banknote("500 kr", R.drawable.sweden_sr_500_kronor_f));
//                banknoteList.add(new Banknote("500 kr", R.drawable.sweden_sr_500_kronor_r));
//                banknoteList.add(new Banknote("1000 kr", R.drawable.sweden_sr_1000_kronor_f));
//                banknoteList.add(new Banknote("1000 kr", R.drawable.sweden_sr_1000_kronor_r));
//                break;
//            case Constants.THB:
//                break;
//            case Constants.TRY:
//                break;
//            case Constants.UAH:
//                break;
//            case Constants.ZAR:
//                break;
//        }
//        if (banknoteList.size() > 0) {
//            SqlService.insertBanknotes(context, exchangeName, banknoteList);
//        }
//    }

}
