package com.khaotikagaming.dccomicspuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Patrick on 8/28/2016.
 */
public class previewDialog extends Dialog {
    private Bitmap image;
    private Typeface myFont;

    public previewDialog(Context paramContext, Bitmap paramBitmap, Typeface paramTypeface)
    {
        super(paramContext);
        this.image = paramBitmap;
        this.myFont = paramTypeface;
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.previewdialog);
        setTitle("Preview");
        setVolumeControlStream(3);
        ((ImageView)findViewById(R.id.image)).setImageBitmap(this.image);
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
            previewDialog.this.dismiss();
        }
    }
}
