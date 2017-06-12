package com.adityaadi1467.facelytx;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityaadi1467.facelytx.Utilities.Common;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebChromeClient;
import com.adityaadi1467.facelytx.WebView.VideoEnabledWebView;
import com.adityaadi1467.facelytx.chatheads.FloatingViewService;
import com.example.adi.facelyt.R;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.InnerOnBoomButtonClickListener;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FlowingDrawer mDrawer;

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
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    boolean ischatHead = false;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    MyHandler linkHandler;
    ImageView launchChatHead;

    ImageView enableNightMode, disableNightMode;
    ImageView shareThisLinkButton;
    boolean showFab = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        settings = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2


                 if (getIntent() != null) {
                     if (getIntent().hasExtra("filePicker")) {
                         ischatHead = true;
                     }
                 }
//                 setTheme(R.style.AppThemeIndigo);
        if(settings.getBoolean("dark_mode",false))
            setTheme(R.style.AppThemeDark);
        else{
            MainActivity.this.setTheme(Common.getCurrentTheme(settings));

        }


        if((!settings.contains("external"))||
                (!settings.contains("light_mode"))||
                (!settings.contains("block_image"))||
                (!settings.contains("dark_mode"))||
                (!settings.contains("sponsored_posts"))||
                (!settings.contains("link_sharing"))||
                (!settings.contains("theme"))
                ){
            editor.putBoolean("external",false);
            editor.putBoolean("light_mode",false);
            editor.putBoolean("block_image",false);
            editor.putBoolean("dark_mode",false);
            editor.putBoolean("sponsored_posts",false);
            editor.putBoolean("link_sharing",false);
            editor.putString("theme","Facelyt");
            editor.putBoolean("fab_button",false);
            editor.commit();

        }

        setContentView(R.layout.activity_main_new);


        if(!checkPermissions()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.CAMERA"},105);
            }


        }

        setupToolbar();


        bmb=(BoomMenuButton)findViewById(R.id.bmb);
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mWebView = (VideoEnabledWebView) findViewById(R.id.webView);
        downloadManager =    (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        linkHandler = new MyHandler(this);
        FragmentManager fm = getSupportFragmentManager();
        sqLiteDatabase=openOrCreateDatabase("Browser",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(name VARCHAR,link VARCHAR);");
        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MyMenuFragment();
            Bundle bundle= new Bundle();
            bundle.putInt("webview",mWebView.getId());
            bundle.putInt("flowingdrawer",mDrawer.getId());
            mMenuFragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.id_container_menu,mMenuFragment).commit();
        }
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout); // Your own view, read class comments
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mWebView,MainActivity.this); // See all available constructors...
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new mWebClient());
        mWebView.setIschatHead(false);
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

        mWebView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {


                int scrollX = mWebView.getScrollX(); //for horizontalScrollView
                int scrollY = mWebView.getScrollY();


                if (oldscrolly > scrollY+100&&showFab) {
                    Log.d("oldScroll",oldscrolly+"");
                    Log.d("scroll",scrollY+"");
                    bmb.setVisibility(View.VISIBLE);
                    oldscrolly = scrollY;
                } else if (scrollY > oldscrolly) {

                    bmb.setVisibility(View.GONE);
                    oldscrolly = scrollY;



                }

            }
        });
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
        if(settings.getBoolean("light_mode",false)){
            mWebView.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/7.6.35766/35.5706; U; en) Presto/2.8.119 Version/11.10");
            mWebView.getSettings().setJavaScriptEnabled(false);
        }
        if(settings.getBoolean("external",false)){
            loadExternal=true;

        }
        if(settings.getBoolean("block_image",false)){
            mWebView.getSettings().setBlockNetworkImage(true);
            mWebView.getSettings().setLoadsImagesAutomatically(false);

        }
        if(settings.getBoolean("link_sharing",false))
            SetupOnLongClickListener();

        if(settings.getBoolean("fab_button",false)){
            showFab = true;
            bmb.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        final String action = intent.getAction();
        String urlInit = "http://m.facebook.com";
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            try {
                urlInit = (new URL(uri.getScheme(), uri.getHost(), uri.getPath())).toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
                 Log.d("url",urlInit);
        }

        if(getIntent()!=null){
            if(ischatHead)
            {
                Log.d("Inside:","isChatHead block");
                urlInit= getIntent().getExtras().getString("url");
                // mFlowingView.setVisibility(View.INVISIBLE);
                Common.showSnack(mWebView,MainActivity.this,"Pick a image now");
            }
            else if(getIntent().hasExtra("url"))
                urlInit = getIntent().getExtras().getString("url");

        }

        mWebView.loadUrl(urlInit);

        conneckBar = new ConneckBar(getApplicationContext(), mWebView, "No Internet Connection!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(conneckBar.isConnected())
                {
                    mWebView.reload();
                }


            }
        }, Snackbar.LENGTH_INDEFINITE, Color.RED, Color.WHITE, Color.LTGRAY);

        refreshList();



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

           // Log.d("a",bookmark.getTitle()+"exists");
            bookMarkThisPage.setVisibility(View.GONE);
            unBookMarkThisPage.setVisibility(View.VISIBLE);
        }else{
           // Log.d("b",bookmark.getTitle()+"doesn't exist");
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


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "https://" + url;
            }


            switchBookmarkAddButton(url);


            if ((url.contains("market://") || url.contains("mailto:")
                    || url.contains("play.google") || url.contains("youtube")
                    || url.contains("tel:")
                    || url.contains("vid:")) == true) {
                Log.d(TAG, "shouldOverrideUrlLoading: External Intent event");
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            if (url.contains("scontent") && url.contains("jpg")) {
                Log.d(TAG, "shouldOverrideUrlLoading: Image Download");
                if (url.contains("l.php?u=")) {
                    return false;
                }
                downloadImage(url);
                return true;
            } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                    || Uri.parse(url).getHost().endsWith("m.facebook.com")
                    || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                    || Uri.parse(url).getHost().endsWith("mobile.facebook.com/messages")
                    || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                    || Uri.parse(url).getHost().endsWith("h.facebook.com")
                    || Uri.parse(url).getHost().endsWith("l.facebook.com")
                    || Uri.parse(url).getHost().endsWith("0.facebook.com")
                    || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                    || Uri.parse(url).getHost().endsWith("fbcdn.net")
                    || Uri.parse(url).getHost().endsWith("akamaihd.net")
                    || Uri.parse(url).getHost().endsWith("fb.me")
                    || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                Log.d(TAG, "shouldOverrideUrlLoading: Loadinh fb link");
                return false;


            } else if (loadExternal) {
                Log.d(TAG, "shouldOverrideUrlLoading: Loading External Link");
                return false;
            } else {
                Log.d(TAG, "shouldOverrideUrlLoading: Firing external intent");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    e.printStackTrace();
                }
                return true;

            }

        }

