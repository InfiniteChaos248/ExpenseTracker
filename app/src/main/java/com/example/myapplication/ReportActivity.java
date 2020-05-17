package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private TableLayout table;
    TableRow.LayoutParams tableRowParams;

    private DatabaseHelper db;

    private Map<Integer, String> categoryNames = new HashMap<>();
    private Map<Integer, String> walletNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

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
        for(Wallet wallet : wallets) {
            walletNames.put(wallet.getId(), wallet.getName());
        }

    }

    public void generateReport() {

        table.removeAllViews();

        List<String> columnNames = new ArrayList<>();
        columnNames.add("Date");columnNames.add("Wallet");columnNames.add("Amount");columnNames.add("Category");columnNames.add("Comments");

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

        List<ActivityLog> logs = db.fetchLogs(false);
        for(ActivityLog log : logs) {

            TableRow row = new TableRow(getApplicationContext());

            for(String columnName : columnNames){
                LinearLayout cell = new LinearLayout(this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(tableRowParams);
                if(columnName.equals("Date")) {
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast toast=Toast. makeText(getApplicationContext(),"Change log date feature coming soon",Toast. LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
                String cellContent;
                switch (columnName){
                    case "Date": {cellContent = log.getLogDate(); break;}
                    case "Time": {cellContent = log.getLogTime(); break;}
                    case "Wallet": {cellContent = walletNames.get(log.getWallet()); break;}
                    case "Amount": {cellContent = Float.toString(log.getAmount()); break;}
                    case "Category": {cellContent = categoryNames.get(log.getCategory()); break;}
                    case "Comments": {cellContent = log.getComments(); break;}
                    default: {cellContent = ""; break;}
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
