package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button expenseButton;
    private Button incomeButton;
    private Button categoryButton;
    private Button reportButton;
    private Button walletButton;
    private Button transferButton;
    private Button viewWalletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent manager = new Intent(getApplicationContext(),AndroidDatabaseManager.class);
                startActivity(manager);
            }
        });

        TextView api = findViewById(R.id.trigger);
        api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
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
                                if(!path.exists())
                                {
                                    // Make it, if it doesn't exit
                                    path.mkdirs();
                                }

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = new Date();
                                String fileName = "data-" + dateFormat.format(date);
                                String fileExtension = ".txt";


                                File file = new File(path, fileName + fileExtension);
                                int i = 1;
                                while(file.exists()) {
                                    file = new File(path, fileName + "-" + i++ + fileExtension);
                                }

                                // Save your stream, don't forget to flush() it before closing it.

                                try
                                {
                                    file.createNewFile();
                                    FileOutputStream fOut = new FileOutputStream(file);
                                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                                    myOutWriter.append(json);

                                    myOutWriter.close();

                                    fOut.flush();
                                    fOut.close();
                                }
                                catch (IOException e)
                                {
                                    Log.e("Exception", "File write failed: " + e.toString());
                                }
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

        TextView clear = findViewById(R.id.dbclear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
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
