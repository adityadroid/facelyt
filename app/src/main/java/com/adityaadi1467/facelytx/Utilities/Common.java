package com.adityaadi1467.facelytx.Utilities;


        import android.app.ActivityManager;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.res.Configuration;
        import android.content.res.Resources;
        import android.content.res.TypedArray;
        import android.graphics.Color;
        import android.support.annotation.ColorInt;
        import android.support.design.widget.Snackbar;
        import android.util.Log;
        import android.util.TypedValue;
        import android.view.View;
        import android.widget.TextView;

        import com.example.adi.facelyt.R;

public class Common {

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

}

