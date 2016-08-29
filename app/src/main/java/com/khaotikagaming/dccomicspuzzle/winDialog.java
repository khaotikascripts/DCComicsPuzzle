package com.khaotikagaming.dccomicspuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Patrick on 8/28/2016.
 */
public class winDialog extends Dialog {
    private Bitmap image;
    private Typeface myFont;
    private ReadyListener readyListener;
    private boolean replaylevel;

    public winDialog(Context paramContext, boolean paramBoolean, ReadyListener paramReadyListener, Typeface paramTypeface, Bitmap paramBitmap)
    {
        super(paramContext);
        this.replaylevel = paramBoolean;
        this.readyListener = paramReadyListener;
        this.myFont = paramTypeface;
        this.image = paramBitmap;
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.windialog);
        setTitle("Congratulations");
        setVolumeControlStream(3);
        Button myButton = (Button)findViewById(R.id.back);
        myButton.setOnClickListener(new Back());
        myButton.setTypeface(this.myFont);
        myButton = (Button)findViewById(R.id.next);
        myButton.setOnClickListener(new Next());
        myButton.setTypeface(this.myFont);
        TextView myTextView = (TextView)findViewById(R.id.text);
        ((ImageView)findViewById(R.id.image)).setImageBitmap(this.image);
        if (!this.replaylevel)
        {
            myTextView.setText(R.string.unlocked);
            return;
        }
        myTextView.setText(R.string.completed);
    }

    private class Back
            implements View.OnClickListener
    {
        private Back() {}

        public void onClick(View paramView)
        {
            winDialog.this.readyListener.ready(false);
            winDialog.this.dismiss();
        }
    }

    private class Next
            implements View.OnClickListener
    {
        private Next() {}

        public void onClick(View paramView)
        {
            winDialog.this.readyListener.ready(true);
            winDialog.this.dismiss();
        }
    }

    public interface ReadyListener
    {
        void ready(boolean paramBoolean);
    }
}
