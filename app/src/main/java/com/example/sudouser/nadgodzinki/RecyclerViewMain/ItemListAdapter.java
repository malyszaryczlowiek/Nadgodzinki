package com.example.sudouser.nadgodzinki.RecyclerViewMain;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sudouser.nadgodzinki.ItemViewModel;
import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.db.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemsViewHolder>
{
    // LayoutInflater rzutuje plik xml jako View object, dzięki temu możemy
    private final LayoutInflater mInflater;
    private List<Item> mItems; // cashed copy of all items
    private Context context;
    private String[] daysList;
    private ItemViewModel mItemViewModel;

    class ItemsViewHolder extends RecyclerView.ViewHolder
    {
        private final LinearLayout layout;
        private final ConstraintLayout hideShowLayout;
        private final TextView dayRecycleView; // dzień tygodnia
        private final TextView dateRecycleView;
        private final ImageButton imageButtonRecycleView;
        private final Button deleteItemButton;
        private final EditText minutesRecycleView;
        private final EditText hoursRecycleView;
        private final EditText notesAboutOvertime;


        private ItemsViewHolder(View itemView)
        {
            super(itemView);
            layout = itemView.findViewById(R.id.mainRecycleView);
            hideShowLayout = itemView.findViewById(R.id.noteAndDeleteButtonLayout);
            dayRecycleView = itemView.findViewById(R.id.dayRecycleView);
            dateRecycleView = itemView.findViewById(R.id.dateRecycleView);
            imageButtonRecycleView = itemView.findViewById(R.id.imageButtonRecycleView);
            deleteItemButton = itemView.findViewById(R.id.deleteItemButtonRecycleView);
            minutesRecycleView = itemView.findViewById(R.id.minutesRecycleView);
            hoursRecycleView = itemView.findViewById(R.id.hoursRecycleView);
            notesAboutOvertime = itemView.findViewById(R.id.notesAboutOvertime);
        }
    }

    public ItemListAdapter(Context context, ItemViewModel itemViewModel)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        daysList = context.getResources().getStringArray(R.array.buckup_list_of_dayweeks);
        mItemViewModel = itemViewModel;
    }


    /**
     * Metoda zwraca ItemViewHolder, który jest itemem obiektu, który będzie wyświetlany w Adapterze
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) //@NonNull skasowałem przed argumentem ViewGroup
    {
        // R.layout.recycleview_item przechowuje info jak ma wyglądać viewholder
        //Layoutinflatere przekształca plik xml na obiekt klasy View.
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ItemsViewHolder(itemView);
    }

    // TODO sprawdzić gdzie należy wstawić listenery czy tutaj czy gdiześ indziej
    /**
     * Metoda wywoływana w momencie gdy adapter tworzy ViewHolder, który ma wyświetlić żądaną zawartość
     * W tym czasie pobiera podszebne dane i wpisuje je w widżety tak jak w poniższym przykładzie.
     * @param holder   holder do, którego będą wpisane dane
     * @param position pozycja holdera na liście
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position)
    {
        holder.imageButtonRecycleView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                switch (holder.hideShowLayout.getVisibility())
                {
                    case View.GONE:
                        holder.notesAboutOvertime.setVisibility(View.VISIBLE);
                        holder.imageButtonRecycleView.setImageResource(android.R.drawable.arrow_up_float);
                        holder.deleteItemButton.setVisibility(View.VISIBLE);
                        holder.hideShowLayout.setVisibility(View.VISIBLE);
                        break;
                    case View.VISIBLE:
                        holder.notesAboutOvertime.setVisibility(View.GONE);
                        holder.imageButtonRecycleView.setImageResource(android.R.drawable.arrow_down_float);
                        holder.deleteItemButton.setVisibility(View.GONE);
                        holder.hideShowLayout.setVisibility(View.GONE);
                        break;
                    case View.INVISIBLE:
                        holder.notesAboutOvertime.setVisibility(View.VISIBLE);
                        holder.deleteItemButton.setVisibility(View.VISIBLE);
                        holder.hideShowLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }

            }
        });

        holder.deleteItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });


        // tutaj należy przypisać
        if (mItems != null)
        {
            Item item = mItems.get(position);
            if (item.getNumberOfHours() >= 0 && item.getNumberOfMinutes() >= 0 )
                holder.layout.setBackgroundColor(Color.GREEN); // light green
            else
                holder.layout.setBackgroundColor(Color.RED); // light red
            holder.dateRecycleView.setText(item.getDateOfOvertime());
            holder.dayRecycleView.setText(daysList[item.getDayOfWeek() - 1]);
            holder.hoursRecycleView.setText(String.valueOf(item.getNumberOfHours()));
            holder.minutesRecycleView.setText(String.valueOf(item.getNumberOfMinutes()));
            String note = item.getNote();
            holder.notesAboutOvertime.setText(note);
        }
        else
        {
            //holder.dayRecycleView.setText("no item");
        }
    }

    /**
     * Metoda zwraca listę itemów do wyświetlenia
     * @return
     */
    @Override
    public int getItemCount()
    {
        if (mItems != null)
            return mItems.size();
        else return 0;
    }

    public void setItems(List<Item> lists)
    {
        mItems = lists;
        notifyDataSetChanged();
    }
}


// to co trzeba zrobić, zeby recycle view działał tak jak zakładamy:
// normalnie notatka jest schowana. a elementy nie są edytowalne po kliknięciu na dany item
// notatka ma się rozwijać i wszystkie elementy za wyjątkiem dnia tygodnia mają być edytowalne.
// notatka w zależności od tego czy jest to godzina dodana czy ujemna może być zielona lub czerwona


