//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        if (intent.resolveActivity(getPackageManager()) != null) {
//        startActivity(intent);
//    }else{
//        Common.showSnack(mWebView,getApplicationContext(),"No browser installed!");
//
//    }

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
            ApplyCustomCss();
       findViewById(R.id.webViewProgress).setVisibility(View.GONE);
            if(ischatHead)
                view.pageDown(true);
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
                mDrawer.toggleMenu(true);
            }
        });
        launchChatHead = (ImageView )toolbar.findViewById(R.id.launchChatHead);
        bookMarkThisPage = (ImageView)toolbar.findViewById(R.id.bookMarkThisPage);
         unBookMarkThisPage =(ImageView) toolbar.findViewById(R.id.unBookMarkThisPage);
        enableNightMode = (ImageView)toolbar.findViewById(R.id.enableNightMode);
        disableNightMode = (ImageView)toolbar.findViewById(R.id.disableNightMode);
        shareThisLinkButton = (ImageView)toolbar.findViewById(R.id.shareThisPage);
        if(settings.getBoolean("dark_mode",false)){
            disableNightMode.setVisibility(View.VISIBLE);
            enableNightMode.setVisibility(View.GONE);
        }else{
            disableNightMode.setVisibility(View.GONE);
            enableNightMode.setVisibility(View.VISIBLE);
        }
        enableNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("dark_mode",true);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("url",mWebView.getUrl());
                startActivity(intent);

                finish();
            }
        });
        disableNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("dark_mode",false);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("url",mWebView.getUrl());
                startActivity(intent);

                finish();

            }
        });
        shareThisLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, shareThisLinkButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.share_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_share_link:
                                shareLink();
                                break;
                            case R.id.menu_share_screenshot:
                                shareScreenshot();
                                break;
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });
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
        launchChatHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {


                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    initializeChatHead("http://m.facebook.com/messages");
                }
            }
        });



    }


    @Override
    public void onBackPressed() {

        mDrawer.closeMenu(true);

        if(mWebView.canGoBack())
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

        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeChatHead("http://m.facebook.com/messages");
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available.",
                        Toast.LENGTH_SHORT).show();


            }
        }
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
        final AlertDialog dialog = builder.create();
        int[] attribute = new int[] { R.attr.colorPrimary };
        TypedArray array = MainActivity.this.getTheme().obtainStyledAttributes(attribute);
       final int color = array.getColor(0, Color.TRANSPARENT);

        array.recycle();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#"+Integer.toHexString(color)));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#"+Integer.toHexString(color)));

            }
        });
        dialog.show();


    }




    /**
     * Set and initialize the view elements.
     */
    private void initializeChatHead(String url) {
        vibrator.vibrate(50);
        if(Common.isServiceRunning(FloatingViewService.class,getApplicationContext()))
        {stopService(new Intent(getApplicationContext(),FloatingViewService.class));
        }
        startService(new Intent(MainActivity.this, FloatingViewService.class).putExtra("isChatHead",true).putExtra("url",url));

      //  Toast.makeText(MainActivity.this, "Started", Toast.LENGTH_SHORT).show();
    }


    private void ApplyCustomCss() {
        String css = "";
        int color = Common.getPrimaryColour(getTheme());
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        css += getString(R.string.customColor).replace("replacementColor",hexColor);

        if(settings.getBoolean("dark_mode",false))
                css += getString(R.string.blackThemeNew);
            if(settings.getBoolean("sponsored_posts",false))
             css += getString(R.string.hideAdsAndPeopleYouMayKnow);

             css += (getString(R.string.fixedBar).replace("$s", "" + Common.heightForFixedFacebookNavbar(getApplicationContext())));
             css += getString(R.string.removeMessengerDownload);

         mWebView.loadUrl(getString(R.string.editCss).replace("$css", css));


    }

    private  class MyHandler extends Handler {
        MainActivity activity;
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            this.activity = activity;
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (settings.getBoolean("link_sharing", true)) {
                MainActivity activity = mActivity.get();
                if (activity != null) {

                    // get url to share
                    String url = (String) msg.getData().get("url");

                    if (url != null) {
                    /* "clean" an url to remove Facebook tracking redirection while sharing
					and recreate all the special characters */
                        url = decodeUrl(cleanUrl(url));

                        // create share intent for long clicked url
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, url);
                        activity.startActivity(Intent.createChooser(intent, "Share this link to"));
                    }
                }
            }
        }

        // "clean" an url and remove Facebook tracking redirection
        private  String cleanUrl(String url) {
            return url.replace("http://lm.facebook.com/l.php?u=", "")
                    .replace("https://m.facebook.com/l.php?u=", "")
                    .replace("http://0.facebook.com/l.php?u=", "")
                    .replaceAll("&h=.*", "").replaceAll("\\?acontext=.*", "") + "&SharedWith=FaceLyt";
        }

        // url decoder, recreate all the special characters
        private  String decodeUrl(String url) {
            return url.replace("%3C", "<").replace("%3E", ">").replace("%23", "#").replace("%25", "%")
                    .replace("%7B", "{").replace("%7D", "}").replace("%7C", "|").replace("%5C", "\\")
                    .replace("%5E", "^").replace("%7E", "~").replace("%5B", "[").replace("%5D", "]")
                    .replace("%60", "`").replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
                    .replace("%3A", ":").replace("%40", "@").replace("%3D", "=").replace("%26", "&")
                    .replace("%24", "$").replace("%2B", "+").replace("%22", "\"").replace("%2C", ",")
                    .replace("%20", " ");
        }
    }


    private void SetupOnLongClickListener() {
        // OnLongClickListener for detecting long clicks on links and images
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                WebView.HitTestResult result =mWebView.getHitTestResult();
                int type = result.getType();
                if (type == WebView.HitTestResult.SRC_ANCHOR_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                        || type == WebView.HitTestResult.IMAGE_TYPE) {
                    Message msg = linkHandler.obtainMessage();
                   mWebView.requestFocusNodeHref(msg);
                    final String imgUrl = (String) msg.getData().get("src");

                    if (imgUrl != null) {
                       downloadImage(imgUrl);
                    }
                    return true;
                }
                return false;
            }
        });
    }







    private void shareScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file

            String mPath = Environment.getExternalStorageDirectory().toString()+DIRECTORY + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            //v1.setDrawingCacheEnabled(false);

            File Root =Environment.getExternalStorageDirectory().getAbsoluteFile();
            File imageDirectory = new File( Root,"/facelyt/");

            if(imageDirectory.mkdirs())
            {
                Log.d("Created Folder","True");
            }else{
                Log.d("Created folder","False");
            }
            Log.i("Local filename:",""+now+".jpg");

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();



            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("image/*");

// For a file in shared storage.  For data in private storage, use a ContentProvider.
            Uri uri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", imageFile);            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            String sAux;
            sAux = "Hey! Check this out on FaceLyt:";
            sAux = sAux + "\n"+mWebView.getUrl().toString()+"\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, sAux);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share This To:"));
//            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }



    public void shareLink(){


                Intent i;
                String sAux;
                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
                sAux = "Hey! Check this out on FaceLyt:";
                sAux = sAux + "\n"+mWebView.getUrl().toString()+"\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share This To:"));
    }


    }
