package com.bigkoo.quicksidebardemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.florent37.viewanimator.ViewAnimator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION;

/**
 * Created by Administrator on 2016/12/1.
 */

public class SideBarView {
    private  WindowManager wmManager;
    private  View sideView;
    private  LinearLayout linearLayout;
    private    ListView listView;

    public  SideBarView(Activity ctx){
        sideView= LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.sideview,null);

        ctx.getWindow().addContentView(sideView,new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));

        linearLayout= (LinearLayout) sideView.findViewById(R.id.linearLayout);
        listView= (ListView) sideView.findViewById(R.id.listView);



        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("linearLayout","click");
                hideSideBarView();
            }
        });
        hideSideBarView();

    }

    public ListView getListView() {
        return listView;
    }


    public void showSideBarView(){
        ViewAnimator.animate(sideView).translationX(1000,0).duration(300).start();

    }
    public void hideSideBarView(){
        ViewAnimator.animate(sideView).translationX(0,1000).duration(300).start();

    }


}
