package com.garfild63.zanovo;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherTask extends AsyncTask<Object, Void, String> {
    private Context context;
    private TextView weather;
    private static final double HPA2MMHG = 0.750062;
    private static final String URL = "https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=1&hours=false";

    @Override
    protected String doInBackground(Object... params) {
        context = (Context) params[0];
        weather = (TextView) params[1];
        double lat = (double) params[2];
        double lng = (double) params[3];
        try {
            URL url = new URL(String.format(URL, lat, lng));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("X-Yandex-Weather-Key", MyApiKeys.YANDEX_WEATHER);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 секунд на установку соединения
            conn.setReadTimeout(5000); // 5 секунд на чтение данных
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject weatherData = new JSONObject(result);
            JSONObject info = weatherData.getJSONObject("info");
            JSONObject fact = weatherData.getJSONObject("fact");
            StringBuilder sb = new StringBuilder();

            String locality = "Новороссийск";
            int temperature = fact.getInt("temp");
            sb.append(String.format(context.getString(R.string.temperature),
                    locality, temperature));

            int temperatureFeels = fact.getInt("feels_like");
            sb.append(String.format(context.getString(R.string.temperature_feels),
                    temperatureFeels));

            int pressureHPa = info.getInt("def_pressure_pa");
            int pressureMmHg = (int) Math.round(pressureHPa * HPA2MMHG);
            sb.append(String.format(context.getString(R.string.pressure),
                    pressureMmHg, pressureHPa));

            int humidity = fact.getInt("humidity");
            sb.append(String.format(context.getString(R.string.humidity),
                    humidity));

            int windSpeed = fact.getInt("wind_speed");
            sb.append(String.format(context.getString(R.string.wind_speed),
                    windSpeed));

            double windDeg = fact.getDouble("wind_angle");
            int direction = (int) Math.round(windDeg / 45) % 8;
            String[] directions = context.getResources().getStringArray(R.array.directions);
            sb.append(String.format(context.getString(R.string.wind_direction),
                    directions[direction]));

            weather.setText(sb);
        } catch (Exception e) {
            weather.setText(result);
        }
    }
}
