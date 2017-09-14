package com.adityaadi1467.facelytx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityaadi1467.facelytx.Utilities.Common;
import com.example.adi.facelyt.R;

import static com.adityaadi1467.facelytx.Utilities.Common.themes;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener {

    SwitchCompat fabToggle, lightModeToggle, externalLinksToggle, blockImagesToggle, darkModeToggle, sponsoredPostsToggle, longPressToShareToggle;
    TextView clearHotLinksTv, aboutDevTv, versionTv, rateUsTv, graphicsTv;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    SQLiteDatabase sqLiteDatabase;
    String url;
    RelativeLayout themeChangeLayout;
    ImageView themePreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        if (settings.contains("dark_mode"))
            if (settings.getBoolean("dark_mode", false))
                setTheme(R.style.AppThemeDark);
            else {
                setTheme(Common.getCurrentTheme(settings));

            }
        setContentView(R.layout.activity_settings);
        url = getIntent().getExtras().getString("hitURL");


        clearHotLinksTv = (TextView) findViewById(R.id.clearHotLinksTextView);
        aboutDevTv = (TextView) findViewById(R.id.aboutDeveloperTextView);
        versionTv = (TextView) findViewById(R.id.versionTextView);
        rateUsTv = (TextView) findViewById(R.id.rateThisAppTextView);
        fabToggle = (SwitchCompat) findViewById(R.id.fabToggle);
        lightModeToggle = (SwitchCompat) findViewById(R.id.lightModeToggle);
        externalLinksToggle = (SwitchCompat) findViewById(R.id.externalLinkToggle);
        blockImagesToggle = (SwitchCompat) findViewById(R.id.blockImagesToggle);
        darkModeToggle = (SwitchCompat) findViewById(R.id.darkModeToggle);
        sponsoredPostsToggle = (SwitchCompat) findViewById(R.id.sponsoredPostsToggle);
        longPressToShareToggle = (SwitchCompat) findViewById(R.id.linkShareToggle);
        themeChangeLayout = (RelativeLayout) findViewById(R.id.themeChangeLayout);
        themePreview = (ImageView) findViewById(R.id.currentThemePreview);
        graphicsTv = (TextView) findViewById(R.id.aboutGraphics);
        sqLiteDatabase = openOrCreateDatabase("Browser", MODE_PRIVATE, null);

        clearHotLinksTv.setOnClickListener(this);
        aboutDevTv.setOnClickListener(this);
        rateUsTv.setOnClickListener(this);
        versionTv.setOnClickListener(this);
        graphicsTv.setOnClickListener(this);
        lightModeToggle.setOnCheckedChangeListener(this);
        externalLinksToggle.setOnCheckedChangeListener(this);
        blockImagesToggle.setOnCheckedChangeListener(this);
        darkModeToggle.setOnCheckedChangeListener(this);
        sponsoredPostsToggle.setOnCheckedChangeListener(this);
        longPressToShareToggle.setOnCheckedChangeListener(this);
        fabToggle.setOnCheckedChangeListener(this);
        fabToggle.setChecked(settings.getBoolean("fab_button", false));
        lightModeToggle.setChecked(settings.getBoolean("light_mode", false));
        blockImagesToggle.setChecked(settings.getBoolean("block_image", false));
        externalLinksToggle.setChecked(settings.getBoolean("external", false));
        darkModeToggle.setChecked(settings.getBoolean("dark_mode", false));
        sponsoredPostsToggle.setChecked(settings.getBoolean("sponsored_posts", false));
        longPressToShareToggle.setChecked(settings.getBoolean("link_sharing", false));
        aboutDevTv.setOnClickListener(this);
        clearHotLinksTv.setOnClickListener(this);
        rateUsTv.setOnClickListener(this);
        themeChangeLayout.setOnClickListener(this);
        try {
            versionTv.setText("Version " + appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.clearHotLinksTextView:


                AlertDialog.Builder b = new AlertDialog.Builder(SettingsActivity.this);
                b.setTitle("Delete Hotlinks");
                b.setMessage("Are you sure you want to delete all hotlinks");
                b.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sqLiteDatabase.execSQL("DELETE FROM bookmarks");
                        Common.showSnack(versionTv, SettingsActivity.this, "Done!");
                    }
                });
                b.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                final AlertDialog dialog = b.create();
                int[] attribute = new int[]{R.attr.colorPrimary};
                TypedArray array = SettingsActivity.this.getTheme().obtainStyledAttributes(attribute);
                final int color = array.getColor(0, Color.TRANSPARENT);

                array.recycle();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#" + Integer.toHexString(color)));
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#" + Integer.toHexString(color)));

                    }
                });
                dialog.show();
                break;
            case R.id.aboutDeveloperTextView:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.adityagurjar.me"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Common.showSnack(aboutDevTv, SettingsActivity.this, "No browser installed!");

                }
                break;
            case R.id.aboutGraphics:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.behance.net/ankitesharora"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Common.showSnack(aboutDevTv, SettingsActivity.this, "No browser installed!");

                }
                break;

            case R.id.rateThisAppTextView:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.adityaadi1467.facelytx"));
                startActivity(intent);
                break;

            case R.id.themeChangeLayout:
                showThemePicker();
                break;

        }

    }

    int currentThemeIndex;

    private void showThemePicker() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Choose a theme");

// add a radio button list

        final String currentTheme = settings.getString("theme", themes[0]);
        currentThemeIndex = 0;
        for (int i = 0; i < themes.length; i++) {
            if (currentTheme == themes[i]) {
                currentThemeIndex = i;
                break;
            }
        }
        builder.setSingleChoiceItems(themes, currentThemeIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentThemeIndex = which;
            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString("theme", themes[currentThemeIndex]);
                editor.commit();
                SettingsActivity.this.setTheme(Common.getCurrentTheme(settings));
                themePreview.setBackgroundColor(Common.getPrimaryColour(getTheme()));
            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {

            case R.id.lightModeToggle:
                if (b) {
                    editor.putBoolean("light_mode", true);
                    Toast.makeText(SettingsActivity.this, "Light mode enabled, web themes won't work.", Toast.LENGTH_SHORT).show();

                } else {
                    editor.putBoolean("light_mode", false);

                }
                break;
            case R.id.externalLinkToggle:
                if (b) {
                    editor.putBoolean("external", true);
                } else {
                    editor.putBoolean("external", false);
                }
                break;
            case R.id.darkModeToggle:
                if (b) {
                    editor.putBoolean("dark_mode", true);
                } else {
                    editor.putBoolean("dark_mode", false);
                }
                break;
            case R.id.blockImagesToggle:
                if (b) {
                    editor.putBoolean("block_image", true);
                } else {
                    editor.putBoolean("block_image", false);
                }
                break;
            case R.id.sponsoredPostsToggle:
                if (b) {
                    editor.putBoolean("sponsored_posts", true);
                } else {
                    editor.putBoolean("sponsored_posts", false);
                }
                break;
            case R.id.linkShareToggle:
                if (b) {
                    editor.putBoolean("link_sharing", true);
                } else {
                    editor.putBoolean("link_sharing", false);
                }
                break;
            case R.id.fabToggle:
                if (b) {
                    editor.putBoolean("fab_button", true);
                } else {
                    editor.putBoolean("fab_button", false);
                }
                break;
        }
        //    Common.showSnack(versionTv, SettingsActivity.this,"Changes applied!");
        editor.commit();


    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);
        startActivity(intent);

        finish();

        super.onBackPressed();
    }

    private String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
}
