package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
