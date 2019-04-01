package io.github.trytonvanmeer.libretrivia.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import io.github.trytonvanmeer.libretrivia.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import java.util.Set;
import android.widget.Toast;


public class ShareActivity extends BaseActivity {

    private BluetoothAdapter bluetoothAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Redirect user to ViewCustomQuestions to share
        Intent viewCustomQuestions = new Intent(this, QuestionViewActivity.class);
        startActivity(viewCustomQuestions);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
