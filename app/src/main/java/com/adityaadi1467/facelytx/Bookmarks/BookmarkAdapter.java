package com.adityaadi1467.facelytx.Bookmarks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adi.facelyt.R;

import java.util.List;

/**
 * Created by adi on 08/11/17.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkViewHolder> {
    List<Bookmark> bookmarkList;
    Context context;
    OnBookmarkClickListener listener;
    public BookmarkAdapter(Context context,List<Bookmark> bookmarkList, OnBookmarkClickListener listener){
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.listener = listener;
    }
    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkViewHolder(LayoutInflater.from(context).inflate(R.layout.bookmark_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, final int position) {
        holder.textView.setText(bookmarkList.get(position).getTitle());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBookmarkClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }
    public interface OnBookmarkClickListener{
        public void onBookmarkClick(int position);
    }
    public void removeItem(int position) {
       bookmarkList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Bookmark item, int position) {
      bookmarkList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
