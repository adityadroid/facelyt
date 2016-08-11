package com.example.adi.facelyt;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.DimenRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.search.material.library.AnimationUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    static    ArrayList<String> bookmarkname=new ArrayList<String>() {
        {
            //add("");
        }

    };
    static  ArrayList<String> bookmarklink=new ArrayList<String>(){
        {
            //  add("");
        }

    };



    static SQLiteDatabase db;
    static Cursor c;

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebSettings webSettings;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;


    boolean externallinks=false;

int oldscrolly=0;
    Button togglebt;
    EditText search;
TextView progresstv;
    String finalurl;
static BookmarkAdapter adapter;
    static WebView vb;
String toggle;
     String prev;
    String current;
    String defaultuseragent;
    SwipeRefreshLayout swipeLayout;
Spinner sp;
    CircularProgressBar circularProgressBar;

    private boolean isConnected = true;
//
//    @Override
//    protected void onResume() {
//        search.clearFocus();
//findViewById(R.id.focuslayout).requestFocus();
//        super.onResume();
//    }

static boolean viewprofile=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
     //   getSupportActionBar().setCustomView(R.layout.customnavbar);
       // View v = getSupportActionBar().getCustomView();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                1);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.circprogbar);
        circularProgressBar.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_light));
        circularProgressBar.setProgressBarWidth(6);
        circularProgressBar.setBackgroundProgressBarWidth(3);
        //  int animationDuration = 1000; // 2500ms = 2,5
        //    circularProgressBar.setProgressWithAnimation(65, animationDuration);
        vb = (WebView) findViewById(R.id.webView);
        WebSettings set = vb.getSettings();
        set.setJavaScriptEnabled(true);
        vb.getSettings().setPluginState(WebSettings.PluginState.ON);
        vb.getSettings().setAllowFileAccess(true);
        vb.setBackgroundColor(Color.TRANSPARENT);
