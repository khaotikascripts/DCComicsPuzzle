package com.khaotikagaming.dccomicspuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class puzzleCore extends Activity {
    public static final int MENU_LEVEL = 1;
    public static final int MENU_QUIT = 6;
    public static final int MENU_RESHUFFLE = 2;
    private static final int numberofpictures = 78;
    private static final CharSequence titlepuzzle = "  DC Comics";
    private ArrayList<ImageView> ImageReferences;
    private boolean MATRIXDETERMINED = false;
    private ArrayList<ImageViewOnTouchListener> puzzleCoreReferences;
    private Bundle b;
    private int currentmoves;
    private int currentphoto;
    private int currentphotogui;
    private int difficulty = 6;
    private AlertDialog.Builder editDialog;
    private FrameLayout fr;
    private TextView guilevel;
    private TextView guimoves;
    private int height;
    private Bitmap image;
    private String imagename;
    private int ipiece;
    private ImageView iv;
    private ImageView ivi;
    private ImageViewOnTouchListener ivotl;
    private ImageViewOnTouchListener ivotli;
    private int jpiece;
    private Matrix matrix;
    private boolean muteSound;
    private Typeface myFont;
    private Button nextpuzzle;
    private Button nonextpuzzle;
    private Button nopreview;
    private int piecenumber;
    private Button preview;
    private int previewsleft;
    private boolean replaylevel;
    private Random rg = new Random();
    private int screenwidth;
    private puzzleCorePiece puzzleCorepiece;
    private Drawable temporary;
    private Bitmap temporaryImage;
    private int temporarywinID;
    private int thresholdpieceline;
    private TableLayout tl;
    private Point touchpoint = new Point();
    private int unlockedlevels = 0;
    private int width;
    private boolean winningstate;

    static {}

    public void checkwinningstate() //A piece was moved; verify piece positions
    {
        //Check each square one-by-one
        for (int i = 0; i < this.puzzleCoreReferences.size(); i++) {
            if (i != this.puzzleCoreReferences.get(i).getWinID()) {
                this.winningstate = false; //piece doesn't belong here; level isn't over
                updateSavegame();
                updateGUI();
                return;
            }
            i += 1; //piece is in correct position; check next piece
        }

        //Every piece has been checked/verified
        this.winningstate = true;
        this.nextpuzzle.setVisibility(View.VISIBLE);
        this.nonextpuzzle.setVisibility(View.GONE);
        if (this.currentphotogui <= this.unlockedlevels) {
            //I assume this means a replayed level; just start next puzzle
            new winDialog(this, true, new OnReadyListener(), this.myFont, this.image).show();
            return;
        }
        //This is a new level; unlock it
        this.unlockedlevels = this.currentphotogui;
        new winDialog(this, false, new OnReadyListener(), this.myFont, this.image).show();
        updateSavegame();
        updateGUI();
    }

    public ArrayList<ImageView> createField(Bitmap paramBitmap)
    {
        this.ImageReferences = new ArrayList<>();
        this.puzzleCoreReferences = new ArrayList<>();
        Bitmap puzzlepiece;
        int j = 0; //num of pieces?
        int m = (int)Math.floor(paramBitmap.getWidth() / this.difficulty); //width of each piece
        int n = (int)Math.floor(paramBitmap.getHeight() / this.difficulty); //height of each piece

        for (int i = 0; i < this.difficulty; i++) {
            //Create new row and fill it
            TableRow localTableRow = new TableRow(this);
            localTableRow.setLayoutParams(new TableRow.LayoutParams(-1, -2));
            for (int k = 0; k < this.difficulty; k++) {
                //Create a piece
                ImageView localImageView = new ImageView(getApplicationContext());
                puzzlepiece = Bitmap.createBitmap(paramBitmap, m * k, n * i, m, n); //(top-left,bottom-right)
                localImageView.setImageBitmap(puzzlepiece);
                localImageView.setLayoutParams(new TableRow.LayoutParams(-2, -2));
                ImageViewOnTouchListener localImageViewOnTouchListener = new ImageViewOnTouchListener(puzzlepiece, localImageView, i, k, j);
                localImageView.setOnTouchListener(localImageViewOnTouchListener);
                j += 1; //count pieces

                //Add piece to current row
                localTableRow.addView(localImageView);
                this.ImageReferences.add(localImageView);
                this.puzzleCoreReferences.add(localImageViewOnTouchListener);
            }
            this.tl.addView(localTableRow, new TableLayout.LayoutParams(-1, -2));
        }
        return this.ImageReferences;
    }

    public void createQuitGame()
    {
        this.editDialog = new AlertDialog.Builder(this);
        this.editDialog.setTitle("Quit Game");
        this.editDialog.setMessage("Are you sure you want to quit?");
        this.editDialog.setIcon(R.drawable.icon);
        this.editDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                puzzleCore.this.finish();
            }
        });
        this.editDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
            }
        });
    }

    public void loadGame()
    {
        SharedPreferences localSharedPreferences = getSharedPreferences("savegame", 0);
        this.currentphoto = localSharedPreferences.getInt("currentphoto", 0);
        this.imagename = localSharedPreferences.getString("currentimagename", null);
        this.currentphotogui = localSharedPreferences.getInt("currentphotogui", 0);
        this.unlockedlevels = localSharedPreferences.getInt("unlockedlevels", 0);
        this.muteSound = localSharedPreferences.getBoolean("muteSound", false);
        if (this.currentphotogui == 0)
        {
            this.currentphoto = R.drawable.image_1;
            this.currentphotogui = 1;
            this.imagename = ("image_" + this.currentphotogui);
            this.unlockedlevels = 0;
        }
        startnextPuzzle();
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if ((paramInt1 == 1) && (paramIntent != null))
        {
            this.b = paramIntent.getExtras();
            this.currentphoto = this.b.getInt("resID");
            this.currentphotogui = this.b.getInt("photo");
            this.imagename = ("image_" + this.currentphotogui);
            startnextPuzzle();
        }
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.main);
        setVolumeControlStream(3);
        this.myFont = Typeface.createFromAsset(getAssets(), "fonts/tahomabd.ttf");
        this.fr = ((FrameLayout)findViewById(R.id.frame));
        this.tl = ((TableLayout)findViewById(R.id.playfield));
        this.guilevel = ((TextView)findViewById(R.id.guilevel));
        this.guimoves = ((TextView)findViewById(R.id.guimoves));
        TextView myTextView = (TextView)findViewById(R.id.title);
        Button localButton = (Button)findViewById(R.id.menu);
        this.nextpuzzle = ((Button)findViewById(R.id.next));
        this.nonextpuzzle = ((Button)findViewById(R.id.nonext));
        this.preview = ((Button)findViewById(R.id.preview));
        this.nopreview = ((Button)findViewById(R.id.nopreview));
        localButton.setOnClickListener(new pressMenu());
        this.nextpuzzle.setOnClickListener(new NextPuzzle());
        this.nonextpuzzle.setOnClickListener(new NextPuzzle());
        this.preview.setOnClickListener(new PreviewImage());
        this.nopreview.setOnClickListener(new PreviewImage());
        this.guilevel.setTypeface(this.myFont);
        this.guimoves.setTypeface(this.myFont);
        myTextView.setTypeface(this.myFont);
        localButton.setTypeface(this.myFont);
        this.nextpuzzle.setTypeface(this.myFont);
        this.nonextpuzzle.setTypeface(this.myFont);
        this.preview.setTypeface(this.myFont);
        this.nopreview.setTypeface(this.myFont);
        myTextView.setText(titlepuzzle);
        this.puzzleCorepiece = new puzzleCorePiece(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point myScreenSize = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(myScreenSize);
            this.screenwidth = myScreenSize.x;
            //System.out.println("-----------Screen width: " + myScreenSize.x);
            //System.out.println("-----------Screen height: " + myScreenSize.y);
        } else {
            this.screenwidth = getWindowManager().getDefaultDisplay().getWidth();
        }
        createQuitGame();
        loadGame();
    }

    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
        paramMenu.add(0, 1, 0, "Levels").setIcon(R.drawable.selectlevel);
        paramMenu.add(0, 2, 0, "Restart Level").setIcon(R.drawable.reshuffle);
        paramMenu.add(0, 3, 0, "Quit Game").setIcon(R.drawable.quitgame);
        return true;
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public boolean onKeyDown(int paramInt, @NonNull KeyEvent paramKeyEvent)
    {
        if (paramInt == 4)
        {
            quitGame();
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            default:
                return false;
            case MENU_LEVEL:
                this.b = new Bundle();
                this.b.putInt("unlockedlevels", this.unlockedlevels);
                this.b.putInt("currentlevel", this.currentphotogui);
                this.b.putInt("numberofpictures", numberofpictures);
                this.b.putBoolean("mutesound", this.muteSound);
                return true;
            case MENU_RESHUFFLE:
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
                myAlertDialog.setTitle("Restart this Level");
                myAlertDialog.setMessage("Your progress in this level will be reset. Are you sure about this?");
                myAlertDialog.setIcon(R.drawable.icon);
                myAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        puzzleCore.this.startnextPuzzle();
                    }
                });
                myAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                    }
                });
                myAlertDialog.show();
                return true;
            case MENU_QUIT:
                quitGame();
                return true;
        }
    }

    public void quitGame()
    {
        this.editDialog.show();
    }

    public void shufflePieces(ArrayList<ImageView> paramArrayList)
    {
        if (paramArrayList.size() == 0) {
            this.ivi = null;
            this.iv = null;
            this.ivotli = null;
            this.ivotl = null;
            this.temporary = null;
            this.temporaryImage = null;
            return;
        }
        int randomnumber; //i: orig pos -- rdm: new pos
        for (int i = 0; i < paramArrayList.size(); i++) {
            randomnumber = this.rg.nextInt(paramArrayList.size() - 1);
            if (i != randomnumber)
            {
                this.ivi = paramArrayList.get(i);
                this.iv = paramArrayList.get(randomnumber);
                this.ivotli = this.puzzleCoreReferences.get(i);
                this.ivotl = this.puzzleCoreReferences.get(randomnumber);
                this.temporary = this.ivi.getDrawable();
                this.ivi.setImageDrawable(this.iv.getDrawable());
                this.iv.setImageDrawable(this.temporary);
                this.temporaryImage = this.ivotli.getImage();
                this.temporarywinID = this.ivotli.getWinID();
                this.ivotli.setImage(this.ivotl.getImage());
                this.ivotli.setWinID(this.ivotl.getWinID());
                this.ivotl.setImage(this.temporaryImage);
                this.ivotl.setWinID(this.temporarywinID);
            }
        }
    }

    public void startnextPuzzle()
    {
        if (this.currentphotogui > 78) //Finished last level; end the game
        {
            this.currentphoto -= 1;
            this.currentphotogui -= 1;
            this.imagename = ("image_" + this.currentphotogui);
            new endDialog(this, this.myFont).show();
            updateSavegame();
            return;
        }
        if (this.currentphotogui > this.unlockedlevels) { //next puzzle is locked; disable NEXT
            this.replaylevel = false;
            this.nonextpuzzle.setVisibility(View.VISIBLE);
            this.nextpuzzle.setVisibility(View.GONE);
        } else {
            this.replaylevel = true; //next puzzle is unlocked; enable NEXT
            this.nonextpuzzle.setVisibility(View.GONE);
            this.nextpuzzle.setVisibility(View.VISIBLE);
        }
        this.previewsleft = 3;
        this.preview.setVisibility(View.VISIBLE);
        this.nopreview.setVisibility(View.GONE);
        this.winningstate = false;
        this.currentmoves = 0;

        //Reset board with new image/puzzle
        if (this.ImageReferences != null)
        {
            this.ImageReferences.clear();
            this.puzzleCoreReferences.clear();
        }
        this.tl.removeAllViews();
        int resID = getResources().getIdentifier(this.imagename, "drawable", getPackageName());
        this.image = BitmapFactory.decodeResource(getResources(), resID);
        if (!this.MATRIXDETERMINED)
        {
            this.MATRIXDETERMINED = true;
            this.width = this.image.getWidth();
            this.height = this.image.getHeight();
            float f1 = this.screenwidth / this.width;
            float f2 = this.screenwidth / this.height;
            this.matrix = new Matrix();
            this.matrix.postScale(f1, f2);
        }
        this.image = Bitmap.createBitmap(this.image, 0, 0, this.width, this.height, this.matrix, true);
        this.ImageReferences = createField(this.image);
        shufflePieces(this.ImageReferences);
        updateSavegame();
        updateGUI();
    }

    public void updateGUI()
    {
        this.guilevel.setText(String.format(Locale.US, "   Level: %d of 78", this.currentphotogui));
        this.guimoves.setText(String.format(Locale.US, "   Moves: %d", this.currentmoves));
        this.preview.setText(String.format(Locale.US, "     Peek: %d", this.previewsleft));
    }

    public void updateSavegame()
    {
        SharedPreferences savegame = getSharedPreferences("savegame", 0);
        SharedPreferences.Editor editor = savegame.edit();
        int i = 0;
        for (;;)
        {
            if (i >= this.ImageReferences.size())
            {
                editor.putBoolean("winningstate", this.winningstate);
                editor.putBoolean("firstgame", true);
                editor.putInt("currentphoto", this.currentphoto);
                editor.putString("currentimagename", this.imagename);
                editor.putInt("currentphotogui", this.currentphotogui);
                editor.putInt("moves", this.currentmoves);
                editor.putInt("unlockedlevels", this.unlockedlevels);
                editor.apply();
                return;
            }
            editor.putInt("fieldsquare" + i, this.puzzleCoreReferences.get(i).getWinID());
            i += 1;
        }
    }

    private class ImageViewOnTouchListener
            implements View.OnTouchListener
    {
        private Bitmap image;
        private Point origin = new Point();
        private int pieceheight;
        private int pieceid;
        private int piecewidth;
        private ImageView tableimage;
        private int winid;

        public ImageViewOnTouchListener(Bitmap paramBitmap, ImageView paramImageView, int pieceRow, int pieceColumn, int pieceIdAndPosition)
        {
            this.image = paramBitmap;
            this.tableimage = paramImageView;
            this.piecewidth = paramBitmap.getWidth();
            this.pieceheight = paramBitmap.getHeight();
            this.origin.x = (this.piecewidth * pieceColumn);
            this.origin.y = (this.pieceheight * pieceRow);
            this.pieceid = pieceIdAndPosition; //ID for this piece
            this.winid = pieceIdAndPosition; //Correct position on board for this piece
        }

        public Bitmap getImage()
        {
            return this.image;
        }

        public int getWinID()
        {
            return this.winid;
        } //Piece's correct position on board

        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
            if (puzzleCore.this.winningstate) {
                return false;
            }
            puzzleCore.this.touchpoint.x = (this.origin.x + (int)paramMotionEvent.getX() - Math.round(this.piecewidth / 2));
            puzzleCore.this.touchpoint.y = (this.origin.y + (int)paramMotionEvent.getY() - Math.round(this.pieceheight / 2));
            if (paramMotionEvent.getAction() == 0) //ACTION_DOWN
            {
                puzzleCore.this.puzzleCorepiece.setImage(this.image, puzzleCore.this.touchpoint);
                puzzleCore.this.puzzleCorepiece.setHapticFeedbackEnabled(true);
                this.tableimage.setVisibility(View.INVISIBLE);
                puzzleCore.this.fr.addView(puzzleCore.this.puzzleCorepiece);
                return true;
            }
            if (paramMotionEvent.getAction() == 2) //ACTION_MOVE
            {
                puzzleCore.this.puzzleCorepiece.moveImage(puzzleCore.this.touchpoint);
                return true;
            }
            if (paramMotionEvent.getAction() == 1) //ACTION_UP -- Swap pieces
            {
                puzzleCore.this.fr.removeView(puzzleCore.this.puzzleCorepiece);
                this.tableimage.setVisibility(View.VISIBLE);
                puzzleCore.this.touchpoint.x = (this.origin.x + (int)paramMotionEvent.getX());
                puzzleCore.this.touchpoint.y = (this.origin.y + (int)paramMotionEvent.getY());
                puzzleCore.this.piecenumber = 0;
                puzzleCore.this.piecenumber = switchPieces(puzzleCore.this.touchpoint); //Get ID of other piece
                if (puzzleCore.this.piecenumber < puzzleCore.this.puzzleCoreReferences.size()) //meaning it's a valid position ID within the grid
                {
                    //System.out.println("---------------Old point: " + this.pieceid);
                    //System.out.println("---------------New point: " + puzzleCore.this.piecenumber);
                    puzzleCore.this.ivi = puzzleCore.this.ImageReferences.get(this.pieceid);
                    puzzleCore.this.iv = puzzleCore.this.ImageReferences.get(puzzleCore.this.piecenumber);
                    puzzleCore.this.ivotli = puzzleCore.this.puzzleCoreReferences.get(this.pieceid);
                    puzzleCore.this.ivotl = puzzleCore.this.puzzleCoreReferences.get(puzzleCore.this.piecenumber);
                    puzzleCore.this.temporary = puzzleCore.this.ivi.getDrawable();
                    puzzleCore.this.ivi.setImageDrawable(puzzleCore.this.iv.getDrawable());
                    puzzleCore.this.iv.setImageDrawable(puzzleCore.this.temporary);
                    puzzleCore.this.temporaryImage = puzzleCore.this.ivotli.getImage();
                    puzzleCore.this.temporarywinID = puzzleCore.this.ivotli.getWinID();
                    puzzleCore.this.ivotli.setImage(puzzleCore.this.ivotl.getImage());
                    puzzleCore.this.ivotli.setWinID(puzzleCore.this.ivotl.getWinID());
                    puzzleCore.this.ivotl.setImage(puzzleCore.this.temporaryImage);
                    puzzleCore.this.ivotl.setWinID(puzzleCore.this.temporarywinID);
                    if (this.pieceid != puzzleCore.this.piecenumber)
                    {
                        puzzleCore.this.currentmoves += 1;
                    }
                    puzzleCore.this.checkwinningstate();
                }
                return true;
            }
            return false;
        }

        public void setImage(Bitmap paramBitmap)
        {
            this.image = paramBitmap;
        }

        public void setWinID(int paramInt)
        {
            this.winid = paramInt;
        }

        public int switchPieces(Point paramPoint)
        {
            puzzleCore.this.ipiece = 0; //col index
            puzzleCore.this.jpiece = 0; //row index

            //Determine column index
            puzzleCore.this.thresholdpieceline = paramPoint.x;
            puzzleCore.this.thresholdpieceline -= this.piecewidth;
            int localPoint = puzzleCore.this.thresholdpieceline;
            for (int i = localPoint; i >= 0; i -= this.piecewidth) {
                puzzleCore.this.ipiece += 1; //if p1 still pos, inc col count, sub again
            }

            //Determine row index
            puzzleCore.this.thresholdpieceline = paramPoint.y;
            puzzleCore.this.thresholdpieceline -= this.pieceheight;
            localPoint = puzzleCore.this.thresholdpieceline;
            for (int i = localPoint; i >= 0; i -= this.pieceheight) {
                puzzleCore.this.jpiece += 1; //if p1 still pos, inc row count, sub again
            }

            //Return board position of piece being replaced
            return puzzleCore.this.jpiece * puzzleCore.this.difficulty + puzzleCore.this.ipiece;
        }
    }

    private class NextPuzzle
            implements View.OnClickListener
    {
        private NextPuzzle() {}

        public void onClick(View paramView)
        {
            if ((puzzleCore.this.winningstate) || (puzzleCore.this.replaylevel))
            {
                puzzleCore.this.currentphoto += 1;
                puzzleCore.this.currentphotogui += 1;
                puzzleCore.this.imagename = ("image_" + puzzleCore.this.currentphotogui);
                puzzleCore.this.startnextPuzzle();
                return;
            }
            Toast.makeText(puzzleCore.this, "Solve this puzzle to unlock a new level", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnReadyListener
            implements winDialog.ReadyListener
    {
        private OnReadyListener() {}

        public void ready(boolean paramBoolean)
        {
            if (paramBoolean)
            {
                puzzleCore localpuzzleCore = puzzleCore.this;
                localpuzzleCore.currentphoto += 1;
                localpuzzleCore = puzzleCore.this;
                localpuzzleCore.currentphotogui += 1;
                puzzleCore.this.imagename = ("image_" + puzzleCore.this.currentphotogui);
                puzzleCore.this.startnextPuzzle();
            }
        }
    }

    private class PreviewImage
            implements View.OnClickListener
    {
        private PreviewImage() {}

        public void onClick(View paramView)
        {
            if (puzzleCore.this.previewsleft > 0)
            {
                puzzleCore.this.previewsleft -= 1;
                puzzleCore.this.updateGUI();
                new previewDialog(puzzleCore.this, puzzleCore.this.image, puzzleCore.this.myFont).show();
                if (puzzleCore.this.previewsleft == 0)
                {
                    puzzleCore.this.preview.setVisibility(View.GONE);
                    puzzleCore.this.nopreview.setVisibility(View.VISIBLE);
                }
                return;
            }
            Toast.makeText(puzzleCore.this, "No more peeking!", Toast.LENGTH_SHORT).show();
        }
    }

    private class pressMenu
            implements View.OnClickListener
    {
        private pressMenu() {}

        public void onClick(View paramView)
        {
            puzzleCore.this.openOptionsMenu();
        }
    }
}
