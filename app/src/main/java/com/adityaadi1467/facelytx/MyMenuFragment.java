package com.adityaadi1467.facelytx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.adi.facelyt.R;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.mxn.soul.flowingdrawer_core.MenuFragment;


public class MyMenuFragment extends MenuFragment {

    private WebView mWebView;
    LeftDrawerLayout mLeftDrawerLayout;




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
        NavigationView mNavigator = (NavigationView)view.findViewById(R.id.vNavigation);
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
                    case R.id.menu_share_this_to:

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
                        String sAux = "Hey! I can now see private photos on Facebook using this awesome app!";
                        sAux = sAux + "\n https://play.google.com/store/apps/details?id=sarathi.icanseeyou \n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Share This To:"));
                        break;

                    case R.id.menu_how_to_use:
//
//                        TaskStackBuilder.create(getContext())
//                                .addNextIntentWithParentStack(new Intent(getContext(), MainActivity.class))
//                                .addNextIntent(new Intent(getContext(), IntroActivity.class))
//                                .startActivities();
                        break;

                }

                mLeftDrawerLayout.closeDrawer();
                return false;
            }
        });
        return  setupReveal(view) ;
    }


    public void onOpenMenu(){
    }
    public void onCloseMenu(){
    }
}
