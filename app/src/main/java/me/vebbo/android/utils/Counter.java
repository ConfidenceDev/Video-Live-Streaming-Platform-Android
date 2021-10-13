package me.vebbo.android.utils;

import android.content.Context;
import java.util.Locale;
import me.vebbo.android.R;

public class Counter {

    static final long THOU = 1000L;
    static final long MILL = 1000000L;
    static final long BILL = 1000000000L;
    static final long TRIL = 1000000000000L;
    static final long QUAD = 1000000000000000L;
    static final long QUIN = 1000000000000000000L;

    public String countVal (long val, Context context){
        if (val < THOU) return Long.toString(val);
        if (val < MILL) return makeDecimal(val, THOU, context.getResources().getString(R.string.k));
        if (val < BILL) return makeDecimal(val, MILL, context.getResources().getString(R.string.m));
        if (val < TRIL) return makeDecimal(val, BILL, context.getResources().getString(R.string.b));
        if (val < QUAD) return makeDecimal(val, TRIL, context.getResources().getString(R.string.t));
        if (val < QUIN) return makeDecimal(val, QUAD, context.getResources().getString(R.string.q));
        return makeDecimal(val, QUIN, context.getResources().getString(R.string.u));
    }

    private static String makeDecimal (long val, long div, String sfx){
        val  = val / (div / 10);
        long whole = val / 10;
        long tenths = val % 10;
        if ((tenths == 0) || (whole >= 10))
            return String.format(Locale.getDefault(), "%d%s", whole, sfx);
        return String .format(Locale.getDefault(), "%d.%d%s", whole, tenths, sfx);
    }
}
