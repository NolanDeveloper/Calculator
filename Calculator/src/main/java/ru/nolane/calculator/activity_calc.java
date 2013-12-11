package ru.nolane.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class activity_calc extends Activity {
    private static Calculator _calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calc);
        TextView textView = (TextView) findViewById(R.id.textViewCurNum);
        if (_calc == null)
            _calc = new Calculator(textView);
        else
            _calc.BindView(textView);
        ((Button) findViewById(R.id.button0)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button1)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button2)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button3)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button4)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button5)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button6)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button7)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button8)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.button9)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonPoint)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonPlus)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonMinus)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonMul)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonDiv)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonAssign)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonPow)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonSqrt)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonDelete)).setOnClickListener(_calc);
        ((Button) findViewById(R.id.buttonClear)).setOnClickListener(_calc);
    }

    @Override
    public void onStop() {

        super.onStop();
    }
}
