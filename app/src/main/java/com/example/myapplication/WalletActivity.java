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

import com.example.myapplication.com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.com.example.myapplication.database.com.example.myapplication.sqlite.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    private EditText amountTextView;
    private EditText nameTextView;

    private Spinner walletSpinner;

    private Button okButton;
    private Button cancelButton;

    private String walletName;
    private Integer walletId;
    private Integer amount;
    private String name;

    private DatabaseHelper db;

    private List<Wallet> walletList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        amountTextView = findViewById(R.id.amount);
        nameTextView = findViewById(R.id.wallet_name);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        db = new DatabaseHelper(this);
        walletList.add(new Wallet("", 0));
        walletList.addAll(db.getAllWallets());

        walletSpinner = findViewById(R.id.s_wallet);
        final ArrayAdapter<Wallet> walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        walletSpinner.setAdapter(walletAdapter);

        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Wallet wallet = (Wallet) adapterView.getSelectedItem();
                walletName = wallet.getName();
                walletId = wallet.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                walletName = "";
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameTextView.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter a new name", Toast.LENGTH_SHORT).show();
                } else if (walletName.isEmpty() && amountTextView.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else{
                    if(amountTextView.getText().toString().equalsIgnoreCase("")){
                        amount = 0;
                    } else{
                        amount = Integer.parseInt(amountTextView.getText().toString());
                    }
                    name = nameTextView.getText().toString();
                    String toastString;
                    if(walletName.isEmpty()){
                        toastString = "Adding new wallet \"" + name + "\" with initial amount " + amount;
                        db.insertNewWallet(name, amount);
                        walletList = new ArrayList<>();
                        walletList.add(new Wallet("", 0));
                        walletList.addAll(db.getAllWallets());
                        walletAdapter.clear();
                        walletAdapter.addAll(walletList);
                        walletAdapter.notifyDataSetChanged();
                    } else {
                        toastString = "Renaming wallet \"" + walletName + "\" as \"" + name + "\"";
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
