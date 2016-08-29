package com.khaotikagaming.dccomicspuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Patrick on 8/28/2016.
 */
public class endDialog extends Dialog {
    private Typeface myFont;

    public endDialog(Context paramContext, Typeface paramTypeface)
    {
        super(paramContext);
        this.myFont = paramTypeface;
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.enddialog);
        setTitle("Winner!");
        setVolumeControlStream(3);
        Button backButton = (Button)findViewById(R.id.back);
        backButton.setOnClickListener(new Back());
        backButton.setTypeface(this.myFont);
    }

    private class Back
            implements View.OnClickListener
    {
        private Back() {}

        public void onClick(View paramView)
        {
            endDialog.this.dismiss();
        }
    }
}
