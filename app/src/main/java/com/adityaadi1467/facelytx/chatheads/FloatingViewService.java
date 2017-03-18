package com.adityaadi1467.facelytx.chatheads;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adityaadi1467.facelytx.MainActivity;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebView;
import com.example.adi.facelyt.R;

/**
 * Created by adi on 3/17/17.
 */

public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private boolean wasInFocus = true;
    private View mTrashView;
    private Point szWindow = new Point();

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    WindowManager.LayoutParams params;
    ImageView removeImg;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        mTrashView = LayoutInflater.from(this).inflate(R.layout.trash_layout,null);

        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        mTrashView.setVisibility(View.GONE);
       removeImg = (ImageView)mTrashView.findViewById(R.id.remove_img);
        mWindowManager.addView(mTrashView, paramRemove);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWindowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = mWindowManager.getDefaultDisplay().getWidth();
            int h = mWindowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,// Not displaying keyboard on bg activity's EditText
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

        VideoEnabledWebView mWebView = (VideoEnabledWebView)mFloatingView.findViewById(R.id.webViewFloat);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
       mWebView.setWebViewClient(new WebViewClient(){

           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
               return true;
           }
       });
        mWebView.loadUrl("http://m.facebook.com/messages");

        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.closeViewButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                editTextDontReceiveFocus();
            }
        });



        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d("chatHead", "Into runnable_longClick");

                    isLongclick = true;
                    mTrashView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {


//                if (!isViewCollapsed()) {
//                    Log.d("Focus","Floating window");
//                    editTextReceiveFocus();
//                } else {
//                    Log.d("Focus","Background window");
//                    editTextDontReceiveFocus();
//                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                editTextReceiveFocus();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    private boolean isViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }


    private void editTextReceiveFocus() {
        if (!wasInFocus) {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mWindowManager.updateViewLayout(mFloatingView, params);
            wasInFocus = true;
        }
    }

    private void editTextDontReceiveFocus() {
        if (wasInFocus) {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mWindowManager.updateViewLayout(mFloatingView, params);
            wasInFocus = false;
        }
    }


    private void chathead_longclick(){
        Log.d("chatHead", "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) mTrashView.getLayoutParams();
        int x_cord_remove = (szWindow.x - mTrashView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (mTrashView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        mWindowManager.updateViewLayout(mTrashView, param_remove);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

}