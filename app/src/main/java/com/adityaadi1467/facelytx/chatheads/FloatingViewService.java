package com.adityaadi1467.facelytx.chatheads;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityaadi1467.facelytx.MainActivity;
import com.adityaadi1467.facelytx.Utilities.Dimension;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebChromeClient;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebView;
import com.example.adi.facelyt.R;

/**
 * Created by adi on 3/17/17.
 */

public class FloatingViewService extends Service {

    private boolean wasInFocus = true;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView;
    private ImageView removeImg;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    private boolean isChatHead = false;
    private TextView uploadImageTextView;
    View collapsedView,expandedView;
    VideoEnabledWebView mWebView;
    String loadUrl = "http://m.facebook.com";



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d("chatHead", "ChatHeadService.onStartCommand() -> startId=" + startId);

        if(intent != null){
            Bundle bd = intent.getExtras();

            if(bd != null){

                isChatHead = bd.getBoolean("isChatHead");
                if(isChatHead)
                    loadUrl = bd.getString("url");


            }


//            if(sMsg != null && sMsg.length() > 0){
//                if(startId == Service.START_STICKY){
//                    new Handler().postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            showMsg(sMsg);
//                        }
//                    }, 300);
//
//                }else{
//                    showMsg(sMsg);
//                }
//
//            }

        }

        if(startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        }else{
            return  Service.START_NOT_STICKY;
        }

    }

    @Override
    public void onCreate() {

        super.onCreate();
    }
    RelativeLayout mView;
    public void handleStart(){


        chatheadView= ((RelativeLayout)LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null));
        removeView =(RelativeLayout) LayoutInflater.from(this).inflate(R.layout.trash_layout,null);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = new RelativeLayout(this) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
                    Log.d("keyEvent","Back Presssed!");
                    if(mWebView.canGoBack()){
                        mWebView.goBack();
                    }else{
                        expandedView.setVisibility(GONE);
                        collapsedView.setVisibility(VISIBLE);
                        chatHeadRemoveFocus();
                    }
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };
        mView.addView(chatheadView);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
       removeImg = (ImageView)removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
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
        windowManager.addView(mView, params);

        //The root element of the collapsed view layout
        collapsedView = chatheadView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        expandedView = chatheadView.findViewById(R.id.expanded_container);

        //Set the close button

         mWebView = (VideoEnabledWebView)chatheadView.findViewById(R.id.webViewFloat);
        uploadImageTextView = (TextView)chatheadView.findViewById(R.id.uploadImageTextView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setIschatHead(true);
       mWebView.setWebViewClient(new WebViewClient(){

           @Override
           public void onPageFinished(WebView view, String url) {
               ApplyCustomCss();
               super.onPageFinished(view, url);
           }

           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               if(isChatHead){
                   if(url.startsWith("http://m.facebook.com/messages")){
                       Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
                       intent.putExtra("url",url);
                       startService(intent);
                   }
               }
               view.loadUrl(url);
               return true;
           }
       });




        mWebView.loadUrl("http://m.facebook.com/messages");

        ImageView closeButton = (ImageView) chatheadView.findViewById(R.id.closeViewButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                chatHeadRemoveFocus();
            }
        });


        uploadImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("filePicker",true);
                intent.putExtra("url",mWebView.getUrl().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

            }
        });


        chatHeadRemoveFocus();
        //Drag and move floating view using user's touch action.
       mView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d("FloatingView", "Into runnable_longClick");

                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

