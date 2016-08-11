package com.example.adi.facelyt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class About extends AppCompatActivity {
    ImageView imgfb,imgtw,imgwp,imgps,imgjecrc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);


       imgfb = (ImageView)findViewById(R.id.textView8);
        imgtw = (ImageView)findViewById(R.id.textView9);
        imgwp = (ImageView)findViewById(R.id.textViewwhatsapp);
        imgps = (ImageView)findViewById(R.id.textViewstore);

        imgfb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               try {
//                   Intent intent = new Intent();
//                   intent.setAction(Intent.ACTION_VIEW);
//                   intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                   intent.setData(Uri.parse("http://facebook.com/adityaadi333"));
//                   startActivity(intent);

                   Intent i=new Intent(About.this,MainActivity.class);
                   MainActivity.viewprofile=true;
                   startActivity(i);
               }
               catch (Exception e)
               {

               }
            }
        });


        imgtw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://twitter.com/eminem_oholic"));
                startActivity(intent);
            }
        });

        imgps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Aditya Gurjar"));
                startActivity(intent);
            }
        });

        imgwp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String sAux = "\n Browse Facebook The Lighter Way! Use The Full Fledged Features From Facebook Embedded into a LYTer Variant. Download FaceLyt Now!\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.adityaadi1467.facelytx \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            }
        });
        TextView email;
            email=    (TextView)findViewById(R.id.textView10);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"adityagurjar.it18@gmail.com"});

                email.putExtra(Intent.EXTRA_SUBJECT, "Bug Report For FaceLyt");
                email.putExtra(Intent.EXTRA_TEXT, "");

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });



    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(About.this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();    }
}
