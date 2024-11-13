//package com.example.mapbox;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.mapbox.database.AppDatabase;
//
//public class EditProfileActivity extends AppCompatActivity {
//    private EditText nameEditText;
//    private ImageView profileImageView;
//    private Button saveButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);
//
//        nameEditText = findViewById(R.id.nameEditText);
//        profileImageView = findViewById(R.id.profileImageView);
//        saveButton = findViewById(R.id.saveButton);
//
//        saveButton.setOnClickListener(v -> saveProfileChanges());
//    }
//
//    private void saveProfileChanges() {
//        String name = nameEditText.getText().toString();
//        int points = 150; // Приклад значення
//        String level = "Новачок"; // Приклад рівня
//
//
//        AppDatabase db = AppDatabase.getInstance(this);
//        db.saveUserProfile(name, points, level); // Збереження змін в базі даних
//
//        // Повертаємо результат в UserProfileActivity
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("updated_name", name);
//        resultIntent.putExtra("updated_points", points);
//        resultIntent.putExtra("updated_level", level);
//        setResult(RESULT_OK, resultIntent);
//
//        Toast.makeText(this, "Зміни збережено успішно!", Toast.LENGTH_SHORT).show();
//        finish(); // Закриваємо EditProfileActivity
//    }
//
//}
//
//
//


package com.example.mapbox;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapbox.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private Button saveButton, loadButton;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton); // Кнопка для завантаження зображень

        saveButton.setOnClickListener(v -> saveProfileChanges());

        loadButton.setOnClickListener(view -> showImageSelectionDialog());
    }

    // Показуємо діалогове вікно з вибором зображення
    private void showImageSelectionDialog() {
        // Отримуємо всі імена ресурсів з папки drawable
        List<String> imageNames = getDrawableImageNames();

        // Створюємо адаптер для GridView
        ImageAdapter imageAdapter = new ImageAdapter(this, imageNames);

        // Створюємо GridView в AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вибір зображення");
        builder.setAdapter(imageAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                // Отримуємо вибране зображення
                String selectedImage = imageNames.get(position);
                setProfileImageFromDrawable(selectedImage);
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // Отримуємо всі імена зображень з папки drawable
    private List<String> getDrawableImageNames() {
        List<String> imageNames = new ArrayList<>();
        // Тут перелічуємо всі зображення в drawable, наприклад:
        imageNames.add("ic_profile_placeholder"); // Назва зображення в drawable
        imageNames.add("location_pin");
        imageNames.add("litter_area"); // Назва зображення в drawable
        imageNames.add("clear_area");        // Інші зображення// Інші зображення
        // Ви можете додавати більше зображень сюди або отримувати їх за допомогою ресурсів.
        return imageNames;
    }

    // Завантажуємо зображення з drawable за ім'ям
    private void setProfileImageFromDrawable(String imageName) {
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (resId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            profileImageView.setImageBitmap(bitmap);
            Toast.makeText(this, "Вибрано зображення: " + imageName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Не вдалося знайти зображення: " + imageName, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        String name = nameEditText.getText().toString();
        int points = 100; // Приклад значення
        String level = "Новачок"; // Приклад рівня

        AppDatabase db = AppDatabase.getInstance(this);
        db.saveUserProfile(name, points, level); // Збереження змін в базі даних

        // Повертаємо результат в UserProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_name", name);
        resultIntent.putExtra("updated_points", points);
        resultIntent.putExtra("updated_level", level);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Зміни збережено успішно!", Toast.LENGTH_SHORT).show();
        finish(); // Закриваємо EditProfileActivity
    }
}

