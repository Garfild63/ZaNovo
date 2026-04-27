package com.garfild63.zanovo;

import android.widget.Toast;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.PolylineMapObject;
import java.util.ArrayList;
import java.util.List;

public class Polyline {
    private final PolylineMapObject polyline;
    // private Point pointStart = new Point(44.67342, 37.77034);
    private int n;

    public Polyline(String filename, int color) {
        List<Point> points = new ArrayList<>();
        n = 0;
        try {
            CSVParser.parse(filename, new CSVParser.Preparator() {
                public void endLine(List<String> args) {
                    switch (args.size()) {
                        case 3:
                            points.add(new Point(Double.parseDouble(args.get(1)),
                                    Double.parseDouble(args.get(0))));
                            break;
                        case 5:
                            new Placemark(Double.parseDouble(args.get(1)),
                                    Double.parseDouble(args.get(0)), args.get(3), args.get(4),
                                    Integer.toString(n++));
                            break;
                    }
                }
                public char prepareChar(char ch, int x) {
                    return (ch == ',' && x < 3) ? '.' : ch;
                }
            }, false);
        } catch (Exception e) {
            Toast.makeText(Holder.context, e.toString(), Toast.LENGTH_LONG).show();
        }
        PolylineMapObject polyline = Holder.mapView.getMap()
                .getMapObjects().addPolyline(new com.yandex.mapkit.geometry.Polyline(points));
        polyline.setStrokeColor(color);
        polyline.setStrokeWidth(5);
        this.polyline = polyline;
    }

    public Point getPointStart() {
        return polyline.getGeometry().getPoints().get(0);
    }
}
