package com.skobbler.sdkdemo.smartbicycle;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.StringTokenizer;
import java.lang.String;
/**
 * Created by DELL on 2/11/2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    private String s;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    @TargetApi(Build.VERSION_CODES.DONUT)
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
         s=sharedpreferences.getString(Name,"");
        }

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String msg = "";
        String lat = "";
        String lon = "";
        if (bundle != null) {
            Object pdus[] = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if(s.equals(msgs[i].getOriginatingAddress())) {
                    str += "SMS from" + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    msg += msgs[i].getMessageBody().toString();
                    StringTokenizer tokens = new StringTokenizer(msg, "\n");
                    lat = tokens.nextToken();// this will contain "Fruit"
                    lon = tokens.nextToken();
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, lat, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, lon, Toast.LENGTH_SHORT).show();
//Create the bundle

                    //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f"+ lat","+lon);
                    //Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    //Intent in = new Intent(android.content.Intent.ACTION_VIEW,
                    //       Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lon));
                    //in.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //    Intent tent = new Intent(android.content.Intent.ACTION_VIEW,
                    //          Uri.parse("http://maps.google.com/maps?saddr="+lat+","+lon));
                    //context.startActivity(tent);


                    Intent tent = new Intent(context, mapss.class);
                    tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//Create the bundle
                    tent.putExtra("One", lat);
                    tent.putExtra("Two", lon);

                    context.startActivity(tent);
                }
            }


        }


    }
}