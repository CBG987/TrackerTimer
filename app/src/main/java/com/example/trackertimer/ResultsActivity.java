package com.example.trackertimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    Button back, send;
    TableLayout layout2;
    TableRow.LayoutParams rowlayout1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
    TableRow.LayoutParams rowlayout2 = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    TableRow row1;
    TextView txt1, txt2, txt3, txt4, txt5;
    ArrayList<Integer> startnr, alder, sluttid;
    ArrayList<String> navn, fødselsdato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        layout2 = findViewById(R.id.tablelayout2);
        back = findViewById(R.id.tilbake);
        send = findViewById(R.id.sendJava);
        row1 = new TableRow(this);
        txt1 = new TextView(this);
        txt2 = new TextView(this);
        txt3 = new TextView(this);
        txt4 = new TextView(this);
        txt5 = new TextView(this);
        setUpRows();
        setup();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });
    }
    public void send(){
        for(int i=0; i<startnr.size(); i++) {
            SendInformation sendinfo = new SendInformation();
            String strengen = startnr.get(i)+"\t"+navn.get(i)+"\t"+fødselsdato.get(i)+"\t"+alder.get(i)+"\t"+sluttid.get(i);
            sendinfo.execute(strengen);
        }
    }
    public void setup(){
        Intent data = getIntent();
        startnr = data.getIntegerArrayListExtra("bib");
        navn = data.getStringArrayListExtra("navn");
        fødselsdato = data.getStringArrayListExtra("fødselsdato");
        alder = data.getIntegerArrayListExtra("alder");
        sluttid = data.getIntegerArrayListExtra("sluttid");
        for(int i=0; i<startnr.size(); i++){
            //System.out.println("HeiHei: "+fødselsdato.get(i));
            String a = startnr.get(i)+""; String b = navn.get(i)+""; String c = fødselsdato.get(i)+"";
            String d = alder.get(i)+""; String e = sluttid.get(i)+"";
            TableRow rows1 = new TableRow(this);
            TextView txts1 = new TextView(this);
            TextView txts2 = new TextView(this);
            TextView txts3 = new TextView(this);
            TextView txts4 = new TextView(this);
            TextView txts5 = new TextView(this);
            txts1.setText(a); txts2.setText(b); txts3.setText(c);
            txts4.setText(d); txts5.setText(e);
            txts1.setLayoutParams(rowlayout1); txts2.setLayoutParams(rowlayout1);
            txts3.setLayoutParams(rowlayout1); txts4.setLayoutParams(rowlayout1);
            txts5.setLayoutParams(rowlayout1); rows1.addView(txts1); rows1.addView(txts2);
            rows1.addView(txts3); rows1.addView(txts4); rows1.addView(txts5);
            rows1.setLayoutParams(rowlayout2); layout2.addView(rows1);
        }
    }
    public void setUpRows(){
        txt1.setLayoutParams(rowlayout1); txt2.setLayoutParams(rowlayout1);
        txt3.setLayoutParams(rowlayout1); txt4.setLayoutParams(rowlayout1);
        txt5.setLayoutParams(rowlayout1); txt1.setText("Startnr."); txt2.setText("Navn");
        txt3.setText("Fødselsdato"); txt4.setText("Alder"); txt5.setText("Sluttid");
        row1.addView(txt1); row1.addView(txt2); row1.addView(txt3); row1.addView(txt4);
        row1.addView(txt5); row1.setLayoutParams(rowlayout2); layout2.addView(row1);
    }
}
