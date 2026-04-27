package com.garfild63.zanovo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

public class DescriptionActivity extends Activity {
    private ImageView routeIcon;
    private TextView routeName, kilometrage, difficulty, time, description, equipment, season;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        getActionBar().hide();

        String name = getIntent().getStringExtra(MainActivity.ROUTE_NAME);
        String file = getIntent().getStringExtra(MainActivity.ROUTE_FILE);
        String icon = getIntent().getStringExtra(MainActivity.ROUTE_ICON);
        routeIcon = findViewById(R.id.routeIcon);
        routeName = findViewById(R.id.routeName);
        kilometrage = findViewById(R.id.kilometrage);
        difficulty = findViewById(R.id.difficulty);
        time = findViewById(R.id.time);
        description = findViewById(R.id.description);
        equipment = findViewById(R.id.equipment);
        season = findViewById(R.id.season);
        startButton = findViewById(R.id.startButton);

        try {
            routeIcon.setImageDrawable(Drawable.createFromStream(
                    getAssets().open(icon), null));
        } catch (IOException e) {
        }
        routeName.setText(name);

        try {
            CSVParser.parse(file, new CSVParser.Preparator() {
                @Override
                public void endLine(List<String> args) {
                    if (args.size() == 6) {
                        kilometrage.setText(args.get(0));
                        difficulty.setText(getDifficulty(args.get(1)));
                        time.setText(args.get(2));
                        description.setText(args.get(3));
                        equipment.setText(args.get(4));
                        season.setText(args.get(5));
                    }
                }
                @Override
                public char prepareChar(char ch, int x) {
                    return ch;
                }
            }, true);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, MapActivity.class);
                intent.putExtra(MainActivity.ROUTE_NAME, name);
                intent.putExtra(MainActivity.ROUTE_FILE, file);
                startActivity(intent);
            }
        });
    }

    private String getDifficulty(String number) {
        int n;
        try {
            n = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            n = 0;
        }
        if (n < 0) {
            n = 0;
        }
        if (n > 5) {
            n = 5;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("\uD83D\uDD34");
        }
        for (int i = 0; i < 5 - n; i++) {
            sb.append("⚪");
        }
        return sb.toString();
    }
}
