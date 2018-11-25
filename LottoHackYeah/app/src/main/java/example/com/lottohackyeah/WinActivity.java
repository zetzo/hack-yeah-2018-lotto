package example.com.lottohackyeah;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class WinActivity extends AppCompatActivity {

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = true;
        setContentView(R.layout.activity_win);

        // change color thread
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutWin);
                    while(running == true) {
                        layout.setBackgroundColor(Color.YELLOW);
                        Log.d("new color", "new color");
                        Thread.sleep(500);

                        layout.setBackgroundColor(Color.RED);
                        Log.d("new color", "new color");
                        Thread.sleep(500);

                        layout.setBackgroundColor(Color.GREEN);
                        Log.d("new color", "new color");
                        Thread.sleep(500);


                        layout.setBackgroundColor(Color.BLUE);
                        Log.d("new color", "new color");
                        Thread.sleep(500);


                        layout.setBackgroundColor(Color.WHITE);
                        Log.d("new color", "new color");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

}
