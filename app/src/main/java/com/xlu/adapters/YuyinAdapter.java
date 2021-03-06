package com.xlu.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pandacard.teavel.R;
import com.pandacard.teavel.apps.MyApplication;
import com.pandacard.teavel.utils.LUtils;
import com.xlu.po.Jieshuo;
import com.xlu.utils.Constance;
import com.xlu.utils.DensityUtil;

import java.util.List;


/**
 * Created by giant on 2017/9/19.
 */

public class YuyinAdapter extends RecyclerView.Adapter<YuyinAdapter.ViewHolder> {
    private List<Jieshuo> jieshuos;
   private  OnClickButtonListener listener;

   private  String TAG ="YuyinAdapter";
   private  Context c;
    private final WindowManager mWm;

    public YuyinAdapter(List<Jieshuo> jieshuos,Context context) {
        this.jieshuos = jieshuos;
        this.c=context;
        mWm = (WindowManager)  c.
                getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_yuyin_view_layout,null);
                RelativeLayout.LayoutParams params=new
                RelativeLayout.LayoutParams(mWm.getDefaultDisplay().getWidth()
                , DensityUtil.dip2px(parent.getContext(),100));
        v.setLayoutParams(params);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Jieshuo j=jieshuos.get(position);
        LUtils.d(TAG,"jieshuos.size()=="+jieshuos.size());
        if(j.isListen()){
            holder.ivPlay.setVisibility(View.GONE);
            holder.ivStop.setVisibility(View.VISIBLE);
        }else{
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.ivStop.setVisibility(View.GONE);
        }
        holder.tv.setText(j.getName());
        holder.tvContent.setText(j.getMemo());

//        LUtils.d(TAG,"j.getName()=="+j.getName());
//        LUtils.d(TAG,"j.getMemo()=="+j.getMemo());
        ImageLoader.getInstance().displayImage(Constance.HTTP_URL+j.getPic(),holder.iv,
                MyApplication.normalOption);
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.playOnClickListener(j,v,holder.ivStop);
            }
        });
        holder.ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.stopOnClickListener(j,holder.ivPlay,v);
            }
        });
        holder.ivHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.hereOnClickListener(j,v);
            }
        });
         holder.ivTw.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(listener!=null)
                     listener.tuWenOnClickListener(j,v);
             }
         });

    }

    @Override
    public int getItemCount() {
        return jieshuos.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv ;
        TextView tvContent;
        ImageView ivHere;
        ImageView ivTw;
        ImageView ivPlay;
        ImageView ivStop;
        ImageView iv;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            tv = view.findViewById(R.id.tv_jieshuo_name);
           tvContent = view
                    .findViewById(R.id.tv_jieshuo_content);
           ivHere = view.findViewById(R.id.iv_get_here);
           ivTw = view.findViewById(R.id.iv_tw);
            ivPlay = view
                    .findViewById(R.id.iv_play);
            ivStop = view
                    .findViewById(R.id.iv_stop);
            iv = view.findViewById(R.id.iv_jieshuo_img);


        }
        public View getView() {
            return view;
        }
    }

    public void setListener(OnClickButtonListener listener) {
        this.listener = listener;
    }

    public interface OnClickButtonListener{
        void playOnClickListener(Jieshuo jieshuo, View v, View v2);
        void stopOnClickListener(Jieshuo jieshuo, View v, View v2);
        void hereOnClickListener(Jieshuo jieshuo, View v);
        void tuWenOnClickListener(Jieshuo jieshuo, View v);
    }
}
