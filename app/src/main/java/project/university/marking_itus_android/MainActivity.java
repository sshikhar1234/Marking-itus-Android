package project.university.marking_itus_android;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    int counter = 0;
    String TAG = "Tag";
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.appCompatSeekBar)
    AppCompatSeekBar appCompatSeekBar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tvTimer)
    TextView tvTimer;


    //Setup background thread for updating time
    final Handler childHandler = new Handler();
    final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            counter = counter + 1;
            Log.d(TAG, "run: " + counter);
            updateUI();
            childHandler.postDelayed(periodicUpdate,1000);
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d(TAG, "onProgressChanged: ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: ");
            }
        });


    }

    private void decreaseTimer() {
       childHandler.postDelayed(periodicUpdate, 1000);
    }

    private void updateUI() {
        tvTimer.setText("Time: "+this.counter);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        decreaseTimer();
    }
}
