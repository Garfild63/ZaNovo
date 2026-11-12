package com.garfild63.zanovo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.UserData;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Holder {
    public static Context context;
    public static MapView mapView;
    public static MapObjectTapListener listener;
    private static SharedPreferences prefs;
    private static String key;
    private static Set<String> markedPoints;

    public static void initialize(Context ctx, MapView mv, String name) {
        context = ctx;

        prefs = context.getSharedPreferences(MainActivity.MARKED_POINTS, Context.MODE_PRIVATE);
        key = name;
        markedPoints = new HashSet<>(prefs.getStringSet(key, new HashSet<>()));

        mapView = mv;
        listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                Map<String, String> userData = ((UserData) mapObject.getUserData()).getData();
                String id = userData.get(Placemark.ID);
                String title = userData.get(Placemark.TITLE);
                String description = userData.get(Placemark.DESCRIPTION);
                String photo = userData.get(Placemark.PHOTO);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                if (!photo.isEmpty()) {
                    LayoutInflater factory = LayoutInflater.from(context);
                    View view = factory.inflate(R.layout.alert_dialog, null);
                    ImageView imageView = view.findViewById(R.id.dialogImageView);
                    try {
                        imageView.setImageDrawable(Drawable.createFromStream(
                                context.getAssets().open(name + "/" + photo), null));
                    } catch (IOException e) {
                    }
                    builder.setView(view);
                }

                builder.setTitle(title);
                if (!description.isEmpty())
                    builder.setMessage(description);

                if (!id.equals(Placemark.GEO)) {
                    boolean marked = markedPoints.contains(id);
                    builder.setPositiveButton(marked ? R.string.remove_marker : R.string.check_in,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (marked) {
                                    markedPoints.remove(id);
                                } else {
                                    markedPoints.add(id);
                                }
                                prefs.edit().putStringSet(key, markedPoints).apply();

                                setIcon((PlacemarkMapObject) mapObject, title, id);
                                Toast.makeText(context,
                                    marked ? R.string.you_removed_marker : R.string.you_checked_in,
                                    Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                }
                builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        };
    }

    public static void setIcon(PlacemarkMapObject placemark, String title, String id) {
        int icon;
        if (id.equals(Placemark.GEO)) {
            icon = R.drawable.geo;
        } else if (markedPoints.contains(id)) {
            icon = R.drawable.marker;
        } else if (title.startsWith("Старт")) {
            icon = R.drawable.green_dot;
        } else if (title.startsWith("Финиш")) {
            icon = R.drawable.red_dot;
        } else {
            icon = R.drawable.blue_dot;
        }
        placemark.setIcon(ImageProvider.fromResource(context, icon));
    }
}
