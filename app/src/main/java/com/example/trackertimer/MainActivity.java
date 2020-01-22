package com.example.trackertimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
//import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


public class MainActivity extends AppCompatActivity {

    ToggleButton togglejakt, toggleintervall, togglefelles;
    Button start, stop, add, results, excelimport;
    ArrayList<String> navn = new ArrayList<>();
    ArrayList<String> fødselsdato = new ArrayList<>();
    ArrayList<Integer> alder = new ArrayList<>();
    ArrayList<Integer> startnr = new ArrayList<>();
    ArrayList<String> stopTimes = new ArrayList<>();
    ArrayList<CountDownTimer> startTimes = new ArrayList<>();
    TableLayout layout;
    TableRow.LayoutParams rowlayout1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
    TableRow.LayoutParams rowlayout2 = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    TableRow row1;
    TextView txt1, txt2, txt21, txt3;

    int antallDeltakere = 0;
    private Chronometer chronometer;
    private boolean running, isnotfellesstart, isjaktstart;
    private long pauseOffset;
    String ipAddress = ""; int port = 0;

    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stopp);
        add = findViewById(R.id.addperson);
        results = findViewById(R.id.results);
        excelimport = findViewById(R.id.excelimport);
        layout = findViewById(R.id.tablelayout);
        togglejakt = findViewById(R.id.togglejakt);
        toggleintervall = findViewById(R.id.toggleintervall);
        togglefelles = findViewById(R.id.togglefelles);
        row1 = new TableRow(this);
        txt1 = new TextView(this);
        txt2 = new TextView(this);
        txt21 = new TextView(this);
        txt3 = new TextView(this);
        chronometer = findViewById(R.id.chronometer);
        isnotfellesstart = false;
        isjaktstart = false;

        try{
            socket = IO.socket(getResources().getString(R.string.ipaddress));
            socket.connect();
            socket.emit("message", "an android unit joined");
            Toast.makeText(this, "joined", Toast.LENGTH_LONG).show();
        }catch(URISyntaxException e){
            e.printStackTrace();
        }



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    if(togglejakt.isChecked()){
                        //MyClientTask myClientTask = new MyClientTask(ipAddress, port);
                        //myClientTask.execute();
                    }
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    if(isnotfellesstart){
                        for(CountDownTimer cdt: startTimes){
                            cdt.start();
                        }
                    }
                    running = true;
                }
                socket.emit("startdetect", "Start");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running){
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    if(isnotfellesstart){
                        for(CountDownTimer cdt: startTimes){
                            cdt.onFinish();
                        }
                    }
                    running = false;
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
                addIntent.putExtra("isnotfellesstart", isnotfellesstart);
                startActivityForResult(addIntent, 12);
            }
        });
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(MainActivity.this, ResultsActivity.class);
                addIntent.putExtra("navn", navn);
                addIntent.putExtra("bib", startnr);
                addIntent.putExtra("fødselsdato", fødselsdato);
                addIntent.putExtra("alder", alder);
                addIntent.putExtra("sluttid", stopTimes);
                System.out.println("Størrelse på Array: "+navn.size());
                startActivity(addIntent);
            }
        });
        excelimport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent excelIntent = new Intent(MainActivity.this, Excelimport.class);
                startActivityForResult(excelIntent, 13);
            }
        });
        /*togglejakt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(togglejakt.isChecked()){
                    isjaktstart = true;
                }else if (!togglejakt.isChecked()){
                    isjaktstart = false;
                }
            }
        });*/
        toggleintervall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleintervall.isChecked()){
                    Toast.makeText(getApplicationContext(),"Intervallstart", Toast.LENGTH_LONG).show();
                    togglefelles.setChecked(false);
                    row1.removeAllViews();
                    layout.removeAllViews();
                    isnotfellesstart = true;
                    setUpRows();
                }
            }
        });
        togglefelles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (togglefelles.isChecked()){
                    Toast.makeText(getApplicationContext(),"Fellesstart", Toast.LENGTH_LONG).show();
                    toggleintervall.setChecked(false);
                    row1.removeAllViews();
                    layout.removeAllViews();
                    isnotfellesstart = false;
                    setUpRows();
                }
            }
        });
        /*try {
            Toast.makeText(this, "Trying to connect to Server", Toast.LENGTH_LONG).show();
            socket = IO.socket("http://localhost:3000/");
            socket.connect();
            socket.emit("result", "Client1");
        }catch (URISyntaxException e){
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.connect : {
                Toast.makeText(this, "Connect selected", Toast.LENGTH_LONG).show();
                Intent connectIntent = new Intent(MainActivity.this, ConnectToServer.class);
                startActivityForResult(connectIntent, 14);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            //Get contestant from app
            case (12) : {
                if (resultCode == Activity.RESULT_OK) {
                    String theData = data.getStringExtra("melding");
                    addPerson(theData);
                    if(running){
                        startTimes.get(antallDeltakere-1).start();
                    }
                }
                break;
            }
            //Gets contestants from Excel
            case (13) : {
                if (resultCode == Activity.RESULT_OK) {
                    String theData = data.getStringExtra("theString");
                    String[] newData = theData.split(";");

                    for(int i=0; i<newData.length; i++){
                        Log.d("MainActivity: ", "ParseStringBuilder: Data from theString: " + newData[i]);
                        addPerson(newData[i]);
                    }
                }
                break;
            }
            case (14) : {
                if (resultCode == Activity.RESULT_OK) {
                    String theData = data.getStringExtra("setconnect");
                    String[] newData = theData.split("//");
                    ipAddress = newData[0];
                    port = Integer.parseInt(newData[1]);
                }
                break;
            }
        }
    }
    public void addPerson(String newText){
        final String[] newT = newText.split("//");
        final TableRow rows1 = new TableRow(this);
        TextView txts1 = new TextView(this);
        TextView txts2 = new TextView(this);
        final TextView starttime = new TextView(this);
        final Button stopTime = new Button(this);
        int s3 = 0;
        if(isnotfellesstart){
            double s1 = Double.parseDouble(newT[3]);
            String s2 = (int)s1+"000"; s3 = Integer.parseInt(s2);
            CountDownTimer cT =  new CountDownTimer(s3, 1000) {
                public void onTick(long millisUntilFinished) {
                    String v = String.format("%02d", millisUntilFinished/60000);
                    int va = (int)( (millisUntilFinished%60000)/1000);
                    starttime.setText(v+":"+String.format("%02d",va));
                }
                public void onFinish() {
                    starttime.setText("Start");
                }
            };
            startTimes.add(cT);
        }
        txts1.setText(newT[0]); txts2.setText(newT[2]);
        txts1.setLayoutParams(rowlayout1); txts2.setLayoutParams(rowlayout1);
        stopTime.setText("Stopp"); stopTime.setLayoutParams(rowlayout1); stopTime.setId(antallDeltakere+1);
        rows1.addView(txts1); rows1.addView(txts2);
        if (isnotfellesstart){
            starttime.setLayoutParams(rowlayout1); rows1.addView(starttime);
        }
        final int s31 = s3;
        stopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isnotfellesstart){
                    int sluttid = convertCronToInt(chronometer)-(s31/1000);
                    int hours = sluttid/3600; int minutes = (sluttid%3600)/60;
                    int seconds = sluttid%60;
                    String sluttsluttid = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Log.d("MainActivity: ", "AddPerson:_ChroData: " + sluttid);
                    Log.d("MainActivity: ", "AddPerson:_ChroData: " + sluttsluttid);
                    stopTimes.add(sluttsluttid);
                }else {
                    stopTimes.add(chronometer.getText().toString());
                }
                navn.add(newT[0]); fødselsdato.add(newT[1]);
                alder.add(getAlder(newT[1]));
                startnr.add(Integer.parseInt(newT[2]));
                int sis = navn.size()-1;
                String sendServer = navn.get(sis)+"//"+startnr.get(sis)+"//"+fødselsdato.get(sis)+"//"+alder.get(sis)+"//"+stopTimes.get(sis);
                socket.emit("result", sendServer);
                layout.removeView(rows1);
            }
        });
        rows1.addView(stopTime);
        rows1.setLayoutParams(rowlayout2); layout.addView(rows1);
        antallDeltakere++;
    }
    public void setUpRows(){
        txt1.setText("Navn"); txt2.setText("Startnr."); txt3.setText("Stopp");
        txt1.setLayoutParams(rowlayout1); txt2.setLayoutParams(rowlayout1);
        txt3.setLayoutParams(rowlayout1);
        row1.addView(txt1); row1.addView(txt2);
        if (isnotfellesstart){
            txt21.setText("Start"); txt21.setLayoutParams(rowlayout1); row1.addView(txt21);
        }
        row1.addView(txt3); row1.setLayoutParams(rowlayout2); layout.addView(row1);
    }
    public int getAlder(String newfødselsdato){
        String[] tall = newfødselsdato.split("-");
        Calendar date = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        int år = Integer.parseInt(tall[2]); int måned = Integer.parseInt(tall[1])-1; int dag = Integer.parseInt(tall[0]);
        date.set(år, måned, dag);
        int age = today.get(Calendar.YEAR) - date.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < date.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        return ageInt;
    }
    public int convertCronToInt(Chronometer chro){
        //Tiden skal være 00:00:00 == hh:mm:ss
        String chroTime = chro.getText().toString();
        Log.d("MainActivity: ", "convertCronToInt:_ChroData: " + chroTime);
        String[] a = chroTime.split(":");
        int newTime = 0;
        if(a.length > 2){
            newTime = Integer.parseInt(a[2]);
            if (Integer.parseInt(a[1])>= 1){
                newTime += (60*Integer.parseInt(a[1]));
            }
            if (Integer.parseInt(a[0])>= 1){
                newTime += (60*Integer.parseInt(a[0]));
            }
        }else{
            newTime = Integer.parseInt(a[1]);
            if (Integer.parseInt(a[0])>= 1){
                newTime += (60*Integer.parseInt(a[0]));
            }
        }
        return newTime;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
    /*public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        Socket socket;

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */
                /*while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        public void closeConnection(){
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            //textResponse.setText(response);
            Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_LONG).show();
            super.onPostExecute(result);
        }

    }*/
}
