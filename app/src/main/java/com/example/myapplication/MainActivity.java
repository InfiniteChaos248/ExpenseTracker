package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.exception.AppException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button expenseButton;
    private Button incomeButton;
    private Button categoryButton;
    private Button reportButton;
    private Button walletButton;
    private Button transferButton;
    private Button viewWalletButton;

    private static final int FILE_SELECT_CODE = 0;
    private static final int STORAGE_PERMISSION_CODE = 101;

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            JsonObject json = null;
            try {
                json = new Gson().fromJson(new InputStreamReader(getContentResolver().openInputStream(uri)), JsonObject.class);
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this,
                        "File not found",
                        Toast.LENGTH_SHORT)
                        .show();
            } catch (JsonSyntaxException e) {
                Toast.makeText(MainActivity.this,
                        "Invalid JSON content",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            if (json != null) {
                Log.i("MainActivity", "importing JSON => " + json.toString());
                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                try {
                    helper.importDatafromJSON(json);
                    Toast.makeText(MainActivity.this,
                            "JSON import successful",
                            Toast.LENGTH_SHORT)
                            .show();
                } catch (AppException e) {
                    Toast.makeText(MainActivity.this,
                            "JSON import unsuccessful : " + e.getErrorMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        expenseButton = findViewById(R.id.expense);
        incomeButton = findViewById(R.id.income);
        categoryButton = findViewById(R.id.category);
        reportButton = findViewById(R.id.report);
        walletButton = findViewById(R.id.wallet_button);
        transferButton = findViewById(R.id.transfer_button);
        viewWalletButton = findViewById(R.id.view_button);

        TextView db = findViewById(R.id.db_manager);
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(manager);
            }
        });


        TextView exportJSONTextView = findViewById(R.id.exportJSON);
        exportJSONTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //YES button clicked
                                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                                String json = helper.saveDbAsJson();
                                Log.i("API", "fetched db data");
                                final File path =
                                        Environment.getExternalStoragePublicDirectory
                                                (
                                                        //Environment.DIRECTORY_PICTURES
                                                        Environment.DIRECTORY_DCIM + "/ExpenseTracker/"
                                                );

                                // Make sure the path directory exists.
                                if (!path.exists()) {
                                    // Make it, if it doesn't exit
                                    path.mkdirs();
                                }

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = new Date();
                                String fileName = "data-" + dateFormat.format(date);
                                String fileExtension = ".json";


                                File file = new File(path, fileName + fileExtension);
                                int i = 1;
                                while (file.exists()) {
                                    file = new File(path, fileName + "-" + i++ + fileExtension);
                                }

                                // Save your stream, don't forget to flush() it before closing it.

                                try {
                                    file.createNewFile();
                                    FileOutputStream fOut = new FileOutputStream(file);
                                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                                    myOutWriter.append(json);

                                    myOutWriter.close();

                                    fOut.flush();
                                    fOut.close();
                                } catch (IOException e) {
                                    Log.e("Exception", "File write failed: " + e.toString());
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //NO button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        TextView importJSONTextView = findViewById(R.id.importJSON);
        importJSONTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //YES button clicked
                                Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                chooseFileIntent.setType("application/json");
                                startActivityForResult(Intent.createChooser(chooseFileIntent, "Select the JSON file to import"), FILE_SELECT_CODE);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //NO button clicked
                                break;
                        }

                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage("This will overwrite existing data, are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        TextView clear = findViewById(R.id.dbclear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                                helper.clearDatabase();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent = new Intent(getApplicationContext(), ExpenseActivity.class);
                expenseIntent.putExtra("expense", true);
                startActivity(expenseIntent);
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent incomeIntent = new Intent(getApplicationContext(), ExpenseActivity.class);
                incomeIntent.putExtra("expense", false);
                startActivity(incomeIntent);
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });

        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(getApplicationContext(), WalletActivity.class);
                startActivity(categoryIntent);
            }
        });

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transferIntent = new Intent(getApplicationContext(), TransferActivity.class);
                startActivity(transferIntent);
            }
        });

        viewWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewWalletIntent = new Intent(getApplicationContext(), WalletViewActivity.class);
                startActivity(viewWalletIntent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportIntent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(reportIntent);
            }
        });

    }
}
