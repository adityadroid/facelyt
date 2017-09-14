package com.adityaadi1467.facelytx;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by adi on 23/7/16.
 */
public class ConneckBar implements ConnectivityReceiver.ConnectivityReceiverListener {
    View currentview;
    String text;
    View.OnClickListener listener;
    int backgroundColor, textColor, actionTextColor;
    Context context;
    int duration;

    public ConneckBar(Context currentApplicationContext, View view, String textToBeDisplayed, View.OnClickListener clickListener, int Duration, int colorBackGround, int colorText, int colorActionText) {
        currentview = view;
        text = textToBeDisplayed;
        listener = clickListener;
        backgroundColor = colorBackGround;
        context = currentApplicationContext;
        this.duration = Duration;
        textColor = colorText;
        actionTextColor = colorActionText;
    }

    public ConneckBar(Context currentApplicationContext, View view, String textToBeDisplayed, View.OnClickListener clickListener, int Duration, int colorBackGround, int colorText) {
        currentview = view;
        text = textToBeDisplayed;
        listener = clickListener;
        backgroundColor = colorBackGround;
        context = currentApplicationContext;
        this.duration = Duration;
        textColor = colorText;
        actionTextColor = colorText;
    }

    public ConneckBar(Context currentApplicationContext, View view, String textToBeDisplayed, View.OnClickListener clickListener, int Duration) {
        currentview = view;
        text = textToBeDisplayed;
        listener = clickListener;
        context = currentApplicationContext;
        this.duration = Duration;
        backgroundColor = Color.RED;
        actionTextColor = Color.WHITE;
        textColor = Color.WHITE;
    }

    public boolean isConnected() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected, currentview);
        return isConnected;
    }

    private void showSnack(boolean isConnected, View v) {

        Snackbar snackbar = null;

        if (isConnected) {


        } else {


            snackbar = Snackbar.make(v, text, duration).setAction(
                    "Retry", listener
            );
            View snackBarView = snackbar.getView();

            snackBarView.setBackgroundColor(backgroundColor);
            TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(textColor);
            TextView retry = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_action);
            retry.setTextColor(textColor);

            snackbar.show();

        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, currentview);
    }
}
