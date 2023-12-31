package com.example.lecturenotesapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.IllegalFormatWidthException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private ImageView imageView;
    private String currentPhotoPath;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.add_note) {
            Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        if (set == null) {
            notes.add("Example note");
        } else {
            notes = new ArrayList<>(set);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, notes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                intent.putExtra("noteId", i);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int itemToDelete = i;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes.remove(itemToDelete);
                                arrayAdapter.notifyDataSetChanged();
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                                HashSet<String> set = new HashSet<>(MainActivity.notes);
                                sharedPreferences.edit().putStringSet("notes", set).apply();
                            }
                        }).setNegativeButton("No", null).show();
                return true;
            }
        });

        FloatingActionButton addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                startActivity(intent); // Start the new note editor activity
            }
        });

        Button takePictureButton = findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with opening the camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create a file to save the full-quality image
                File imageFile = createImageFile();
                if (imageFile != null) {
                    Uri imageUri = FileProvider.getUriForFile(this, "com.example.myapp.fileprovider", imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // Save the image to this file
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private File createImageFile() {
        String imageFileName = "lecture_image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);
        // Save the file path for later use
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Load the full-quality image from the saved file
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);

            if (imageBitmap != null) {
                // Display the full-quality image in an ImageView
                ImageView imageView = findViewById(R.id.noteImageView);
                imageView.setImageBitmap(imageBitmap);
                imageView.setVisibility(View.VISIBLE);
            } else {
                Log.e("ImageCapture", "ImageBitmap is null");
            }
        }
    }
}


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//
//            if (imageBitmap != null) {
//                // You can save the image to internal storage
//                String imageFileName = "lecture_image_" + System.currentTimeMillis() + ".jpg";
//                saveImageToInternalStorage(imageBitmap, imageFileName);
//
//                // Display the image in an ImageView
//                ImageView imageView = findViewById(R.id.noteImageView);
//                imageView.setImageBitmap(imageBitmap);
//                imageView.setVisibility(View.VISIBLE);
//            } else {
//                Log.e("ImageCapture", "ImageBitmap is null");
//            }
//        }
//    }
//
//
//    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
//        try {
//            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

//        Button takePictureButton = findViewById(R.id.takePictureButton);
//        takePictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCamera();
//            }
//        });
//    }
//
//    private void openCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            // Permission is granted, proceed with opening the camera
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//            }
//        } else {
//            // Permission is not granted, request it from the user
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//            Intent intent = new Intent(this, AddPictureByCamera.class);
//            intent.putExtra("imageBitmap", imageBitmap);
//            startActivity(intent);
//        }
//        // Inside onActivityResult of MainActivity
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//            // You can save the image to internal storage
//            String imageFileName = "lecture_image_" + System.currentTimeMillis() + ".jpg";
//            saveImageToInternalStorage(imageBitmap, imageFileName);
//        }
//
//    }
//    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
//        try {
//            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.close();
//            // Optionally, you can also save the file path or other metadata to a list or database.
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//}



    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Convert the image bitmap to a Base64-encoded string
                String imageString = encodeImageToBase64(imageBitmap);

                // Create a note with the Base64-encoded image string
                String imageNote = "Image: " + imageString;
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                String combinedNote = imageNote + "\n\nLast Edited: " + currentDate;
                notes.add(combinedNote);
                arrayAdapter.notifyDataSetChanged();

                // Store the updated notes in SharedPreferences
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet<>(MainActivity.notes);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }
        }
    }

    private String encodeImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }*/


