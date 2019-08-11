package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton typeRadioButton;

    private Spinner categorySpinner;
    ArrayAdapter<String> categoryAdapter;

    private EditText newCategoryTextView;

    private Button okButton;
    private Button cancelButton;

    private List<String> categories;
    private Boolean isExpense;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

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

        categorySpinner = findViewById(R.id.category_spinner);

        newCategoryTextView = findViewById(R.id.new_category);

        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.income_radio){
                    isExpense = false;
                    categories = incomeCategories;
                    categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                    categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);
                } else if(i == R.id.expense_radio){
                    isExpense = true;
                    categories = expenseCategories;
                    categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                    categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);
                }
            }
        });
        radioGroup.check(R.id.income_radio);

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

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newCategoryTextView.getText().toString().equalsIgnoreCase("")){
                    String toastText;
                    if(category.equalsIgnoreCase("")){
                        toastText = "Adding new category : ";
                    } else {
                        toastText = "Modifying " + category + " into ";
                    }
                    toastText += newCategoryTextView.getText().toString();
                    toastText += " to " + (isExpense ? ("expense") : ("income"));
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Type new category name", Toast.LENGTH_LONG).show();
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
