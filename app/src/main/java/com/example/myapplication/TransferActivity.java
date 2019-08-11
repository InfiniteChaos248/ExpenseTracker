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

public class TransferActivity extends AppCompatActivity {

    private Spinner fromWalletSpinner;
    private Spinner toWalletSpinner;

    private Button okButton;
    private Button cancelButton;

    private EditText amountEditText;

    private String fromWallet;
    private String toWallet;
    private Integer transferAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        final List<String> walletCategories = new ArrayList<>();
        walletCategories.add("");
        walletCategories.add("Bank account : ICICI");
        walletCategories.add("Cash");
        walletCategories.add("Infosys smart card");

        fromWalletSpinner = findViewById(R.id.from_wallet_spinner);
        toWalletSpinner = findViewById(R.id.to_wallet_spinner);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        amountEditText = findViewById(R.id.amount);

        ArrayAdapter<String> walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletCategories);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fromWalletSpinner.setAdapter(walletAdapter);
        toWalletSpinner.setAdapter(walletAdapter);

        fromWalletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                fromWallet = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fromWallet = "";
            }
        });

        toWalletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                toWallet = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                toWallet = "";
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromWallet == ""){
                    Toast.makeText(getApplicationContext(), "Choose a wallet to transfer from", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(toWallet == ""){
                    Toast.makeText(getApplicationContext(), "Choose a wallet to transfer to", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(toWallet == fromWallet){
                    Toast.makeText(getApplicationContext(), "Not applicable", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amountEditText.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Not applicable", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    transferAmount = Integer.parseInt(amountEditText.getText().toString());
                    if(transferAmount <= 0){
                        Toast.makeText(getApplicationContext(), "Not applicable", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Transferring amount " + transferAmount + " from " + fromWallet + " to " + toWallet, Toast.LENGTH_SHORT).show();
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
