package me.vebbo.android.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CommaCounter {

    public String getFormattedValue(int num){
        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###,###");
        return formatter.format(num);
    }

    //===================================================
    public String getFormattedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    //===================================================
    public String getFormattedNumber(String number){
        if(!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.getDefault()).format(val);
        }else{
            return "0";
        }
    }
}
