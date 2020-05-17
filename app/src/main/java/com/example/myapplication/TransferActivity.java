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

public class TransferActivity extends AppCompatActivity {

    private Spinner fromWalletSpinner;
    private Spinner toWalletSpinner;

    private Button okButton;
    private Button cancelButton;

    private EditText amountEditText;

    private String fromWallet;
    private Integer fromWalletId;
    private String toWallet;
    private Integer toWalletId;
    private Float transferAmount;

    private DatabaseHelper db;

    private List<Wallet> walletList = new ArrayList<>();

    private void refreshWalletList() {
        walletList = new ArrayList<>();
        walletList.add(new Wallet(Constants.EMPTY_STRING, Constants.ZERO_F));
        walletList.addAll(db.getAllWallets(true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        db = new DatabaseHelper(this);
        refreshWalletList();

        fromWalletSpinner = findViewById(R.id.from_wallet_spinner);
        toWalletSpinner = findViewById(R.id.to_wallet_spinner);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);

        amountEditText = findViewById(R.id.amount);

        ArrayAdapter<Wallet> walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fromWalletSpinner.setAdapter(walletAdapter);
        toWalletSpinner.setAdapter(walletAdapter);

        fromWalletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Wallet selectedItem = (Wallet) adapterView.getItemAtPosition(i);
                fromWallet = selectedItem.getName();
                fromWalletId = selectedItem.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fromWallet = "";
            }
        });

        toWalletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Wallet selectedItem = (Wallet) adapterView.getItemAtPosition(i);
                toWallet = selectedItem.getName();
                toWalletId = selectedItem.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                toWallet = "";
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromWallet.equalsIgnoreCase(Constants.EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(), "Choose a wallet to transfer from", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(toWallet.equalsIgnoreCase(Constants.EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(), "Choose a wallet to transfer to", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(toWallet == fromWallet){
                    Toast.makeText(getApplicationContext(), "Same wallet transfer not applicable", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amountEditText.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)){
                    Toast.makeText(getApplicationContext(), "Amount not entered", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    transferAmount = Float.parseFloat(amountEditText.getText().toString());
                    if(transferAmount <= Constants.ZERO){
                        Toast.makeText(getApplicationContext(), "Cannot do empty transfers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Transferring amount " + transferAmount + " from " + fromWallet + " to " + toWallet, Toast.LENGTH_SHORT).show();
                    String responseMessage = db.walletTransfer(fromWalletId, toWalletId, transferAmount);
                    Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
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
