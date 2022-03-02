package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.myapplication.database.model.ActivityLog;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateDeleteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);

    private TextView logTextView;
    private EditText amountTextView;
    private EditText descriptionTextView;

    private TextView dateTextView;
    private TextView timeTextView;

    private Spinner categorySpinner;
    ArrayAdapter<Category> categoryAdapter;
    private Spinner walletSpinner;
    private ArrayAdapter<Wallet> walletAdapter;
    private Spinner walletSSpinner;
    private ArrayAdapter<Wallet> walletSAdapter;

    private Button deleteButton;
    private Button updateButton;
    private Button cancelButton;

    private String category;
    private Integer categoryId;
    private String wallet;
    private Integer walletId;
    private String walletS;
    private Integer walletSId;
    private Float amount;
    private String description;
    private String date;
    private String time;

    private DatabaseHelper db;

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

    private void refreshCategoryAdapter(List<Category> categories, Integer categoryId) {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        for (int position = 0; position < categories.size(); position++) {
            if (categoryId.equals(categories.get(position).getId())) {
                categorySpinner.setSelection(position);
                break;
            }
        }
    }

    private void refreshWalletAdapter(Integer walletId) {
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);
        for (int position = 0; position < walletList.size(); position++) {
            if (walletId.equals(walletList.get(position).getId())) {
                walletSpinner.setSelection(position);
                break;
            }
        }
    }

    private void refreshWalletSAdapter(Integer walletId) {
        walletSAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletSAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSSpinner.setAdapter(walletSAdapter);
        for (int position = 0; position < walletList.size(); position++) {
            if (walletId.equals(walletList.get(position).getId())) {
                walletSSpinner.setSelection(position);
                break;
            }
        }
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
        Calendar inputTime = Calendar.getInstance();
        inputTime.set(Calendar.HOUR, hour);
        inputTime.set(Calendar.MINUTE, minute);
        timeTextView.setText(timeFormat.format(inputTime.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        getSupportActionBar().setTitle("Update/Delete Log");

        Bundle bundle = getIntent().getExtras();
        final ActivityLog activityLog = (ActivityLog) bundle.getSerializable("activity_log");
        final Boolean isExpense = activityLog.getType() == 1;
        final Boolean isIncome = activityLog.getType() == 2;
        final Boolean isTransfer = activityLog.getType() == 3;

        db = new DatabaseHelper(this);
        refreshWalletList();
        refreshCategories();

        logTextView = findViewById(R.id.h0);
        logTextView.setText(isTransfer ? "wallet transfer" : isExpense ? "expense" : "income");

        amountTextView = findViewById(R.id.edit_amount);
        amountTextView.setText(String.format("%.2f", activityLog.getAmount()));

        descriptionTextView = findViewById(R.id.edit_description);
        descriptionTextView.setText(activityLog.getComments());

        dateTextView = findViewById(R.id.edit_date);
        dateTextView.setText(activityLog.getLogDate());
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
                    // continue
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateDeleteActivity.this, UpdateDeleteActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        timeTextView = findViewById(R.id.edit_time);
        timeTextView.setText(activityLog.getLogTime());
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
                    // continue
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateDeleteActivity.this, UpdateDeleteActivity.this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), DateFormat.is24HourFormat(UpdateDeleteActivity.this));
                timePickerDialog.show();
            }
        });

        categorySpinner = findViewById(R.id.edit_category);
        if (isTransfer) {
            categorySpinner.setEnabled(false);
        } else {
            refreshCategoryAdapter(isExpense ? expenseCategories : incomeCategories, activityLog.getCategory());
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
        }

        walletSpinner = findViewById(R.id.edit_wallet);
        refreshWalletAdapter(activityLog.getWallet());
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

        walletSSpinner = findViewById(R.id.edit_wallet_s);
        if (isTransfer) {
            refreshWalletSAdapter(activityLog.getWalletS());
            walletSSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Wallet selectedItem = (Wallet) adapterView.getItemAtPosition(i);
                    walletS = selectedItem.getName();
                    walletSId = selectedItem.getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    walletS = Constants.EMPTY_STRING;
                }
            });
        } else {
            walletSSpinner.setEnabled(false);
        }


        deleteButton = findViewById(R.id.delete_button);
        updateButton = findViewById(R.id.update_button);
        cancelButton = findViewById(R.id.cancel_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //'yes' button clicked
                                String response = db.deleteActivityLog(activityLog);
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //'no' button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDeleteActivity.this);
                builder
                        .setMessage("are you sure you want to delete log?")
                        .setPositiveButton("yes", dialogClickListener)
                        .setNegativeButton("no", dialogClickListener);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("update", isExpense + ", " + isIncome + ", " + isTransfer);
                Log.i("update", " existing log ===> " + new Gson().toJson(activityLog));
                Log.i("update", " amount ===> " + amountTextView.getText());
                Log.i("update", " description ===> " + descriptionTextView.getText());
                Log.i("update", " date ===> " + dateTextView.getText());
                Log.i("update", " time ===> " + timeTextView.getText());
                Log.i("update", " category ===> " + category);
                Log.i("update", " wallet ===> " + wallet);
                Log.i("update", " wallet2 ===> " + walletS);

                if (wallet.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a wallet", Toast.LENGTH_SHORT).show();
                } else if (isTransfer && walletS.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a destination wallet", Toast.LENGTH_SHORT).show();
                } else if (!isTransfer && category.equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_SHORT).show();
                } else if (amountTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else {
                    amount = Float.parseFloat(amountTextView.getText().toString());
                    if (amount <= 0) {
                        Toast.makeText(getApplicationContext(), "Enter amount > 0", Toast.LENGTH_SHORT).show();
                    } else {
                        description = descriptionTextView.getText().toString();
                        date = dateTextView.getText().toString();
                        time = timeTextView.getText().toString();

                        if ((isIncome || isExpense) && walletId.equals(activityLog.getWallet())
                                && categoryId.equals(activityLog.getCategory())
                                && amount.equals(activityLog.getAmount())
                                && date.equals(activityLog.getLogDate())
                                && time.equals(activityLog.getLogTime())
                                && description.equals(activityLog.getComments())) {
                            Toast.makeText(getApplicationContext(), "no details updated", Toast.LENGTH_SHORT).show();
                        } else if (isTransfer && walletId.equals(activityLog.getWallet())
                                && walletSId.equals(activityLog.getWalletS())
                                && amount.equals(activityLog.getAmount())
                                && date.equals(activityLog.getLogDate())
                                && time.equals(activityLog.getLogTime())
                                && description.equals(activityLog.getComments())) {
                            Toast.makeText(getApplicationContext(), "no details updated", Toast.LENGTH_SHORT).show();
                        } else {
                            String responseMessage = "";
                            SQLiteDatabase dbo = db.getWritableDatabase();
                            dbo.beginTransaction();
                            if (isIncome) {
                                responseMessage = db.deleteIncome(activityLog);
                                if (responseMessage.contains("successfully")) {
                                    responseMessage = db.newIncome(activityLog.getId(), walletId, categoryId, amount, description, date, time);
                                    if (responseMessage.contains("successfully")) {
                                        dbo.setTransactionSuccessful();
                                        responseMessage = "income log updated successfully";
                                    } else {
                                        responseMessage = "error updating income log";
                                    }
                                } else {
                                    responseMessage = "error updating income log";
                                }
                            }
                            if (isExpense) {
                                responseMessage = db.deleteExpense(activityLog);
                                if (responseMessage.contains("successfully")) {
                                    responseMessage = db.newExpense(activityLog.getId(), walletId, categoryId, amount, description, date, time);
                                    if (responseMessage.contains("successfully")) {
                                        dbo.setTransactionSuccessful();
                                        responseMessage = "expense log updated successfully";
                                    } else {
                                        responseMessage = "error updating expense log";
                                    }
                                } else {
                                    responseMessage = "error updating expense log";
                                }
                            }
                            if (isTransfer) {
                                responseMessage = db.deleteWalletTransfer(activityLog);
                                if (responseMessage.contains("successfully")) {
                                    responseMessage = db.walletTransfer(activityLog.getId(), walletId, walletSId, amount, description, date, time);
                                    if (responseMessage.contains("successfully")) {
                                        dbo.setTransactionSuccessful();
                                        responseMessage = "transfer log updated successfully";
                                    } else {
                                        responseMessage = "error updating transfer log";
                                    }
                                } else {
                                    responseMessage = "error updating transfer log";
                                }
                            }
                            dbo.endTransaction();
                            Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_SHORT).show();
                            finish();
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
