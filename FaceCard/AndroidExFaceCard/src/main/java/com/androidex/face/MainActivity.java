package com.androidex.face;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by cts on 17/5/26.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    public void initView() {
        Button btn_face_demo = (Button) findViewById(R.id.btn_face_demo);
        Button btn_facecard_demo = (Button) findViewById(R.id.btn_facecard_demo);

        btn_facecard_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FaceCardActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "人证合一", Toast.LENGTH_LONG).show();
            }
        });
        btn_face_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_face = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(intent_face);
                Toast.makeText(MainActivity.this, "人脸识别", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_face_demo://人脸识别
                Intent intent_face = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(intent_face);
                Toast.makeText(MainActivity.this, "人脸识别", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_facecard_demo://人证合一
                Intent intent = new Intent(MainActivity.this, FaceCardActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "人证合一", Toast.LENGTH_LONG).show();
                break;

        }
    }
}
