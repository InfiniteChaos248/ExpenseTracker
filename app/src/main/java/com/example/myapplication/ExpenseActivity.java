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

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private EditText amountTextView;
    private EditText descriptionTextView;

    private Spinner categorySpinner;
    ArrayAdapter<Category> categoryAdapter;
    private Spinner walletSpinner;
    private ArrayAdapter<Wallet> walletAdapter;

    private Button okButton;
    private Button cancelButton;

    private String category;
    private Integer categoryId;
    private String wallet;
    private Integer walletId;
    private Integer amount;
    private String description;

    private DatabaseHelper db;

    private List<Wallet> walletList = new ArrayList<>();
    private List<Category> incomeCategories = new ArrayList<>();
    private List<Category> expenseCategories = new ArrayList<>();

    private void refreshWalletList() {
        walletList = new ArrayList<>();
        walletList.add(new Wallet(Constants.EMPTY_STRING, Constants.ZERO));
        walletList.addAll(db.getAllWallets());
    }

    private void refreshCategories() {
        List<Category> allCategories = db.getAllCategories();
        incomeCategories = new ArrayList<>();
        expenseCategories = new ArrayList<>();
        incomeCategories.add(new Category(Constants.EMPTY_STRING, Constants.CATEGORY_TYPE_INCOME));
        expenseCategories.add(new Category(Constants.EMPTY_STRING, Constants.CATEGORY_TYPE_EXPENSE));
        for(Category category : allCategories){
            if(category.getType() == Constants.CATEGORY_TYPE_INCOME){
                incomeCategories.add(category);
            } else if (category.getType() == Constants.CATEGORY_TYPE_EXPENSE) {
                expenseCategories.add(category);
            }
        }
    }

    private void refreshCategoryAdapter(List<Category> categories) {
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void refreshWalletAdapter(){
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        final Boolean isExpense = getIntent().getBooleanExtra("expense", true);

        db = new DatabaseHelper(this);
        refreshWalletList();
        refreshCategories();

        amountTextView = findViewById(R.id.amount);
        descriptionTextView = findViewById(R.id.description);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        categorySpinner = findViewById(R.id.s_category);
        refreshCategoryAdapter(isExpense ? expenseCategories : incomeCategories);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category selectedItem = (Category) adapterView.getItemAtPosition(i);
                category = selectedItem.getName();
                categoryId = selectedItem.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = Constants.EMPTY_STRING;
            }
        });

        walletSpinner = findViewById(R.id.s_wallet);
        refreshWalletAdapter();

        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Wallet selectedItem = (Wallet) adapterView.getItemAtPosition(i);
                wallet = selectedItem.getName();
                walletId = selectedItem.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                wallet = Constants.EMPTY_STRING;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a wallet", Toast.LENGTH_SHORT).show();
                } else if(category.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_SHORT).show();
                } else if (amountTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else{
                    amount = Integer.parseInt(amountTextView.getText().toString());
                    description = descriptionTextView.getText().toString();
                    String toastString = "Wallet: " + wallet + "\nCategory: " + category + "\nAmount: " + amount + " Rs\n" + "Description: " + description;
                    Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                    String responseMessage;
                    if(isExpense){
                        responseMessage = db.newExpense(walletId, categoryId, amount, description);
                        Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                    } else{
                        responseMessage = db.newIncome(walletId, categoryId, amount, description);
                        Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                    }
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
