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

    @Override
    protected String doInBackground(Object... params) {
        context = (Context) params[0];
        weather = (TextView) params[1];
        double lat = (double) params[2];
        double lng = (double) params[3];
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                    "&lon=" + lng + "&units=metric&lang=ru&appid=" + MyApiKeys.OPENWEATHERMAP);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
            String locality = weatherData.getString("name");

            JSONObject main = weatherData.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double temperatureFeels = main.getDouble("feels_like");
            int pressureHPa = main.getInt("pressure");
            int pressureMmHg = (int) Math.round(pressureHPa * HPA2MMHG);
            int humidity = main.getInt("humidity");

            JSONObject wind = weatherData.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            double windDeg = wind.getDouble("deg");
            int direction = (int) Math.round(windDeg / 45) % 8;
            String[] directions = context.getResources().getStringArray(R.array.directions);

            weather.setText(context.getString(R.string.temperature) + " " + locality + " " + temperature + "°C\n" +
                    context.getString(R.string.temperature_feels) + " " + temperatureFeels + "°C\n" +
                    context.getString(R.string.pressure) + " " + pressureMmHg + " " + context.getString(R.string.mmhg) +
                    " (" + pressureHPa + " " + context.getString(R.string.hpa) + ")\n" +
                    context.getString(R.string.humidity) + " " + humidity + "%\n" +
                    context.getString(R.string.wind_speed) + " " + windSpeed + " " + context.getString(R.string.mps) + "\n" +
                    context.getString(R.string.wind_direction) + " " + directions[direction]);
        } catch (Exception e) {
            weather.setText(result);
        }
    }
}
