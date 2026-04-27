package com.garfild63.zanovo;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.UserData;
import java.util.HashMap;
import java.util.Map;

public class Placemark {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String GEO = "geomarker";
    private final PlacemarkMapObject placemark;

    public Placemark(double lat, double lng, String title, String description, String id) {
        placemark = Holder.mapView.getMap().getMapObjects().addPlacemark(new Point(lat, lng));
        Holder.setIcon(placemark, title, id);

        Map<String, String> userData = new HashMap<>();
        userData.put(ID, id);
        userData.put(TITLE, title);
        userData.put(DESCRIPTION, description);
        placemark.setUserData(new UserData(userData));

        placemark.addTapListener(Holder.listener);
    }

    public void setCoordinates(double lat, double lng) {
        placemark.setGeometry(new Point(lat, lng));
    }
}
