package com.gurutel.gurufit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;


import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by udnet_pc1 on 2015-08-31.
 */
public class MapViewActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
    private  static final String TAG = "GuruFit";

    private static final int MENU_MAP_TYPE = Menu.FIRST + 1;
    private static final int MENU_MAP_MOVE = Menu.FIRST + 2;
    private static final int MENU_MAP_RETURN = Menu.FIRST + 3;
    private MapPoint DEFAULT_MARKER_POINT = null;

    private static final String LOG_TAG = "GuruFit";
    private MapView mMapView;
    private MapPOIItem mDefaultMarker;

    String responseBody="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent i = getIntent();
        String serial = i.getStringExtra("serial");
        Log.d(TAG,"MapViewSerial : " + serial);

        try{
            String sURL = "http://data.udnet.co.kr/GetCustLocation.php?serial="+serial;
            HttpClient httpclient = new DefaultHttpClient(); //httpclient 객체를 생성
            HttpGet httpget = new HttpGet(sURL);

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
            Log.d(TAG, "========= response [" + responseBody + "]===============================================");
            httpclient.getConnectionManager().shutdown();        //서버와의 통신 종료
        }catch(Exception e){
            e.printStackTrace();
        }


        initList();

        setContentView(R.layout.mapview);
        MapLayout mapLayout = new MapLayout(this);
        mMapView = mapLayout.getMapView();


        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
//        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);

        mMapView.setMapType(MapView.MapType.Standard);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapLayout);
    }


    List<Map<String,String>> LocationList = new ArrayList<Map<String,String>>();

    private void initList(){
        try{
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("LocationList");
            Log.d(TAG, "========= JSON CNT [" + jsonMainNode.length() + "]===============================================");

            for(int i = 0; i<jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String lat = jsonChildNode.getString("lat").toString();
                String lon = jsonChildNode.optString("lon").toString();
                String accuracy = jsonChildNode.optString("accuracy").toString();
                String reg_date = jsonChildNode.optString("reg_date").toString();
                LocationList.add(createChannel(lat, lon, accuracy,reg_date));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String, String> createChannel(String lat,String lon,String accuracy,String reg_date){
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put("lat", lat);
        employeeNameNo.put("lon", lon);
        employeeNameNo.put("accuracy", accuracy);
        employeeNameNo.put("reg_date", reg_date);
        return employeeNameNo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_MAP_TYPE, Menu.NONE, "MapType");
        menu.add(0, MENU_MAP_MOVE, Menu.NONE, "Move");
        menu.add(0, MENU_MAP_RETURN, Menu.NONE, "Main");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();

        switch (itemId) {
            case MENU_MAP_TYPE: {

                String hdMapTile = mMapView.isHDMapTileEnabled()? "HD Map Tile Off" : "HD Map Tile On";
                String[] mapTypeMenuItems = { "Standard", "Satellite", "Hybrid", hdMapTile, "Clear Map Tile Cache"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("MapType");
                dialog.setItems(mapTypeMenuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controlMapTile(which);
                    }
                });
                dialog.show();


                return true;
            }

            case MENU_MAP_MOVE: {
                String rotateMapMenu = mMapView.getMapRotationAngle() == 0.0f? "Rotate Map 60" : "Unrotate Map";
                String[] mapMoveMenuItems = { "Move to", "Zoom to", "Move and Zoom to", "Zoom In", "Zoom Out", rotateMapMenu};

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Move");
                dialog.setItems(mapMoveMenuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controlMapMove(which);
                    }

                });
                dialog.show();

                return true;
            }

            case MENU_MAP_RETURN:{
                startActivity(new Intent(this, CustomerListActivity.class));
                finish();
                return true;
            }

        }


        return super.onOptionsItemSelected(item);
    }

    private void controlMapMove(int which) {
        switch (which) {
            case 0: // Move to
            {
               // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
            }
            break;
            case 1: // Zoom to
            {
                mMapView.setZoomLevel(7, true);
            }
            break;
            case 2: // Move and Zoom to
            {
                mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
            }
            break;
            case 3: // Zoom In
            {
                mMapView.zoomIn(true);
            }
            break;
            case 4: // Zoom Out
            {
                mMapView.zoomOut(true);
            }
            break;
            case 5: // Rotate Map 60, Unrotate Map
            {
                if (mMapView.getMapRotationAngle() == 0.0f) {
                    mMapView.setMapRotationAngle(60.0f, true);
                } else {
                    mMapView.setMapRotationAngle(0.0f, true);
                }
            }
            break;
        }
    }

    /**
     * 지도 타일 컨트롤.
     */
    private void controlMapTile(int which) {
        switch (which) {
            case 0: // Standard
            {
                mMapView.setMapType(MapView.MapType.Standard);
            }
            break;
            case 1: // Satellite
            {
                mMapView.setMapType(MapView.MapType.Satellite);
            }
            break;
            case 2: // Hybrid
            {
                mMapView.setMapType(MapView.MapType.Hybrid);
            }
            break;
            case 3: // HD Map Tile On/Off
            {
                if (mMapView.isHDMapTileEnabled()) {
                    mMapView.setHDMapTileEnabled(false);
                } else {
                    mMapView.setHDMapTileEnabled(true);
                }
            }
            break;
            case 4: // Clear Map Tile Cache
            {
                MapView.clearMapTilePersistentCache();
            }
            break;
        }
    }

    //	/////////////////////////////////////////////////////////////////////////////////////////////////
//	// net.daum.mf.map.api.MapView.OpenAPIKeyAuthenticationResultListener
//
//	@Override
//	public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int resultCode, String resultMessage) {
//		Log.i(LOG_TAG,	String.format("Open API Key Authentication Result : code=%d, message=%s", resultCode, resultMessage));
//	}
//
//	/////////////////////////////////////////////////////////////////////////////////////////////////
//	// net.daum.mf.map.api.MapView.MapViewEventListener


    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely ");
        double lat;
        double lon;
        String reg_date;
        if(LocationList.isEmpty()){
            lat = 37.537229;
            lon = 127.005515;
            reg_date = "There is no data.  ";
        }else {
            lat = Double.parseDouble(LocationList.get(0).get("lat"));
            lon = Double.parseDouble(LocationList.get(0).get("lon"));
            reg_date = LocationList.get(0).get("reg_date");
        }


        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lon), 2, true);

        DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(lat, lon);
        createDefaultMarker(mMapView,reg_date);

        if(!LocationList.isEmpty()){
            MapPolyline polyline = new MapPolyline();
            polyline.setTag(1000);
            polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

            for(int i=0;i<LocationList.size();i++){
                lat = Double.parseDouble(LocationList.get(i).get("lat"));
                lon = Double.parseDouble(LocationList.get(i).get("lon"));
                reg_date = LocationList.get(i).get("reg_date");
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(lat,lon));
            }

            mapView.addPolyline(polyline);
            DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(lat, lon);
            createDefaultMarker(mMapView, reg_date);
        }

    }

    private void createDefaultMarker(MapView mapView,String reg_date) {
        mDefaultMarker = new MapPOIItem();
        String name = reg_date.substring(0,16);
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(DEFAULT_MARKER_POINT);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(mDefaultMarker);
        mapView.selectPOIItem(mDefaultMarker, true);
        mapView.setMapCenterPoint(DEFAULT_MARKER_POINT, false);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Double-Tap on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Long-Press on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewSingleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewDragStarted (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewDragEnded (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        Log.i(LOG_TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel));
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
