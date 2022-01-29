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
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    private EditText amountTextView;
    private EditText nameTextView;

    private Spinner walletSpinner;
    private ArrayAdapter<Wallet> walletAdapter;

    private Button okButton;
    private Button cancelButton;

    private String walletName;
    private Integer walletId;
    private Float amount;
    private String name;

    private DatabaseHelper db;

    private List<Wallet> walletList = new ArrayList<>();

    private void refreshWalletList() {
        walletList = new ArrayList<>();
        walletList.add(new Wallet(Constants.EMPTY_STRING, Constants.ZERO_F));
        walletList.addAll(db.getAllWallets(true));
    }

    private void refreshWalletAdapter() {
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        getSupportActionBar().setTitle("Add/Modify Wallet");

        amountTextView = findViewById(R.id.amount);
        nameTextView = findViewById(R.id.wallet_name);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        db = new DatabaseHelper(this);
        refreshWalletList();

        walletSpinner = findViewById(R.id.s_wallet);
        refreshWalletAdapter();

        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Wallet wallet = (Wallet) adapterView.getSelectedItem();
                walletName = wallet.getName();
                walletId = wallet.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                walletName = Constants.EMPTY_STRING;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Enter a new name", Toast.LENGTH_SHORT).show();
                } else if (walletName.isEmpty() && amountTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else {
                    if (amountTextView.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                        amount = 0f;
                    } else {
                        amount = Float.parseFloat(amountTextView.getText().toString());
                    }
                    name = nameTextView.getText().toString();
                    String toastString;
                    if (walletName.isEmpty()) {
                        toastString = "Adding new wallet \"" + name + "\" with initial amount " + amount;
                        db.insertNewWallet(name, amount);
                        refreshWalletList();
                        refreshWalletAdapter();
                    } else {
                        toastString = "Renaming wallet \"" + walletName + "\" as \"" + name + "\"";
                        db.updateWalletName(walletId, name);
                        refreshWalletList();
                        refreshWalletAdapter();
                    }

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
