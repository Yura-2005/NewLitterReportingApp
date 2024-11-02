package com.example.mapbox;

import com.example.mapbox.database.AppDatabase;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import android.database.Cursor;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private FloatingActionButton floatingActionButton;
    private PointAnnotationManager pointAnnotationManager;
    private AppDatabase appDatabase; // Ініціалізація бази даних

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
            // Порожній метод для завершення переміщення
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація бази даних
        appDatabase = AppDatabase.getInstance(this);

        mapView = findViewById(R.id.mapView);
        floatingActionButton = findViewById(R.id.focusLocation);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        floatingActionButton.hide();
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);
                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(AppCompatResources.getDrawable(MainActivity.this, R.drawable.baseline_location_on_24));
                locationComponentPlugin.setLocationPuck(locationPuck2D);
                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListener);

                // Ініціалізація PointAnnotationManager
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                // Додавання слухача натиску на карту
                addOnMapClickListener(mapView.getMapboxMap(), new com.mapbox.maps.plugin.gestures.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        showInputDialog(point); // Виклик методу для введення тексту
                        return true; // Повертаємо true, щоб позначити, що подію оброблено
                    }
                });

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        floatingActionButton.hide();
                    }
                });

                loadAnnotations();
            }
        });
    }

    private void loadAnnotations() {
        Cursor cursor = appDatabase.getData(); // Отримуємо курсор з даними з бази даних
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int latitudeIndex = cursor.getColumnIndex(AppDatabase.COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(AppDatabase.COLUMN_LONGITUDE);
                int textIndex = cursor.getColumnIndex(AppDatabase.COLUMN_TEXT);

                // Перевіряємо, що індекси знайдені
                if (latitudeIndex != -1 && longitudeIndex != -1 && textIndex != -1) {
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    String annotationText = cursor.getString(textIndex);

                    // Додаємо мітку на карту
                    Point point = Point.fromLngLat(longitude, latitude);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_pin);

                    if (bitmap != null) { // Переконайтесь, що bitmap завантажений
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withTextField(annotationText)
                                .withTextAnchor(TextAnchor.CENTER)
                                .withIconImage(bitmap)
                                .withPoint(point);

                        pointAnnotationManager.create(pointAnnotationOptions); // Додаємо мітку на карту
                    } else {
                        Log.e("Bitmap Error", "Bitmap for location pin is null");
                    }
                } else {
                    Log.e("Cursor Error", "One or more columns not found in the cursor");
                }
            }
            cursor.close(); // Закриваємо курсор
        }
    }


    private void showInputDialog(Point point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введіть текст для мітки");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Додати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String annotationText = input.getText().toString(); // Отримання тексту
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_pin); // Ваше зображення мітки

                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextField(annotationText) // Додаємо текст
                        .withTextAnchor(TextAnchor.CENTER)
                        .withIconImage(bitmap)
                        .withPoint(point);

                pointAnnotationManager.create(pointAnnotationOptions);

                // Збереження координат та тексту в базу даних
                appDatabase.insertData(point.latitude(), point.longitude(), annotationText);
            }
        });

        builder.setNegativeButton("Скасувати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
