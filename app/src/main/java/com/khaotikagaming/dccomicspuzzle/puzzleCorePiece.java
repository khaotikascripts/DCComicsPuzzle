package com.khaotikagaming.dccomicspuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by Patrick on 8/28/2016.
 */
public class puzzleCorePiece extends ImageView {
    private Bitmap mImage;
    private Point mStartPosition = new Point();

    public puzzleCorePiece(Context paramContext)
    {
        super(paramContext);
    }

    public void moveImage(Point paramPoint)
    {
        this.mStartPosition = paramPoint;
        invalidate();
    }

    protected void onDraw(Canvas paramCanvas)
    {
        if (this.mImage != null) {
            paramCanvas.drawBitmap(this.mImage, this.mStartPosition.x, this.mStartPosition.y, null);
        }
    }

    public void setImage(Bitmap paramBitmap, Point paramPoint)
    {
        this.mImage = paramBitmap;
        this.mStartPosition = paramPoint;
    }
}
