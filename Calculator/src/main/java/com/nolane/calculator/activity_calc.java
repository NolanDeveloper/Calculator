package com.nolane.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class activity_calc extends Activity {
    private static final int MAX_PRECISION = 10;
    private static final int MIN_PRECISION = 1;

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
        getMenuInflater().inflate(R.menu.calculator_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /* ... */
        return super.onPrepareOptionsMenu(menu);
    }

    private void SetPrecision(int n) {
        assert (n >= MIN_PRECISION && n <= MAX_PRECISION);
        getPreferences(MODE_PRIVATE).edit().putInt("precision", 3).commit();
        _calc.SetPrecision(n);
    }

    private int GetPrecision() {
        return getPreferences(MODE_PRIVATE).getInt("precision", 3);
    }

    class PrecisionChanger implements SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {

        private int _precision;
        private TextView _textView;

        PrecisionChanger(TextView view, Integer precision) {
            _textView = view;
            _precision = precision;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            _textView.setText("0.123456789123456789".subSequence(0, i + 3));
            _precision = i + 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            SetPrecision(_precision);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_set_precision:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.icon_change_precision);
                int currentValue = GetPrecision();
                View layout = getLayoutInflater().inflate(R.layout.precision_dialog, null);
                assert layout != null;
                final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
                final TextView textView = (TextView) layout.findViewById(R.id.textView);
                seekBar.setMax(MAX_PRECISION - MIN_PRECISION);
                seekBar.setProgress(currentValue - 1);
                PrecisionChanger listener = new PrecisionChanger(textView, currentValue);
                seekBar.setOnSeekBarChangeListener(listener);
                builder.setTitle(getResources().getString(R.string.text_set_precision_item))
                        .setPositiveButton(getResources().getString(R.string.text_ok_button),
                                listener)
                        .setNegativeButton(getResources().getString(R.string.text_cancel_button), null);
                textView.setText("0.123456789123456789".subSequence(0, currentValue + 2));
                builder.setView(layout);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
