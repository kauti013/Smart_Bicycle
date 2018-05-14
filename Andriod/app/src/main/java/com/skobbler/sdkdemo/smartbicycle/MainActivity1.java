package com.skobbler.sdkdemo.smartbicycle;

/**
 * Created by DELL on 3/8/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.skobbler.sdkdemo.R;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener{
//    private TextView mStatusTv;
  //  private Button mActivateBtn;
    //private Button mPairedBtn;
    //private Button mScanBtn;
   // private Button btn1;
    private ProgressDialog mProgressDlg;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private BluetoothAdapter mBluetoothAdapter;

























    private Boolean isFabOpen = false;
    private Boolean isFabOpen1 = false;
    private Boolean isFabOpen2= false;
    private FloatingActionButton fab,fab1,fab2,fab3,fab4,fab5,fab6,fab7,fab8,fab9,fab10,fab11,fab12;
    private Animation fab_open2,fab_close2,rotate_forward2,rotate_backward2,fab_open1,fab_close1,rotate_forward1,rotate_backward1,fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        fab6 = (FloatingActionButton) findViewById(R.id.fab6);
        fab7 = (FloatingActionButton) findViewById(R.id.fab7);
        fab8 = (FloatingActionButton) findViewById(R.id.fab8);
        fab9 = (FloatingActionButton) findViewById(R.id.fab9);
        fab10 = (FloatingActionButton) findViewById(R.id.fab10);
        fab11 = (FloatingActionButton) findViewById(R.id.fab11);
        fab12 = (FloatingActionButton) findViewById(R.id.fab12);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab_open1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open1);
        fab_close1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close1);
        rotate_forward1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward1);
        rotate_backward1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward1);
        fab_open2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open2);
        fab_close2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close2);
        rotate_forward2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward2);
        rotate_backward2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward2);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        fab5.setOnClickListener(this);
        fab6.setOnClickListener(this);
        fab7.setOnClickListener(this);
        fab8.setOnClickListener(this);
        fab9.setOnClickListener(this);
        fab10.setOnClickListener(this);
        fab11.setOnClickListener(this);
        fab12.setOnClickListener(this);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mProgressDlg = new ProgressDialog(this);

        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mBluetoothAdapter.cancelDiscovery();
            }
        });
        if (mBluetoothAdapter == null) {
            showUnsupported();
        }


        if (mBluetoothAdapter.isEnabled()) {
            showEnabled();
        } else {
            showDisabled();
        }


        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);


    }
        @Override
        public void onClick (View v){
            int id = v.getId();
            switch (id) {
                case R.id.fab:

                    animateFAB();
                    break;
                case R.id.fab1:
                    if(mBluetoothAdapter.isEnabled()){
                    Intent i = new Intent(getApplicationContext(),com.skobbler.sdkdemo.activity.SplashActivity.class);
                    startActivity(i);
                }else
                {
                    Toast.makeText(this,"Please enable Bluetooth",Toast.LENGTH_SHORT).show();
                }


                    break;
                case R.id.fab2:
                case R.id.fab8:
                    //    Intent a=new Intent(getApplicationContext(),jason.bluetooth.MainActivity.class);
                    //  startActivity(a);
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();

                        showDisabled();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        startActivityForResult(intent, 1000);
                    }

                    break;
                case R.id.fab3:

                    animateFAB1();
                    break;
                case R.id.fab4:
                    if (!((LocationManager) getSystemService(LOCATION_SERVICE))
                            .isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        //setdo();
                        Toast.makeText(this,"Please enable GPS",Toast.LENGTH_SHORT).show();
                    }else{
                        //getdo();
                    Intent b = new Intent(getApplicationContext(), SendSmsActivity.class);
                    startActivity(b);
                 }
                    break;
                case R.id.fab5:
                    if (!((LocationManager) getSystemService(LOCATION_SERVICE))
                            .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // Open dialog to inform the user that the GPS is disabled
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getResources().getString(R.string.gpsDisabled));
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.openSettings,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Open the location settings if it is disabled
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                });
                        builder.setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Dismiss the dialog
                                        dialog.cancel();
                                    }
                                });

                        // Display the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else{
                        Toast.makeText(this,"GPS already enabled",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.fab9:
                    mBluetoothAdapter.startDiscovery();

                    break;
                case R.id.fab11:
                    mBluetoothAdapter.startDiscovery();

                    break;
                case R.id.fab12:


                case R.id.fab10:
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices == null || pairedDevices.size() == 0) {
                        showToast("No Paired Devices Found");
                    } else {
                        ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

                        list.addAll(pairedDevices);

                        Intent intent = new Intent(getApplicationContext(), com.skobbler.sdkdemo.bluetooth.DeviceListActivity.class);

                        intent.putParcelableArrayListExtra("device.list", list);

                        startActivity(intent);
                    }

                    break;
                case R.id.fab6:

                    animateFAB2();

                    break;
                case R.id.fab7:
                    if(mBluetoothAdapter.isEnabled()) {
                    Intent j = new Intent(getApplicationContext(), lights.class);
                    startActivity(j);

                }else
                {
                    Toast.makeText(this,"Please enable Bluetooth",Toast.LENGTH_SHORT).show();
                }
                    break;


            }
        }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab9.startAnimation(fab_close);
            fab10.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab9.setClickable(false);
            fab10.setClickable(false);
            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab9.startAnimation(fab_open);
            fab10.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab9.setClickable(true);
            fab10.setClickable(true);
            isFabOpen = true;
            if(isFabOpen1){
                fab3.startAnimation(rotate_backward1);
                fab4.startAnimation(fab_close1);
                fab5.startAnimation(fab_close1);

                fab4.setClickable(false);
                fab5.setClickable(false);

                isFabOpen1 = false;
            }
            else if(isFabOpen2){
                fab6.startAnimation(rotate_backward2);
                fab7.startAnimation(fab_close2);
                fab8.startAnimation(fab_close2);
                fab11.startAnimation(fab_close2);
                fab12.startAnimation(fab_close2);
                fab7.setClickable(false);
                fab8.setClickable(false);
                fab11.setClickable(false);
                fab12.setClickable(false);
                isFabOpen2 = false;
            }


        }
    }

    public void animateFAB1(){

        if(isFabOpen1){

            fab3.startAnimation(rotate_backward1);
            fab4.startAnimation(fab_close1);
            fab5.startAnimation(fab_close1);

            fab4.setClickable(false);
            fab5.setClickable(false);

            isFabOpen1 = false;


        } else {

            fab3.startAnimation(rotate_forward1);
            fab4.startAnimation(fab_open1);
            fab5.startAnimation(fab_open1);

            fab4.setClickable(true);
            fab5.setClickable(true);

            isFabOpen1 = true;
            if(isFabOpen){
                fab.startAnimation(rotate_backward);
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                fab9.startAnimation(fab_close);
                fab10.startAnimation(fab_close);
                fab1.setClickable(false);
                fab2.setClickable(false);
                fab9.setClickable(false);
                fab10.setClickable(false);
                isFabOpen = false;
            }
            else if(isFabOpen2){
                fab6.startAnimation(rotate_backward2);
                fab7.startAnimation(fab_close2);
                fab8.startAnimation(fab_close2);
                fab11.startAnimation(fab_close2);
                fab12.startAnimation(fab_close2);
                fab7.setClickable(false);
                fab8.setClickable(false);
                fab11.setClickable(false);
                fab12.setClickable(false);
                isFabOpen2 = false;
            }


        }
    }


    public void animateFAB2(){

        if(isFabOpen2){

            fab6.startAnimation(rotate_backward2);
            fab7.startAnimation(fab_close2);
            fab8.startAnimation(fab_close2);
            fab11.startAnimation(fab_close2);
            fab12.startAnimation(fab_close2);
            fab7.setClickable(false);
            fab8.setClickable(false);
            fab11.setClickable(false);
            fab12.setClickable(false);
            isFabOpen2 = false;

        } else {

            fab6.startAnimation(rotate_forward2);
            fab7.startAnimation(fab_open2);
            fab8.startAnimation(fab_open2);
            fab11.startAnimation(fab_open2);
            fab12.startAnimation(fab_open2);
            fab7.setClickable(true);
            fab8.setClickable(true);
            fab11.setClickable(true);
            fab12.setClickable(true);
            isFabOpen2 = true;
            if(isFabOpen){
                fab.startAnimation(rotate_backward);
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                fab9.startAnimation(fab_close);
                fab10.startAnimation(fab_close);
                fab1.setClickable(false);
                fab2.setClickable(false);
                fab9.setClickable(false);
                fab10.setClickable(false);
                isFabOpen = false;
            }
            else if(isFabOpen1){
                fab3.startAnimation(rotate_backward1);
                fab4.startAnimation(fab_close1);
                fab5.startAnimation(fab_close1);

                fab4.setClickable(false);
                fab5.setClickable(false);

                isFabOpen1 = false;
            }


        }
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private void showEnabled() {
       // mStatusTv.setText("Bluetooth is On");
       // mStatusTv.setTextColor(Color.BLUE);

       // mActivateBtn.setText("Disable");
        fab2.setEnabled(true);
      //  fab10.setVisibility(FloatingActionButton.VISIBLE);
       // fab9.setVisibility(FloatingActionButton.VISIBLE);
        //fab8.setVisibility(FloatingActionButton.VISIBLE);
        //
        fab10.setEnabled(true);
        fab9.setEnabled(true);
        fab11.setEnabled(true);
        fab12.setEnabled(true);
    //   fab1.setEnabled(true);
        fab8.setEnabled(true);
    }

    private void showDisabled() {
      //  mStatusTv.setText("Bluetooth is Off");
       // mStatusTv.setTextColor(Color.RED);

       // mActivateBtn.setText("Enable");
        fab2.setEnabled(true);
       // fab10.setVisibility(FloatingActionButton.INVISIBLE);
       // fab9.setVisibility(FloatingActionButton.INVISIBLE);
       // fab8.setVisibility(FloatingActionButton.INVISIBLE);
       fab10.setEnabled(false);
        fab9.setEnabled(false);
        fab11.setEnabled(false);
        fab12.setEnabled(false);
      //  fab1.setEnabled(false);
        fab8.setEnabled(true);
    }
private void setdo(){
    fab4.setEnabled(false);
}
private void getdo(){fab4.setEnabled(true);}
    private void showUnsupported() {
        //mStatusTv.setText("Bluetooth is unsupported by this device");

        //mActivateBtn.setText("Enable");
        fab2.setEnabled(false);

        fab10.setEnabled(false);
        fab9.setEnabled(false);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");

                    showEnabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                Intent newIntent = new Intent(getApplicationContext(), com.skobbler.sdkdemo.bluetooth.DeviceListActivity.class);

                newIntent.putParcelableArrayListExtra("device.list", mDeviceList);

                startActivity(newIntent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            }
        }
    };
}
