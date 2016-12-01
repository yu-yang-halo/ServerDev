package com.bigkoo.quicksidebardemo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.quicksidebardemo.R;

/**
 * Created by Administrator on 2016/12/1.
 */

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View rootView;
    private TextView textView;
    private ImageView imageView;

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    MyItemClickListener listener;

    public MyViewHolder(View itemView,MyItemClickListener listener) {
        super(itemView);

        this.textView= (TextView) itemView.findViewById(R.id.textView);
        this.imageView= (ImageView) itemView.findViewById(R.id.imageView);

        this.listener=listener;
        this.rootView=itemView;


        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
         listener.onItemClick(v,getLayoutPosition());
    }


    public static interface MyItemClickListener {
        public void onItemClick(View view,int postion);
    }

}