//                        if(txtView != null){
//                            txtView.setVisibility(View.GONE);
//                            myHandler.removeCallbacks(myRunnable);
//                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if(isLongclick){
                            int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

                            if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top){
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

                                if(removeImg.getLayoutParams().height == remove_img_height){
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - mView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - mView.getHeight())) / 2 ;

                                windowManager.updateViewLayout(mView, layoutParams);
                                break;
                            }else{
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                            }

                        }


                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(mView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if(inBounded){
//                            if(MyDialog.active){
//                                MyDialog.myDialog.finish();
//                            }


                            stopSelf();
                            //stop the service here
//                            stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5){
                            time_end = System.currentTimeMillis();
                            if((time_end - time_start) < 300){
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                chatHeadReceiveFocus();
//                                chathead_click();
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight =  getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (mView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mView.getHeight() + BarHeight );
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_cord);

                        break;
                    default:
                        Log.d("chatHead", "chatheadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });

    }

        /**
         * Detect if the floating view is collapsed or expanded.
         *
         * @return true if the floating view is collapsed.
         */
    private boolean isViewCollapsed() {
        return chatheadView == null || chatheadView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) windowManager.removeView(mView);
    }

    private boolean isViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }


    private void chatHeadReceiveFocus() {
        if (!wasInFocus) {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            windowManager.updateViewLayout(mView, params);
            mView.requestFocus();
            wasInFocus = true;
        }
    }

    private void chatHeadRemoveFocus() {
        if (wasInFocus) {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            windowManager.updateViewLayout(mView, params);
            wasInFocus = false;
        }
    }


    private void chathead_longclick(){
        Log.d("chatHead", "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void resetPosition(int x_cord_now) {
        if(x_cord_now <= szWindow.x / 2){
            isLeft = true;
            moveToLeft(x_cord_now);

        } else {
            isLeft = false;
            moveToRight(x_cord_now);

        }

    }


    private void moveToLeft(final int x_cord_now){
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = 0 - (int)(double)bounceValue(step, x );
                windowManager.updateViewLayout(mView, mParams);
            }
            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(mView, mParams);
            }
        }.start();
    }
    private  void moveToRight(final int x_cord_now){
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = szWindow.x + (int)(double)bounceValue(step, x_cord_now) - mView.getWidth();
                windowManager.updateViewLayout(mView, mParams);
            }
            public void onFinish() {
                mParams.x = szWindow.x - mView.getWidth();
                windowManager.updateViewLayout(mView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale){
        double value = scale * java.lang.Math.exp(-0.055 * step) * java.lang.Math.cos(0.08 * step);
        return value;
    }

//
//    public class MyWindow
//    {
//
//        // Add this empty layout:
//        private floatLayout myLayout;
//
//        public MyWindow()
//        {
//
//            // Add your original view to the new empty layout:
//            myLayout = new MyLayout(this);
//            myLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        }
//
//        // And show this layout instead of your original view:
//        public void show()
//        {
//            windowManager.addView(myLayout, params);
//        }
//
//        public void hide()
//        {
//            windowManager.removeView(myLayout);
//        }
//    }
//
//
//    public class floatLayout extends  RelativeLayout{
//
//        private floatWindow myWindow;
//
//        public floatLayout(floatWindow myWindow)
//        {
//            super(myWindow.context);
//
//            this.myWindow = myWindow;
//        }
//
//        public floatLayout(Context context) {
//            super(context);
//        }
//
//
//
//        @Override public boolean dispatchKeyEvent(KeyEvent event)
//        {
//            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
//            {
//                if (event.getAction() == KeyEvent.ACTION_DOWN  &&  event.getRepeatCount() == 0)
//                {
//                    getKeyDispatcherState().startTracking(event, this);
//                    return true;
//
//                }
//
//                else if (event.getAction() == KeyEvent.ACTION_UP)
//                {
//                    getKeyDispatcherState().handleUpEvent(event);
//
//                    if (event.isTracking() && !event.isCanceled())
//                    {
//                        // dismiss your window:
//                        myWindow.hide();
//
//                        return true;
//                    }
//                }
//            }
//
//            return super.dispatchKeyEvent(event);
//        }
//    }
//
//
//


    private void ApplyCustomCss() {
        String css = "";
        //  if (savedPreferences.getBoolean("pref_centerTextPosts", false)) {
        css += getString(R.string.centerTextPosts);
        //  }
        //   if (savedPreferences.getBoolean("pref_addSpaceBetweenPosts", false)) {
        css += getString(R.string.addSpaceBetweenPosts);
        //  }
        //    if (savedPreferences.getBoolean("pref_hideSponsoredPosts", false)) {
        css += getString(R.string.hideAdsAndPeopleYouMayKnow);
        // }
        //  if (savedPreferences.getBoolean("pref_fixedBar", true)) {//without add the barHeight doesn't scroll
        css += (getString(R.string.fixedBar).replace("$s", "" + Dimension.heightForFixedFacebookNavbar(getApplicationContext())));
        //   }
        //   if (savedPreferences.getBoolean("pref_removeMessengerDownload", true)) {
        css += getString(R.string.removeMessengerDownload);
        // }

        //  switch (savedPreferences.getString("pref_theme", "standard")) {
        //      case "DarkTheme":

        //      case "DarkNoBar": {
        css += getString(R.string.blackThemeNew);
        //       }
        //   default:
        //           break;
        //  }

        //apply the customizations
        mWebView.loadUrl(getString(R.string.editCss).replace("$css", css));
    }


}