package com.garfild63.zanovo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yandex.mapkit.MapKitFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_FILE = "route_file";
    public static final String ROUTE_ICON = "route_icon";
    public static final String MARKED_POINTS = "marked_points";
    private static final int ALL_POINTS = 85;
    private static final double DEFAULT_LATITUDE = 44.7239;
    private static final double DEFAULT_LONGITUDE = 37.7708; // координаты Новороссийска
    private List<String> routeNames, routeFiles, routeIcons;
    private int routesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MyApiKeys.YANDEX_MAPKIT);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        LinearLayout horizontalListView = findViewById(R.id.horizontalListView);
        routeNames = new ArrayList<>();
        routeFiles = new ArrayList<>();
        routeIcons = new ArrayList<>();
        try {
            CSVParser.parse("routes.csv", new CSVParser.Preparator() {
                @Override
                public void endLine(List<String> args) {
                    if (args.size() == 3) {
                        routeNames.add(args.get(0));
                        routeFiles.add(args.get(1));
                        routeIcons.add(args.get(2));
                    }
                }
                @Override
                public char prepareChar(char ch, int x) {
                    return ch;
                }
            }, false);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        routesCount = routeNames.size();

        TextView routes = findViewById(R.id.routes);
        routes.setText(routesCount + " " + getString(R.string.routes));

        for (int i = 0; i < routesCount; i++) {
            int position = i;
            ListItem listItem = new ListItem(this, routeIcons.get(i),
                    150, 150, routeNames.get(i), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListItemClick(position);
                }
            });
            horizontalListView.addView(listItem);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(MARKED_POINTS, Context.MODE_PRIVATE);
        int count = 0;
        for (int i = 0; i < routesCount; i++) {
            String key = routeFiles.get(i);
            count += prefs.getStringSet(key, new HashSet<>()).size();
        }
        TextView explored = findViewById(R.id.explored);
        explored.setText(getString(R.string.explored) + " " + count + " "
                + getString(R.string.points) + " " + ALL_POINTS + getString(R.string.keep_up));

        TextView weather = findViewById(R.id.weather);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            new WeatherTask().execute(this, weather,
                    location.getLatitude(), location.getLongitude());
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.access_gps, Toast.LENGTH_LONG).show();
            new WeatherTask().execute(this, weather,
                    DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        } catch (NullPointerException e) {
            new WeatherTask().execute(this, weather,
                    DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }
    }

    protected void onListItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
        intent.putExtra(ROUTE_NAME, routeNames.get(position));
        intent.putExtra(ROUTE_FILE, routeFiles.get(position));
        intent.putExtra(ROUTE_ICON, routeIcons.get(position));
        startActivity(intent);
    }
}
