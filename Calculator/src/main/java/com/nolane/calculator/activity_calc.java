package com.nolane.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
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
        View.OnFocusChangeListener tempListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) view.setBackgroundColor(getResources().getColor(R.color.button_focused));
            }
        };
        findViewById(R.id.button0).setOnClickListener(_calc);
        findViewById(R.id.button0).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button1).setOnClickListener(_calc);
        findViewById(R.id.button1).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button2).setOnClickListener(_calc);
        findViewById(R.id.button2).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button3).setOnClickListener(_calc);
        findViewById(R.id.button3).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button4).setOnClickListener(_calc);
        findViewById(R.id.button4).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button5).setOnClickListener(_calc);
        findViewById(R.id.button5).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button6).setOnClickListener(_calc);
        findViewById(R.id.button6).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button7).setOnClickListener(_calc);
        findViewById(R.id.button7).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button8).setOnClickListener(_calc);
        findViewById(R.id.button8).setOnFocusChangeListener(tempListener);
        findViewById(R.id.button9).setOnClickListener(_calc);
        findViewById(R.id.button9).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonPoint).setOnClickListener(_calc);
        findViewById(R.id.buttonPoint).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonPlus).setOnClickListener(_calc);
        findViewById(R.id.buttonPlus).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonMinus).setOnClickListener(_calc);
        findViewById(R.id.buttonMinus).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonMul).setOnClickListener(_calc);
        findViewById(R.id.buttonMul).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonDiv).setOnClickListener(_calc);
        findViewById(R.id.buttonDiv).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonAssign).setOnClickListener(_calc);
        findViewById(R.id.buttonAssign).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonPow).setOnClickListener(_calc);
        findViewById(R.id.buttonPow).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonSqrt).setOnClickListener(_calc);
        findViewById(R.id.buttonSqrt).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonDelete).setOnClickListener(_calc);
        findViewById(R.id.buttonDelete).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonClear).setOnClickListener(_calc);
        findViewById(R.id.buttonClear).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonM).setOnClickListener(_calc);
        findViewById(R.id.buttonM).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonMplus).setOnClickListener(_calc);
        findViewById(R.id.buttonMplus).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonMminus).setOnClickListener(_calc);
        findViewById(R.id.buttonMminus).setOnFocusChangeListener(tempListener);
        findViewById(R.id.buttonPlusMinus).setOnClickListener(_calc);
        findViewById(R.id.buttonPlusMinus).setOnFocusChangeListener(tempListener);
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
        _calc.SetPrecision(n);
    }

    private int GetPrecision() {
        return getPreferences(MODE_MULTI_PROCESS).getInt("precision", 2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menu_item_set_precision:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.text_set_precision_item))
                            .setPositiveButton(getResources().getString(R.string.text_ok_button), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.text_cancel_button), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    int currentValue;
                    if (Build.VERSION.SDK_INT >= 11) {
                        currentValue = GetPrecision();
                        View layout = getLayoutInflater().inflate(R.layout.precision_dialog, null);
                        assert layout != null;
                        final NumberPicker numberPicker = (NumberPicker) layout.findViewById(R.id.numberPicker);
                        numberPicker.setMinValue(MIN_PRECISION);
                        numberPicker.setMaxValue(MAX_PRECISION);
                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                                SetPrecision(newVal);
                            }
                        });
                        numberPicker.setValue(currentValue);
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                SetPrecision(numberPicker.getValue());
                            }
                        });
                        builder.setView(layout);
                    } else {
                        currentValue = GetPrecision();
                        View layout = getLayoutInflater().inflate(R.layout.precision_dialog_v7, null);
                        assert layout != null;
                        final TextView textView = (TextView) layout.findViewById(R.id.textView);
                        textView.setText("0.123456".subSequence(0, currentValue + 2));
                        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
                        seekBar.setMax(MAX_PRECISION - MIN_PRECISION);
                        seekBar.setProgress(currentValue - 1);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                textView.setText("0.123456".subSequence(0, i + 3));
                                SetPrecision(i + 1);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        builder.setView(layout);
                    }
                    builder.show();
                    break;
                case R.id.menu_item_about:
                    startActivity(new Intent(this, activity_about.class));
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }
}
