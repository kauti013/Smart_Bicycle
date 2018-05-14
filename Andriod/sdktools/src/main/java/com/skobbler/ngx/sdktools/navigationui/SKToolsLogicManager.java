package com.skobbler.ngx.sdktools.navigationui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.skobbler.ngx.R;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKMapViewStyle;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.reversegeocode.SKReverseGeocoderManager;
import com.skobbler.ngx.routing.SKRouteAdvice;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.sdktools.navigationui.autonight.SKToolsAutoNightManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.trail.SKTrailType;
import com.skobbler.ngx.util.SKLogging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class handles the logic related to the navigation and route calculation.
 */
public class SKToolsLogicManager implements SKMapSurfaceListener, SKNavigationListener, SKRouteListener,
        SKCurrentPositionListener {
private int c=0;
    private int b=0;
    private int d=0;
    private String curr="";
    private int a=0;
    private String l="left";
    private String r="right";
    private String sl="slight";

    private String sh="sharp";
private String lt="      <--L";
    private String rt="      R-->";
    private String C="       C";
    private String dest="80";
    private String slt="      <--SL";
    private String srt="      SR-->";
    private String shr="     SHR-->";
    private String shl="     <--SHL";
    private String left="10";
    private String right="20";
    private String slightleft="30";
    private String slightright="40";
    private String sharpleft="50";
    private String sharpright="60";
    private String uturn="70";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket=null;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;






    /**
     * Singleton instance for current class
     */
    private static volatile SKToolsLogicManager instance = null;

    /**
     * the map view instance
     */
    private SKMapSurfaceView mapView;

    /**
     * the view that holds the map view
     */
    private SKMapViewHolder mapHolder;

    /**
     * the current activity
     */
    private Activity currentActivity;

    /**
     * Current position provider
     */
    private SKCurrentPositionProvider currentPositionProvider;

    /**
     * Navigation manager
     */
    private SKNavigationManager naviManager;

    /**
     * the initial configuration for calculating route and navigating
     */
    private SKToolsNavigationConfiguration configuration;

    /**
     * number of options pressed (in navigation settings).
     */
    public int numberOfSettingsOptionsPressed;

    /**
     * last audio advices that needs to be played when the visual advice is
     * pressed
     */
    private String[] lastAudioAdvices;

    /**
     * the distance left to the destination after every route update
     */
    private long navigationCurrentDistance;

    /**
     * boolean value which shows if there are blocks on the route or not
     */
    private boolean roadBlocked;

    /**
     * flag for when a re-routing was done, which is set to true only until the
     * next update of the navigation state
     */
    private boolean reRoutingInProgress = false;

    /**
     * the current location
     */
    public static volatile SKPosition lastUserPosition;

    /**
     * true, if the navigation was stopped
     */
    private boolean navigationStopped;

    /**
     * Map surface listener
     */
    private SKMapSurfaceListener previousMapSurfaceListener;

    /**
     * SKRouteInfo list
     */
    private List<SKRouteInfo> skRouteInfoList = new ArrayList<SKRouteInfo>();

    /**
     * Navigation listener
     */
    private SKToolsNavigationListener navigationListener;

    /*
    Current map style
     */
    private SKMapViewStyle currentMapStyle;

    /*
    Current display mode
     */
    private SKMapSettings.SKMapDisplayMode currentUserDisplayMode;
    /**
     * start pedestrian navigation
     */
    public boolean startPedestrian=false;

    /**
     * Creates a single instance of {@link SKToolsNavigationUIManager}
     *
     * @return
     */
    public static SKToolsLogicManager getInstance() {
        if (instance == null) {
            synchronized (SKToolsLogicManager.class) {
                if (instance == null) {
                    instance = new SKToolsLogicManager();
                }
            }
        }
        return instance;
    }

    private SKToolsLogicManager() {
        naviManager = SKNavigationManager.getInstance();
    }

    /**
     * Sets the current activity.
     *
     * @param activity
     * @param rootId
     */
    protected void setActivity(Activity activity, int rootId) {
        this.currentActivity = activity;
        currentPositionProvider = new SKCurrentPositionProvider(currentActivity);
        if (SKToolsUtils.hasGpsModule(currentActivity)) {
            currentPositionProvider.requestLocationUpdates(true, false, true);
        } else if (SKToolsUtils.hasNetworkModule(currentActivity)) {
            currentPositionProvider.requestLocationUpdates(false, true, true);
        }
        currentPositionProvider.setCurrentPositionListener(this);
        SKToolsNavigationUIManager.getInstance().setActivity(currentActivity, rootId);


    }

    /**
     * Sets the listener.
     *
     * @param navigationListener
     */
    public void setNavigationListener(SKToolsNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }


    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            //myLabel.setText("No bluetooth adapter available");
        }
        /*if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }*/
        Set <BluetoothDevice>pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) //this name have to be replaced with your bluetooth device name
                {
                    mmDevice = device;
                    Log.v("ArduinoBT", "findBT found device named " + mmDevice.getName());
                    Log.v("ArduinoBT", "device address is " + mmDevice.getAddress());
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }
    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
        //myLabel.setText("Bluetooth Opened");
    }
    void openblue() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID

          mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
          mmSocket.connect();
          mmOutputStream = mmSocket.getOutputStream();
        //myLabel.setText("Bluetooth Opened");
    }
    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                           // myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }
    protected void calculateRoute(SKToolsNavigationConfiguration configuration, SKMapViewHolder mapHolder) throws IOException {
        try
        {
            findBT();
            openBT();
        }
        catch (IOException ex) {
        return;
        }
        this.mapHolder = mapHolder;
        this.mapView = mapHolder.getMapSurfaceView();
        this.configuration = configuration;
        SKToolsMapOperationsManager.getInstance().setMapView(mapView);
        currentPositionProvider.requestUpdateFromLastPosition();
        currentMapStyle = mapView.getMapSettings().getMapStyle();
        SKRouteSettings route = new SKRouteSettings();
        route.setStartCoordinate(configuration.getStartCoordinate());
        route.setDestinationCoordinate(configuration.getDestinationCoordinate());
        SKToolsMapOperationsManager.getInstance().drawDestinationNavigationFlag(configuration
                        .getDestinationCoordinate().getLongitude(),
                configuration.getDestinationCoordinate().getLatitude());
        List<SKViaPoint> viaPointList;
        viaPointList = configuration.getViaPointCoordinateList();
        if (viaPointList != null) {
            route.setViaPoints(viaPointList);
        }

        SKToolsNavigationUIManager.getInstance().setRouteType(configuration.getRouteType());
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            SKRouteManager.getInstance().enablePedestrianTrail(true, 5);
        }
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.CAR_SHORTEST) {
            route.setNoOfRoutes(1);
        } else {
            route.setNoOfRoutes(3);
        }

        route.setRouteMode(configuration.getRouteType());
        route.setRouteExposed(true);
        route.setTollRoadsAvoided(configuration.isTollRoadsAvoided());
        route.setAvoidFerries(configuration.isFerriesAvoided());
        route.setHighWaysAvoided(configuration.isHighWaysAvoided());
        SKRouteManager.getInstance().setRouteListener(this);

        SKRouteManager.getInstance().calculateRoute(route);
        SKToolsNavigationUIManager.getInstance().showPreNavigationScreen();

        if (configuration.isAutomaticDayNight() && lastUserPosition != null) {
            SKToolsAutoNightManager.getInstance().setAutoNightAlarmAccordingToUserPosition(lastUserPosition.getCoordinate(), currentActivity);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotificationAfterKitKat(currentActivity, true);
            } else {
                SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotification(currentActivity);
            }

            checkCorrectMapStyle();
        }
        navigationStopped = false;

        if (navigationListener != null) {
            navigationListener.onRouteCalculationStarted();
        }
    }


    /**
     * Starts a navigation with the specified configuration.
     *
     * @param configuration
     * @param mapHolder
     * @param isFreeDrive
     */
    protected void startNavigation(SKToolsNavigationConfiguration configuration,
                                   SKMapViewHolder mapHolder, boolean isFreeDrive) {

        SKNavigationSettings navigationSettings = new SKNavigationSettings();
        reRoutingInProgress = false;
        this.configuration = configuration;
        this.mapHolder = mapHolder;
        mapView = mapHolder.getMapSurfaceView();
        SKToolsNavigationUIManager.getInstance().setRouteType(configuration.getRouteType());
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            currentUserDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_2D;
            Toast.makeText(this.currentActivity, "The map will turn based on your recent positions.", Toast.LENGTH_SHORT).show();
            mapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.HISTORIC_POSITION);
            SKTrailType trailType = new SKTrailType();
            trailType.setPedestrianTrailEnabled(true, 1);
            navigationSettings.setTrailType(trailType);
            navigationSettings.setCcpAsCurrentPosition(true);
            mapView.getMapSettings().setCompassPosition(new SKScreenPoint(10, 70));
            mapView.getMapSettings().setCompassShown(true);
            navigationSettings.setNavigationMode(SKNavigationSettings.SKNavigationMode.PEDESTRIAN);
            startPedestrian=true;
        } else {
            currentUserDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_3D;
            mapView.getMapSettings().setFollowerMode(SKMapSettings.SKMapFollowerMode.NAVIGATION);
        }
        mapView.getMapSettings().setMapDisplayMode(currentUserDisplayMode);
        mapView.getMapSettings().setStreetNamePopupsShown(true);
        mapView.getMapSettings().setMapZoomingEnabled(false);
        previousMapSurfaceListener = mapView.getMapSurfaceListener();
        mapHolder.setMapSurfaceListener(this);
        SKToolsMapOperationsManager.getInstance().setMapView(mapView);

        navigationSettings.setNavigationType(configuration.getNavigationType());
        navigationSettings.setPositionerVerticalAlignment(-0.25f);
        navigationSettings.setShowRealGPSPositions(false);
        navigationSettings.setDistanceUnit(configuration.getDistanceUnitType());
        navigationSettings.setSpeedWarningThresholdInCity(configuration.getSpeedWarningThresholdInCity());
        navigationSettings.setSpeedWarningThresholdOutsideCity(configuration.getSpeedWarningThresholdOutsideCity());
        if (configuration.getNavigationType().equals(SKNavigationSettings.SKNavigationType.FILE)) {
            navigationSettings.setFileNavigationPath(configuration.getFreeDriveNavigationFilePath());
        }
        naviManager.setNavigationListener(this);
        naviManager.setMapView(mapView);
        naviManager.startNavigation(navigationSettings);


        SKToolsNavigationUIManager.getInstance().inflateNavigationViews(currentActivity);
        SKToolsNavigationUIManager.getInstance().reset(configuration.getDistanceUnitType());
        SKToolsNavigationUIManager.getInstance().setFollowerMode();
        if (configuration.getNavigationType() == SKNavigationSettings.SKNavigationType.SIMULATION) {
            SKToolsNavigationUIManager.getInstance().inflateSimulationViews();
        }
        if (isFreeDrive) {
            SKToolsNavigationUIManager.getInstance().setFreeDriveMode();
            currentMapStyle = mapView.getMapSettings().getMapStyle();
        }
        currentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("", "lastUserPosition = " + lastUserPosition);
        if (configuration.isAutomaticDayNight() && lastUserPosition != null) {
            if (isFreeDrive) {
                SKToolsAutoNightManager.getInstance().setAutoNightAlarmAccordingToUserPosition(lastUserPosition.getCoordinate(), currentActivity);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotificationAfterKitKat(currentActivity,
                            true);
                } else {
                    SKToolsAutoNightManager.getInstance().setAlarmForHourlyNotification(currentActivity);
                }

                checkCorrectMapStyle();
            }
        }
        SKToolsNavigationUIManager.getInstance().switchDayNightStyle(SKToolsMapOperationsManager
                .getInstance().getCurrentMapStyle());
        navigationStopped = false;

        if (navigationListener != null) {
            navigationListener.onNavigationStarted();
        }
    }
    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();

    }
    /**
     * Stops the navigation.
     */
    protected void stopNavigation() {
       try {
            onButton(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
      }
        SKToolsMapOperationsManager.getInstance().startPanningMode();
        mapView.getMapSettings().setMapStyle(currentMapStyle);
        mapView.getMapSettings().setCompassShown(false);
        SKRouteManager.getInstance().clearCurrentRoute();
        naviManager.stopNavigation();
        currentPositionProvider.stopLocationUpdates();
        mapHolder.setMapSurfaceListener(previousMapSurfaceListener);
        mapView.rotateTheMapToNorth();
        navigationStopped = true;
        startPedestrian=false;
        if (configuration.getDestinationCoordinate() != null) {
            SKToolsMapOperationsManager.getInstance().drawDestinationPoint(configuration.getDestinationCoordinate().getLongitude(), configuration.getDestinationCoordinate().getLatitude());
        }
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SKToolsNavigationUIManager.getInstance().removeNavigationViews();
                currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        if (navigationListener != null) {
            navigationListener.onNavigationEnded();
        }

        SKToolsAdvicePlayer.getInstance().stop();
    }


    /**
     * Checks the correct map style, taking into consideration auto night configuration settings.
     */
    private void checkCorrectMapStyle() {
        int currentMapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        int correctMapStyle = SKToolsMapOperationsManager.getInstance().getMapStyleBeforeStartDriveMode(configuration
                .isAutomaticDayNight());
        if (currentMapStyle != correctMapStyle) {
            SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, correctMapStyle);
            SKToolsNavigationUIManager.getInstance().changePanelsBackgroundAndTextViewsColour
                    (SKToolsMapOperationsManager
                            .getInstance().getCurrentMapStyle());
        }
    }

    /**
     * Checks if the navigation is stopped.
     *
     * @return
     */
    public boolean isNavigationStopped() {
        return navigationStopped;
    }

    /**
     * Gets the current activity.
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * Handles orientation changed.
     */
    public void notifyOrientationChanged() {
        int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        SKMapSettings.SKMapDisplayMode displayMode = mapView.getMapSettings().getMapDisplayMode();
        SKToolsNavigationUIManager.getInstance().handleOrientationChanged(mapStyle, displayMode);
    }

    /**
     * Handles the block roads list items click.
     *
     * @param parent
     * @param position
     */
    protected void handleBlockRoadsItemsClick(AdapterView<?> parent, int position) {

        SKToolsNavigationUIManager.getInstance().setFollowerMode();
        SKToolsNavigationUIManager.getInstance().showFollowerModePanels(configuration.getNavigationType() ==
                SKNavigationSettings.SKNavigationType.SIMULATION);

        String item = (String) parent.getItemAtPosition(position);
        if (item.equals(currentActivity.getResources().getString(
                R.string.unblock_all))) {
            naviManager.unblockAllRoads();
            roadBlocked = false;
        } else {
            // blockedDistance[0] - value, blockedDistance[1] - unit
            String[] blockedDistance = item.split(" ");
            int distance;
            try {
                distance = Integer.parseInt(blockedDistance[0]);
            } catch (NumberFormatException e) {
                distance = -1;
            }
            // set unit type based on blockDistance[1]
            int type = -1;
            if ("ft".equals(blockedDistance[1])) {
                type = 0;
            } else if ("yd".equals(blockedDistance[1])) {
                type = 1;
            } else if ("mi".equals(blockedDistance[1])) {
                type = 2;
            } else if ("km".equals(blockedDistance[1])) {
                type = 3;
            }

            naviManager.blockRoad(SKToolsUtils
                    .distanceInMeters(distance, type));
            roadBlocked = true;
        }
    }


    /**
     * Handles the items click.
     *
     * @param v
     */
    protected void handleItemsClick(View v) {
        int id = v.getId();

        if (id == R.id.first_route || id == R.id.second_route || id == R.id.third_route) {

            int routeIndex = 0;
            if (id == R.id.first_route) {
                routeIndex = 0;

            } else if (id == R.id.second_route) {
                routeIndex = 1;

            } else if (id == R.id.third_route) {
                routeIndex = 2;

            }

            SKToolsMapOperationsManager.getInstance().zoomToRoute(currentActivity);
            if (skRouteInfoList.size() > routeIndex) {
                int routeId = skRouteInfoList.get(routeIndex).getRouteID();
                SKRouteManager.getInstance().setCurrentRouteByUniqueId(routeId);
                SKToolsNavigationUIManager.getInstance().selectAlternativeRoute(routeIndex);
            }
        } else if (id == R.id.start_navigation_button) {
            SKToolsNavigationUIManager.getInstance().removePreNavigationViews();
            SKRouteManager.getInstance().clearRouteAlternatives();
            skRouteInfoList.clear();
            startNavigation(configuration, mapHolder, false);
        } else if (id == R.id.navigation_top_back_button) {
            SKToolsMapOperationsManager.getInstance().setMapInNavigationMode();
            SKToolsNavigationUIManager.getInstance().setFollowerMode();
            SKToolsNavigationUIManager.getInstance().showFollowerModePanels(configuration.getNavigationType() ==
                    SKNavigationSettings.SKNavigationType.SIMULATION);
            mapView.getMapSettings().setCompassShown(false);
            mapView.getMapSettings().setMapZoomingEnabled(false);
            if (currentUserDisplayMode != null) {
                SKToolsMapOperationsManager.getInstance().switchMapDisplayMode(currentUserDisplayMode);
            }
        } else if (id == R.id.cancel_pre_navigation_button) {
            removeRouteCalculationScreen();
        } else if (id == R.id.menu_back_prenavigation_button) {
            SKToolsNavigationUIManager.getInstance().handleNavigationBackButton();
        } else if (id == R.id.navigation_increase_speed) {
            SKNavigationManager.getInstance().increaseSimulationSpeed(3);
        } else if (id == R.id.navigation_decrease_speed) {
            SKNavigationManager.getInstance().decreaseSimulationSpeed(3);
        } else if (id == R.id.menu_back_follower_mode_button) {
            SKToolsNavigationUIManager.getInstance().handleNavigationBackButton();
        } else if (id == R.id.navigation_bottom_right_estimated_panel || id == R.id
                .navigation_bottom_right_arriving_panel) {
            SKToolsNavigationUIManager.getInstance().switchEstimatedTime();
        } else if (id == R.id.pedestrian_compass_panel_layout) {
            SKToolsNavigationUIManager.getInstance().setTheCorrespondingImageForCompassPanel(mapView.getMapSettings());

        } else if (id == R.id.position_me_real_navigation_button) {
            if (lastUserPosition != null) {
                mapView.centerMapOnCurrentPositionSmooth(15, 1000);
            } else {
                Toast.makeText(currentActivity, currentActivity.getResources().getString(R.string
                        .no_position_available), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.current_advice_image_holder || id == R.id.current_advice_text_holder) {
            playLastAdvice();
        }

    }

    /**
     * Removes the pre navigation screen.
     */
    protected void removeRouteCalculationScreen() {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SKToolsNavigationUIManager.getInstance().removePreNavigationViews();
        SKRouteManager.getInstance().clearCurrentRoute();
        SKRouteManager.getInstance().clearRouteAlternatives();
        skRouteInfoList.clear();
        System.out.println("------ current map style remove" + mapView.getMapSettings().getMapStyle());
        mapView.getMapSettings().setMapStyle(currentMapStyle);
        SKToolsAutoNightManager.getInstance().cancelAlarmForForHourlyNotification();
        SKToolsMapOperationsManager.getInstance().drawDestinationPoint(configuration.getDestinationCoordinate().getLongitude(), configuration.getDestinationCoordinate().getLatitude());
        if (navigationListener != null) {
            navigationListener.onRouteCalculationCanceled();
        }
    }

    /**
     * handles the click on different views
     *
     * @param v the current view on which the click is detected
     */
    protected void handleSettingsItemsClick(View v) {
        boolean naviScreenSet = false;

        int id = v.getId();
        if (id == R.id.navigation_settings_audio_button) {
            numberOfSettingsOptionsPressed++;
            if (numberOfSettingsOptionsPressed == 1) {
                SKToolsNavigationUIManager.getInstance().loadAudioSettings();
            }
        } else if (id == R.id.navigation_settings_day_night_mode_button) {
            numberOfSettingsOptionsPressed++;
            if (numberOfSettingsOptionsPressed == 1) {
                loadDayNightSettings(configuration);
            }
        } else if (id == R.id.navigation_settings_overview_button) {
            final SKSearchResult destination = SKReverseGeocoderManager
                    .getInstance().reverseGeocodePosition(configuration.getDestinationCoordinate());
            if (destination != null) {
                SKToolsMapOperationsManager.getInstance().switchToOverViewMode(currentActivity, configuration);
                SKToolsNavigationUIManager.getInstance().showOverviewMode(SKToolsUtils.getFormattedAddress
                        (destination.getParentsList()));
                naviScreenSet = true;
            }
        } else if (id == R.id.navigation_settings_route_info_button) {
            numberOfSettingsOptionsPressed++;
            if (numberOfSettingsOptionsPressed == 1) {
                final SKSearchResult startCoord = SKReverseGeocoderManager
                        .getInstance().reverseGeocodePosition(configuration.getStartCoordinate());
                final SKSearchResult destCoord = SKReverseGeocoderManager
                        .getInstance().reverseGeocodePosition(configuration.getDestinationCoordinate());
                String startAdd = SKToolsUtils.getFormattedAddress
                        (startCoord.getParentsList());
                String destAdd = SKToolsUtils.getFormattedAddress
                        (destCoord.getParentsList());
                SKToolsNavigationUIManager.getInstance().showRouteInfoScreen(startAdd, destAdd);
                naviScreenSet = true;
            }
        } else if (id == R.id.navigation_settings_roadblock_info_button) {
            naviScreenSet = true;

            if (!SKToolsNavigationUIManager.getInstance().isFreeDriveMode()) {
                SKToolsNavigationUIManager.getInstance().showRoadBlockMode(configuration.getDistanceUnitType(),
                        navigationCurrentDistance);
            } else {
                SKToolsNavigationUIManager.getInstance().showRouteInfoFreeDriveScreen();
            }

        } else if (id == R.id.navigation_settings_panning_button) {
            SKToolsMapOperationsManager.getInstance().startPanningMode();
            SKToolsNavigationUIManager.getInstance().showPanningMode(configuration.getNavigationType() ==
                    SKNavigationSettings.SKNavigationType.REAL);
            naviScreenSet = true;
        } else if (id == R.id.navigation_settings_view_mode_button) {
            loadMapDisplayMode();
        } else if (id == R.id.navigation_settings_quit_button) {
            SKToolsNavigationUIManager.getInstance().showExitNavigationDialog();
        } else if (id == R.id.navigation_settings_back_button) {
            if (currentUserDisplayMode != null) {
                SKToolsMapOperationsManager.getInstance().switchMapDisplayMode(currentUserDisplayMode);
            }
        }

        SKToolsNavigationUIManager.getInstance().hideSettingsPanel();
        numberOfSettingsOptionsPressed = 0;

        if (!naviScreenSet) {
            SKToolsNavigationUIManager.getInstance().setFollowerMode();
            SKToolsNavigationUIManager.getInstance().showFollowerModePanels(configuration.getNavigationType() ==
                    SKNavigationSettings.SKNavigationType.SIMULATION);
        }
    }

    private void switchToPanningMode() {
        if (!SKToolsNavigationUIManager.getInstance().isPanningMode()) {
            SKToolsMapOperationsManager.getInstance().startPanningMode();
            SKToolsNavigationUIManager.getInstance().showPanningMode(configuration.getNavigationType() ==
                    SKNavigationSettings.SKNavigationType.REAL);
        }
    }

    /**
     * play the last advice
     */
    protected void playLastAdvice() {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            SKToolsAdvicePlayer.getInstance().playAdvice(lastAudioAdvices, SKToolsAdvicePlayer.PRIORITY_USER);
        }
    }

    /**
     * Checks if the roads are blocked.
     *
     * @return
     */
    protected boolean isRoadBlocked() {
        return roadBlocked;
    }

    /**
     * Changes the map style from day -> night or night-> day
     */
    private void loadDayNightSettings(SKToolsNavigationConfiguration configuration) {
        int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        int newStyle;
        if (mapStyle == SKToolsMapOperationsManager.DAY_STYLE) {
            newStyle = SKToolsMapOperationsManager.NIGHT_STYLE;
        } else {
            newStyle = SKToolsMapOperationsManager.DAY_STYLE;
        }

        SKToolsNavigationUIManager.getInstance().switchDayNightStyle(newStyle);
        SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, newStyle);
    }


    /**
     * Decides the style in which the map needs to be changed next.
     */
    public void computeMapStyle(boolean isDaytime) {
        Log.d("", "Update the map style after receiving the broadcast");
        int mapStyle;
        if (isDaytime) {
            mapStyle = SKToolsMapOperationsManager.DAY_STYLE;
        } else {
            mapStyle = SKToolsMapOperationsManager.NIGHT_STYLE;
        }
        SKToolsNavigationUIManager.getInstance().switchDayNightStyle(mapStyle);
        SKToolsMapOperationsManager.getInstance().switchDayNightStyle(configuration, mapStyle);
    }


    /**
     * Changes the map display from 3d-> 2d and vice versa
     */
    private void loadMapDisplayMode() {
        SKMapSettings.SKMapDisplayMode displayMode = mapView.getMapSettings().getMapDisplayMode();
        SKMapSettings.SKMapDisplayMode newDisplayMode;
        if (displayMode == SKMapSettings.SKMapDisplayMode.MODE_3D) {
            newDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_2D;
        } else {
            newDisplayMode = SKMapSettings.SKMapDisplayMode.MODE_3D;
        }

        currentUserDisplayMode = newDisplayMode;
        SKToolsNavigationUIManager.getInstance().switchMapMode(newDisplayMode);
        SKToolsMapOperationsManager.getInstance().switchMapDisplayMode(newDisplayMode);
    }

    @Override
    public void onActionPan() {
        switchToPanningMode();
    }

    @Override
    public void onActionZoom() {
        float currentZoom = mapView.getZoomLevel();
        if (currentZoom < 5) {
            // do not show the blue dot
            mapView.getMapSettings().setCurrentPositionShown(false);
        } else {
            mapView.getMapSettings().setCurrentPositionShown(true);
        }
        switchToPanningMode();
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder holder) {
    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {
    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {
    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {
    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {
    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {
        if (SKToolsNavigationUIManager.getInstance().isFollowerMode()) {
            SKToolsNavigationUIManager.getInstance().showSettingsMode();
        }
    }

    @Override
    public void onRotateMap() {
        switchToPanningMode();
    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {
    }

    @Override
    public void onInternetConnectionNeeded() {
    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {
    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {
    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {
    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {
    }

    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation) {
    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {
    }

    @Override
    public void onCompassSelected() {
    }

    @Override
    public void onCurrentPositionSelected() {
    }

    @Override
    public void onObjectSelected(int i) {
    }


    @Override
    public void onInternationalisationCalled(int i) {
    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String messsage) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    public void onDestinationReached() {

        if (configuration.getNavigationType() == SKNavigationSettings.SKNavigationType.REAL && configuration
                .isContinueFreeDriveAfterNavigationEnd()) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SKRouteManager.getInstance().clearCurrentRoute();
                    SKToolsMapOperationsManager.getInstance().deleteDestinationPoint();
                    SKToolsNavigationUIManager.getInstance().setFreeDriveMode();
                }
            });
        } else {
            stopNavigation();
        }
    }

    @Override
    public void onSignalNewAdviceWithInstruction(String instruction) {
    }

    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean specialSoundFile) {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);
        }
    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] adviceList, boolean speedExceeded) {
        if (!(configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN)) {
            playSoundWhenSpeedIsExceeded(adviceList, speedExceeded);
        }
    }

    /**
     * play sound when the speed is exceeded
     *
     * @param adviceList    - the advices that needs to be played
     * @param speedExceeded - true if speed is exceeded, false otherwise
     */
    private void playSoundWhenSpeedIsExceeded(final String[] adviceList, final boolean speedExceeded) {
        if (!navigationStopped) {
            currentActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (speedExceeded) {
                        SKToolsAdvicePlayer.getInstance()
                                .playAdvice(adviceList, SKToolsAdvicePlayer.PRIORITY_SPEED_WARNING);
                    }
                    SKToolsNavigationUIManager.getInstance().handleSpeedExceeded(speedExceeded);
                }
            });
        }
    }

    @Override
    public void onSpeedExceededWithInstruction(String instruction, boolean speedExceeded) {
    }
