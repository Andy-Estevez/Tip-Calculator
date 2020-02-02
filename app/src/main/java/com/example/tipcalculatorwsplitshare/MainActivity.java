package com.example.tipcalculatorwsplitshare;

import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.NumberFormat;



public class MainActivity extends AppCompatActivity implements TextWatcher,
        SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private EditText editTextBillAmount;
    private SeekBar  seekBar;

    private TextView textViewBillAmount;
    private TextView textViewPercent;
    private TextView tipTextView;
    private TextView textView_BillTotal;

    private TextView textViewBillPerPerson;

    private double billAmount = 0.0;
    private double percent    = 0.15;
    private int people;

    private double tip;
    private double total;
    private static final NumberFormat currencyFormat =  NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat  =  NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewBillAmount   = (TextView)findViewById(R.id.billAmountView);
        textViewPercent      = (TextView)findViewById(R.id.tipPercentView);
        textView_BillTotal   = (TextView)findViewById(R.id.BillTotalAmount);
        tipTextView          = (TextView)findViewById(R.id.tipAmount);
        editTextBillAmount   = (EditText)findViewById(R.id.billEdit);
        seekBar              = (SeekBar) findViewById(R.id.seekbar);

        textViewBillPerPerson = (TextView)findViewById(R.id.billPerPersonAmount);

        // only need listeners for what the user is interacting with
        editTextBillAmount.addTextChangedListener((TextWatcher) this);
        seekBar           .setOnSeekBarChangeListener(this);

        // create the spinner
        Spinner spinner = findViewById(R.id.peopleView);

        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        // Create ArrayAdapter using the string array and default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Person, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+s);
        //surround risky calculations with try catch (what if billAmount is 0 ?
        //charSequence is converted to a String and parsed to a double for you
        billAmount = Double.parseDouble(s.toString()) / 100; Log.d("MainActivity", "Bill Amount = "+billAmount);
        //setText on the textView

        textViewBillAmount.setText(currencyFormat.format(billAmount));
        //perform tip and total calculation and update UI by calling calculate
        calculate();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        percent = progress / 100.0;
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void calculate(){
        Log.d("MainActivity", "inside calculate method");


        // format percent and display in percentTextView
        textViewPercent.setText(percentFormat.format(percent));

        // calculate the tip and total
         tip = (billAmount * percent);

        //use the tip example to do the same for the Total
         total = billAmount + tip;

        // display tip and total formatted as currency
        //user currencyFormat instead of percentFormat to set the textViewTip
        tipTextView.setText(currencyFormat.format(tip));
        textView_BillTotal.setText(currencyFormat.format(total));
        textViewBillPerPerson.setText(currencyFormat.format(total/people));
        //use the tip example to do the same for the Total


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String x = (String) parent.getItemAtPosition(position);
            people = Integer.parseInt(x);
            calculate();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onClickShowAlert(MenuItem item) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setMessage("The spinner is used to split the total");
        alertBuilder.show();
    }

    public void sendMessage(MenuItem item) {
        String txt = "The bill amount is "+textViewBillAmount.getText()+". The tip is "+tipTextView.getText()
                +". The total bill is "+textView_BillTotal.getText()+". The total bill per person is "+textViewBillPerPerson.getText();
        String mimeType = "text/plain";

        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle("share")
                .setText(txt)
                .startChooser();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.ExactBill:
                if(checked)
                    calculate();
                    break;
            case R.id.TipRounded:
                if(checked)
                    calculate();
                    tip = (int) Math.ceil(billAmount*percent);
                    tipTextView.setText(currencyFormat.format(tip));
                    total = tip + billAmount;
                    textView_BillTotal.setText(currencyFormat.format(total));
                    break;
            case R.id.TotalRounded:
                if(checked)
                    calculate();
                    tip = billAmount*percent;
                    total = (int) Math.ceil(billAmount+tip);
                    textView_BillTotal.setText(currencyFormat.format(total));
                    break;
                default:
                    calculate();
                    break;
        }

    }
}
