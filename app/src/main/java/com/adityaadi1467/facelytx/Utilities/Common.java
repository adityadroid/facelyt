package com.adityaadi1467.facelytx.Utilities;


        import android.app.ActivityManager;
        import android.app.DownloadManager;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.res.Configuration;
        import android.content.res.Resources;
        import android.content.res.TypedArray;
        import android.graphics.Color;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.Vibrator;
        import android.support.annotation.ColorInt;
        import android.support.design.widget.Snackbar;
        import android.util.Log;
        import android.util.TypedValue;
        import android.view.View;
        import android.webkit.WebView;
        import android.widget.TextView;

        import com.example.adi.facelyt.R;

        import java.io.File;

        import static android.content.Context.DOWNLOAD_SERVICE;

public class Common {
    public static final String DIRECTORY = "/facelyt";

    // get navigation bar height
    private static int getNavigationBarHeight(Context context, int orientation) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ?
                "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // window's height minus navbar minus extra top padding, all divided by density
    public static int heightForFixedFacebookNavbar(Context context) {
        final int navbar = getNavigationBarHeight(context, context.getResources().getConfiguration().orientation);
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) ((context.getResources().getDisplayMetrics().heightPixels - navbar - 44) / density);
    }
    public static void showSnack(View view,Context context,String message){
        Snackbar snackbar= Snackbar.make(view,message,Snackbar.LENGTH_SHORT );
        View snackBarView = snackbar.getView();
        int[] attribute = new int[] { R.attr.colorPrimary };
        TypedArray array = context.getTheme().obtainStyledAttributes(attribute);
        int backgroundColor = array.getColor(0, Color.TRANSPARENT);

        array.recycle();

        Log.d("Colors:",""+Integer.toHexString(backgroundColor)+" ");

        snackBarView.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(backgroundColor)));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);

        textView.setTextColor(context.getResources().getColor(R.color.white));

        snackbar.show();
    }
    public static boolean isServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static final String[] themes = {"Facelyt", "Midnight Blue", "Pomegranate", "Clouds", "Emerald","Concrete","Peter River","Cyan","Indigo"};

    public  static  int getCurrentTheme(SharedPreferences settings){
        String currentTheme = settings.getString("theme","Facelyt");
        switch (currentTheme){
            case "Facelyt":
               return  R.style.AppTheme;
            case "Midnight Blue":
                return  R.style.AppThemeMidnightBlue;
            case "Pomegranate":
                return  R.style.AppThemePomegranate;
            case "Clouds":
                return  R.style.AppThemeClouds;
            case "Emerald":
                return  R.style.AppThemeEmerald;
            case "Concrete":
                return  R.style.AppThemeConcrete;
            case "Peter River":
                return R.style.AppThemePeterRiver;
            case "Cyan":
                return R.style.AppThemeCyan;
            case "Indigo":
                return R.style.AppThemeIndigo;
        }

        return R.style.AppTheme;
    }
    public static int getPrimaryColour(Resources.Theme theme){
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;

    }

    public static void downloadImage(final Context context,View view,String url){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        vibrator.vibrate(50);
        Uri source = Uri.parse(url);
        // Make a new request pointing to the mp3 url
        DownloadManager.Request request = new DownloadManager.Request(source);
        // Use the same file name for the destination
        File destinationFile = new File(Environment.getExternalStorageDirectory() + DIRECTORY, source.getLastPathSegment());
        request.setDestinationUri(Uri.fromFile(destinationFile));
        // Add it to the manager
        downloadManager.enqueue(request);
        Snackbar snackbar = Snackbar.make(view, "Download started.", Snackbar.LENGTH_LONG).setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
        });
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.style_color_primary));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.white));
        TextView retry = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_action);
        retry.setTextColor(context.getResources().getColor(R.color.white));

        snackbar.show();
    }
    public static void shareLink(Context context, WebView mWebView) {


        Intent i;
        String sAux;
        i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "FaceLyt");
        sAux = "Hey! Check this out on FaceLyt:";
        sAux = sAux + "\n" + mWebView.getUrl().toString() + "\n";
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        context.startActivity(Intent.createChooser(i, "Share This To:"));
    }
}

