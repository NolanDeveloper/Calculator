package com.nolane.calculator;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@SuppressWarnings("ConstantConditions")
public final class Calculator implements OnClickListener {

    private static final int MAX_NUMBER_LENGTH = 30;
    private static final int MAX_PRECISION = 6;
    private static final CharSequence TOO_LONG_VALUE_TEXT = "too long value";
    private static final CharSequence INFINITY_TEXT = "∞";

    private static BigDecimal MaxNumber;

    private static class ZeroDivisionException extends ArithmeticException {
    }

    private static class TooLongValueException extends ArithmeticException {
    }

    private static enum States {
        GET_NUMBER, GET_OPERATION
    }

    private static enum Operations {
        PLUS, MINUS, DIV, MUL, POW, NOP
    }

    private TextView _numberTextView;
    private Activity _activity;
    private States _currentState;
    private Operations _currentOperation;
    private BigDecimal _leftArgument;
    private BigDecimal _memory;

    public Calculator(TextView numberTextView, Activity activity) {
        super();
        if (MaxNumber == null) {
            String maxNumber = "";
            for (int i = 0; i < MAX_NUMBER_LENGTH; i++)
                maxNumber += '9';
            MaxNumber = new BigDecimal(maxNumber);
        }
        _activity = activity;
        _numberTextView = numberTextView;
        _leftArgument = new BigDecimal(BigInteger.ZERO);
        _memory = new BigDecimal(BigInteger.ZERO);
        _currentState = States.GET_NUMBER;
        _currentOperation = Operations.NOP;
    }

    private static BigDecimal pow(BigDecimal base, int power) throws TooLongValueException {
        if (power == 0)
            return BigDecimal.valueOf(1);
        else if (power == 1)
            return base;
        else if (power < 0)
            return BigDecimal.valueOf(1).divide(pow(base, -power));
        else {
            BigDecimal result = new BigDecimal(1);
            for (int i = 0; i < power; i++) {
                result = result.multiply(base).stripTrailingZeros();
                if (result.toString().length() > MAX_NUMBER_LENGTH) {
                    result = round(result);
                    if (result.toString().length() > MAX_NUMBER_LENGTH)
                        throw new TooLongValueException();
                }
            }
            return result;
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private static BigDecimal round(BigDecimal number) {
        return number.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
    }

    private static boolean IsInteger(BigDecimal number) {
        return number.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(number) == 0;
    }

    private void AddSymbol(char c) {
        if (_numberTextView.length() + 1 < MAX_NUMBER_LENGTH)
            _numberTextView.append(Character.toString(c));
    }

    private BigDecimal GetValue() {
        if (_numberTextView.getText() == TOO_LONG_VALUE_TEXT || _numberTextView.getText() == INFINITY_TEXT) {
            return BigDecimal.ZERO;
        } else
            return new BigDecimal(_numberTextView.getText().toString());
    }

    private void SetZero() {
        _numberTextView.setText("0");
    }

    private void Clear() {
        _numberTextView.setText(null);
    }

    private void SetValue(BigDecimal value) {
        if (value.compareTo(BigDecimal.valueOf(0)) == 0)
            SetZero();
        else if (value.toBigInteger().toString().length() > MAX_NUMBER_LENGTH)
            throw new TooLongValueException();
        else {
            value = value.stripTrailingZeros();
            if (IsInteger(value)) {
                _numberTextView.setText(value.toBigInteger().toString());
            } else {
                _numberTextView.setText(value.toString());
                _numberTextView.getEllipsize();
            }
        }
    }

    private BigDecimal Calculate() throws ZeroDivisionException {
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
                if (rightArgument.compareTo(BigDecimal.ZERO) != 0) {
                    result = _leftArgument.divide(rightArgument, MAX_PRECISION,
                            BigDecimal.ROUND_HALF_UP);
                    result = result.stripTrailingZeros();
                } else {
                    throw new ZeroDivisionException();
                }
                break;
            case MUL:
                result = _leftArgument.multiply(rightArgument);
                break;
            case POW:
                if ((_leftArgument.compareTo(BigDecimal.valueOf(Math.pow(MaxNumber.doubleValue(),
                        0.5)).setScale(MAX_PRECISION, RoundingMode.HALF_UP)) == 0 &&
                        rightArgument.compareTo(BigDecimal.valueOf(2)) > 0))
                    throw new TooLongValueException();
                else {
                    if (IsInteger(rightArgument))
                        result = pow(_leftArgument, rightArgument.intValue());
                    else {
                        result = BigDecimal.valueOf(Math.pow(_leftArgument.doubleValue(),
                                rightArgument.doubleValue()));
                    }
                }
                break;
        }
        result = round(result);
        if (result.toString().length() > MAX_NUMBER_LENGTH)
            throw new TooLongValueException();
        return result;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button0:
                    if (_currentState != States.GET_NUMBER) {
                        _currentState = States.GET_NUMBER;
                        Clear();
                    }
                    if (_numberTextView.getText().toString().compareTo("0") != 0)
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
                    if (_numberTextView.getText().toString().compareTo("0") == 0)
                        Clear();
                    AddSymbol(((Button) v).getText().charAt(0));
                    break;
                case R.id.buttonPoint:
                    if (_currentState != States.GET_NUMBER)
                        _currentState = States.GET_NUMBER;
                    if (!_numberTextView.getText().toString().contains(Character.toString('.')))
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
                    _leftArgument = GetValue();
                    _leftArgument = BigDecimal.valueOf(Math.sqrt(_leftArgument.doubleValue()));
                    round(_leftArgument);
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
                    _leftArgument = BigDecimal.ZERO;
                    SetZero();
                    break;
                case R.id.buttonDelete:
                    if (_numberTextView.getText().length() > 1) {
                        _currentState = States.GET_NUMBER;
                        _numberTextView.setText(_numberTextView.getText().toString().substring(0, _numberTextView.length() - 1));
                        _leftArgument = GetValue();
                    } else if (_numberTextView.getText().length() == 1)
                        SetZero();
                    break;
                case R.id.buttonPlusMinus:
                    if (GetValue().compareTo(BigDecimal.valueOf(0)) != 0 && _numberTextView.length() < MAX_NUMBER_LENGTH - 1)
                        _numberTextView.setText(_numberTextView.getText().charAt(0) == '-' ? _numberTextView.getText().subSequence(1, _numberTextView.getText().length()) : "-" + _numberTextView.getText());
                    break;
                case R.id.buttonM:
                    SetValue(_memory);
                    break;
                case R.id.buttonMplus:
                    _memory = _memory.add(GetValue());
                    break;
                case R.id.buttonMminus:
                    _memory = _memory.subtract(GetValue());
                    break;
            }
        } catch (ZeroDivisionException ex) {
            _numberTextView.setText(INFINITY_TEXT);
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (TooLongValueException ex) {
            _numberTextView.setText(TOO_LONG_VALUE_TEXT);
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (NullPointerException ex) {
            _activity.finish();
        }
    }

    public void BindView(TextView text) {
        CharSequence content = _numberTextView.getText();
        _numberTextView = text;
        _numberTextView.setText(content);

    }

}
