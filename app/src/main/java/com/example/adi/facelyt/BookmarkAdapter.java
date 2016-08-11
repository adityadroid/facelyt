package com.example.adi.facelyt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class BookmarkAdapter extends BaseAdapter {

    boolean delflag=true;
   ArrayList<String> bmname= new ArrayList<>();
    ArrayList<String> bmlink=new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    Cursor c;
    BookmarkAdapter(Context contextmain, ArrayList<String> bmnamemain, ArrayList<String> bmlinkmain)
    {
        context=contextmain;
        bmname=bmnamemain;
        bmlink=bmlinkmain;
inflater=LayoutInflater.from(contextmain);

    }




    @Override
    public int getCount() {
        return bmname.size();
    }

    @Override
    public Object getItem(int position) {
        return bmlink.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View itemView, ViewGroup parent) {
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
itemView=inflater.inflate(R.layout.activity_bookmark_adapter,null);

TextView  tv1=(TextView)itemView.findViewById(R.id.bmname);
        tv1.setText(bmname.get(position));

tv1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        MainActivity.vb.loadUrl(bmlink.get(position));

    }
});



        ImageButton bt=(ImageButton)itemView.findViewById(R.id.button2);



            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




         removeBookmark(bmlink.get(position),bmname.get(position));

         MainActivity.adapter.notifyDataSetChanged();


                }
            });




        return itemView;
    }
    public void removeBookmark( final String link,final String name)
    {


                c = MainActivity.db.rawQuery("SELECT * FROM bookmarks WHERE link='" +link+ "'", null);
                if (c.moveToFirst()) {



                    MainActivity.bookmarklink.remove(c.getString(1));
                    MainActivity.bookmarkname.remove(c.getString(0));
                    MainActivity.db.execSQL("DELETE FROM bookmarks WHERE link='" + link + "'");



                    MainActivity.adapter.notifyDataSetChanged();
                }



        Snackbar sk;
        sk=Snackbar.make(MainActivity.vb,"DELETED :)",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url =link;

                c=MainActivity.db.rawQuery("SELECT * FROM bookmarks WHERE link='"+url+"'",null);

                if(c.getCount()>0)
                {
                }
                else {
                    MainActivity.db.execSQL("INSERT INTO bookmarks VALUES('" + name + "','" + url + "');");
                    c = MainActivity.db.rawQuery("SELECT * FROM bookmarks", null);
                    while (c.moveToNext()) {
                        if (!MainActivity.bookmarklink.contains(c.getString(1))) {
                            MainActivity.bookmarkname.add(c.getString(0));
                            MainActivity.bookmarklink.add(c.getString(1));
                        }

                    }

                    MainActivity.adapter.notifyDataSetChanged();

                }


            }
        });
        sk.show();


                //MainActivity.trains.remove(position);
                // MainActivity.adapter.notifyDataSetChanged();


        c.close();
    }

}
