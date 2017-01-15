package com.adityaadi1467.facelytx;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
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
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class MainActivity extends AppCompatActivity {


    /*
    class MyJavaScriptInterface
    {
        private Handler handler = new Handler();
        private WeakReference<FabSpeedDial> fabSpeedDialRef = new WeakReference<FabSpeedDial>(
                fabSpeedDial);

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processHTML(final String result)
        {
            Log.i("processed html","gggg"+result);
          //  Toast.makeText(getApplicationContext(),result.length()+"",Toast.LENGTH_SHORT).show();
            if(result.contains("profile_id"))
            {

                Log.d("Result","Result contains profile id,Entering the block now");

                int start=0;
                int i =0;
                while(i<result.length()-15)
                {
                    String pin = result.substring(i,i+10);
                    if(pin.equals("profile_id"))
                    {


                       // Toast.makeText(getApplicationContext(),pin,Toast.LENGTH_SHORT).show();
                        Log.d("PIN:",pin);
                        start =17+i;

                        profileID=result.substring(start,start+15);
                        Log.d("PROFILEID:",profileID);
                       // Toast.makeText(getApplicationContext(),profileID,Toast.LENGTH_SHORT).show();

                        if(isNumeric(profileID)) {

                            Log.d("isnumeric",profileID);
                            // Log.d("URL",url);
                            Log.d("RESULT",result);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fabSpeedDial=fabSpeedDialRef.get();
                                    fabSpeedDial.setVisibility(View.VISIBLE);
                                    fabSpeedDial.show();
                                }
                            });

                            break;
                        }
                    }


                    i++;

                }


                // profileID = result.substring(start,start+15);
             //   Toast.makeText(getApplicationContext(),profileID,Toast.LENGTH_SHORT).show();




            }
            else
            {
              //  Toast.makeText(getApplicationContext(),"no profile_id",Toast.LENGTH_SHORT).show();
                Log.d("RESULT", "NO PRODILW ID"+result);
                //  Log.d("URL",url);

                fabSpeedDial.hide();
            }

        }
    }

    */

    volatile boolean someBoolean;
    private LeftDrawerLayout mLeftDrawerLayout;

    private VideoEnabledWebView mWebView;
  //  private FabSpeedDial fabSpeedDial;
    private String profileID="";
    ConneckBar conneckBar;
    RecyclerView bookMarkRecycler;
    BookMarkAdapter bookMarkAdapter;
    VideoEnabledWebChromeClient webChromeClient;
    BoomMenuButton bmb;
    List<Bookmark> bookmarkList = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    ImageView bookMarkThisPage, unBookMarkThisPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

    //    fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
      //  fabSpeedDial.hide();
        bmb=(BoomMenuButton)findViewById(R.id.bmb);
        sqLiteDatabase=openOrCreateDatabase("Browser",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(name VARCHAR,link VARCHAR);");

               refreshList();

        setupToolbar();


        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        mWebView = (VideoEnabledWebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

//        MyJavaScriptInterface interf=new MyJavaScriptInterface();

  //      mWebView.addJavascriptInterface(interf, "HTMLOUT");
        mWebView.setWebViewClient(new mWebClient());
        mWebView.setWebChromeClient(new VideoEnabledWebChromeClient());
        FragmentManager fm = getSupportFragmentManager();
        conneckBar = new ConneckBar(getApplicationContext(), mWebView, "No Internet Connection!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(conneckBar.isConnected())
                {
                    mWebView.reload();
                }


            }
        }, Snackbar.LENGTH_INDEFINITE, Color.RED, Color.WHITE, Color.LTGRAY);


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


        mWebView.loadUrl("http://m.facebook.com");
        someBoolean=(mWebView.getProgress()==100);

        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout); // Your own view, read class comments

        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mWebView) // See all available constructors...
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
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        mWebView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        mWebView.loadUrl("http://m.youtube.com");



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
        if(size<10&&size!=0) {
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
                case 9:
                    bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
                    bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
                    break;

            }
            for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                mWebView.loadUrl(bookmarkList.get(0).getUrl().trim());
                            }
                        })
                        .innerListener(new InnerOnBoomButtonClickListener() {
                            @Override
                            public void onButtonClick(int index, BoomButton boomButton) {

                            }
                        })
                        .ellipsize(TextUtils.TruncateAt.MIDDLE)
                        .textRect(new Rect())
                        .textRect(new Rect(0, 0, 150, 150))
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



    public void removeBookmark(final Bookmark bookmark)
    {


        final Cursor cursor= sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='" +bookmark.getUrl()+ "'", null);
        if (cursor.moveToFirst()) {



           sqLiteDatabase.execSQL("DELETE FROM bookmarks WHERE link='" + bookmark.getUrl() + "'");

            refreshList();
        }

        cursor.close();


        Snackbar sk;
        sk=Snackbar.make(mWebView,"Removed bookmark!",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
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
        if(bookmarkList.size()<10){
        String url =bookmark.getUrl();

        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+url+"'",null);
        if(cursor.getCount()>0)
        {
            Snackbar.make(mWebView,"I already have this one HotLinked!",Snackbar.LENGTH_SHORT).show();
        }
        else if(url.contains("about:blank"))
        {
            Snackbar.make(mWebView,"Bookmarking an error page? :/",Snackbar.LENGTH_SHORT).show();

        }
        else
        {
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


    public void switchBookmarkAddButton(){
        Bookmark bookmark = new Bookmark(mWebView.getTitle().toString().trim(),mWebView.getUrl().toString().trim());

        final Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM bookmarks WHERE link='"+bookmark.getUrl()+"'",null);

        if(cursor.getCount()>0)
        {
            Log.d("a","a");
            bookMarkThisPage.setVisibility(View.GONE);
            unBookMarkThisPage.setVisibility(View.VISIBLE);
        }else{
            Log.d("b","b");
            unBookMarkThisPage.setVisibility(View.GONE);
            bookMarkThisPage.setVisibility(View.VISIBLE);
        }
    }
    public class mWebClient extends WebViewClient
    {

        /*

        boolean status=false;
        @Override
        public void doUpdateVisitedHistory(WebView view, final String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            // somecode to run on hash change.
            //Log.i("PROGRESS:","BBBB"+mWebView.getProgress()+"");
            if(status==false && !url.contains("soft=")) {
                Log.i("processed html", "vvvv" + url);
                mWebView.loadUrl(url);
                status=true;
                return;
            }
            status=false;
            //mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            waitForComplete(1000);
        }
        public void waitForComplete(int val)
        {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Log.i("PROGRESS:","BBBB"+mWebView.getProgress()+"");
                            if(mWebView.getProgress()==100) {
                                mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                            }
                            else
                            {
                                waitForComplete(1000);
                            }
                        }
                    },
                    val);
        }
        */


//
//            if(conneckBar.isConnected())
//            {
//
//
//            Ion.with(getApplicationContext()).load(url).asString().setCallback(new FutureCallback<String>() {
//
//
//                @Override
//                public void onCompleted(Exception e, String result) {
//
//                    if(result.contains("profile_id"))
//                    {
//
//
//
//                        int start=0;
//                        int i =0;
//                        while(i<result.length()-15)
//                        {
//                            String pin = result.substring(i,i+10);
//                            if(pin.equals("profile_id"))
//                            {
//
//
//                                Toast.makeText(getApplicationContext(),pin,Toast.LENGTH_SHORT).show();
//                                Log.d("PIN:",pin);
//                                start =17+i;
//
//                                profileID=result.substring(start,start+15);
//                                Log.d("PROFILEID:",profileID);
//                                Toast.makeText(getApplicationContext(),profileID,Toast.LENGTH_SHORT).show();
//
//                                if(isNumeric(profileID)) {
//
//                                    Log.d("isnumeric",profileID);
//                                    Log.d("URL",url);
//                                    Log.d("RESULT",result);
//                                    fabSpeedDial.setVisibility(View.VISIBLE);
//                                    fabSpeedDial.show();
//                                    break;
//                                }
//                            }
//
//
//                            i++;
//
//                        }
//
//
//                        // profileID = result.substring(start,start+15);
//                        Toast.makeText(getApplicationContext(),profileID,Toast.LENGTH_SHORT).show();
//
//
//
//
//                    }
//                    else
//                    {
//                        Toast.makeText(getApplicationContext(),mWebView.getUrl()+"no profile_id",Toast.LENGTH_SHORT).show();
//                        Log.d("RESULT", result);
//                        Log.d("URL",url);
//
//                        fabSpeedDial.hide();
//                    }
//
//
//
//                }
//            });
//            }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

//            if(!url.contains("www.facebook.com/search"))
//            {
//                mWebView.setDesktopMode(false);
//
//            }
            view.loadUrl(url);

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
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

            //       view.loadUrl("javascript:window.onhashchange = function() { HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); };");
//            if(mWebView.getSettings().getUserAgentString().equals(desktopUA))
//            {
//                mWebView.getSettings().setUserAgentString(defaultUA);
//            }
            findViewById(R.id.webViewProgress).setVisibility(View.GONE);
            switchBookmarkAddButton();

            super.onPageFinished(view, url);
        }
    }


    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

                    Bookmark bookmark = new Bookmark(mWebView.getTitle().toString().trim(),mWebView.getUrl().toString().trim());
                    addBookmark(bookmark);

            }
        });
        unBookMarkThisPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bookmark bookmark = new Bookmark(mWebView.getTitle().toString().trim(),mWebView.getUrl().toString().trim());
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


    public int indexOfFirstContainedCharacter(String s1, String s2) {
        Set<Character> set = new HashSet<Character>();
        for (int i=0; i<s2.length(); i++) {
            set.add(s2.charAt(i)); // Build a constant-time lookup table.
        }
        for (int i=0; i<s1.length(); i++) {
            if (set.contains(s1.charAt(i))) {
                return i; // Found a character in s1 also in s2.
            }
        }
        return -1; // No matches.
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }



//    public class chromeClient extends WebChromeClient
//    {
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//
//
//                mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//
//
//            super.onProgressChanged(view, newProgress);
//        }
//    }


}
