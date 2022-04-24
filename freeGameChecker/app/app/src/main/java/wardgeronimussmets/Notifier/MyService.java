package wardgeronimussmets.Notifier;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {

    private FireBaseManager fireBaseManager;
    private ValueEventListener valueEventListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        valueEventListener = fireBaseManager.startGameTracking(getApplicationContext());
        return START_STICKY;

    }

    @Override
    public void onCreate() {
        fireBaseManager = new FireBaseManager();
    }

    @Override
    public void onDestroy() {
        fireBaseManager.removeValueEventListener(valueEventListener);
    }



}