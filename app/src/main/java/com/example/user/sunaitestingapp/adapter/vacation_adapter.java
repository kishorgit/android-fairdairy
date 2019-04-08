package com.example.user.sunaitestingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.model.vacation_model;

import java.util.List;

public class vacation_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 1;
    public vacation_adapter.MyAdapterListener onClickListener;
    private List<vacation_model> empList;
    private static int currentSelectedIndex = -1;

    private SparseBooleanArray selectedItems;
    public vacation_adapter() {

    }

    public vacation_adapter(List<vacation_model> empList, vacation_adapter.MyAdapterListener listener) {
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacation_list_row, parent, false);
            return new vacation_adapter.ItemViewHolder(itemView);
        }/* else if (viewType == TYPE_HEADER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_list_header, parent, false);
        return new vacation_adapter.HeaderViewHolder(itemView);
        } *//*else if (viewType == TYPE_FOOTER) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout, parent, false);
        return new vacation_adapter.HeaderViewFooter(itemView);
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
       /* if (holder instanceof vacation_adapter.HeaderViewHolder) {

        vacation_adapter.HeaderViewHolder headerHolder = (vacation_adapter.HeaderViewHolder) holder;
        //  headerHolder.headerTitle.setText("Header View");

        } *//*else if (holder instanceof vacation_adapter.HeaderViewFooter) {
        vacation_adapter.HeaderViewFooter headerHolder = (vacation_adapter.HeaderViewFooter) holder;
        //  headerHolder.headerTitle.setText("Header View");
        }*//* else*/
        if (holder instanceof vacation_adapter.ItemViewHolder) {
            final vacation_adapter.ItemViewHolder itemViewHolder = (vacation_adapter.ItemViewHolder) holder;

            //   vacation_model vacation_model = empList.get(position - 1);
            //  itemViewHolder.leave_detail.setImageResource(R.drawable.tick_green);
            Log.d("tag", "onBindViewHolder:position "+position);
            final vacation_model vacation_model = empList.get(position);

            itemViewHolder.start_date.setText(vacation_model.getStartdate());
            itemViewHolder.end_date.setText(vacation_model.getEnddate());

           /* itemViewHolder.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.modifyInfoOnClick(v, vacation_model);
                }
            });
            itemViewHolder.sub_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.subQuantityOnClick(v, vacation_model);
                }
            });
*/
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
        void modifyInfoOnClick(View v, vacation_model vacation_model);

        void subQuantityOnClick(View v, vacation_model vacation_model);

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
        public TextView start_date, end_date;


        public ItemViewHolder(View itemView) {
            super(itemView);

            start_date = (TextView) itemView.findViewById(R.id.start_date);
            end_date = (TextView) itemView.findViewById(R.id.end_date);

        }


    }

}
