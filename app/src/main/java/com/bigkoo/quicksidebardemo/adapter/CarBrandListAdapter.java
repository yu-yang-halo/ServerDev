package com.bigkoo.quicksidebardemo.adapter;

import android.support.v7.widget.RecyclerView;

import com.bigkoo.quicksidebardemo.api.AllCarBrandJSONBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class CarBrandListAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  private ArrayList<AllCarBrandJSONBean.CarBrandOBJ> items = new ArrayList<AllCarBrandJSONBean.CarBrandOBJ>();

  public CarBrandListAdapter() {
    setHasStableIds(true);
  }

  public void add(AllCarBrandJSONBean.CarBrandOBJ object) {
    items.add(object);
    notifyDataSetChanged();
  }

  public void add(int index, AllCarBrandJSONBean.CarBrandOBJ object) {
    items.add(index, object);
    notifyDataSetChanged();
  }

  public void addAll(Collection<? extends AllCarBrandJSONBean.CarBrandOBJ> collection) {
    if (collection != null) {
      items.addAll(collection);
      notifyDataSetChanged();
    }
  }

  public void addAll(AllCarBrandJSONBean.CarBrandOBJ... items) {
    addAll(Arrays.asList(items));
  }

  public void clear() {
    items.clear();
    notifyDataSetChanged();
  }

  public void remove(String object) {
    items.remove(object);
    notifyDataSetChanged();
  }

  public AllCarBrandJSONBean.CarBrandOBJ getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).hashCode();
  }

  @Override
  public int getItemCount() {
    return items.size();
  }
}
