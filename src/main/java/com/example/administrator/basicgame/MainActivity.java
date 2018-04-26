package com.example.administrator.basicgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BallSurface(this));
    }
}

class BarMoveView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder sh;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentX;
    private int halfSize = 50;

    public BarMoveView(Context context)
    {
        super(context);

        sh = getHolder();
        sh.addCallback(this);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        setFocusable(true);
        requestFocus();
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.BLACK);

        currentX = (canvas.getWidth()/2);

        canvas.drawRect(currentX - halfSize, canvas.getHeight()-10, currentX + halfSize, canvas.getHeight(), paint);
        sh.unlockCanvasAndPost(canvas);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.BLACK);

        if(KeyEvent.KEYCODE_L == keyCode)
            currentX -= 5;
        else if(KeyEvent.KEYCODE_R == keyCode)
            currentX += 5;


        if(currentX - halfSize < 0)
            currentX = halfSize;
        else if(currentX + halfSize > canvas.getWidth())
            currentX = canvas.getWidth() - halfSize;


        canvas.drawRect(currentX - halfSize, canvas.getHeight()-10, currentX + halfSize, canvas.getHeight(), paint);
        sh.unlockCanvasAndPost(canvas);

        return true;
    }

}

class BallSurface extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder sh;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BubbleThread thread;
    private Context ctx;
    private int currentX;
    private float bubbleX;
    private float bubbleY;
    private int radius = 10;
    private int SPEEDX = 5;
    private int SPEEDY = 5;
    private int score = 0;
    private int halfSize = 50;

    private int startPosX = 0;
    private int startPosYs = 0;

    private ArrayList<Rect> arrRect;
    private ArrayList<Boolean> arrVisible;

    public BallSurface(Context context) {
        super(context);
        sh = getHolder();
        sh.addCallback(this);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(25);

        ctx = context;
        setFocusable(true);

        arrRect = new ArrayList<Rect>();

	    /*
	    arrRect.add(new Rect(20, 20, 59, 39));
	    arrRect.add(new Rect(60, 20, 99, 39));
	    arrRect.add(new Rect(20, 40, 59, 59));
	    arrRect.add(new Rect(60, 40, 99, 59));
	    */

        arrVisible = new ArrayList<Boolean>();

        arrVisible.add(true);
        arrVisible.add(true);
        arrVisible.add(true);
        arrVisible.add(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Canvas canvas = sh.lockCanvas();
        //canvas.drawColor(Color.BLACK);

        if (KeyEvent.KEYCODE_L == keyCode)
            currentX -= 10;
        else if (KeyEvent.KEYCODE_R == keyCode)
            currentX += 10;


        //canvas.drawColor(Color.BLACK);
        //canvas.drawCircle(bubbleX, bubbleY, radius, paint);

        //canvas.drawRect(currentX - 40, canvas.getHeight()-10, currentX + 40, canvas.getHeight(), paint);
        //sh.unlockCanvasAndPost(canvas);

        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        currentX = (int) event.getX();

        return true;
    }

    public void surfaceCreated(SurfaceHolder holder) {

        score = 0;

        thread = new BubbleThread(sh, ctx, new Handler());
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    class BubbleThread extends Thread {
        private int canvasWidth = 200;
        private int canvasHeight = 400;
        private boolean run = false;

        private float headingX;
        private float headingY;

        public BubbleThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            sh = surfaceHolder;
            ctx = context;
        }

        public void doStart() {
            synchronized (sh) {
                // Start bubble in centre and create some random motion
                bubbleX = canvasWidth / 2;
                bubbleY = canvasHeight / 2;

                headingX = (float) (-1 + (Math.random() * 2));
                headingY = (float) (-1 + (Math.random() * 2));

                currentX = canvasWidth / 2;
            }
        }

        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = sh.lockCanvas(null);
                    synchronized (sh) {
                        doDraw(c);
                    }
                    //sleep(1);
                } catch (Exception ex) {

                } finally {
                    if (c != null) {
                        sh.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (sh) {
                canvasWidth = width;
                canvasHeight = height;
                doStart();
            }
        }

		  /*
		  private void doDraw(Canvas canvas) {
		    bubbleX = bubbleX + (headingX * SPEED);
		    bubbleY = bubbleY + (headingY * SPEED);
		    //canvas.restore();
		    canvas.drawColor(Color.BLACK);
		    canvas.drawCircle(bubbleX, bubbleY, 50, paint);
		  }
		  */

        private void doDraw(Canvas canvas) {
            // Calculate the ball's new position
            bubbleX += SPEEDX;
            bubbleY += SPEEDY;
            // Check if the ball moves over the bounds
            // If so, adjust the position and speed.
            if (bubbleX - radius < 0) {
                SPEEDX = -SPEEDX; // Reflect along normal
                bubbleX = radius; // Re-position the ball at the edge
            } else if (bubbleX + radius > canvasWidth) {
                SPEEDX = -SPEEDX;
                bubbleX = canvasWidth - radius;
            }
            // May cross both x and y bounds
            if (bubbleY - radius < 0) {
                SPEEDY = -SPEEDY; // Reflect along normal
                bubbleY = radius; // Re-position the ball at the edge
            } else if (bubbleY + radius > canvasHeight) {

                if (bubbleX >= currentX - halfSize && bubbleX <= currentX + halfSize) {
                    SPEEDY = -SPEEDY;
                    bubbleY = canvasHeight - radius;
                    score += 10;
                } else {
                    run = false;
                }
            }

            canvas.drawColor(Color.BLACK);

            if (run)
                canvas.drawCircle(bubbleX, bubbleY, radius, paint);

            if (currentX - halfSize < 0)
                currentX = halfSize;
            else if (currentX + halfSize > canvas.getWidth())
                currentX = canvas.getWidth() - halfSize;

            canvas.drawRect(currentX - halfSize, canvas.getHeight() - 10, currentX + halfSize, canvas.getHeight(), paint);

            canvas.drawText("Your Score is :: " + String.valueOf(score), 50, 50, paint);

            for (int i = 0; i < arrRect.size(); i++) {
                if (arrVisible.get(i).booleanValue() == true)
                    canvas.drawRect(arrRect.get(i), paint);
            }


        }
    }
}