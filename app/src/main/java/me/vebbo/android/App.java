package me.vebbo.android;

import android.app.Application;
import java.net.URISyntaxException;

import co.paystack.android.PaystackSdk;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.PollingXHR;
import io.socket.engineio.client.transports.WebSocket;
import me.vebbo.android.utils.Constant;

import static me.vebbo.android.utils.GetTimeAgo.getTimeAgo;

public class App extends Application {

    protected Socket socketStream;
    protected static final int SUSPENSION_TIME = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        PaystackSdk.initialize(this);
        initialize();
    }

    public void initialize() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionDelay = 2000;
            options.transports = new String[]{WebSocket.NAME, PollingXHR.NAME};
            socketStream = IO.socket(Constant.URL, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocketStream() {
        return socketStream;
    }

    public boolean checkSuspension(long utc) {
        String date = getTimeAgo(utc, this);
        if (date.contains(getResources().getString(R.string.days_ago))) {
            String raw = date.replace(getResources().getString(R.string.days_ago), "").trim();
            int val = Integer.parseInt(raw);

            return val > SUSPENSION_TIME;
        } else
            return false;
    }
}

