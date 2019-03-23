package android.practice.g0ku.roundeprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    RoundProgressView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        view = findViewById(R.id.progress);

        view.setIndeterminsate(false);

        view.setMaxProgress(100);
        new Thread(new Runnable() {
            @Override
            public void run() {

                int progress = 0;
                while (progress <= 100) {
                    try {
                        progress++;
                        Thread.sleep(300);

                        final int finalProgress = progress;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalProgress <= 50)
                                    view.setIndeterminsate(true);
                                else
                                    view.setIndeterminsate(false);

                                view.setProgress(finalProgress);
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
