package com.example.testalarm2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.InFilter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.zip.Inflater;

public class SettingAlarm extends MainActivity {
    private static final String TAG="SettingAlarm";
    private FirebaseFirestore firebaseFirestore;
    private TimePicker timePicker;
    private EditText drugEditText;
    private AlarmInfo alarmInfo2; //database에 올린 결과들을 가져오는 변수
    private AlarmManager alarmManager;
    private String hour, minute;
    private int notificationId;

    private String notificationText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_alarm);

        timePicker = findViewById(R.id.timepicker);
        drugEditText = findViewById(R.id.editText);
        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmInfo2 = (AlarmInfo)getIntent().getSerializableExtra("alarmInfo");

        findViewById(R.id.btnset).setOnClickListener(onClickListener);

    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() ==R.id.btnset){
                contentsUpdate();
                setAlarm();
                myStartActivity(MainActivity.class);
            }
        }
    };
    private void setAlarm(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("AlarmDemo").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                //데이터를 다 가져와 postList배열에 넣어줌.
                                notificationText = document.getData().get("drugtext").toString();
                                hour = document.getData().get("hour").toString();
                                minute = document.getData().get("minute").toString();
                              Log.e("data : ",notificationText);
                                Log.e("data : ",hour);
                                Log.e("data : ",minute);

                            }
                        }
                    }
                });

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.O){
            Toast.makeText(this,"버전을 확인해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        hour = timePicker.getCurrentHour().toString();
        minute = timePicker.getCurrentMinute().toString();
        String drugtext = drugEditText.getText().toString();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(Calendar.SECOND,0);

        long intervalTime = 1000*24*60*60;
        long currentTime = System.currentTimeMillis();

        if(currentTime>calendar.getTimeInMillis()){
            //알림설정한 시간이 이미 지나간 시간이라면 하루뒤로 알림설정하도록함.
            calendar.setTimeInMillis(calendar.getTimeInMillis()+intervalTime);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);


        intent.putExtra("id", notificationId );
        intent.putExtra("drug", notificationText);

        PendingIntent pIntent = PendingIntent.getBroadcast(this, notificationId ,intent,0);
        notificationId ++;
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pIntent);
        Toast.makeText(this,"알림이 설정되었습니다.",Toast.LENGTH_SHORT).show();


    }

    private void contentsUpdate(){
        TimePicker timePicker = (TimePicker)findViewById(R.id.timepicker);
        final String hour = timePicker.getCurrentHour().toString();
        final String minute = timePicker.getCurrentMinute().toString();
        final String drugText = ((EditText)findViewById(R.id.editText)).getText().toString();


        //시,분이 입력되었을때
        if(hour.length()>0 && minute.length()>0){
            FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
            AlarmInfo alarmInfo = new AlarmInfo(hour,minute,drugText);
            uploader(alarmInfo);//시,분,약이름이 uploader로 들어감.
        }else{
            Toast.makeText(this,"알림시간을 설정해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    //저장 버튼을 누르면 hour,minute,drugtext를 파이어베이스에 넘어감
    private void uploader(AlarmInfo alarmInfo){
        //contentsupdte에서 받아온 정보들을 입력함.
        firebaseFirestore = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = alarmInfo2 ==null? firebaseFirestore.collection("AlarmDemo").document()
                :firebaseFirestore.collection("AlarmDemo").document(alarmInfo2.getId());

        documentReference.set(alarmInfo.getAlarmInfo())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG,"id");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"ERROR",e);
                    }
                });
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivityForResult(intent,1);
    }
}
