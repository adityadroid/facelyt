package com.adityaadi1467.facelytx;

import android.app.Activity;
import android.app.DownloadManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityaadi1467.facelytx.WebView.VideoEnabledWebChromeClient;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebView;
import com.example.adi.facelyt.R;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.InnerOnBoomButtonClickListener;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Util;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
 private LeftDrawerLayout mLeftDrawerLayout;

    private VideoEnabledWebView mWebView;
    ConneckBar conneckBar;
    VideoEnabledWebChromeClient webChromeClient;
    BoomMenuButton bmb;
    List<Bookmark> bookmarkList = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    ImageView bookMarkThisPage, unBookMarkThisPage;
    int oldscrolly=0;
    Toolbar toolbar;
    public static boolean loadExternal=false;
    SwipeRefreshLayout swipeLayout;
    public static String webViewTitle="";
    DownloadManager downloadManager;
    public final String DIRECTORY = "/facelyt";
    public Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
      bmb=(BoomMenuButton)findViewById(R.id.bmb);
        if(!checkPermissions()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.CAMERA"},105);
            }


        }



        setupToolbar();
        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        mWebView = (VideoEnabledWebView) findViewById(R.id.webView);
        downloadManager =    (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(new mWebClient());
        conneckBar = new ConneckBar(getApplicationContext(), mWebView, "No Internet Connection!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(conneckBar.isConnected())
                {
                    mWebView.reload();
                }


            }
        }, Snackbar.LENGTH_INDEFINITE, Color.RED, Color.WHITE, Color.LTGRAY);

        sqLiteDatabase=openOrCreateDatabase("Browser",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(name VARCHAR,link VARCHAR);");

        refreshList();

        FragmentManager fm = getSupportFragmentManager();





        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            mMenuFragment = new MyMenuFragment();
            Bundle bundle= new Bundle();
            bundle.putInt("webview",mWebView.getId());
            bundle.putInt("flowingdrawer",mLeftDrawerLayout.getId());
            mMenuFragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.id_container_menu,mMenuFragment).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);


        //Setting up the webview


        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout); // Your own view, read class comments

        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mWebView,MainActivity.this) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    toolbar.setVisibility(View.GONE);
                }
                else
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    toolbar.setVisibility(View.VISIBLE);
                }

            }
        });
        mWebView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site




        //hiding the bmb button on scroll

        mWebView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {


                int scrollX = mWebView.getScrollX(); //for horizontalScrollView
                int scrollY = mWebView.getScrollY();


                if (oldscrolly > scrollY) {

                    bmb.setVisibility(View.VISIBLE);
                    oldscrolly = scrollY;
                } else if (scrollY > oldscrolly) {

                    bmb.setVisibility(View.GONE);
                    oldscrolly = scrollY;



                }

            }
        });
        mWebView.loadUrl("http://m.facebook.com");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setBackgroundColor(getResources().getColor(R.color.style_color_primary_dark));
        swipeLayout.setDrawingCacheBackgroundColor(getResources().getColor(R.color.style_color_primary));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mWebView.reload();

                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });





    }

    public void openDataBase(){
         Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks", null);
        bookmarkList.clear();
        while(cursor.moveToNext())
        {

            Bookmark bookmark= new Bookmark(cursor.getString(0),cursor.getString(1));
            if(!bookmarkList.contains(bookmark)){
            bookmarkList.add(bookmark);
                Log.d("added",bookmark.getTitle());
            }
        }


    }
    public void refreshList(){
        openDataBase();
        int size = bookmarkList.size();
        Log.d("size",size+"");
        if(size!=0) {
            bmb.clearBuilders();
            switch (size) {
                case 1:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_1);
                    break;
                case 2:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
                    break;
                case 3:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
                    break;
                case 4:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_1);
                    break;
                case 5:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_1);
                    break;
                case 6:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_1);
                    break;
                case 7:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_1);
                    break;
                case 8:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_8_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_8_1);
                    break;
                default:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
                    break;

            }

            for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {

                                mWebView.loadUrl(bookmarkList.get(index).getUrl().trim());
                            }
                        })
                        .innerListener(new InnerOnBoomButtonClickListener() {
                            @Override
                            public void onButtonClick(int index, BoomButton boomButton) {

                            }
                        })
                        .ellipsize(TextUtils.TruncateAt.MIDDLE)
                       .textRect(new Rect(Util.dp2px(0), Util.dp2px(0),Util.dp2px(80),Util.dp2px(80)))
                        .typeface(Typeface.MONOSPACE)
                        .textSize(15)
                        .maxLines(3)
                        .normalText(bookmarkList.get(i).getTitle());
                bmb.addBuilder(builder);
            }
        }
        else if(size==0){
            bmb.clearBuilders();
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_1);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_1);
            for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                        .ellipsize(TextUtils.TruncateAt.MIDDLE)
                        .textRect(new Rect())
                        .textRect(new Rect(0, 0, 150, 150))
                        .typeface(Typeface.MONOSPACE)
                        .textSize(15)
                        .maxLines(3)
                        .normalText("Add a bookmark to get started.");
                bmb.addBuilder(builder);
            }

        }



    }


        public boolean checkPermissions(){
            int res = checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            int res1 = checkCallingOrSelfPermission("android.permission.CAMERA");
            return (res== PackageManager.PERMISSION_GRANTED&&res1==PackageManager.PERMISSION_GRANTED);
        }


    public void removeBookmark(final Bookmark bookmark)
    {


        final Cursor cursor= sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='" +bookmark.getUrl()+ "'", null);
        if (cursor.moveToFirst()) {



           sqLiteDatabase.execSQL("DELETE FROM bookmarks WHERE link='" + bookmark.getUrl() + "'");

            refreshList();
        }

        cursor.close();


        Snackbar sk;
        sk=Snackbar.make(mWebView,"Removed hotlink!",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Cursor innerCursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+bookmark.getUrl()+"'",null);

                if(!(innerCursor.getCount()>0))
                {
                    sqLiteDatabase.execSQL("INSERT INTO bookmarks VALUES('" + bookmark.getTitle() + "','" + bookmark.getUrl() + "');");
                   refreshList();
                    switchBookmarkAddButton();


                }
            }
        });
        sk.show();
        switchBookmarkAddButton();

    }



    public void addBookmark(Bookmark bookmark)
    {

         Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks",null);

        if(cursor.getCount()<9)
        {
        String url =bookmark.getUrl();
        cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+url+"'",null);
        if(cursor.getCount()>0)
        {
            Snackbar.make(mWebView,"I already have this one hotlinked!",Snackbar.LENGTH_SHORT).show();
        }
        else if(url.contains("about:blank"))
        {
            Snackbar.make(mWebView,"Bookmarking an error page?",Snackbar.LENGTH_SHORT).show();

        }
        else
        {
            bookmark.setTitle(bookmark.getTitle().replace("'",""));
            sqLiteDatabase.execSQL("INSERT INTO bookmarks VALUES('"+bookmark.getTitle()+"','"+url+"');");
             refreshList();

            Snackbar.make(mWebView,mWebView.getTitle().toString().trim()+" added to HotLinks",Snackbar.LENGTH_SHORT).show();

        }

        cursor.close();
            refreshList();
        switchBookmarkAddButton();

        }else{
            Snackbar.make(mWebView,"All hotlinks occupied!",Snackbar.LENGTH_SHORT).show();
        }
    }



    public void switchBookmarkAddButton(String url){
            Bookmark bookmark = new Bookmark(webViewTitle, url.replace("'","''"));
            Log.d("DET:",webViewTitle+url);
        final Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+bookmark.getUrl()+"'",null);

        if(cursor.getCount()>0)
        {

            Log.d("a",bookmark.getTitle()+"exists");
            bookMarkThisPage.setVisibility(View.GONE);
            unBookMarkThisPage.setVisibility(View.VISIBLE);
        }else{
            Log.d("b",bookmark.getTitle()+"doesn't exist");
            unBookMarkThisPage.setVisibility(View.GONE);
            bookMarkThisPage.setVisibility(View.VISIBLE);
        }
    }

    public void switchBookmarkAddButton(){
        Bookmark bookmark = new Bookmark(webViewTitle, mWebView.getUrl().toString().replace("'","''").trim());

        final Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+bookmark.getUrl()+"'",null);

        if(cursor.getCount()>0)
        {

            Log.d("a",bookmark.getTitle()+"exists");
            bookMarkThisPage.setVisibility(View.GONE);
            unBookMarkThisPage.setVisibility(View.VISIBLE);
        }else{
            Log.d("b",bookmark.getTitle()+"doesn't exist");
            unBookMarkThisPage.setVisibility(View.GONE);
            bookMarkThisPage.setVisibility(View.VISIBLE);
        }
    }
    public class mWebClient extends WebViewClient
    {


        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
           switchBookmarkAddButton(url);
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(!(url.startsWith("http://")|| url.startsWith("https://"))){
                url  = "https://"+url;
            }
            switchBookmarkAddButton(url);
            if(url.contains("intent://")){
                return true;
            }
            else if(url.contains("fbcdn.net")){
                Log.d("url","Downloading image");
                downloadImage(url);
            }else if(loadExternal){
                view.loadUrl(url);
            }
            else
            {
                if(url.length()>=22){

                if(url.substring(0,22).contains("facebook")){
                    view.loadUrl(url);
                    Log.d("url",url+url.length());
                    Log.d("substr",url.substring(0,22));
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }else{
                        showSnack("No browser installed!");

                    }
                }
                }
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            switchBookmarkAddButton(url);

            if(conneckBar.isConnected())
            {

                mWebView.setVisibility(View.VISIBLE);
                findViewById(R.id.webViewProgress).setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);

            }
            else
            {
                mWebView.setVisibility(View.GONE);
            }
        }


        @Override
        public void onPageFinished(WebView view, String url) {
       findViewById(R.id.webViewProgress).setVisibility(View.GONE);

            super.onPageFinished(view, url);
        }
    }


    protected void setupToolbar() {
       toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftDrawerLayout.toggle();
            }
        });
        bookMarkThisPage = (ImageView)toolbar.findViewById(R.id.bookMarkThisPage);
         unBookMarkThisPage =(ImageView) toolbar.findViewById(R.id.unBookMarkThisPage);
        bookMarkThisPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Bookmark bookmark = new Bookmark(mWebView.getTitle().toString().trim(),mWebView.getUrl().toString().trim().replace("'","''"));
                    addBookmark(bookmark);

            }
        });
        unBookMarkThisPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bookmark bookmark = new Bookmark(mWebView.getTitle().toString().trim(),mWebView.getUrl().toString().trim().replace("'","''"));
                removeBookmark(bookmark);

            }
        });



    }


    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
        }
        else if(mWebView.canGoBack())
        {
            mWebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }




    //Camera Gallery Code





    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int FILECHOOSER_RESULTCODE = 1;
    public static final String TAG = "MainActivity";
    public static ValueCallback<Uri> mUploadMessage;
    public static  Uri mCapturedImageURI = null;
    public static ValueCallback<Uri[]> mFilePathCallback;
    public static String mCameraPhotoPath;



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }






    //Camera Gallery Code



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted =true;
        for(int grant : grantResults) {
            if(grant==PackageManager.PERMISSION_DENIED) {
                granted=false;
                break;
            }
        }


        if(granted)
        {
            switch (requestCode)
            {

                case 105:
                    break;

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please enable permissions needed to run this application.",Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    public void downloadImage(final String url){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Image Action  Required");
        builder.setMessage("What would you like to do with this image?");
        builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            mWebView.loadUrl(url);
            }
        });
        builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                vibrator.vibrate(50);
                Uri source = Uri.parse(url);
                // Make a new request pointing to the mp3 url
                DownloadManager.Request request = new DownloadManager.Request(source);
                // Use the same file name for the destination
                File destinationFile = new File (Environment.getExternalStorageDirectory() +DIRECTORY, source.getLastPathSegment());
                request.setDestinationUri(Uri.fromFile(destinationFile));
                // Add it to the manager
                downloadManager.enqueue(request);
                Snackbar snackbar= Snackbar.make(bookMarkThisPage, "Download started.",Snackbar.LENGTH_LONG ).setAction("View", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    }
                });
                View snackBarView = snackbar.getView();

                snackBarView.setBackgroundColor(getResources().getColor(R.color.style_color_primary));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                TextView retry = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_action);
                retry.setTextColor(getResources().getColor(R.color.white));

                snackbar.show();


            }
        });
        builder.show();


    }




    public void showSnack(String message){
        Snackbar snackbar= Snackbar.make(bookMarkThisPage, message,Snackbar.LENGTH_SHORT );
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(getResources().getColor(R.color.style_color_primary));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));

        snackbar.show();

    }


}
