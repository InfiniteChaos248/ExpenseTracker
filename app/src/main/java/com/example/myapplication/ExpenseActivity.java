package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText amountTextView;
    private EditText descriptionTextView;

    private TextView dateTextView;
    private TextView timeTextView;

    private Spinner categorySpinner;
    ArrayAdapter<Category> categoryAdapter;
    private Spinner walletSpinner;
    private ArrayAdapter<Wallet> walletAdapter;

    private Button okButton;
    private Button cancelButton;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);

    private String category;
    private Integer categoryId;
    private String wallet;
    private Integer walletId;
    private Float amount;
    private String description;

    private DatabaseHelper db;

    private BroadcastReceiver timeBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        timeTextView.setText(timeFormat.format(Calendar.getInstance().getTime()));
        timeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timeTextView.setText(timeFormat.format(Calendar.getInstance().getTime()));
            }
        };
        registerReceiver(timeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(timeBroadcastReceiver);
        } catch (Exception e) {
            Log.e("unregister", "receiver already unregistered");
            e.printStackTrace();
        }
    }

    private List<Wallet> walletList = new ArrayList<>();
    private List<Category> incomeCategories = new ArrayList<>();
    private List<Category> expenseCategories = new ArrayList<>();

    private void refreshWalletList() {
        walletList = new ArrayList<>();
        walletList.add(new Wallet(Constants.EMPTY_STRING, Constants.ZERO_F));
        walletList.addAll(db.getAllWallets(true));
    }

    private void refreshCategories() {
        List<Category> allCategories = db.getAllCategories(true);
        incomeCategories = new ArrayList<>();
        expenseCategories = new ArrayList<>();
        incomeCategories.add(new Category(Constants.EMPTY_STRING, Constants.CATEGORY_TYPE_INCOME));
        expenseCategories.add(new Category(Constants.EMPTY_STRING, Constants.CATEGORY_TYPE_EXPENSE));
        for (Category category : allCategories) {
            if (category.getType() == Constants.CATEGORY_TYPE_INCOME) {
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

    private void refreshWalletAdapter() {
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar inputDate = Calendar.getInstance();
        inputDate.set(Calendar.YEAR, year);
        inputDate.set(Calendar.MONTH, month);
        inputDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateTextView.setText(dateFormat.format(inputDate.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        try {
            unregisterReceiver(timeBroadcastReceiver);
        } catch (Exception e) {
            Log.e("unregister", "receiver already unregistered");
            e.printStackTrace();
        }
        Calendar inputTime = Calendar.getInstance();
        inputTime.set(Calendar.HOUR, hour);
        inputTime.set(Calendar.MINUTE, minute);
        timeTextView.setText(timeFormat.format(inputTime.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        final Boolean isExpense = getIntent().getBooleanExtra("expense", true);

        getSupportActionBar().setTitle(isExpense ? "Log New Expense" : "Log New Income");

        db = new DatabaseHelper(this);
        refreshWalletList();
        refreshCategories();

        amountTextView = findViewById(R.id.amount);
        descriptionTextView = findViewById(R.id.description);

        dateTextView = findViewById(R.id.dateText);
        dateTextView.setText(dateFormat.format(Calendar.getInstance().getTime()));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                try {
                    Date date = dateFormat.parse(dateTextView.getText().toString());
                    if (date != null) {
                        now.setTime(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseActivity.this, ExpenseActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        timeTextView = findViewById(R.id.timeText);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                try {
                    Date time = timeFormat.parse(timeTextView.getText().toString());
                    if (time != null) {
                        now.setTime(time);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(ExpenseActivity.this, ExpenseActivity.this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), DateFormat.is24HourFormat(ExpenseActivity.this));
                timePickerDialog.show();
            }
        });

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
                if (wallet.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a wallet", Toast.LENGTH_SHORT).show();
                } else if (category.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_SHORT).show();
                } else if (amountTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else {
                    amount = Float.parseFloat(amountTextView.getText().toString());
                    if (amount <= 0) {
                        Toast.makeText(getApplicationContext(), "Enter amount > 0", Toast.LENGTH_SHORT).show();
                    } else {
                        description = descriptionTextView.getText().toString();
                        String date = dateTextView.getText().toString();
                        String time = timeTextView.getText().toString();
                        String toastString = "Wallet: " + wallet + "\nCategory: " + category + "\nAmount: " + amount + " Rs\n" + "Description: " + description;
                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                        String responseMessage;
                        if (isExpense) {
                            responseMessage = db.newExpense(null, walletId, categoryId, amount, description, date, time);
                            Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                        } else {
                            responseMessage = db.newIncome(null, walletId, categoryId, amount, description, date, time);
                            Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                        }
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
