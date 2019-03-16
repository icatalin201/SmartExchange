package smartexchange.expert.sql;

import android.provider.BaseColumns;

/**
 * Created by icatalin on 11.02.2018.
 */

public class SqlStructure {

    private SqlStructure() {}

    public static class SqlData implements BaseColumns {

        public static final String CURRENCY = "currency";
        public static final String exchange_name = "name";
        public static final String exchange_value = "value";
        public static final String exchange_multiplier = "multiplier";
        public static final String calculator_favorite = "calculator_favorite";
        public static final String convertor_favorite = "convertor_favorite";
        public static final String BANKNOTE = "banknote";
        public static final String banknote_name = "name";
        public static final String banknote_image = "image";
        public static final String banknote_exchange_name = "exchange_name";
    }
}
