package com.adityaadi1467.facelytx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adi.facelyt.R;

import java.util.List;

/**
 * Created by adi on 30/12/16.
 */
public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.MyViewHolder> {

    private Context mContext;
    List<Bookmark> bookmarkList;

    public BookMarkAdapter(Context context, List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bookmark_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ((MyViewHolder) holder).mTextView.setText(bookmarkList.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.bookMarkTitleTextView);
        }
    }
}
