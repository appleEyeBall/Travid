package com.example.hellojava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView screenText;
    ArrayList<String> queue = new ArrayList<>();
    String number = "";
    String result = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenText = findViewById(R.id.calc_screen);
        ImageView zero_btn = findViewById(R.id.zero_btn);
        ImageView one_btn = findViewById(R.id.one_btn);
        ImageView two_btn = findViewById(R.id.two_btn);
        ImageView three_btn = findViewById(R.id.three_btn);
        ImageView four_btn = findViewById(R.id.four_btn);
        ImageView five_btn = findViewById(R.id.five_btn);
        ImageView six_btn = findViewById(R.id.six_btn);
        ImageView seven_btn = findViewById(R.id.seven_btn);
        ImageView eight_btn = findViewById(R.id.eight_btn);
        ImageView nine_btn = findViewById(R.id.nine_btn);
        ImageView dot_btn = findViewById(R.id.dot_btn);
        ImageView plus_btn = findViewById(R.id.plus_btn);
        ImageView minus_btn = findViewById(R.id.minus_btn);
        ImageView times_btn = findViewById(R.id.times_btn);
        ImageView divide_btn = findViewById(R.id.divide_btn);
        ImageView equals_btn = findViewById(R.id.equals_btn);

//        Long click clears the screen
        screenText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                number = "";
                result = "";
                queue.clear();
                ((TextView) view).setText("0");
                return true;
            }
        });

        zero_btn.setOnClickListener(this);
        one_btn.setOnClickListener(this);
        two_btn.setOnClickListener(this);
        three_btn.setOnClickListener(this);
        four_btn.setOnClickListener(this);
        five_btn.setOnClickListener(this);
        six_btn.setOnClickListener(this);
        seven_btn.setOnClickListener(this);
        eight_btn.setOnClickListener(this);
        nine_btn.setOnClickListener(this);
        dot_btn.setOnClickListener(this);
        plus_btn.setOnClickListener(this);
        minus_btn.setOnClickListener(this);
        times_btn.setOnClickListener(this);
        divide_btn.setOnClickListener(this);
        equals_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.zero_btn:
                number+="0";
                break;
            case R.id.one_btn:
                number+="1";
                break;
            case R.id.two_btn:
                number+="2";
                break;
            case R.id.three_btn:
                number+="3";
                break;
            case R.id.four_btn:
                number+="4";
                break;
            case R.id.five_btn:
                number+="5";
                break;
            case R.id.six_btn:
                number+="6";
                break;
            case R.id.seven_btn:
                number+="7";
                break;
            case R.id.eight_btn:
                number+="8";
                break;
            case R.id.nine_btn:
                number+="9";
                break;
            case R.id.dot_btn:
                number+=".";
                break;
            case R.id.plus_btn:
                operatorPress("+");
                break;
            case R.id.minus_btn:
                operatorPress("-");
                break;
            case R.id.times_btn:
                operatorPress("*");
                break;
            case R.id.divide_btn:
                operatorPress("/");
                break;
            case R.id.equals_btn:
                if (!number.equals("")) queue.add(number);
                execute();
                break;
        }

//        Display 'number' without displaying empty string
        if (number.equals("")) {
            if(result.equals("")) screenText.setText("0");
            else screenText.setText(result);
        }
        else {
//            if "=" has just been recently pressed, override
            if(queue.size()==1) {
                queue.clear();
                result = "";
            }

            screenText.setText(number);
        }

    }

    private boolean isOperator(String value){
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/");
    }


    private void operatorPress(String operator){
        int lastpos = Math.max(queue.size() - 1, 0);

//        TODO: FIx not working for 1+2+3...
//        Log.v("queue", "Inspecting..."+queue+ " "+number);
//        if (!queue.isEmpty() && isOperator(queue.get(lastpos))) return;      // if there is an unprocessed operator in queue

        if (!number.equals("")) queue.add(number);      // e.g 1 +
        else if (queue.isEmpty()) queue.add("0");   // e.g +
        execute();
        queue.add(operator);
    }

    private void operatorPress(){
        if (!number.equals("")) queue.add(number);
        execute();

    }

    private void execute(){
        Log.i("queue", String.valueOf(queue));
//        do nothing if we don't have all three values
        if (queue.size() != 3){
            Log.i("queue", "not 3 values");
            number = "";
            return;
        }

        Log.i("queue", "is 3 values");
//        initialize all three values
        double first = Double.valueOf(queue.get(0));
        String operator = queue.get(1);
        double second = Double.valueOf(queue.get(2));

        switch (operator){
            case"+":
                result = String.valueOf(first+second);
                break;
            case "-":
                result = String.valueOf(first-second);
                break;
            case "*":
                result = String.valueOf(first*second);
                break;
            case "/":
                result = String.valueOf(first/second);
                break;
            default:
                result = "0";
        }

//        add result to the queue and make number empty
        queue.clear();
        queue.add(result);
        number = "";
        Log.i("queue", String.valueOf(queue));


    }


}
