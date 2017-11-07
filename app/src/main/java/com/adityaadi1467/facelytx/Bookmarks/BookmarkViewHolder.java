package com.adityaadi1467.facelytx.Bookmarks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adi.facelyt.R;

/**
 * Created by adi on 08/11/17.
 */

public class BookmarkViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    public RelativeLayout viewBackground, viewForeground;
    public BookmarkViewHolder(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.bookmarkTitle);
        viewBackground =(RelativeLayout) itemView.findViewById(R.id.view_background);
        viewForeground =(RelativeLayout) itemView.findViewById(R.id.view_foreground);

    }

}
