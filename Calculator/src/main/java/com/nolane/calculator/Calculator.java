package com.nolane.calculator;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@SuppressWarnings("ConstantConditions")
public final class Calculator implements OnClickListener {

    private int MAX_NUMBER_LENGTH = 30;
    private final CharSequence WRONG_ARGUMENT_TEXT;
    private final CharSequence TOO_LONG_VALUE_TEXT;
    private final CharSequence INFINITY_TEXT;

    private static BigDecimal _maxNumber;

    private static class ZeroDivisionException extends ArithmeticException {
    }

    private static class SquareRootOfNegativeNumber extends ArithmeticException {
    }

    private static class TooLongValueException extends Exception {
    }

    private static class CustomButtonAnimation extends Animation {

        View _object;

        int _from;
        int _to;

        public CustomButtonAnimation() {
            super();
            _object = null;
            _from = 0;
            _to = 0;
        }

        public void setFromColor(int from) {
            _from = from;
        }

        public void setToColor(int to) {
            _to = to;
        }

        public void setView(View objectToAnimate) {
            _object = objectToAnimate;
        }

        @SuppressWarnings("RedundantCast")
        private int interpolateColor(int a, int b, float proportion) {
            int startA = (a >> 24) & 0xff;
            int startR = (a >> 16) & 0xff;
            int startG = (a >> 8) & 0xff;
            int startB = a & 0xff;
            int endA = (b >> 24) & 0xff;
            int endR = (b >> 16) & 0xff;
            int endG = (b >> 8) & 0xff;
            int endB = b & 0xff;

            return (int) ((startA + (int) (proportion * (endA - startA))) << 24) |
                    (int) ((startR + (int) (proportion * (endR - startR))) << 16) |
                    (int) ((startG + (int) (proportion * (endG - startG))) << 8) |
                    (int) ((startB + (int) (proportion * (endB - startB))));
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (_object != null) {
                _object.setBackgroundColor(interpolateColor(_from, _to, interpolatedTime));
                _object.invalidate();
            }
            super.applyTransformation(interpolatedTime, t);
        }
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
    private int _precision;

    public Calculator(TextView numberTextView, Activity activity) {
        super();

        WRONG_ARGUMENT_TEXT = activity.getResources().getString(R.string.text_wrong_argument);
        TOO_LONG_VALUE_TEXT = activity.getResources().getString(R.string.text_too_long_value);
        INFINITY_TEXT = activity.getResources().getString(R.string.text_infinity);

        if (_maxNumber == null) {
            String maxNumber = "";
            for (int i = 0; i < MAX_NUMBER_LENGTH; i++)
                maxNumber += '9';
            _maxNumber = new BigDecimal(maxNumber);
        }
        _activity = activity;
        _numberTextView = numberTextView;
        _leftArgument = new BigDecimal(BigInteger.ZERO);
        _memory = new BigDecimal(BigInteger.ZERO);
        _currentState = States.GET_NUMBER;
        _currentOperation = Operations.NOP;
        _precision = activity.getPreferences(Context.MODE_PRIVATE).getInt("precision", 2);
    }

