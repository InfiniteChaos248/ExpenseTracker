package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RadioGroup radioGroup;

    private Spinner categorySpinner;
    ArrayAdapter<Category> categoryAdapter;

    private EditText newCategoryTextView;

    private Button okButton;
    private Button cancelButton;

    private Boolean isExpense;
    private String category;
    private Integer categoryId;

    private DatabaseHelper db;

    private List<Category> incomeCategories = new ArrayList<>();
    private List<Category> expenseCategories = new ArrayList<>();

    private void setAdapter(List<Category> categories) {
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        db = new DatabaseHelper(this);
        refreshCategories();

        categorySpinner = findViewById(R.id.category_spinner);

        newCategoryTextView = findViewById(R.id.new_category);

        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.income_radio){
                    isExpense = false;
                    setAdapter(incomeCategories);
                } else if(i == R.id.expense_radio){
                    isExpense = true;
                    setAdapter(expenseCategories);
                }
            }
        });
        radioGroup.check(R.id.income_radio);

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

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newCategoryTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)){
                    String newName = newCategoryTextView.getText().toString();
                    Integer type = isExpense ? Constants.CATEGORY_TYPE_EXPENSE : Constants.CATEGORY_TYPE_INCOME;
                    String toastText;
                    if(category.equalsIgnoreCase(Constants.EMPTY_STRING)){
                        toastText = "Adding new category : ";
                        db.insertNewCategory(newName, type);
                        refreshCategories();
                        setAdapter(isExpense ? expenseCategories : incomeCategories);
                    } else {
                        toastText = "Modifying " + category + " into ";
                        db.updateCategoryName(categoryId, newName);
                        refreshCategories();
                        setAdapter(isExpense ? expenseCategories : incomeCategories);
                    }
                    toastText += newName;
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
