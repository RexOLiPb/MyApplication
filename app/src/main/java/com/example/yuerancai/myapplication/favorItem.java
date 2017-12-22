package com.example.yuerancai.myapplication;

/**
 * Created by yuerancai on 2017/11/27.
 */

public class favorItem {
    private String symbol;
    private String lastPrice;
    private String change;


    public favorItem(String symbol, String lastPrice, String change) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return lastPrice;
    }

    public String getChange() {
        return change;
    }

}
