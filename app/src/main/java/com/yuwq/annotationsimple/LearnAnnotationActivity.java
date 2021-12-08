package com.yuwq.annotationsimple;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yuwq.apt_annotation.BindView;

/**
 * @author liuyuzhe
 */
public class LearnAnnotationActivity extends AppCompatActivity {

    @BindView(R.id.btn_click)
    Button btnClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_annotation);
//        LearnAnnotationActivityView
        ButterKnife.bind(this);
        if (btnClick == null){
            return;
        }
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "bind view", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
