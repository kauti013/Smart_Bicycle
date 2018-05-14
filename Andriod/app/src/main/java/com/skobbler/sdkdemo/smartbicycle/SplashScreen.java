package com.skobbler.sdkdemo.smartbicycle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import com.skobbler.sdkdemo.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        WebView splashFrame = (WebView)findViewById(R.id.splashFrame);

        splashFrame.setBackgroundColor(Color.TRANSPARENT);
        splashFrame.loadDataWithBaseURL("file:///android_res/drawable/", "<img align='middle' src='cycle34.gif' width='100%' />", "text/html", "utf-8", null);
        splashFrame.reload();
        splashFrame.clearCache(false);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,MainActivity1.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
