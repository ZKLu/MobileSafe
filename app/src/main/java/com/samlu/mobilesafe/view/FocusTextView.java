package com.samlu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 能够获取焦点的自定义TextView
 * Created by sam lu on 2019/10/27.
 */

public class FocusTextView extends TextView {
    //使用在通过java代码创建控件.new TextView(context)
    public FocusTextView(Context context) {
        super(context);
    }
    //由系统调用(带属性+上下文环境构造方法)，通过布局文件创建控件
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //由系统调用（带属性+上下文环境+布局文件中定义样式文件）
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写获取焦点的方法，由系统调用，调用时默认获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
