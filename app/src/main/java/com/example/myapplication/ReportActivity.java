package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private TableLayout table;
    TableRow.LayoutParams tableRowParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        table = findViewById(R.id.report);
        table.setStretchAllColumns(true);
        table.setHorizontalScrollBarEnabled(true);
        table.setVerticalScrollBarEnabled(true);

        generateReport();

    }

    public void generateReport() {

        table.removeAllViews();

        List<String> columnNames = new ArrayList<>();
        columnNames.add("Date");columnNames.add("Time");columnNames.add("Wallet");columnNames.add("Amount");columnNames.add("Category");columnNames.add("Comments");

        tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 0, 2, 0);

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.setBackgroundColor(Color.BLACK);
        headerRow.setPadding(0, 2, 0, 2);

        for(String columnName : columnNames){
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

    }

}
