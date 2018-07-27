package cn.mycommons.codeanalysis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int a_ = 100;
        int B = 200;
        int c = a_ + B;
        System.out.println(c);
    }
}