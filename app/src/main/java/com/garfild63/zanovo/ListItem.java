package com.garfild63.zanovo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;

public class ListItem extends LinearLayout {
    public ListItem(Context context, String image, int width, int height,
                    String text, View.OnClickListener onClickListener) {
        super(context);
        float density = getResources().getDisplayMetrics().density;

        setOrientation(LinearLayout.VERTICAL);
        int padding = Math.round(getResources().getDimension(R.dimen.padding));
        setLayoutParams(new ViewGroup.LayoutParams(
                Math.round(width * density + padding), ViewGroup.LayoutParams.WRAP_CONTENT));
        setPadding(0, padding, padding, 0);
        setOnClickListener(onClickListener);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                Math.round(width * density), Math.round(height * density)));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            imageView.setImageDrawable(Drawable.createFromStream(
                    context.getAssets().open(image), null));
        } catch (IOException e) {
        }
        addView(imageView);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(getResources().getDimension(R.dimen.small_text) / density);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setTypeface(getResources().getFont(R.font.centurygothic_bold));
        }
        textView.setTextColor(getResources().getColor(R.color.blue));
        textView.setText(text);
        addView(textView);
    }
}
