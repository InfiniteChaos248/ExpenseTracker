package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private EditText amountTextView;
    private EditText descriptionTextView;

    private Spinner categorySpinner;
    private Spinner walletSpinner;

    private Button okButton;
    private Button cancelButton;

    private String category;
    private String wallet;
    private Integer amount;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Boolean isExpense = getIntent().getBooleanExtra("expense", true);

        final List<String> expenseCategories = new ArrayList<>();
        expenseCategories.add("");
        expenseCategories.add("Food");
        expenseCategories.add("Entertainment");
        expenseCategories.add("Petrol");
        expenseCategories.add("Groceries");

        final List<String> incomeCategories = new ArrayList<>();
        incomeCategories.add("");
        incomeCategories.add("Salary");
        incomeCategories.add("Friends");
        incomeCategories.add("Family");

        final List<String> walletCategories = new ArrayList<>();
        walletCategories.add("");
        walletCategories.add("Bank account : ICICI");
        walletCategories.add("Cash");
        walletCategories.add("Infosys smart card");

        final List<String> categories = isExpense ? expenseCategories : incomeCategories;

        amountTextView = findViewById(R.id.amount);
        descriptionTextView = findViewById(R.id.description);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        categorySpinner = findViewById(R.id.s_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                category = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = "";
            }
        });

        walletSpinner = findViewById(R.id.s_wallet);
        ArrayAdapter<String> walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletCategories);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);

        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                wallet = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                wallet = "";
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet == "") {
                    Toast.makeText(getApplicationContext(), "Select a wallet", Toast.LENGTH_SHORT).show();
                } else if(category == "") {
                    Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_SHORT).show();
                } else if (amountTextView.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else{
                    amount = Integer.parseInt(amountTextView.getText().toString());
                    description = descriptionTextView.getText().toString();
                    String toastString = "Wallet: " + wallet + "\nCategory: " + category + "\nAmount: " + amount + " Rs\n" + "Description: " + description;
                    Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
