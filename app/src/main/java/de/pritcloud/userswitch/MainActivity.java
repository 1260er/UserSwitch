package de.pritcloud.userswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("UserSwitch");
        text.setTextSize(24);
        text.setGravity(Gravity.CENTER);

        setContentView(text);
    }
}
