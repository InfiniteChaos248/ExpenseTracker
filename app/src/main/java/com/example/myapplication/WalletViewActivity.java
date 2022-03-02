package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletViewActivity extends AppCompatActivity {

    private TableLayout wallets;

    private DatabaseHelper db;

    private List<Wallet> walletList = new ArrayList<>();

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(40, 40, 40, 40);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }

    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
    }

    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_view);

        getSupportActionBar().setTitle("Wallets");

        db = new DatabaseHelper(this);
        walletList.addAll(db.getAllWallets(true));

        wallets = findViewById(R.id.wallets_table);

        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(getLayoutParams());
        headerRow.addView(getTextView(0, "Wallet", Color.WHITE, Typeface.BOLD, Color.BLUE));
        headerRow.addView(getTextView(0, "Amount", Color.WHITE, Typeface.BOLD, Color.BLUE));
        wallets.addView(headerRow, getTblLayoutParams());

        for (Wallet wallet : walletList) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(wallet.getId(), wallet.getName(), Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(wallet.getId() + walletList.size(), String.format("%.2f", wallet.getAmount()), Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            wallets.addView(tr, getTblLayoutParams());
        }

    }
}
