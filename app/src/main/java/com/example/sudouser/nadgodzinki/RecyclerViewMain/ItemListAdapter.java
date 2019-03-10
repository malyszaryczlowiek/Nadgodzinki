package com.example.sudouser.nadgodzinki.RecyclerViewMain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.db.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemsViewHolder>
{
    private final LayoutInflater mInflater;
    private List<Item> mItems; // cashed copy of all items

    class ItemsViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textView;

        private ItemsViewHolder(View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public ItemListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) //@NonNull skasowałem przed argumentem ViewGroup
    {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ItemsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position)
    {
        if (mItems != null)
        {
            Item item = mItems.get(position);
            holder.textView.setText(item.toString());
        }
        else
        {
            holder.textView.setText("no item");
        }
    }

    @Override
    public int getItemCount()
    {
        if (mItems != null)
            return mItems.size();
        else return 0;
    }

    public void setmItems(List<Item> lists)
    {
        mItems = lists;
        notifyDataSetChanged();
    }
}




































