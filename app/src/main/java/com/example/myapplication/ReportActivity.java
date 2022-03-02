package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.model.ActivityLog;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.database.model.Wallet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private static final int UPDATE_DELETE_ACTIVITY_CODE = 100;

    private TableLayout table;
    TableRow.LayoutParams tableRowParams;

    private DatabaseHelper db;

    private Map<Integer, String> categoryNames = new HashMap<>();
    private Map<Integer, String> walletNames = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DELETE_ACTIVITY_CODE) {
            recreate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().setTitle("Report");

        db = new DatabaseHelper(this);

        table = findViewById(R.id.report);
        table.setStretchAllColumns(true);
        table.setHorizontalScrollBarEnabled(true);
        table.setVerticalScrollBarEnabled(true);
        setMaps();
        generateReport();

    }

    public void setMaps() {

        List<Category> categories = db.getAllCategories(false);
        List<Wallet> wallets = db.getAllWallets(false);
        for (Category category : categories) {
            categoryNames.put(category.getId(), category.getName());
        }
        for (Wallet wallet : wallets) {
            walletNames.put(wallet.getId(), wallet.getName());
        }

    }

    public void generateReport() {

        table.removeAllViews();

        List<String> columnNames = new ArrayList<>();
        columnNames.add("Date");
        columnNames.add("Wallet");
        columnNames.add("Amount");
        columnNames.add("Category");
        columnNames.add("Comments");

        tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 0, 2, 0);

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.setBackgroundColor(Color.BLACK);
        headerRow.setPadding(4, 4, 4, 4);

        for (String columnName : columnNames) {
            LinearLayout cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.WHITE);
            cell.setLayoutParams(tableRowParams);

            TextView headerRowColumns = new TextView(getApplicationContext());
            headerRowColumns.setPadding(0, 0, 4, 3);
            headerRowColumns.setText(columnName);
            headerRowColumns.setTextColor(Color.parseColor("#000000"));

            cell.addView(headerRowColumns);
            headerRow.addView(cell);
        }

        table.addView(headerRow);

        List<ActivityLog> logs = db.fetchLogs(false);
        Collections.sort(logs, Comparator.comparing(ActivityLog::getLogDate).thenComparing(ActivityLog::getLogTime));
        for (ActivityLog log : logs) {

            TableRow row = new TableRow(getApplicationContext());
            row.setBackgroundColor(Color.BLACK);
            row.setPadding(4, 4, 4, 4);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //'update/delete' button clicked
                                    Intent updateDeleteIntent = new Intent(getApplicationContext(), UpdateDeleteActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("activity_log", log);
                                    updateDeleteIntent.putExtras(bundle);
                                    startActivityForResult(updateDeleteIntent, UPDATE_DELETE_ACTIVITY_CODE);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //'cancel' button clicked
                                    break;
                            }
                        }
                    };

                    String operation = "";
                    if (log.getType() == 1) {
                        operation = "expense";
                    } else if (log.getType() == 2) {
                        operation = "income";
                    } else if (log.getType() == 3) {
                        operation = "wallet transfer";
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder
                            .setMessage(operation + " log on date: " + log.getLogDate() + " of amount: " + String.format("%.2f", log.getAmount()) + " with wallet: " + walletNames.get(log.getWallet()))
                            .setPositiveButton("update/delete", dialogClickListener)
                            .setNegativeButton("cancel", dialogClickListener);

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            for (String columnName : columnNames) {
                LinearLayout cell = new LinearLayout(this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(tableRowParams);
                String cellContent;
                switch (columnName) {
                    case "Date": {
                        cellContent = log.getLogDate();
                        break;
                    }
                    case "Time": {
                        cellContent = log.getLogTime();
                        break;
                    }
                    case "Wallet": {
                        cellContent = walletNames.get(log.getWallet());
                        break;
                    }
                    case "Amount": {
                        cellContent = String.format("%.2f", log.getAmount());
                        break;
                    }
                    case "Category": {
                        cellContent = categoryNames.get(log.getCategory());
                        break;
                    }
                    case "Comments": {
                        cellContent = log.getComments();
                        break;
                    }
                    default: {
                        cellContent = "";
                        break;
                    }
                }
                TextView rowColumns = new TextView(getApplicationContext());
                rowColumns.setPadding(0, 0, 4, 3);
                rowColumns.setText(cellContent);
                rowColumns.setTextColor(Color.parseColor("#000000"));

                cell.addView(rowColumns);
                row.addView(cell);
            }

            table.addView(row);

        }

    }

}