    private BigDecimal pow(BigDecimal base, int power) throws TooLongValueException {
        if (base.compareTo(BigDecimal.ZERO) == 0)
            return new BigDecimal(0);
        else if (base.compareTo(BigDecimal.ONE) == 0)
            return new BigDecimal(1);
        else if (power == 0)
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

    private static int GetLength(BigDecimal value) {
        int len = value.toString().replace(" ", "").length();
        if (len == 0)
            return 0;
        else
            return len - (value.toString().charAt(0) == '-' ? 1 : 0);
    }

    private static int GetLength(String value) {
        int len = value.replace(" ", "").length();
        if (len == 0)
            return 0;
        else
            return len - (value.charAt(0) == '-' ? 1 : 0);
    }

    private static int GetLength(TextView value) {
        int len = value.getText().toString().replace(" ", "").length();
        if (len == 0)
            return 0;
        else
            return len - (value.getText().charAt(0) == '-' ? 1 : 0);
    }

    public void SetPrecision(int precision) {
        _precision = precision;
    }

    private BigDecimal round(BigDecimal number) {
        return new BigDecimal(StripTailingZeros(number.setScale(_precision, RoundingMode.HALF_UP).toString()));
    }

    private static boolean IsInteger(BigDecimal number) {
        return number.setScale(0, BigDecimal.ROUND_HALF_UP).compareTo(number) == 0;
    }

    private void SetZero() {
        _numberTextView.setText("0");
    }

    private void Clear() {
        _numberTextView.setText(null);
    }

    private void AddSymbol(char c) throws TooLongValueException {
        if (GetLength(_numberTextView) + 1 <= MAX_NUMBER_LENGTH) {
            if (c == '.')
                _numberTextView.setText(_numberTextView.getText().toString() + c);
            else
                SetValue(_numberTextView.getText().toString().replace(" ", "") + c);
        }
    }

    private BigDecimal GetValue() {
        String text = _numberTextView.getText().toString();
        if (text.compareTo(TOO_LONG_VALUE_TEXT.toString()) == 0 ||
                text.compareTo(INFINITY_TEXT.toString()) == 0 ||
                text.compareTo(WRONG_ARGUMENT_TEXT.toString()) == 0 ||
                GetLength(_numberTextView) == 0) {
            return new BigDecimal(0);
        } else
            return new BigDecimal(text.replace(" ", ""));
    }

    private void SetValue(BigDecimal value) throws TooLongValueException {
        SetValue(value.toString());
    }

    private String StripTailingZeros(String value) {
        if (value.compareTo("0") == 0) {
            return value;
        } else if (value.contains(".")) {
            String[] number = value.split("\\.");
            if (number[0].compareTo("0") != 0)
                for (int i = 0; i < number[0].length(); i++)
                    if (number[0].charAt(i) != '0') {
                        number[0] = number[i].substring(i, number[0].length());
                        break;
                    }
            for (int i = number[1].length() - 1; i >= 0; i--)
                if (number[1].charAt(i) != '0') {
                    number[1] = number[1].substring(0, i + 1);
                    return number[0] + '.' + number[1];
                }
            return number[0];
        } else {
            for (int i = 0; i < value.length(); i++)
                if (value.charAt(i) != '0') {
                    value = value.substring(i, value.length());
                    break;
                }
            return value;
        }
    }

    private void SetValue(String value) throws TooLongValueException {
        BigDecimal devValue = new BigDecimal(value);
        if (value.compareTo("0") == 0)
            SetZero();
        else if (GetLength(value) > MAX_NUMBER_LENGTH)
            throw new TooLongValueException();
        else {
            value = devValue.toString();
            if (IsInteger(devValue)) {
                if (value.length() > 3) {
                    String newText;
                    int i;
                    newText = value.substring(value.length() - 3, value.length());
                    for (i = value.length() - 6; i > 0; i -= 3)
                        newText = value.substring(i, i + 3) + ' ' + newText;
                    if (i > -3)
                        newText = value.substring(0, i + 3) + ' ' + newText;
                    _numberTextView.setText(newText);
                } else
                    _numberTextView.setText(value);
            } else {
                String integer = value.split("\\.")[0];
                String newInteger;
                int i;

                if (integer.length() > 3) {
                    newInteger = integer.substring(integer.length() - 3, integer.length());
                    for (i = integer.length() - 6; i > 0; i -= 3)
                        newInteger = integer.substring(i, i + 3) + ' ' + newInteger;
                    if (i > -3)
                        newInteger = integer.substring(0, i + 3) + ' ' + newInteger;
                } else
                    newInteger = integer;

                String real = value.split("\\.")[1];
                String newReal;

                if (real.length() > 3) {
                    newReal = real.substring(0, 3);
                    for (i = 6; i < real.length(); i += 3)
                        newReal = newReal + ' ' + real.substring(i - 3, i);
                    if (i < real.length() + 3)
                        newReal = newReal + ' ' + real.substring(i - 3, real.length());
                } else
                    newReal = real;

                _numberTextView.setText(newInteger + '.' + newReal);
            }
        }
    }

    private BigDecimal Calculate() throws ZeroDivisionException, TooLongValueException {
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
                    result = _leftArgument.divide(rightArgument, _precision,
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
                if (_leftArgument.compareTo(round(
                        BigDecimal.valueOf(Math.pow(_maxNumber.doubleValue(), 0.5)))) > 0 &&
                        rightArgument.compareTo(BigDecimal.valueOf(2)) > 0)
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
        if (GetLength(result) > MAX_NUMBER_LENGTH)
            throw new TooLongValueException();
        return result;
    }

    @Override
    public void onClick(View v) {
        try {
            CustomButtonAnimation anim = new CustomButtonAnimation();
            anim.setFromColor(_activity.getResources()
                    .getColor(R.color.button_pressed));
            if (v.getTag().toString().compareTo("number_button") == 0) {
                anim.setToColor(_activity.getResources()
                        .getColor(R.color.number_button_normal));
            } else if (v.getTag().toString().compareTo("operation_button") == 0) {
                anim.setToColor(_activity.getResources()
                        .getColor(R.color.operation_button_normal));
            } else if (v.getTag().toString().compareTo("functional_button") == 0) {
                anim.setToColor(_activity.getResources()
                        .getColor(R.color.functional_button_normal));
            }
            anim.setView(v);
            anim.setDuration(500);
            anim.setInterpolator(new LinearInterpolator());
            v.startAnimation(anim);
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
                    if (_currentState != States.GET_NUMBER) {
                        _currentState = States.GET_NUMBER;
                        SetZero();
                    }
                    if (!_numberTextView.getText().toString().contains(Character.toString('.')))
                        AddSymbol('.');
                    break;
                case R.id.buttonPlus:
                    _leftArgument = Calculate();
                    if (_currentState == States.GET_NUMBER) {
                        SetValue(_leftArgument);
                        _currentState = States.GET_OPERATION;
                    }
                    _currentOperation = Operations.PLUS;
                    break;
                case R.id.buttonMinus:
                    _leftArgument = Calculate();
                    if (_currentState == States.GET_NUMBER) {
                        SetValue(_leftArgument);
                        _currentState = States.GET_OPERATION;
                    }
                    _currentOperation = Operations.MINUS;
                    break;
                case R.id.buttonMul:
                    _leftArgument = Calculate();
                    if (_currentState == States.GET_NUMBER) {
                        SetValue(_leftArgument);
                        _currentState = States.GET_OPERATION;
                    }
                    _currentOperation = Operations.MUL;
                    break;
                case R.id.buttonDiv:
                    _leftArgument = Calculate();
                    if (_currentState == States.GET_NUMBER) {
                        SetValue(_leftArgument);
                        _currentState = States.GET_OPERATION;
                    }
                    _currentOperation = Operations.DIV;
                    break;
                case R.id.buttonPow:
                    _leftArgument = Calculate();
                    if (_currentState == States.GET_NUMBER) {
                        SetValue(_leftArgument);
                        _currentState = States.GET_OPERATION;
                    }
                    _currentOperation = Operations.POW;
                    break;
                case R.id.buttonSqrt:
                    _leftArgument = GetValue();
                    if (_leftArgument.compareTo(BigDecimal.ZERO) < 0)
                        throw new SquareRootOfNegativeNumber();
                    _leftArgument = BigDecimal.valueOf(Math.sqrt(_leftArgument.doubleValue()));
                    _leftArgument = round(_leftArgument);
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
                        _numberTextView.setText(_numberTextView.getText().toString()
                                .substring(0, _numberTextView.length() - 1));
                        _leftArgument = GetValue();
                    } else if (_numberTextView.getText().length() == 1)
                        SetZero();
                    break;
                case R.id.buttonPlusMinus:
                    if (GetValue().compareTo(BigDecimal.valueOf(0)) != 0)
                        _numberTextView.setText(_numberTextView.getText().charAt(0) == '-' ?
                                _numberTextView.getText()
                                        .subSequence(1, _numberTextView.getText().length()) :
                                "-" + _numberTextView.getText());
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
        } catch (SquareRootOfNegativeNumber ex) {
            _numberTextView.setText(WRONG_ARGUMENT_TEXT);
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (ArithmeticException ex) {
            _numberTextView.setText(INFINITY_TEXT);
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (TooLongValueException ex) {
            _numberTextView.setText(TOO_LONG_VALUE_TEXT);
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (NumberFormatException ex) {
            _numberTextView.setText("0");
            _currentState = States.GET_OPERATION;
            _currentOperation = Operations.NOP;
            _leftArgument = BigDecimal.ZERO;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void BindView(TextView text) {
        CharSequence content = _numberTextView.getText();
        _numberTextView = text;
        _numberTextView.setText(content);
    }

}
