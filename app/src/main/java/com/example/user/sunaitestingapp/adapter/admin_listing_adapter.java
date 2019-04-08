package com.example.user.sunaitestingapp.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.model.admin_listing_model;

import java.util.List;

public class admin_listing_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 1;
    public admin_listing_adapter.MyAdapterListener onClickListener;
    private List<admin_listing_model> empList;
    private static int currentSelectedIndex = -1;

    private SparseBooleanArray selectedItems;
    public admin_listing_adapter() {

    }

    public admin_listing_adapter(List<admin_listing_model> empList, admin_listing_adapter.MyAdapterListener listener) {
        this.empList = empList;
        onClickListener = listener;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  View itemView = LayoutInflater.from(parent.getContext())
        //        .inflate(R.layout.student_list_row, parent, false);
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_list_row, parent, false);
            return new admin_listing_adapter.ItemViewHolder(itemView);
        }/* else if (viewType == TYPE_HEADER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_list_header, parent, false);
        return new admin_listing_adapter.HeaderViewHolder(itemView);
        } *//*else if (viewType == TYPE_FOOTER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout, parent, false);
        return new admin_listing_adapter.HeaderViewFooter(itemView);
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
       /* if (holder instanceof admin_listing_adapter.HeaderViewHolder) {

        admin_listing_adapter.HeaderViewHolder headerHolder = (admin_listing_adapter.HeaderViewHolder) holder;
        //  headerHolder.headerTitle.setText("Header View");

        } *//*else if (holder instanceof admin_listing_adapter.HeaderViewFooter) {
        admin_listing_adapter.HeaderViewFooter headerHolder = (admin_listing_adapter.HeaderViewFooter) holder;
        //  headerHolder.headerTitle.setText("Header View");
        }*//* else*/
        if (holder instanceof admin_listing_adapter.ItemViewHolder) {
            final admin_listing_adapter.ItemViewHolder itemViewHolder = (admin_listing_adapter.ItemViewHolder) holder;

            //   admin_listing_model admin_listing_model = empList.get(position - 1);
            //  itemViewHolder.leave_detail.setImageResource(R.drawable.tick_green);
            admin_listing_model admin_listing_model = empList.get(position);
            itemViewHolder.emp_name.setText(admin_listing_model.getCust_name());
            itemViewHolder.emp_addrs.setText(admin_listing_model.getAddrs());
            itemViewHolder.emp_instruc.setText(admin_listing_model.getRouteName());
         /*   Log.d("requestLeaveListData",  "deliveryInstructionsAdapter" + admin_listing_model.getDeliveryinstruction());
            Log.d("getSpecialin", "getSpecialinstructionAdapter" + admin_listing_model.getSpecialinstruction());*/
         /*   if(admin_listing_model.getSpecialinstruction().equals(null)||admin_listing_model.getSpecialinstruction().equalsIgnoreCase("null"))
            {
                itemViewHolder.emp_instruc.setText("Instruction: " + admin_listing_model.getDeliveryinstruction());
                itemViewHolder.emp_instruc.setTextColor(Color.parseColor("#11a14a"));
            }
            else {
                itemViewHolder.emp_instruc.setText("Instruction: " + admin_listing_model.getSpecialinstruction());
                itemViewHolder.emp_instruc.setTextColor(Color.parseColor("#FF0000"));
            }*/

            /*    itemViewHolder. emp_sch_req.setClickable(true);
            // make_payment.setMovementMethod(LinkMovementMethod.getInstance());
         String text = "<a href='test' >"+String.valueOf(admin_listing_model.getScheduleDelivery())+"</a>";
            itemViewHolder.emp_sch_req.setLinkTextColor(Color.rgb(0, 0, 0));
            itemViewHolder.emp_sch_req.setText(Html.fromHtml(text));*/
           /* itemViewHolder.emp_sch_req.setText(String.valueOf(admin_listing_model.getScheduleDelivery())+"     ");
            Log.d("test--","--getActualDelivery--"+admin_listing_model.getActualDelivery());
            Log.d("test--","--getScheduleDelivery--"+admin_listing_model.getScheduleDelivery());
            if (selectedItems.get(position, false)) {
                itemViewHolder.movie_list.setBackgroundResource(R.color.bg_tv_color);
            } else {
                if(Double.compare(admin_listing_model.getActualDelivery(), admin_listing_model.getScheduleDelivery()) == 0)
                {
                    itemViewHolder.movie_list.setBackgroundResource(R.color.row_light);
                }
                else{
                    itemViewHolder.movie_list.setBackgroundResource(R.color.rowColor);
                }
            }
*/




            itemViewHolder.ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   admin_listing_model admin_listing_model = empList.get(position - 1);
                    admin_listing_model admin_listing_model = empList.get(position);
                    onClickListener.modifyInfoOnClick(v, admin_listing_model);


                }
            });

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
        void modifyInfoOnClick(View v, admin_listing_model admin_listing_model);



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
        public TextView emp_name, emp_addrs, emp_instruc, emp_sch_req,make_payment, ok_btn, modify;
        RelativeLayout movie_list;
        LinearLayout emp_info;
        public ItemViewHolder(View itemView) {
            super(itemView);
            emp_name = (TextView) itemView.findViewById(R.id.emp_name);
            emp_addrs = (TextView) itemView.findViewById(R.id.emp_addrs);
            emp_instruc = (TextView) itemView.findViewById(R.id.emp_instruc);
            emp_sch_req = (TextView) itemView.findViewById(R.id.emp_sch_req);
            ok_btn = (TextView) itemView.findViewById(R.id.ok_btn);
            movie_list = (RelativeLayout) itemView.findViewById(R.id.movie_list);
            emp_info = (LinearLayout) itemView.findViewById(R.id.emp_info);
        }


    }
}
