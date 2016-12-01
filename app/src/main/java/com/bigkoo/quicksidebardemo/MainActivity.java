package com.bigkoo.quicksidebardemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.bigkoo.quicksidebardemo.adapter.CarBrandListAdapter;
import com.bigkoo.quicksidebardemo.adapter.CarTypeAdapter;
import com.bigkoo.quicksidebardemo.api.AllCarBrandJSONBean;
import com.bigkoo.quicksidebardemo.api.ConstantsURLUtils;
import com.bigkoo.quicksidebardemo.api.DataFetcherUtils;
import com.bigkoo.quicksidebardemo.viewholder.MyViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnQuickSideBarTouchListener {
    public static int RESULT_CODE=1111;
    public static String KEY_RESULT="result_key";
    RecyclerView recyclerView;
    HashMap<String,Integer> letters = new HashMap<>();
    QuickSideBarView quickSideBarView;
    QuickSideBarTipsView quickSideBarTipsView;
    CityListWithHeadersAdapter adapter;
    SideBarView sideBarView;
    ListView    listView;
    CarTypeAdapter carypeAdapter;

    String carInfoDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);

        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(this);


        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        adapter = new CityListWithHeadersAdapter();






        recyclerView.setAdapter(adapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));



        ArrayList<String> customLetters = new ArrayList<>();

        new NetRequestTask(0,-1).execute();


        sideBarView= new SideBarView(this);

        listView=sideBarView.getListView();




        carypeAdapter=new CarTypeAdapter(null,this);

        listView.setAdapter(carypeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String resutlString="";


                Log.v("TAG","list view "+position+" | "+carypeAdapter.getCarBrandOBJs().get(position).getCarCategoryName());

                resutlString=carInfoDescription+" "+carypeAdapter.getCarBrandOBJs().get(position).getCarCategoryName();


                Toast.makeText(MainActivity.this,resutlString,Toast.LENGTH_LONG).show();
                getIntent().putExtra(KEY_RESULT,resutlString);
                setResult(RESULT_CODE,getIntent());



            }
        });






    }


    class NetRequestTask extends AsyncTask<String,String,String>{
        private  int type;
        private  int parentId;
        NetRequestTask(int type,int parentId){
            this.type=type;
            this.parentId=parentId;
        }


        List<AllCarBrandJSONBean.CarBrandOBJ> infos;
        @Override
        protected String doInBackground(String... params) {

            String JSON_ALL_CAR_BRAND;
            if(type==0){
                JSON_ALL_CAR_BRAND= DataFetcherUtils.fetechNetWorkData(ConstantsURLUtils.URL_ALL_CAR_BRAND);
            }else{
                JSON_ALL_CAR_BRAND= DataFetcherUtils.fetechNetWorkData(String.format(ConstantsURLUtils.URL_Car_By_ParentId,parentId));
            }

            Gson gson=new Gson();
            Type type = new TypeToken<AllCarBrandJSONBean>(){}.getType();
            AllCarBrandJSONBean bean=gson.fromJson(JSON_ALL_CAR_BRAND,type);

            infos = bean.getInfo();


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(type==0){
                adapter.addAll(infos);
                int position = 0;
                for(AllCarBrandJSONBean.CarBrandOBJ obj: infos){
                    String letter = obj.getShortCut();
                    //如果没有这个key则加入并把位置也加入
                    if(!letters.containsKey(letter)){
                        letters.put(letter,position);
                    }
                    position++;
                }
            }else{
                carypeAdapter.setCarBrandOBJs(infos);
                carypeAdapter.notifyDataSetChanged();
            }

        }
    }


    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if(letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching? View.VISIBLE:View.INVISIBLE);
    }

    private class CityListWithHeadersAdapter extends CarBrandListAdapter<RecyclerView.ViewHolder>
            implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item, parent, false);
            return new MyViewHolder(view, new MyViewHolder.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int postion) {



                    sideBarView.showSideBarView();
                    new NetRequestTask(1, getItem(postion).getCarCategoryId()).execute();
                    Log.v("TAG","pos "+getItem(postion).getCarCategoryName());
                    carInfoDescription=getItem(postion).getCarCategoryName();

                }
            }) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder= (MyViewHolder) holder;

            viewHolder.getTextView().setText(getItem(position).getCarCategoryName());


            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this).build();
            ImageLoader.getInstance().init(config);

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance

            imageLoader.displayImage(getItem(position).getLogoImg(),  viewHolder.getImageView());



        }

        @Override
        public long getHeaderId(int position) {
            return getItem(position).getShortCut().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_header, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            View view =  holder.itemView;
            TextView textView= (TextView) view.findViewById(R.id.shortcutView);

            textView.setText(String.valueOf(getItem(position).getShortCut()));

        }



    }
}