vb.setBackgroundColor(Color.WHITE);

        defaultuseragent = vb.getSettings().getUserAgentString();


        //    togglebt = (Button) v.findViewById(R.id.button);
        //  search = (EditText) v.findViewById(R.id.editText);

        progresstv = (TextView) findViewById(R.id.progresstv);




        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        try {





            if (Intent.ACTION_VIEW.equals(action)) {
//                String scheme = data.getScheme(); // "http"
//                String host = data.getHost(); // "twitter.com"
//                List<String> params = data.getPathSegments();
//
//                finalurl=scheme+"/"+host+"/";
//                Toast.makeText(MainActivity.this,params.size(),Toast.LENGTH_SHORT).show();
//
//                for (int i=0;i<params.size();i++)
//                {
//                    finalurl.concat(params.get(i)+"/");
//                }
                finalurl=data.toString();

                vb.loadUrl(finalurl);
            }


           else if(viewprofile)
            {
                vb.loadUrl("http://mobile.facebook.com/adityaadi333");
                viewprofile=false;
            }
            else


            {
                vb.loadUrl("http://mobile.facebook.com");
            }




        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
        }


        db=openOrCreateDatabase("Browser",MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(name VARCHAR,link VARCHAR);");
        c=db.rawQuery("SELECT * FROM bookmarks", null);
        while(c.moveToNext())
        {

            if(!(bookmarklink.contains(c.getString(1))))
            {
                bookmarkname.add(c.getString(0));
                bookmarklink.add(c.getString(1));
            }
        }
        adapter=new BookmarkAdapter(MainActivity.this,bookmarkname,bookmarklink);

        sp=(Spinner)findViewById(R.id.fabsp);

        sp.setAdapter(adapter);


//
//
//        togglebt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    if(toggle=="m") {
//                        toggle = "mbasic";
//
//
//                        current = vb.getUrl();
//                        String temp = current.substring(9, current.length());
//                        vb.getSettings().setJavaScriptEnabled(false);
//                        vb.loadUrl("https://" + toggle + temp);
//
//                    }
//                }
//                else
//                {
//
//                    if(toggle=="mbasic") {
//                        toggle = "m";
//
//
//                        current = vb.getUrl();
//
//                        String temp = current.substring(14, current.length());
//                        vb.getSettings().setJavaScriptEnabled(true);
//                        vb.loadUrl("https://" + toggle + temp);
//
//                    }
//                }
//            }
//        });

//        togglebt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (toggle == "m") {
//                    toggle = "mbasic";
//                    vb.getSettings().setJavaScriptEnabled(false);
//
//                    vb.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/7.6.35766/35.5706; U; en) Presto/2.8.119 Version/11.10");
//
//
//                    //             current = vb.getUrl();
//                    //       String temp=current.substring(9,current.length());
//                    //         vb.loadUrl("https://" + toggle + temp);
//                    vb.reload();
//                    togglebt.setText("Normal");
//                    togglebt.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//
//
//                } else if (toggle == "mbasic") {
//                    toggle = "m";
//                    vb.getSettings().setJavaScriptEnabled(true);
//
//vb.getSettings().setUserAgentString(defaultuseragent);
//                    //      current = vb.getUrl();
//
//                    //           String temp=current.substring(14,current.length());
//                    //         vb.loadUrl("https://" + toggle + temp);
//vb.reload();
//                    togglebt.setText("Basic");
//                    togglebt.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                }
//
//
//            }
//        });
//
//
//        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//
//                int result = actionId & EditorInfo.IME_MASK_ACTION;
//                if (actionId == KeyEvent.KEYCODE_ENTER || result == EditorInfo.IME_ACTION_DONE || result == EditorInfo.IME_ACTION_GO || result == EditorInfo.IME_ACTION_NEXT) {
//
//
//                    final String query = Uri.encode(search.getText().toString(), "UTF-8");
//                    vb.loadUrl("http://m.facebook.com/search/?refid=17&search=Search&search_source=top_nav&query=" + query);
//
//                }
//                return false;
//            }
//        });


toggle="m";




//c=MainActivity.this;
//        ConnectivityManager connectivityManager = (ConnectivityManager)
//                c.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
//            if (ni.getState() != NetworkInfo.State.CONNECTED) {
//                // record the fact that there is not connection
//                isConnected = false;
//            }
//        }
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        swipeLayout.setDrawingCacheBackgroundColor(Color.parseColor("#3F51B5"));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        vb.reload();

                        swipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        vb.setWebChromeClient(new ChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            vb.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
            vb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        vb.setWebViewClient(new WebViewClient() {




            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {






                if(!vb.getUrl().contains("about:blank"))
                {
                    prev = vb.getUrl();


                }
                vb.loadUrl("about:blank");

                Snackbar sk = Snackbar.make(vb,"Duh! I could work better with an Internet Connection!", 20000).setAction("Reload", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            vb.loadUrl(prev);
                        }
                    });
                    sk.show();

//
//                Snackbar sk = Snackbar.make(vb, "Duh! I could work better with an Internet Connection!", 20000).setAction("Reload", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        vb.reload();
//
//                    }
//                });
//                sk.show();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(externallinks)
                {
                    vb.loadUrl(url);

                }

                else {
                    if (url.contains("facebook")) {
                        vb.loadUrl(url);
                    } else {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                }
                return true;

            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                //   view.setVisibility(View.GONE);
                circularProgressBar.setVisibility(View.VISIBLE);
                //   findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.progresstv).setVisibility(View.VISIBLE);
//                if (!isConnected) {
//                    findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
//
//                } else if (isConnected) {
//                    findViewById(R.id.imageView2).setVisibility(View.GONE);
//
//                }


            }

            @Override
            public void onPageFinished(WebView view, String url) {

                view.clearAnimation();
//                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.progresstv).setVisibility(View.GONE);
                circularProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);



            }

        });



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(adapter.getCount()==0)
                {



                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.demo);
                    dialog.show();
                }
