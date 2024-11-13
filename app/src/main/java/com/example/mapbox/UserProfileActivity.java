package com.example.mapbox;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapbox.database.AppDatabase;

public class UserProfileActivity extends AppCompatActivity {
    private TextView nameTextView, trashCountTextView, levelTextView, pointsTextView;
    private ImageView profileImageView;
    private EditText trashKgEditText; // Поле введення для введення кількості сміття в кг
    private Button editProfileButton, redeemPointsButton, collectTrashButton;
    private Switch achievementsSwitch, promotionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ініціалізація елементів інтерфейсу
        nameTextView = findViewById(R.id.nameTextView);
        trashCountTextView = findViewById(R.id.trashCountTextView);
        levelTextView = findViewById(R.id.levelTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        profileImageView = findViewById(R.id.profileImageView);
        trashKgEditText = findViewById(R.id.trashKgEditText); // Новий EditText для кількості сміття
        editProfileButton = findViewById(R.id.editProfileButton);
        redeemPointsButton = findViewById(R.id.redeemPointsButton);
        collectTrashButton = findViewById(R.id.collectTrashButton); // Нова кнопка для збирання сміття
        achievementsSwitch = findViewById(R.id.achievementsSwitch);
        promotionSwitch = findViewById(R.id.promotionSwitch);

        // Завантаження даних користувача
        loadUserProfile();


        // Обробник кнопки "Редагувати профіль"
        editProfileButton.setOnClickListener(v -> openEditProfile());

        redeemPointsButton.setOnClickListener(v -> redeemPoints());

        // Обробник кнопки "Зібрати сміття"
        collectTrashButton.setOnClickListener(v -> collectTrash());
    }

    private void loadUserProfile() {
        AppDatabase db = AppDatabase.getInstance(this);
        Cursor cursor = db.getUserProfile();

        if (cursor != null && cursor.moveToFirst()) {
            // Отримуємо індекси колонок
            int nameIndex = cursor.getColumnIndex(AppDatabase.COLUMN_NAME);
            int pointsIndex = cursor.getColumnIndex(AppDatabase.COLUMN_POINTS);
            int levelIndex = cursor.getColumnIndex(AppDatabase.COLUMN_LEVEL);

            // Перевірка, чи колонки існують
            if (nameIndex != -1 && pointsIndex != -1 && levelIndex != -1) {
                String name = cursor.getString(nameIndex);
                int points = cursor.getInt(pointsIndex);
                String level = cursor.getString(levelIndex);

                nameTextView.setText(name);
                pointsTextView.setText(String.valueOf(points));
                levelTextView.setText(level);
            } else {
                // Якщо колонка не знайдена, повідомляємо про це
                Toast.makeText(this, "Помилка: деякі колонки відсутні в базі даних.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Помилка: не вдалося завантажити профіль користувача.", Toast.LENGTH_LONG).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    private void openEditProfile() {
        Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
        startActivityForResult(intent, 1); // Код запиту 1
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Отримуємо оновлені дані
            String updatedName = data.getStringExtra("updated_name");
            int updatedPoints = data.getIntExtra("updated_points", 0);
            String updatedLevel = data.getStringExtra("updated_level");

            // Оновлюємо інтерфейс в UserProfileActivity
            nameTextView.setText(updatedName);
            pointsTextView.setText(String.valueOf(updatedPoints));
            levelTextView.setText(updatedLevel);

            // Виводимо повідомлення про успішне оновлення
            Toast.makeText(this, "Профіль оновлено!", Toast.LENGTH_SHORT).show();
        }
    }


    private void redeemPoints() {
        Toast.makeText(this, "Бали обміняно успішно!", Toast.LENGTH_SHORT).show();
    }

    public void onBackToMapClicked(View view) {
        finish();
    }

    // Метод для збирання сміття
    private void collectTrash() {
        String trashKgText = trashKgEditText.getText().toString();

        if (!trashKgText.isEmpty()) {
            try {
                int trashKg = Integer.parseInt(trashKgText);
                int earnedPoints = trashKg * 10; // Конвертація: 1 кг = 10 балів

                // Оновлення балів у базі даних
                AppDatabase db = AppDatabase.getInstance(this);
                db.addUserPoints(earnedPoints, this); // Передача контексту

                // Оновлення інтерфейсу
                int currentPoints = Integer.parseInt(pointsTextView.getText().toString());
                pointsTextView.setText(String.valueOf(currentPoints + earnedPoints));
                Toast.makeText(this, "Зібрано " + trashKg + " кг сміття! Отримано " + earnedPoints + " балів.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введене значення не є числом. Будь ласка, введіть коректне число.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Будь ласка, введіть кількість сміття в кг.", Toast.LENGTH_SHORT).show();
        }
    }

}