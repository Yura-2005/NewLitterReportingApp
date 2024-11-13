package com.example.mapbox;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageNames;
    public ImageAdapter(Context context, List<String> imageNames) {
        this.context = context;
        this.imageNames = imageNames;
    }
    @Override
    public int getCount() {
        return imageNames.size();    }
    @Override
    public Object getItem(int position) {
        return imageNames.get(position);    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);            imageView = new ImageView(context);
            convertView = imageView;        } else {
            imageView = (ImageView) convertView;
        }
        // Завантажуємо зображення з drawable
        String imageName = imageNames.get(position);
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        imageView.setImageBitmap(bitmap);
        return convertView;    }
}