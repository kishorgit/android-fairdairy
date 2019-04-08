package com.example.user.sunaitestingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.model.change_schedule_model;

import java.util.List;

public class subsproductlist_adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 1;
    public subsproductlist_adapter.MyAdapterListener onClickListener;
    private List<change_schedule_model> empList;
    private static int currentSelectedIndex = -1;
    Context context;
  //  String imgUrl = "https://api.androidhive.info/images/glide/medium/deadpool.jpg";
  String imgUrl ="http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/images/products/"   ;
    private SparseBooleanArray selectedItems;
    public subsproductlist_adapter() {

    }

    public subsproductlist_adapter(List<change_schedule_model> empList,Context context, subsproductlist_adapter.MyAdapterListener listener) {
        this.empList = empList;
        onClickListener = listener;
        selectedItems = new SparseBooleanArray();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  View itemView = LayoutInflater.from(parent.getContext())
        //        .inflate(R.layout.student_list_row, parent, false);
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subsproductlist_row, parent, false);
            return new subsproductlist_adapter.ItemViewHolder(itemView);
        }/* else if (viewType == TYPE_HEADER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_list_header, parent, false);
        return new subsproductlist_adapter.HeaderViewHolder(itemView);
        } *//*else if (viewType == TYPE_FOOTER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout, parent, false);
        return new subsproductlist_adapter.HeaderViewFooter(itemView);
        }*/ else {
            // return new MyViewHolder(itemView);
            return null;
        }


    }

    @Override
    public int getItemViewType(int position) {
      /*  if (position == 0) {
        return TYPE_HEADER;
        }*//* else if (position == empList.size() + 1) {
        return TYPE_FOOTER;
        }*//* else {
        return TYPE_ITEM;
        }*/
        return 2;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
       /* if (holder instanceof subsproductlist_adapter.HeaderViewHolder) {

        subsproductlist_adapter.HeaderViewHolder headerHolder = (subsproductlist_adapter.HeaderViewHolder) holder;
        //  headerHolder.headerTitle.setText("Header View");

        } *//*else if (holder instanceof subsproductlist_adapter.HeaderViewFooter) {
        subsproductlist_adapter.HeaderViewFooter headerHolder = (subsproductlist_adapter.HeaderViewFooter) holder;
        //  headerHolder.headerTitle.setText("Header View");
        }*//* else*/
        if (holder instanceof subsproductlist_adapter.ItemViewHolder) {
            final subsproductlist_adapter.ItemViewHolder itemViewHolder = (subsproductlist_adapter.ItemViewHolder) holder;

            //   change_schedule_model change_schedule_model = empList.get(position - 1);
            //  itemViewHolder.leave_detail.setImageResource(R.drawable.tick_green);
            Log.d("tag", "onBindViewHolder:position "+position);
            final change_schedule_model change_schedule_model = empList.get(position);
            itemViewHolder.product_name.setText(change_schedule_model.getProduct_name());
            itemViewHolder.price_per_unit.setText(String.valueOf("₹ "+change_schedule_model.getPricePerUnit()));
            itemViewHolder.edit_order.setText(String.valueOf(change_schedule_model.getProductQty()));
           /* if(change_schedule_model.getImageurl().equals(null)||change_schedule_model.getImageurl().equalsIgnoreCase("")||change_schedule_model.getImageurl().equalsIgnoreCase("null"))
            {}
            else {
                Log.d("tag", "onBindViewHolder:imgUrl "+imgUrl+change_schedule_model.getImageurl());
                Glide.with(context).load(imgUrl+change_schedule_model.getImageurl())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemViewHolder.product_image);
                itemViewHolder.edit_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.modifyInfoOnClick(v, change_schedule_model);
                    }
                });
            }*/
           /* itemViewHolder.sub_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.subQuantityOnClick(v, change_schedule_model);
                }
            });*/
          /*  itemViewHolder.edit_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.modifyInfoOnClick(v, change_schedule_model);
                }
            });*/
        }


    }

    @Override
    public int getItemCount() {
        return empList.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        selectedItems.clear();
        notifyDataSetChanged();
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);

    }
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }
    public interface MyAdapterListener {
        void modifyInfoOnClick(View v, change_schedule_model change_schedule_model);

        void subQuantityOnClick(View v, change_schedule_model change_schedule_model);

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(View view) {
            super(view);

        }
    }

    private class HeaderViewFooter extends RecyclerView.ViewHolder {


        public HeaderViewFooter(View view) {
            super(view);

        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, price_per_unit,edit_order, modify;
        RelativeLayout movie_list;
        LinearLayout emp_info;
        ImageView product_image ;
        public ItemViewHolder(View itemView) {
            super(itemView);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            price_per_unit = (TextView) itemView.findViewById(R.id.price_per_unit);
            edit_order = (TextView) itemView.findViewById(R.id.edit_order);
            movie_list = (RelativeLayout) itemView.findViewById(R.id.movie_list);
            emp_info = (LinearLayout) itemView.findViewById(R.id.emp_info);
            product_image  = (ImageView)itemView. findViewById(R.id.product_image);
        }


    }
}


