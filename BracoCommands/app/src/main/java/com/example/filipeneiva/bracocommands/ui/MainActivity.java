package com.example.filipeneiva.bracocommands.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filipeneiva.bracocommands.R;
import com.example.filipeneiva.bracocommands.connection.ConnectionThread;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView txtMostraFala;
    private ImageButton btnFala;
    private final int req_codigo_entrada_fala = 100;

    static TextView statusMessage;
    static TextView counterMessage;
    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMostraFala = findViewById(R.id.txtMostraFala);
        btnFala = findViewById(R.id.btnFala);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this, "Que pena! Hardware Bluetooth não está funcionando", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ótimo! Hardware Bluetooth está funcionando", Toast.LENGTH_SHORT).show();
        }

        btAdapter.enable();

        connect = new ConnectionThread("00:14:03:18:43:45");
        connect.start();

        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }

        btnFala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));
                try {
                    startActivityForResult(intent, req_codigo_entrada_fala);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.erro),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case req_codigo_entrada_fala: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtMostraFala.setText(result.get(0).toUpperCase());
                    connect.write(result.get(0).toUpperCase().getBytes());
                }
                break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                 return; // Ocorreu um erro durante a conexão
            else if(dataString.equals("---S"))
                return; // Conectado
            else {

                counterMessage.setText(dataString);
            }

        }
    };
}