//String curr;
void onButton(String s) throws IOException
{

    mmOutputStream.write(s.getBytes());

}
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {

        SKLogging.writeLog("SKToolsLogicManager", "NAVIGATION STATE " + skNavigationState.toString(), SKLogging.LOG_DEBUG);
        if (currentUserDisplayMode == SKMapSettings.SKMapDisplayMode.MODE_3D) {
            mapView.getMapSettings().setStreetNamePopupsShown(true);
        }
        lastAudioAdvices = skNavigationState.getCurrentAdviceAudioAdvices();


        navigationCurrentDistance = (int) Math.round(skNavigationState.getDistanceToDestination());
        if(skNavigationState.getCurrentAdviceDistanceToAdvice() > 40){
            if(b!=skNavigationState.getAdviceID()){
                d=0;
            }

            b=skNavigationState.getAdviceID();
            if(d<1) {
                Toast.makeText(this.currentActivity, "Continue Straight", Toast.LENGTH_SHORT).show();
               // try {
               //     onButton("Straight");
              //  } catch (IOException e) {
              //      e.printStackTrace();
               // }
                d++;
            }
        }
        if (skNavigationState.getCurrentAdviceDistanceToAdvice() < 30){
            // if(curr!=skNavigationState.getAdviceInstruction()){
            //    c=0;
            //    Toast.makeText(this.currentActivity,curr+"c=0"+c,Toast.LENGTH_SHORT).show();
            //}
            // if(c<1) {
            // if(curr!=skNavigationState.getAdviceInstruction()){
    if(c!=skNavigationState.getAdviceID()){
        a=0;
    }else{}
            c=skNavigationState.getAdviceID();
            if(a<1) {
                if (mmSocket==null) {
                    try {
                        openblue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{}
                // Toast.makeText(this.currentActivity, skNavigationState.getAdviceInstruction(), Toast.LENGTH_SHORT).show();
               if(skNavigationState.getFirstCrossingDescriptor().getCrossingType()==0) {
                  String[] splitter=skNavigationState.getAdviceInstruction().split("\\s+");
                   if(splitter[2].equals(l)){
                       Toast.makeText(this.currentActivity,"<--L1", Toast.LENGTH_SHORT).show();
                    try {
                           //onButton(lt);
                        onButton(left);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   else if(splitter[2].equals(r)){
                       Toast.makeText(this.currentActivity,"R1-->", Toast.LENGTH_SHORT).show();
                       try {
                           //onButton(rt);
                          onButton(right);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   else if(splitter[2].equals(sl)){
                       if(splitter[3].equals(l)) {
                           Toast.makeText(this.currentActivity, "<--SL1", Toast.LENGTH_SHORT).show();
                       try {
                           //onButton(slt);
                           onButton(slightleft);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       }
                       else if(splitter[3].equals(r)){
                           Toast.makeText(this.currentActivity, "SR1-->", Toast.LENGTH_SHORT).show();
                      try {
                          //onButton(srt);
                          onButton(slightright);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       }
                   }

                   else if(splitter[2].equals(sh)){
                       if(splitter[3].equals(l)) {
                           Toast.makeText(this.currentActivity, "<--SHL1", Toast.LENGTH_SHORT).show();
                      try {
                          // onButton("     <--SHL");
                          onButton(sharpleft);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       }
                       else if(splitter[3].equals(r)){
                           Toast.makeText(this.currentActivity, "SHR1-->", Toast.LENGTH_SHORT).show();
                       try {
                          // onButton("     SHR-->");
                          onButton(sharpright);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       }
                   }
                }
                 else if(skNavigationState.getFirstCrossingDescriptor().getCrossingType()==1) {
                   String[] splitter=skNavigationState.getAdviceInstruction().split("\\s+");

                       Toast.makeText(this.currentActivity,"C"+splitter[3], Toast.LENGTH_SHORT).show();
        C="       C"+splitter[3];
                  try {
                      // onButton(C);
                      onButton(splitter[3]);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
                    else if(skNavigationState.getFirstCrossingDescriptor().getCrossingType()==2){
                   String[] splitter=skNavigationState.getAdviceInstruction().split("\\s+");

                   Toast.makeText(this.currentActivity,splitter[2], Toast.LENGTH_SHORT).show();
                  try {
                       //onButton(splitter[2]);
                     onButton(uturn);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
              //  try {
               //     onButton(skNavigationState.getAdviceInstruction());
               // } catch (IOException e) {
                //    e.printStackTrace();
              //  }
                a++;
            }
            //curr=skNavigationState.getAdviceInstruction();
            // c"=w ++;
            //  curr=skNavigationState.getAdviceInstruction();
            // Toast.makeText(this.currentActivity,curr+"c<1"+c,Toast.LENGTH_SHORT).show();
        }

 //curr=skNavigationState.getAdviceInstruction();

        if (reRoutingInProgress) {
            reRoutingInProgress = false;

            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    boolean followerMode = SKToolsNavigationUIManager.getInstance().isFollowerMode();
                    if (followerMode) {
                        SKToolsNavigationUIManager.getInstance().showFollowerModePanels(configuration
                                .getNavigationType() == SKNavigationSettings.SKNavigationType.SIMULATION);
                    }
                }
            });
        }
        int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        SKToolsNavigationUIManager.getInstance().handleNavigationState(skNavigationState, mapStyle);
    }

    @Override
    public void onReRoutingStarted() {
        if (SKToolsNavigationUIManager.getInstance().isFollowerMode()) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SKToolsNavigationUIManager.getInstance().hideTopPanels();
                    SKToolsNavigationUIManager.getInstance().hideBottomAndLeftPanels();
                    SKToolsNavigationUIManager.getInstance().showReroutingPanel();
                    reRoutingInProgress = true;
                }
            });
        }
    }

    @Override
    public void onFreeDriveUpdated(String countryCode, String streetName, String referenceName, SKNavigationState.SKStreetType streetType,
                                   double currentSpeed, double speedLimit) {
        if (SKToolsNavigationUIManager.getInstance().isFollowerMode()) {
            int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
            SKToolsNavigationUIManager.getInstance().handleFreeDriveUpdated(countryCode, streetName,
                    currentSpeed, speedLimit, configuration.getDistanceUnitType(), mapStyle);
        }
    }


    @Override
    public void onVisualAdviceChanged(final boolean firstVisualAdviceChanged, final boolean secondVisualAdviceChanged,
                                      SKNavigationState skNavigationState) {

        final int mapStyle = SKToolsMapOperationsManager.getInstance().getCurrentMapStyle();
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SKToolsNavigationUIManager.getInstance().setTopPanelsBackgroundColour(mapStyle,
                        firstVisualAdviceChanged, secondVisualAdviceChanged);
            }
        });

    }

    @Override
    public void onTunnelEvent(boolean b) {
    }

    @Override
    public void onRouteCalculationCompleted(final SKRouteInfo skRouteInfo) {
        if (!skRouteInfo.isCorridorDownloaded()) {
            return;
        }
        skRouteInfoList.add(skRouteInfo);
        if (SKToolsNavigationUIManager.getInstance().isPreNavigationMode()) {
            SKToolsMapOperationsManager.getInstance().zoomToRoute(currentActivity);
        }
        if (configuration.getRouteType() == SKRouteSettings.SKRouteMode.PEDESTRIAN) {
            SKRouteManager.getInstance().renderRouteAsPedestrian(skRouteInfo.getRouteID());
        }


        final List<SKRouteAdvice> advices = SKRouteManager.getInstance().getAdviceList(skRouteInfo.getRouteID(), SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS);
        if (advices != null){
            for (SKRouteAdvice advice : advices){
                SKLogging.writeLog("SKToolsLogicManager", " Route advice is " + advice.toString(), SKLogging.LOG_DEBUG);
            }
        }

        final String[] routeSummary = skRouteInfo.getRouteSummary();
        if (routeSummary != null){
            for( String street :routeSummary){
                SKLogging.writeLog("SKToolsLogicManager" , " Route Summary street = " + street , SKLogging.LOG_ERROR);
            }
        }else{
            SKLogging.writeLog("SKToolsLogicManager", "Route summary is null " , SKLogging.LOG_ERROR);
        }
    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {
        SKToolsNavigationUIManager.getInstance().showRouteCalculationFailedDialog(skRoutingErrorCode);
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SKToolsNavigationUIManager.getInstance().removePreNavigationViews();
            }
        });
    }

    @Override
    public void onAllRoutesCompleted() {
        if (!skRouteInfoList.isEmpty()) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (SKToolsNavigationUIManager.getInstance().isPreNavigationMode()) {
                        SKToolsNavigationUIManager.getInstance().showStartNavigationPanel();
                    }
                    for (int i = 0; i < skRouteInfoList.size(); i++) {
                        final String time = SKToolsUtils.formatTime(skRouteInfoList.get(i).getEstimatedTime());
                        final String distance = SKToolsUtils.convertAndFormatDistance(skRouteInfoList.get(i)
                                        .getDistance(),
                                configuration.getDistanceUnitType(), currentActivity);

                        SKToolsNavigationUIManager.getInstance().sePreNavigationButtons(i, time, distance);
                    }

                    int routeId = skRouteInfoList.get(0).getRouteID();
                    SKRouteManager.getInstance().setCurrentRouteByUniqueId(routeId);
                    SKToolsNavigationUIManager.getInstance().selectAlternativeRoute(0);
                    if (SKToolsNavigationUIManager.getInstance().isPreNavigationMode()) {
                        SKToolsMapOperationsManager.getInstance().zoomToRoute(currentActivity);
                    }

                }
            });
        }

        if (navigationListener != null) {
            navigationListener.onRouteCalculationCompleted();
        }
    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {
    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {
    }

    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition) {
        lastUserPosition = skPosition;
        if (configuration.getNavigationType() == SKNavigationSettings.SKNavigationType.REAL) {
            SKPositionerManager.getInstance().reportNewGPSPosition(skPosition);
        }
    }


    @Override
    public void onViaPointReached(int index) {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SKToolsNavigationUIManager.getInstance().showViaPointPanel();
            }
        });
    }

}
