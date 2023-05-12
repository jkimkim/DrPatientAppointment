package app.web.jkimtech.drpatientappointment.controller;

import static app.web.jkimtech.drpatientappointment.model.Common.Common.step;
import static app.web.jkimtech.drpatientappointment.model.fragment.BookingStep1Fragment.spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;
import app.web.jkimtech.drpatientappointment.model.Common.NonSwipeViewPager;
import app.web.jkimtech.drpatientappointment.model.adapter.MyViewPagerAdapter;
import butterknife.Unbinder;

public class TestActivity extends AppCompatActivity {
    StepView stepView;
    NonSwipeViewPager viewPager;
    Button btn_previous_step;
    Button btn_next_step;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(step == 2){
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);
            }
            btn_next_step.setEnabled(true);
            setColorButton();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // init view
        stepView = findViewById(R.id.step_view);
        viewPager = findViewById(R.id.view_pager);
        btn_next_step  = findViewById(R.id.btn_next_step);
        btn_previous_step = findViewById(R.id.btn_previous_step);

        setupStepView();
        setColorButton();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // event
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setColorButton();
            }

            // when the page is selected
            @Override
            public void onPageSelected(int position) {
                // show the step
                stepView.go(position,true);
                if( position == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);
                if(position == 2)
                    btn_next_step.setEnabled(false);
                else
                    btn_next_step.setEnabled(true);
                setColorButton();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // next button
        btn_next_step.setOnClickListener(v -> {
            if(step < 3 || step == 0 ){
                step++ ;
                Common.Currentaappointementatype=spinner.getSelectedItem().toString();
                Log.e("Spinner", Common.Currentaappointementatype);

                if(step==1){
                    if(Common.CurrentDoctor != null) {
                        Common.currentTimeSlot = -1;
                        Common.currentDate = Calendar.getInstance();
                        loadTimeSlotOfDoctor(Common.CurrentDoctor);
                    }
                }
                else if(step == 2){
                    // if(Common.currentTimeSlot != -1)
                    confirmBooking();
                }
                viewPager.setCurrentItem(step);
            }
        });

        // previous button
        btn_previous_step.setOnClickListener(v -> {
            if(step == 3 || step > 0 ){
                step-- ;
                viewPager.setCurrentItem(step);
            }
        });

        loadTimeSlotOfDoctor("testdoc@testdoc.com");
    }

    // confirm the booking
    private void confirmBooking() {
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    // load the time slot of the doctor
    private void loadTimeSlotOfDoctor(String doctorId) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    // set the color of the buttons
    private void setColorButton() {
        if(btn_previous_step.isEnabled()){
            btn_previous_step.setBackgroundResource(R.color.colorPrimaryLight);
        }
        else{
            btn_previous_step.setBackgroundResource(R.color.colorAccent);
        }
        if(btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.colorPrimaryLight);
        }
        else{
            btn_next_step.setBackgroundResource(R.color.colorAccent);
        }
    }

    // set the steps
    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Purpose");
        stepList.add("Tme and Date");
        stepList.add("finish");
        stepView.setSteps(stepList);
    }

    // unregister the receiver
    @Override
    protected void onDestroy() {
        step = 0 ;
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }
}