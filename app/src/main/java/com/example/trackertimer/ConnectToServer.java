package com.example.trackertimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectToServer extends AppCompatActivity {

    EditText editTextAddress, editTextPort;
    Button buttonSet, buttonBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_socket);

        editTextAddress = findViewById(R.id.address);
        editTextPort = findViewById(R.id.port);
        buttonSet = findViewById(R.id.connect);
        buttonBack = findViewById(R.id.finish);

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String melding = editTextAddress.getText().toString()+"//"+editTextPort.getText().toString();
                intent.putExtra("setconnect", melding);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
