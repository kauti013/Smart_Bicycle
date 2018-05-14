package com.skobbler.sdkdemo.smartbicycle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by DELL on 2/12/2016.
 */
public class mapss extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.menu2_layout);
        GPSTracker gps;
       // Button orderButton = (Button)findViewById(R.id.sendbutton2);

       // orderButton.setOnClickListener(new View.OnClickListener() {
        gps = new GPSTracker(mapss.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
            //@Override
            //public void onClick(View view) {
                Intent intent = getIntent();
                String stuff = intent.getStringExtra("One");
                String stuff2= intent.getStringExtra("Two");

//Extract the data…
               // String stuff = bundle.getString("One");
               // String stuff2= bundle.getString("Two");
                Intent tent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+stuff+","+stuff2));
                 tent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(tent);
        finish();
            }

      //  });
    //}



}
