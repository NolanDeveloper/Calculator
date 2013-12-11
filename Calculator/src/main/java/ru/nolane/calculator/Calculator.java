package ru.nolane.calculator;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Calculator implements OnClickListener {

    public static final int MAX_NUMBER_LENGTH = 20;
    public static final int MAX_PRECISION = 6;
    public static final CharSequence TOO_LONG_VALUE_TEXT = "TOO LONG VALUE";

    private static enum States {
        GET_NUMBER, GET_OPERATION
    }

    private static enum Operations {
        PLUS, MINUS, DIV, MUL, POW, NOP
    }

    private TextView _value;
    private States _currentState;
    private Operations _currentOperation;
    private BigDecimal _leftArgument;

    public Calculator(TextView value) {
        super();
        _value = value;
        _leftArgument = new BigDecimal("0");
        _currentState = States.GET_NUMBER;
        _currentOperation = Operations.NOP;
    }

    private void AddSymbol(char c) {
        if (_value.length() + 1 < MAX_NUMBER_LENGTH)
            _value.append(Character.toString(c));
    }

    private BigDecimal GetValue() {
        if (_value.getText() == TOO_LONG_VALUE_TEXT) {
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.valueOf(0);
            return BigDecimal.valueOf(0);
        } else
            return new BigDecimal(_value.getText().toString());
    }

    private void SetZero() {
        _value.setText("0");
    }

    private void Clear() {
        _value.setText(null);
    }

    private void SetValue(BigDecimal value) {
        if (value.compareTo(BigDecimal.valueOf(0)) == 0)
            SetZero();
        else {
            value = value.stripTrailingZeros();
            if (value.setScale(0, RoundingMode.HALF_UP).compareTo(value) == 0) {
                _value.setText(value.toBigInteger().toString().length() < MAX_NUMBER_LENGTH ? value.toBigInteger().toString() : TOO_LONG_VALUE_TEXT);
            } else {
                _value.setText(value.toString().length() < MAX_NUMBER_LENGTH ? value.toString() : TOO_LONG_VALUE_TEXT);
                _value.getEllipsize();
            }
        }
    }

    private BigDecimal Calculate() {
        BigDecimal rightArgument = GetValue();
        BigDecimal result = rightArgument;
        switch (_currentOperation) {
            case PLUS:
                result = _leftArgument.add(rightArgument);
                break;
            case MINUS:
                result = _leftArgument.subtract(rightArgument);
                break;
            case DIV:
                result = _leftArgument.divide(rightArgument, MAX_PRECISION, RoundingMode.FLOOR);
                result = result.stripTrailingZeros();
                break;
            case MUL:
                result = _leftArgument.multiply(rightArgument);
                break;
            case POW:
                result = BigDecimal.valueOf(Math.pow(_leftArgument.doubleValue(),
                        rightArgument.doubleValue()));
                break;
            case NOP:
                break;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button0:
                if (_currentState != States.GET_NUMBER) {
                    _currentState = States.GET_NUMBER;
                    Clear();
                }
                if (_value.getText().toString().compareTo("0") != 0)
                    AddSymbol('0');
                break;
            case R.id.button1:
            case R.id.button2:
            case R.id.button3:
            case R.id.button4:
            case R.id.button5:
            case R.id.button6:
            case R.id.button7:
            case R.id.button8:
            case R.id.button9:
                if (_currentState != States.GET_NUMBER) {
                    _currentState = States.GET_NUMBER;
                    Clear();
                }
                if (_value.getText().toString().compareTo("0") == 0)
                    Clear();
                AddSymbol(((Button) v).getText().charAt(0));
                break;
            case R.id.buttonPoint:
                if (_currentState != States.GET_NUMBER)
                    _currentState = States.GET_NUMBER;
                if (!_value.getText().toString().contains(Character.toString('.')))
                    AddSymbol('.');
                break;
            case R.id.buttonPlus:
                if (_currentState == States.GET_NUMBER) {
                    _leftArgument = Calculate();
                    SetValue(_leftArgument);
                    _currentState = States.GET_OPERATION;
                }
                _currentOperation = Operations.PLUS;
                break;
            case R.id.buttonMinus:
                if (_currentState == States.GET_NUMBER) {
                    _leftArgument = Calculate();
                    SetValue(_leftArgument);
                    _currentState = States.GET_OPERATION;
                }
                _currentOperation = Operations.MINUS;
                break;
            case R.id.buttonMul:
                if (_currentState == States.GET_NUMBER) {
                    _leftArgument = Calculate();
                    SetValue(_leftArgument);
                    _currentState = States.GET_OPERATION;
                }
                _currentOperation = Operations.MUL;
                break;
            case R.id.buttonDiv:
                if (_currentState == States.GET_NUMBER) {
                    _leftArgument = Calculate();
                    SetValue(_leftArgument);
                    _currentState = States.GET_OPERATION;
                }
                _currentOperation = Operations.DIV;
                break;
            case R.id.buttonPow:
                if (_currentState == States.GET_NUMBER) {
                    _leftArgument = Calculate();
                    SetValue(_leftArgument);
                    _currentState = States.GET_OPERATION;
                }
                _currentOperation = Operations.POW;
                break;
            case R.id.buttonSqrt:
                _leftArgument = Calculate();
                _leftArgument = BigDecimal.valueOf(Math.sqrt(_leftArgument.doubleValue()));
                _leftArgument = _leftArgument.setScale(MAX_PRECISION, RoundingMode.FLOOR);
                _leftArgument = _leftArgument.stripTrailingZeros();
                _currentState = States.GET_OPERATION;
                _currentOperation = Operations.NOP;
                SetValue(_leftArgument);
                break;
            case R.id.buttonAssign:
                _leftArgument = Calculate();
                SetValue(_leftArgument);
                _currentState = States.GET_OPERATION;
                _currentOperation = Operations.NOP;
                break;
            case R.id.buttonClear:
                _currentState = States.GET_NUMBER;
                _currentOperation = Operations.NOP;
                _leftArgument = BigDecimal.valueOf(0);
                SetZero();
                break;
            case R.id.buttonDelete:
                if (_value.getText().length() > 1) {
                    _currentState = States.GET_NUMBER;
                    _value.setText(_value.getText().toString().substring(0, _value.length() - 1));
                    _leftArgument = GetValue();
                } else if (_value.getText().length() == 1)
                    SetZero();
                break;
        }
    }

    public void BindView(TextView text) {
        CharSequence content = _value.getText();
        _value = text;
        _value.setText(content);

    }

}
