package smartexchange.expert.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import smartexchange.expert.model.Exchange;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.sql.SqlStructure;

public class ServerHelper {

    public interface OnRequestCompleted {
        void onSuccess();
        void onFailure(String message);
    }

    private OnRequestCompleted onRequestCompleted;
    private Context context;

    public ServerHelper(OnRequestCompleted onRequestCompleted, Context context) {
        this.onRequestCompleted = onRequestCompleted;
        this.context = context;
    }

    public void updateRates(String url) {
        new Thread(() -> {
            String result = callServer(url);
            parseStringXml(context, result);
        }).start();
    }

    private String callServer(String urlString) {
        StringBuilder xmlString = new StringBuilder();
        try {
            InputStream inputStream = null;
            URL url = new URL(urlString);
            HttpURLConnection cc = (HttpURLConnection) url.openConnection();
            cc.setReadTimeout(5000);
            cc.setConnectTimeout(5000);
            cc.setRequestMethod("GET");
            cc.setDoInput(true);
            int response = cc.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                inputStream = cc.getInputStream();
            }
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                while ((line = reader.readLine()) != null) {
                    xmlString.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            onRequestCompleted.onFailure(e.getMessage());
        }
        return xmlString.toString();
    }

    private void parseStringXml(Context context, String result) {
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
                                Exchange exchange = SqlService.getExchange(context, currency);
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
                if (firstTime.equals("no_value") || !firstTime.equals("no")) {
                    Exchange exchange = new Exchange();
                    exchange.setMultiplier(1);
                    exchange.setValue(1);
                    exchange.setName(Constants.RON);
                    exchange.setConvertorFavorite("1");
                    exchange.setCalculatorFavorite("1");
                    exchangeList.add(exchange);
                    SqlService.clearAll(context, SqlStructure.SqlData.CURRENCY);
                    SqlService.insertExchangeList(context, exchangeList);
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
            new Handler(Looper.getMainLooper()).post(() -> onRequestCompleted.onSuccess());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> onRequestCompleted.onFailure(e.getMessage()));
        }
    }

}
