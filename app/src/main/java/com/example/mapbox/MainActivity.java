package com.example.mapbox;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                ViewAnnotationManager viewAnnotationManager = mapView.getViewAnnotationManager();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_pin);

                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                                .withPoint(point);
                        pointAnnotationManager.create(pointAnnotationOptions);

                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                            @Override
                            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                                ViewAnnotationOptions options = new ViewAnnotationOptions.Builder().geometry(pointAnnotation.getPoint()).build();
                                View viewAnnotation = viewAnnotationManager.addViewAnnotation(R.layout.test_layout, options);

                                viewAnnotation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(MainActivity.this, "View Clicked!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        return false;
                    }
                });
            }
        });
    }
}