package org.haojun.represent;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/** This is a customized text view that changes the font of the view.
 * Created by Haojun on 2/28/16.
 */
public class XTextView extends TextView {

    public XTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public XTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OldStandard-Regular.ttf");
            setTypeface(tf);
        }
    }

}