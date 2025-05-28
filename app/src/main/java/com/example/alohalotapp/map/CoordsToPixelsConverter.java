package com.example.alohalotapp.map;

import android.graphics.Point;

public class CoordsToPixelsConverter {
    private static final int tileSize = 256;
    public static Point latLngToPoint(double lat, double lng, int zoom) {
        double scale = tileSize * Math.pow(2, zoom);

        double x = (lng + 180) / 360 * scale;

        double siny = Math.sin(Math.toRadians(lat));
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        double y = (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)) * scale;

        return new Point((int) x, (int) y);
    }

    public static Point convertToPixels(
            double centerLatitude, double centerLongitude,
            double targetLatitude, double targetLongitude,
            int zoom, int mapWidth, int mapHeight
    ) {
        Point centerPx = latLngToPoint(centerLatitude, centerLongitude, zoom);
        Point targetPx = latLngToPoint(targetLatitude, targetLongitude, zoom);

        int dx = targetPx.x - centerPx.x;
        int dy = targetPx.y - centerPx.y;

        int x = mapWidth / 2 + dx;
        int y = mapHeight / 2 + dy;

        return new Point(x, y);
    }
}