else {
                    sp.performClick();
                }


            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                addBookmark();
                return false;
            }
        });


        vb.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(vb.getAnimation()!=null) {

                    vb.getAnimation().cancel();
                }
                int scrollX = vb.getScrollX(); //for horizontalScrollView
                int scrollY = vb.getScrollY();


                if (oldscrolly > scrollY) {
                    overridePendingTransition(R.anim.move, R.anim.move);

                    fab.show();
                    oldscrolly = scrollY;
                } else if (scrollY > oldscrolly) {
                    overridePendingTransition(R.anim.move, R.anim.move);

            fab.hide();
            oldscrolly = scrollY;



        }

            }
});




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setBackgroundResource(R.color.primary_light);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (vb.canGoBack())
        {
            vb.goBack();
            return;
        }
        else {
            super.onBackPressed();
        }
    }






    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }







    public class ChromeClient extends WebChromeClient {



        @Override
        public void onProgressChanged(WebView view, int newProgress) {

//
//if(newProgress==100&&vb.getSettings().getUserAgentString()==defaultuseragent)
//{
//        animate(view,R.anim.slide_down);
//        view.setVisibility(View.VISIBLE);
//
//}

  circularProgressBar.setProgress(newProgress);
progresstv.setText(String.valueOf(newProgress));
            super.onProgressChanged(view, newProgress);
        }

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }
        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }
        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }
        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }






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
                if (null == this.mUploadMessage) {
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

vb.loadUrl("https://mobile.facebook.com");
        }
        else if (id == R.id.profile) {

            vb.loadUrl("http://mobile.facebook.com/profile.php");

        }
        else if (id == R.id.chat) {
            vb.loadUrl("http://mobile.facebook.com/buddylist.php");

        }
        else if (id == R.id.friends) {
            vb.loadUrl("http://mobile.facebook.com/friends.php");

        }
        else if (id == R.id.groups) {
            vb.loadUrl("http://mobile.facebook.com/groups");

        }
        else if (id == R.id.pages) {
            vb.loadUrl("http://mobile.facebook.com/pages");

        }
        else if (id == R.id.notifications) {
            vb.loadUrl("http://mobile.facebook.com/notifications.php");

        }
        else if (id == R.id.messages) {
            vb.loadUrl("http://mobile.facebook.com/messages");

        }
        else if (id == R.id.onthisday) {
            vb.loadUrl("http://mobile.facebook.com/onthisday");
        }
        else if(id==R.id.about)
        {

            Intent i=new Intent(MainActivity.this,About.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }


//        else if(id==R.id.logout)
//        {
//            vb.loadUrl("https://mobilefacebook.com/logout.php");
//
//        }

        else if(id==R.id.sharepage)
        {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
            String sAux = "Hey! Check this out on FaceLyt:";
            sAux = sAux + "\n"+vb.getUrl().toString()+"\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share This To:"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



   static public void addBookmark()
    {
        String url =vb.getUrl();

        c=db.rawQuery("SELECT * FROM bookmarks WHERE link='"+url+"'",null);
        if(c.getCount()>0)
        {
            Snackbar.make(vb,"I already have this one flHotLinked! :)",Snackbar.LENGTH_SHORT).show();
        }
        else if(url.contains("about:blank"))
        {
            Snackbar.make(vb,"Bookmarking an error page? :/",Snackbar.LENGTH_SHORT).show();

        }
        else
        {
            db.execSQL("INSERT INTO bookmarks VALUES('"+vb.getTitle().toString()+"','"+url+"');");
            c=db.rawQuery("SELECT * FROM bookmarks",null);
            while (c.moveToNext())
            {
                if(!bookmarklink.contains(c.getString(1)))
                {
                    bookmarkname.add(c.getString(0));
                    bookmarklink.add(c.getString(1));
                }

            }

            adapter.notifyDataSetChanged();
            Snackbar.make(vb,vb.getTitle().toString()+" added to flHotLinks :)",Snackbar.LENGTH_SHORT).show();

        }

        c.close();
    }

    private void animate(final WebView view, int id) {
        Animation anim = AnimationUtils.loadAnimation(getBaseContext(),
                id);

        view.startAnimation(anim);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));




       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {

               final String finquery = Uri.encode(query, "UTF-8");
               vb.loadUrl("http://m.facebook.com/search/?refid=17&search=Search&search_source=top_nav&query=" + finquery);
               searchView.clearFocus();

               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               return false;


           }
       });




        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toggle) {

            if (toggle == "m") {
                toggle = "mbasic";
                vb.getSettings().setJavaScriptEnabled(false);

                vb.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/7.6.35766/35.5706; U; en) Presto/2.8.119 Version/11.10");


                vb.reload();
                item.setTitle("Normal");


            } else if (toggle == "mbasic") {
                toggle = "m";
                vb.getSettings().setJavaScriptEnabled(true);

                vb.getSettings().setUserAgentString(defaultuseragent);

                vb.reload();
                item.setTitle("Basic");
           }


            return true;
        }

        if(id==R.id.toggleexternallinks)
        {
            if(!externallinks) {
                item.setTitle("Disable External Links");
                externallinks=true;
            }
            else
            {
                item.setTitle("Enable External Links");

                externallinks=false;
            }

        }

        return super.onOptionsItemSelected(item);
    }



}
