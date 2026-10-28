package com.example.mydb;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etStudentName, etStudentID;
    TextView tvAgeLabel;
    SeekBar sbStudentAge;
    Button btnAdd, btnEdit, btnDelete, btnView;

    DatabaseHelper dbHelper;
    int selectedAge = 17; // Nilai default minimal 17

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Database Helper
        dbHelper = new DatabaseHelper(this);

        // Inisialisasi Komponen UI
        etStudentName = findViewById(R.id.etStudentName);
        etStudentID = findViewById(R.id.etStudentID);
        tvAgeLabel = findViewById(R.id.tvAgeLabel);
        sbStudentAge = findViewById(R.id.sbStudentAge);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);

        // Perhitungan SeekBar Umur (17 - 50)
        sbStudentAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedAge = progress + 17;
                tvAgeLabel.setText("Student Age: " + selectedAge);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Event Tombol ADD
        btnAdd.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String id = etStudentID.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id)) {
                Toast.makeText(MainActivity.this, "Isi Nama dan ID terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = dbHelper.addStudent(id, name, selectedAge);
                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(MainActivity.this, "Gagal Tambah Data (ID Sudah Ada)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Event Tombol EDIT
        btnEdit.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String id = etStudentID.getText().toString().trim();

            if (TextUtils.isEmpty(id)) {
                Toast.makeText(MainActivity.this, "Masukkan Student ID yang ingin diubah", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUpdated = dbHelper.editStudent(id, name, selectedAge);
                if (isUpdated) {
                    Toast.makeText(MainActivity.this, "Data Berhasil Diperbarui", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(MainActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Event Tombol DELETE
        btnDelete.setOnClickListener(v -> {
            String id = etStudentID.getText().toString().trim();

            if (TextUtils.isEmpty(id)) {
                Toast.makeText(MainActivity.this, "Masukkan Student ID yang ingin dihapus", Toast.LENGTH_SHORT).show();
            } else {
                boolean isDeleted = dbHelper.deleteStudent(id);
                if (isDeleted) {
                    Toast.makeText(MainActivity.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(MainActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Event Tombol VIEW
        btnView.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllStudents();
            if (cursor.getCount() == 0) {
                showMessage("Data Mahasiswa", "Tidak ada data tersimpan.");
                return;
            }

            StringBuilder buffer = new StringBuilder();
            while (cursor.moveToNext()) {
                buffer.append("ID: ").append(cursor.getString(0)).append("\n");
                buffer.append("Nama: ").append(cursor.getString(1)).append("\n");
                buffer.append("Umur: ").append(cursor.getInt(2)).append("\n\n");
            }

            showMessage("Data Mahasiswa", buffer.toString());
        });
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void clearFields() {
        etStudentName.setText("");
        etStudentID.setText("");
        sbStudentAge.setProgress(0);
    }
}