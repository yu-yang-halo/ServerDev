package com.bigkoo.quicksidebardemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigkoo.quicksidebardemo.R;
import com.bigkoo.quicksidebardemo.api.AllCarBrandJSONBean.CarBrandOBJ;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class CarTypeAdapter extends BaseAdapter {
    private List<CarBrandOBJ> carBrandOBJs;
    private Context ctx;

    public CarTypeAdapter(List<CarBrandOBJ> carBrandOBJs, Context ctx){
        this.carBrandOBJs=carBrandOBJs;
        this.ctx=ctx;
    }
    @Override
    public int getCount() {
        if(carBrandOBJs==null){
            return 0;
        }
        return carBrandOBJs.size();
    }

    @Override
    public Object getItem(int position) {
        if(carBrandOBJs==null){
            return null;
        }
        return carBrandOBJs.get(position);
    }

    public List<CarBrandOBJ> getCarBrandOBJs() {
        return carBrandOBJs;
    }

    public void setCarBrandOBJs(List<CarBrandOBJ> carBrandOBJs) {
        this.carBrandOBJs = carBrandOBJs;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(ctx).inflate(R.layout.adapter_item,null);

        }
        TextView textView= (TextView) convertView.findViewById(R.id.textView2);

        textView.setText(carBrandOBJs.get(position).getCarCategoryName());


        return convertView;
    }
}
