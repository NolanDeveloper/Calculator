package com.nolane.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class activity_calc extends Activity {
    private static Calculator _calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calc);
        TextView textView = (TextView) findViewById(R.id.textViewCurNum);
        if (_calc == null)
            _calc = new Calculator(textView, this);
        else
            _calc.BindView(textView);
        findViewById(R.id.button0).setOnClickListener(_calc);
        findViewById(R.id.button1).setOnClickListener(_calc);
        findViewById(R.id.button2).setOnClickListener(_calc);
        findViewById(R.id.button3).setOnClickListener(_calc);
        findViewById(R.id.button4).setOnClickListener(_calc);
        findViewById(R.id.button5).setOnClickListener(_calc);
        findViewById(R.id.button6).setOnClickListener(_calc);
        findViewById(R.id.button7).setOnClickListener(_calc);
        findViewById(R.id.button8).setOnClickListener(_calc);
        findViewById(R.id.button9).setOnClickListener(_calc);
        findViewById(R.id.buttonPoint).setOnClickListener(_calc);
        findViewById(R.id.buttonPlus).setOnClickListener(_calc);
        findViewById(R.id.buttonMinus).setOnClickListener(_calc);
        findViewById(R.id.buttonMul).setOnClickListener(_calc);
        findViewById(R.id.buttonDiv).setOnClickListener(_calc);
        findViewById(R.id.buttonAssign).setOnClickListener(_calc);
        findViewById(R.id.buttonPow).setOnClickListener(_calc);
        findViewById(R.id.buttonSqrt).setOnClickListener(_calc);
        findViewById(R.id.buttonDelete).setOnClickListener(_calc);
        findViewById(R.id.buttonClear).setOnClickListener(_calc);
        findViewById(R.id.buttonM).setOnClickListener(_calc);
        findViewById(R.id.buttonMplus).setOnClickListener(_calc);
        findViewById(R.id.buttonMminus).setOnClickListener(_calc);
        findViewById(R.id.buttonPlusMinus).setOnClickListener(_calc);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* ... */
        return super.onCreateOptionsMenu(menu);
    }
}
