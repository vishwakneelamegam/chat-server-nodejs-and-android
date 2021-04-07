package com.sailors.live;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {
    TextView showData;
    EditText logData;
    WebSocket webSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showData = findViewById(R.id.showMessage);
        logData = findViewById(R.id.putData);
        logData.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                showData.append(logData.getText().toString() + "\n");
                webSocket.send(logData.getText().toString());
                logData.setText("");
                return true;
            }
            return false;
        });
        appWebSocket();
    }
    public void appWebSocket(){
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url("ws://192.168.29.194:3000").build();
        SocketListener socketListener = new SocketListener(this);
        webSocket = client.newWebSocket(req,socketListener);
    }
    public class SocketListener extends WebSocketListener{

        public  MainActivity activity;
        public SocketListener(MainActivity activity){
            this.activity = activity;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            activity.runOnUiThread(() -> showData.setText("connected\n"));
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            activity.runOnUiThread(() -> showData.append(text + "\n"));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            activity.runOnUiThread(() -> showData.setText("failed to connect\n"));
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            activity.runOnUiThread(() -> showData.setText("connection closed\n"));
        }
    }
}