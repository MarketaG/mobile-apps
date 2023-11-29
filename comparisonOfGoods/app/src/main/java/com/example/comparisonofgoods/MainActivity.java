package com.example.comparisonofgoods;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout horizontalLayoutProduct1;
    private LinearLayout horizontalLayoutProduct2;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalLayoutProduct1 = findViewById(R.id.horizontalLayoutProduct1);
        horizontalLayoutProduct2 = findViewById(R.id.horizontalLayoutProduct2);
        textResult = findViewById(R.id.textResult);
        Button buttonCalculation = findViewById(R.id.buttonCalculation);
        buttonCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comparePrices();
            }
        });
    }

    private void comparePrices () {
        double price1 = Double.parseDouble(((TextView) findViewById(R.id.editTextPrice1)) . getText().toString());
        double weight1 = Double.parseDouble(((TextView) findViewById(R.id.editTextWeight1)) . getText().toString());
        double price2 = Double.parseDouble(((TextView) findViewById(R.id.editTextPrice2)) . getText().toString());
        double weight2 = Double.parseDouble(((TextView) findViewById(R.id.editTextWeight2)) . getText().toString());

        double pricePerKg1 = price1 / (weight1 / 1000);
        double pricePerKg2 = price2 / (weight2 / 1000);

        horizontalLayoutProduct1.setBackgroundColor((Color.TRANSPARENT));
        horizontalLayoutProduct2.setBackgroundColor(Color.TRANSPARENT);

        if (pricePerKg1 < pricePerKg2) {
            horizontalLayoutProduct1.setBackgroundColor(Color.parseColor("#80ff80"));
        } else if (pricePerKg2 < pricePerKg1) {
            horizontalLayoutProduct2.setBackgroundColor(Color.parseColor("#80ff80"));
        } else if (pricePerKg2 == pricePerKg1) {
            horizontalLayoutProduct1.setBackgroundColor(Color.parseColor("#80ff80"));
            horizontalLayoutProduct2.setBackgroundColor(Color.parseColor("#80ff80"));
        } else {
            textResult.setText("Zadal jsi špatně hodnoty!!!");
        }

        textResult.setText("Cena prvního zboží v přepočtu na 1 kilogram je " + String.format("%.2f", pricePerKg1) + " Kč.\n" + "Cena druhého zboží v přepočtu na 1 kilogram je " + String.format("%.2f", pricePerKg2) + " Kč.");

        String cheaperProduct;
        if (pricePerKg1 < pricePerKg2) {
            cheaperProduct = "Zboží 1";
        } else if (pricePerKg2 < pricePerKg1) {
            cheaperProduct = "Zboží 2";
        } else if (pricePerKg1 == pricePerKg2) {
            cheaperProduct = "Zboží 1 a Zboží 2 mají stejnou hodnotu";
        } else {
            cheaperProduct = "Někde je chyba v zadání...";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((this));
        builder.setTitle("CENOVĚ VÝHODNĚJŠÍ ZBOŽÍ")
                .setMessage("Levnější variantou zboží je: " + cheaperProduct)
                .setPositiveButton("OK", null)
                .show();
    }
}