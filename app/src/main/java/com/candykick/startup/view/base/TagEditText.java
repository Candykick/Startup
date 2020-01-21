package com.candykick.startup.view.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candykick.startup.R;

/**
 * Created by candykick on 2019. 9. 15..
 */

public class TagEditText extends EditText {
    TextWatcher textWatcher;

    String lastString;

    int endTag = 0;

    String separator = ",";

    public TagEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(getText().toString().length()!=0 & start==0) {
                    //setText(getText().toString().substring(1,getText().toString().length()));
                    setSelection(getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*String thisString = s.toString();
                if (thisString.length() > 0 && !thisString.equals(lastString)) {

                }*/
            }
        };

        addTextChangedListener(textWatcher);
    }


    /*private void format() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String fullString = getText().toString();

        String[] strings = fullString.split(separator);


        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            sb.append(string);

            if (fullString.charAt(fullString.length() - 1) != separator.charAt(0) && i == strings.length - 1) {
                break;
            }

            BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(createTokenView(string));
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

            int startIdx = sb.length() - (string.length());
            int endIdx = sb.length();

            sb.setSpan(new ImageSpan(bd), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            MyClickableSpan myClickableSpan = new MyClickableSpan(startIdx, endIdx);
            sb.setSpan(myClickableSpan, Math.max(endIdx-2, startIdx), endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (i < strings.length - 1) {
                sb.append(separator);
            } else if (fullString.charAt(fullString.length() - 1) == separator.charAt(0)) {
                sb.append(separator);
            }
        }


        lastString = sb.toString();

        setText(sb);
        setSelection(sb.length());

    }*/

    public void setUserTag(String tag) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(tag);

        endTag = tag.length();

        BitmapDrawable bd = (BitmapDrawable)convertViewToDrawable(createTokenView(tag));
        bd.setBounds(0,0,bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

        //int startIndex = sb.length() - (tag.length());
        int startIndex = 0;
        int endIndex = sb.length();

        sb.setSpan(new ImageSpan(bd), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        MyClickableSpan myClickableSpan = new MyClickableSpan(startIndex, endIndex);
        //sb.setSpan(myClickableSpan, Math.max(endIndex-2, startIndex), endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(myClickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        lastString = sb.toString();
        setText(sb);
        setSelection(sb.length());

        //태그 추가 시 키보드 띄워주기
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public String returnComment() {
        return getText().toString().substring(0,endTag)+":"+getText().toString().substring(endTag,getText().toString().length());
    }

    public View createTokenView(String text) {
        LinearLayout l = new LinearLayout(getContext());
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setBackgroundResource(R.drawable.bordered_rectangle_rounded_corners);

        TextView tv = new TextView(getContext());
        l.addView(tv);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        ImageView im = new ImageView(getContext());
        l.addView(im);
        im.setImageResource(R.drawable.ic_cross_15dp);
        im.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return l;
    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);

        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getContext().getResources(), viewBmp);
    }

    private class MyClickableSpan extends ClickableSpan {

        int startIdx;
        int endIdx;

        public MyClickableSpan(int startIdx, int endIdx) {
            super();
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void onClick(View widget) {
            String s = getText().toString();

            String s1 = s.substring(0, startIdx);
            String s2 = s.substring(Math.min(endIdx+1, s.length()-1), s.length() );

            TagEditText.this.setText(s1 + s2);
        }
    }
}
