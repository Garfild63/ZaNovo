package com.garfild63.zanovo;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class MapActivity extends Activity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle(getIntent().getStringExtra(MainActivity.ROUTE_NAME));
        // getActionBar().hide();
        mapView = findViewById(R.id.mapView);
        String filename = getIntent().getStringExtra(MainActivity.ROUTE_FILE);
        Holder.initialize(this, mapView, filename);

        Placemark yourPlacemark = new Placemark(0, 0, getString(R.string.your_geo),
                getString(R.string.this_is_your_geo), Placemark.GEO);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                yourPlacemark.setCoordinates(location.getLatitude(), location.getLongitude());
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.access_gps, Toast.LENGTH_LONG).show();
        }

        Polyline polyline = new Polyline(filename, getResources().getColor(R.color.green));
        mapView.getMap().move(
                new CameraPosition(polyline.getPointStart(), 15, 0, 0),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}
