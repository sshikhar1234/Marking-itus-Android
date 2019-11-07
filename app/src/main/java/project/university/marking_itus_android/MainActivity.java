package project.university.marking_itus_android;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {

    int counter = 0;
    String TAG = "Tag";
    int currentProgress = 0;
    int prevProgress = 0;

    int UPDATE_TIME_PERIOD = 2000;

    // MARK: Particle device-specific info
    private final String PARTICLE_USERNAME = "sshikharshah@gmail.com";
    private final String PARTICLE_PASSWORD = "Particle_2022";
    private final String DEVICE_ID = "3e0031001447363333343437";
    private ParticleDevice mDevice;

    //Setup background thread for updating time
    final Handler childHandler = new Handler();
    Runnable periodicUpdate;


    @BindView(R.id.button)
    Button button;
    @BindView(R.id.appCompatSeekBar)
    AppCompatSeekBar appCompatSeekBar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.slowDownTime)
    TextView slowDownTime;
    @BindView(R.id.textViewTimerUpdate)
    TextView textViewTimerUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this);

        // 2. Setup your device variable
        getDeviceFromCloud();
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
                //Remove the previous callbacks
                childHandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    int i = seekBar.getProgress();
                    Log.d(TAG, "onStopTrackingTouch: progress "+ seekBar.getProgress());
                    UPDATE_TIME_PERIOD = 2000+i;
                    slowDownTime.setText(String.format("Time slows down by : %d miliseconds", i));
                    Log.d(TAG, "onStopTrackingTouch: new update time "+ UPDATE_TIME_PERIOD);
                    //Add new callback with updated interval time
                    childHandler.postDelayed(periodicUpdate, UPDATE_TIME_PERIOD);
            }
        });
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    //Function to
    public void getDeviceFromCloud() {
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                mDevice = particleCloud.getDevice(DEVICE_ID);
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    //Function to call Particle's Exposed Function
    private void callParticleFunction(final String functionName) throws RejectedExecutionException {
        try {

            Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                    Log.d(TAG, "Availble functions: " + mDevice.getFunctions());
                    List<String> parameters = new ArrayList<>();
                    try {
                        mDevice.callFunction(functionName, parameters);
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }

                @Override
                public void onSuccess(Object o) {
                    // put your success message here
                    Log.d(TAG, "Success!");
                }

                @Override
                public void onFailure(ParticleCloudException exception) {
                    // put your error handling code here
                    Log.d(TAG, exception.getBestMessage());
                }
            });
        } catch (RejectedExecutionException e) {
            Log.d(TAG, "callParticleFunction: "+e.getLocalizedMessage());
        }
    }

    private void updateUIandCallFunction() {

        if (counter >= 0 && counter < 20) {

            counter = counter + 1;
            textViewTimerUpdate.setText(""+counter);
//            textViewTimer.setText(this.counter);
            if (isBetween(counter, 0, 2)) {
                callParticleFunction("Smile6");
//Smile6
            }
            if (isBetween(counter, 3, 5)) {
//Smile4
                Log.d(TAG, "decreaseTimer: 4");
                callParticleFunction("Smile4");

            }
            if (isBetween(counter, 6, 10)) {
//Smile3
                Log.d(TAG, "decreaseTimer: 3");
                callParticleFunction("Smile3");

            }
            if (isBetween(counter, 11, 13)) {
//Smile2
                Log.d(TAG, "decreaseTimer: 2");

                callParticleFunction("Smile2");
            }
            if (isBetween(counter, 14, 15)) {
//Smile1
                callParticleFunction("Smile1");
            }
            if (isBetween(counter, 16, 17)) {
//anger1
                callParticleFunction("Anger1");
            } else if (isBetween(counter, 18, 19)) {
//anger2
                callParticleFunction("Anger2");

            } else if (counter == 0) {
                callParticleFunction("reset");
            }
        }

        if (counter == 20) {
            callParticleFunction("resetAll");
        }
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        Log.d(TAG, "onViewClicked: ");
        periodicUpdate = () -> {
            Log.d(TAG, "insideHandler: ");
            updateUIandCallFunction();
            childHandler.postDelayed(periodicUpdate, UPDATE_TIME_PERIOD);
            textViewTimerUpdate = findViewById(R.id.textViewTimerUpdate);
//            Log.d(TAG, "onViewClicked: "+textViewTimerUpdate.setText("as"););
           // textViewTimerUpdate.setText(" "+counter);
        };
        childHandler.postDelayed(periodicUpdate, UPDATE_TIME_PERIOD);

    }


}
