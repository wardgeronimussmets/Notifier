package wardgeronimussmets.Notifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewUserActivity extends AppCompatActivity {

    private TextView txtUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        System.out.println("New user");
        txtUserName = findViewById(R.id.userNameField);
    }

    public void onSaveButtonClicked(View caller){
        FireBaseManager fireBaseManager = new FireBaseManager();
        String userKey = fireBaseManager.createNewUser(txtUserName.getText().toString());
        new SharedLoader(getApplicationContext()).putNewUser(userKey);
    }
}
