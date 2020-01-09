package com.example.trackertimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity{

    TextView startidTekst;
    Button send, cancel;
    EditText name, bib, startid;
    Spinner dayspinner, monthspinner, yearspinner;
    ArrayList<Integer> dates = new ArrayList<>();
    boolean isnotfellesstart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person);
        makescroll();
        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel);
        name = findViewById(R.id.name);
        bib = findViewById(R.id.bib);
        startid = findViewById(R.id.starttid);
        startidTekst = findViewById(R.id.starttidtekst);
        isnotfellesstart = getIntent().getBooleanExtra("isnotfellesstart", false);

        if(isnotfellesstart){
            startidTekst.setVisibility(View.VISIBLE);
            startid.setVisibility(View.VISIBLE);
        }else {
            startidTekst.setVisibility(View.INVISIBLE);
            startid.setVisibility(View.INVISIBLE);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String melding = name.getText().toString()+"//"+dates.get(0)+"-"+dates.get(1)+"-"+dates.get(2)+"//"+bib.getText().toString();
                if (isnotfellesstart){
                    melding = melding+"//"+startid.getText().toString();
                }
                intent.putExtra("melding", melding);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dayspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = dayspinner.getSelectedItem().toString();
                Log.e("ItemSelected: ", selectedItem);
                dates.add(0, Integer.parseInt(selectedItem));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        monthspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedItem = monthspinner.getSelectedItemPosition();
                Log.e("ItemSelected: ", selectedItem+"");
                dates.add(1, selectedItem+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        yearspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = yearspinner.getSelectedItem().toString();
                Log.e("ItemSelected: ", selectedItem);
                dates.add(2, Integer.parseInt(selectedItem));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void makescroll(){
        dayspinner = findViewById(R.id.day_spinner);
        monthspinner = findViewById(R.id.month_spinner);
        yearspinner = findViewById(R.id.year_spinner);
        //DAY
        ArrayAdapter<Integer> days = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, arrayCreate(0, 31));
        dayspinner.setAdapter(days);
        //MONTH
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.montharray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthspinner.setAdapter(adapter);
        //YEAR
        ArrayAdapter<Integer> years = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, arrayCreate(1940,2019));
        yearspinner.setAdapter(years);
    }
    public ArrayList<Integer> arrayCreate(int from, int to){
        ArrayList<Integer> dummy = new ArrayList<>();
        for(int i = from; i<to; i++){
            dummy.add(i+1);
        }
        return dummy;
    }
}
