package com.adityaadi1467.facelytx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;

import com.example.adi.facelyt.R;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.mxn.soul.flowingdrawer_core.MenuFragment;


public class MyMenuFragment extends MenuFragment {

    private WebView mWebView;
    LeftDrawerLayout mLeftDrawerLayout;
    SwitchCompat lightModeSwitch,externalLinksSwitch,imagesSwitch,clearBookMarksSwitch;
    String DefaultUA;
    SQLiteDatabase sqLiteDatabase;

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    public MyMenuFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container,
                false);

        mWebView=(WebView) getActivity().findViewById(getArguments().getInt("webview"));
        mLeftDrawerLayout=(LeftDrawerLayout)getActivity().findViewById(getArguments().getInt("flowingdrawer"));
        lightModeSwitch = (SwitchCompat) view.findViewById(R.id.lightModeToggle);
        externalLinksSwitch=(SwitchCompat) view.findViewById(R.id.externalLinksToggle);
        imagesSwitch=(SwitchCompat) view.findViewById(R.id.loadImagesToggle);
        clearBookMarksSwitch=(SwitchCompat)view.findViewById(R.id.clearBookmarksToggle);
        NavigationView mNavigator = (NavigationView)view.findViewById(R.id.vNavigation);
        DefaultUA = mWebView.getSettings().getUserAgentString();
        sqLiteDatabase=getActivity().openOrCreateDatabase("Browser",getActivity().MODE_PRIVATE,null);


        settings = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        if((!settings.contains("external"))||
                (!settings.contains("light_mode"))||
                (!settings.contains("block_image"))){
            editor.putBoolean("external",false);
            editor.putBoolean("light_mode",false);
            editor.putBoolean("block_image",false);
            editor.commit();

        }
        lightModeSwitch.setChecked(settings.getBoolean("light_mode",false));
        externalLinksSwitch.setChecked(settings.getBoolean("external",false));
        imagesSwitch.setChecked(settings.getBoolean("block_image",false));
        if(lightModeSwitch.isChecked()){
            mWebView.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/7.6.35766/35.5706; U; en) Presto/2.8.119 Version/11.10");
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.reload();
        }
        if(externalLinksSwitch.isChecked()){
            MainActivity.loadExternal=true;

        }
        if(imagesSwitch.isChecked()){
            mWebView.getSettings().setBlockNetworkImage(true);
            mWebView.getSettings().setLoadsImagesAutomatically(false);

        }


        mNavigator.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId())
                {

                    case R.id.menu_news_feed:
                    mWebView.loadUrl("http://m.facebook.com");
                    break;

                    case R.id.menu_messages:
                        mWebView.loadUrl("http://m.facebook.com/messages");
                        break;
                    case R.id.menu_notifications:
                        mWebView.loadUrl("http://m.facebook.com/notifications.php");
                        break;
                    case R.id.menu_friends:
                        mWebView.loadUrl("http://m.facebook.com/friends.php");
                        break;
                    case R.id.menu_groups:
                        mWebView.loadUrl("http://m.facebook.com/groups");
                        break;
                    case R.id.menu_profile:
                        mWebView.loadUrl("http://m.facebook.com/profile.php");
                        break;
                    case R.id.menu_chat:
                        mWebView.loadUrl("http://m.facebook.com/buddylist.php");
                        break;
                    case R.id.menu_on_this_day:
                        mWebView.loadUrl("http://m.facebook.com/onthisday");
                        break;
                    case R.id.menu_share_this_to:

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
                        String sAux = "Hey! Check out this awesome app on the store:";
                        sAux = sAux + "\n https://play.google.com/store/apps/details?id=com.adityaadi1467.facelytx \n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Share This To:"));
                        break;

                    case R.id.menu_share_this_page:
                        i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
                         sAux = "Hey! Check this out on FaceLyt:";
                        sAux = sAux + "\n"+mWebView.getUrl().toString()+"\n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Share This To:"));

                        break;

                }

                mLeftDrawerLayout.closeDrawer();
                return false;
            }
        });

        imagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    mWebView.getSettings().setBlockNetworkImage(true);
                    mWebView.getSettings().setLoadsImagesAutomatically(false);
                }else{
                    mWebView.getSettings().setBlockNetworkImage(false);
                    mWebView.getSettings().setLoadsImagesAutomatically(true);
                }
                mWebView.reload();
                editor.putBoolean("block_image",b);
                editor.commit();

            }
        });
        lightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mWebView.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/7.6.35766/35.5706; U; en) Presto/2.8.119 Version/11.10");
                    mWebView.getSettings().setJavaScriptEnabled(false);
                    mWebView.reload();
                }else{
                    mWebView.getSettings().setUserAgentString(DefaultUA);
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.reload();
                }
                editor.putBoolean("light_mode",b);
                editor.commit();

            }
        });
        externalLinksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.loadExternal = b;
                editor.putBoolean("external",b);
                editor.commit();

            }
        });
        clearBookMarksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bi) {
                if(bi){
                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle("Delete Hotlinks");
                b.setMessage("Are you sure you want to delete all hotlinks");
                b.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sqLiteDatabase.execSQL("DELETE FROM bookmarks");
                        Intent intent = new Intent(getContext(),MainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
                b.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearBookMarksSwitch.setChecked(false);
                    }
                });
                b.show();
                }
            }
        });
        return  setupReveal(view) ;
    }


    public void onOpenMenu(){
    }
    public void onCloseMenu(){
    }
}
