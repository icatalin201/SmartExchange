package smartexchange.expert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.model.Region;
import smartexchange.expert.util.Constants;

public class CalculatorActivity extends AppCompatActivity {

    private TextView userInputValue;
    private TextView uservalue;
    private Region region;

    private double valueOne = Double.NaN;

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';

    private char CURRENT_ACTION = '9';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        setTitle(R.string.calculator);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        userInputValue = findViewById(R.id.user_input_value);
        uservalue = findViewById(R.id.user_value);
        TextView symbol = findViewById(R.id.symbol);
        TextView regionName = findViewById(R.id.region_name);
        TextView regionExchangeName = findViewById(R.id.region_exchange_name);
        ImageView regionFlag = findViewById(R.id.region_flag);
        region = getIntent().getParcelableExtra(Constants.REGION);
        regionName.setText(region.getName());
        regionExchangeName.setText(region.getExchange().getName());
        symbol.setText(region.getSymbol());
        uservalue.setText(String.format(Locale.ENGLISH, "%s", region.getValue()));
        userInputValue.setText(String.valueOf(region.getValue()));
        regionFlag.setBackground(getDrawable(region.getFlag()));
        findViewById(R.id.zero).setOnClickListener(new KeyListenerEvent("number", "0"));
        findViewById(R.id.one).setOnClickListener(new KeyListenerEvent("number", "1"));
        findViewById(R.id.two).setOnClickListener(new KeyListenerEvent("number", "2"));
        findViewById(R.id.three).setOnClickListener(new KeyListenerEvent("number", "3"));
        findViewById(R.id.four).setOnClickListener(new KeyListenerEvent("number", "4"));
        findViewById(R.id.five).setOnClickListener(new KeyListenerEvent("number", "5"));
        findViewById(R.id.six).setOnClickListener(new KeyListenerEvent("number", "6"));
        findViewById(R.id.seven).setOnClickListener(new KeyListenerEvent("number", "7"));
        findViewById(R.id.eight).setOnClickListener(new KeyListenerEvent("number", "8"));
        findViewById(R.id.nine).setOnClickListener(new KeyListenerEvent("number", "9"));
        findViewById(R.id.point).setOnClickListener(new KeyListenerEvent("float", "."));
        findViewById(R.id.divider).setOnClickListener(new KeyListenerEvent("operand", "/"));
        findViewById(R.id.dif).setOnClickListener(new KeyListenerEvent("operand", "-"));
        findViewById(R.id.sum).setOnClickListener(new KeyListenerEvent("operand", "+"));
        findViewById(R.id.mux).setOnClickListener(new KeyListenerEvent("operand", "*"));
        Button enter = findViewById(R.id.enter);
        enter.setOnClickListener(new KeyListenerEvent("equal", "="));
        findViewById(R.id.backspace).setOnClickListener(new KeyListenerEvent("delete", "del"));
        findViewById(R.id.backspace).setOnLongClickListener(v -> {
            userInputValue.setText("");
            uservalue.setText("");
            return true;
        });
    }

    class KeyListenerEvent implements View.OnClickListener {

        private String mode;
        private String value;

        KeyListenerEvent(String mode, String value) {
            this.mode = mode;
            this.value = value;
        }

        @Override
        public void onClick(View v) {
            String input = userInputValue.getText().toString();
            String userValueInput = uservalue.getText().toString();
            switch (this.mode) {
                case "equal":
                    computeCalculation();
                    userInputValue.setText(String.format(Locale.ENGLISH, "%.2f", valueOne));
                    uservalue.setText(String.format(Locale.ENGLISH, "%.2f", valueOne));
                    valueOne = Double.NaN;
                    CURRENT_ACTION = '0';
                    break;
                case "operand":
                    if (userValueInput.equals("")){
                        if (value.equals("-")) {
                            uservalue.setText(value);
                            userInputValue.setText(value);
                        }
                        else {
                            return;
                        }
                    }
                    String substring = userValueInput.substring(userValueInput.length() - 1);
                    if (substring.equals("*") || substring.equals("/") ||
                            substring.equals("+") || substring.equals("-")) {
                        CURRENT_ACTION = value.equals("+") ? ADDITION :
                                value.equals("-") ? SUBTRACTION :
                                        value.equals("*") ? MULTIPLICATION :
                                                value.equals("/") ? DIVISION : '0';
                        uservalue.setText(userValueInput
                                .substring(0, userValueInput.length() - 1)
                                .concat(value));
                        return;
                    }
                    computeCalculation();
                    CURRENT_ACTION = value.equals("+") ? ADDITION :
                            value.equals("-") ? SUBTRACTION :
                                    value.equals("*") ? MULTIPLICATION :
                                            value.equals("/") ? DIVISION : '0';
                    uservalue.setText(userValueInput.concat(value));
                    userInputValue.setText("");
                    break;
                case "float":
                    if (input.equals("")) {
                        userInputValue.setText("0".concat(value));
                        uservalue.setText(userValueInput.concat(value));
                    }
                    else if (input.contains(".")) {
                        return;
                    }
                    else {
                        userInputValue.setText(input.concat(value));
                        uservalue.setText(userValueInput.concat(value));
                    }
                    break;
                case "number":
                    if (CURRENT_ACTION == '9') {
                        uservalue.setText(value);
                        userInputValue.setText(value);
                        CURRENT_ACTION = '1';
                    }
                    else {
                        uservalue.setText(userValueInput.concat(value));
                        userInputValue.setText(input.concat(value));
                    }
                    break;
                case "delete":
                    if (input.equals("")) return;
                    userInputValue.setText(input.substring(0, input.length() - 1));
                    uservalue.setText(userValueInput.substring(0, userValueInput.length() - 1));
                    break;
            }
        }
    }

    private void computeCalculation() {
        if(!Double.isNaN(valueOne)) {
            double valueTwo = Double.parseDouble(userInputValue.getText().toString());
            userInputValue.setText(null);
            if(CURRENT_ACTION == ADDITION)
                valueOne = this.valueOne + valueTwo;
            else if(CURRENT_ACTION == SUBTRACTION)
                valueOne = this.valueOne - valueTwo;
            else if(CURRENT_ACTION == MULTIPLICATION)
                valueOne = this.valueOne * valueTwo;
            else if(CURRENT_ACTION == DIVISION)
                valueOne = this.valueOne / valueTwo;
        }
        else {
            try {
                valueOne = Double.parseDouble(userInputValue.getText().toString());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        setResult();
//        finish();
//        return super.onSupportNavigateUp();
//    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    private void setResult() {
        Intent intent = new Intent();
        if (!userInputValue.getText().toString().equals("")) {
            region.setValue(Float.parseFloat(userInputValue.getText().toString()));
        }
        intent.putExtra(Constants.REGION, region);
        setResult(Constants.REGION_CALCULATOR_RESULT_CODE, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult();
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
