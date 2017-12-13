package ch.ethz.inf.vs.a4.qaise.genkidama.animation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Qais on 04-Dec-17.
 */

public class Animation {

    Bitmap bitmap;
    public boolean isMoving = true; // animation true

    // New variables for the sprite sheet animation

    // These next two values can be anything you like
    // As long as the ratio doesn't distort the sprite too much
    private int frameWidth;
    private int frameHeight;

    // How many frames are there on the sprite sheet?
    private int frameCount;     // total frame count
//    private int framesInX, framesInY; // count of frame in x and y direction
//    private int currFrameX, currFrameY;

    // Start at the first frame
    private int currentFrame = 0;
    public boolean forward = true; // forward animation if true; backward animation if false


    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int frameLengthInMilliseconds = 100;

    // A rectangle to define an area of the sprite sheet that represents 1 frame
    private Rect frameToDraw;

    // A rect that defines an area of the screen on which to draw that selected 1 frame
    private RectF whereToDraw;
    private float x, y;


    Paint paint;

    // Animation from a one-dimensional sprite sheet
    public Animation(Context context, int drawable, int frameWidth,
                     int frameHeight, int frameCount, float x, float y, boolean scaled, int scaleFactor) {

        if (scaleFactor < 1) scaleFactor = 1;

        this.x = x;
        this.y = y;
        // Following variables should never be zero!
        this.frameCount = frameCount;
        this.frameWidth = scaleFactor*frameWidth;
        this.frameHeight = scaleFactor*frameHeight;

        frameToDraw = new Rect(0,0, this.frameWidth, this.frameHeight);
        whereToDraw = new RectF(x, y, x + this.frameWidth, y + this.frameHeight);
//        this.framesInX = framesInX;
//        this.framesInY = framesInY;
        paint = new Paint();

        //bitmap downsampled version
//        bitmap=decodeSampledBitmapFromResource(context.getResources(),drawable, frameWidth*frameCount, frameHeight);

        //old version without bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);

        // Scale the bitmap to the correct size. We need to do this because Android automatically
        // scales bitmaps based on screen density
//        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * frameCount, frameHeight,false);

//        bitmap = Bitmap.createScaledBitmap(
//                bitmap,
//                frameWidth * framesInX,
//                frameHeight * framesInY,
//                false);

        if (scaled) {
            bitmap = Bitmap.createScaledBitmap(bitmap, this.frameWidth * frameCount, this.frameHeight,false);
        } else {
            bitmap = Bitmap.createBitmap(bitmap);
            this.frameWidth = bitmap.getWidth()/frameCount;
            this.frameHeight = bitmap.getHeight();
        }

    }
    //method to scale down image only to size we need
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        //raw height and width of image
        final int height=options.outHeight;
        final int width= options.outWidth;
        int inSampleSize=1;
        if (height >reqHeight || width > reqWidth){
            final int halfHeight=height/2;
            final int halfWidth=width/2;
            //calculate largest inSmapleSize value that is a power of 2 and keeps height
            //and width larger than the requested height and width
            while((halfHeight/ inSampleSize)>reqHeight && (halfWidth/inSampleSize)>reqWidth){
                inSampleSize*=2;
            }
        }


        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        //first decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options= new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(res,resId, options);
        //calculate inSmaplesize
        options.inSampleSize= calculateInSampleSize(options, reqWidth,reqHeight);
        //decode bitmap with inSampleSize set
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Rect getCurrentFrame(){

        long time  = System.currentTimeMillis();
        if(isMoving) {// Only animate if is moving
            if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                if (forward) { // if forward, then we start with frame 0 and animate up to frameCount
                    currentFrame ++;
                    if (currentFrame >= frameCount) {
                        currentFrame = 0;
                    }
                } else {    // if forward=false, then we animate backward, that is, beginning from frameCount
                    currentFrame --;
                    if (currentFrame <= -1) {
                        currentFrame = frameCount - 1;
                    }
                }
            }
        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;

//        if (framesInX == 0) {
//            currFrameX = 0;
//        } else {
//            currFrameX = currentFrame % framesInX;
//        }
//
//        if (framesInY == 0) {
//            currFrameY = 0;
//        } else {
//            currFrameY = currentFrame % framesInY;
//        }
//
//        frameToDraw.left = currFrameX * frameWidth;
//        frameToDraw.right = frameToDraw.left + frameWidth;
//
//        frameToDraw.top = currFrameY * frameHeight;
//        frameToDraw.bottom = frameToDraw.top + frameHeight;


        return frameToDraw;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(
                this.resource(),
                this.getCurrentFrame(),
                this.getWhereToDraw(),
                paint
        );
    }

    public RectF getWhereToDraw() {
        return whereToDraw;
    }

    public void setWhereToDraw(float x, float y) {

        whereToDraw.set(new RectF(x - frameWidth/2, y - frameHeight, x + frameWidth/2, y));
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    private Bitmap resource() {
        return bitmap;
    }

    public void setFrameDuration(int frameLengthInMilliseconds) {
        this.frameLengthInMilliseconds = frameLengthInMilliseconds;
    }

//    public void scaleBitmap(int scale){
//        this.frameWidth = scale*frameWidth;
//        this.frameHeight = scale*frameHeight;
//
//        // use this for hitbox
//        frameToDraw = new Rect(0,0, frameWidth, frameHeight);
//        whereToDraw = new RectF(x, y, x + frameWidth, y + frameHeight);
//
//        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * frameCount, frameHeight,false);
//
//    }

}