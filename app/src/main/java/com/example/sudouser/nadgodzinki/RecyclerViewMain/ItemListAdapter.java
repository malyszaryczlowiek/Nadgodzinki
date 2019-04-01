package com.example.sudouser.nadgodzinki.RecyclerViewMain;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.db.Item;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemsViewHolder>
    //implements DateChangeCalendarDialogue.DateChangeCalendarDialogueListener
{
    // LayoutInflater rzutuje plik xml jako View object, dzięki temu możemy
    private final LayoutInflater mInflater;
    private List<Item> mItems; // cashed copy of all items
    private Context context;
    private String[] daysList;
    private ItemListAdapterListener listener;



    class ItemsViewHolder extends RecyclerView.ViewHolder
    {
        private final LinearLayout layout;
        private final ConstraintLayout hideShowLayout;
        private final TextView dayRecycleView;
        private final TextView dateRecycleView;
        private final ImageButton imageButtonRecycleView;
        private final Button deleteItemButton;
        private final EditText minutesRecycleView;
        private final EditText hoursRecycleView;
        private final EditText notesAboutOvertime;
        private final Button saveNoteButton;


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
            saveNoteButton = itemView.findViewById(R.id.saveNoteButtonRecycleView);
        }
    }

    public ItemListAdapter(Context context)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        daysList = context.getResources().getStringArray(R.array.buckup_list_of_dayweeks);
        listener = (ItemListAdapterListener) context;
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
                        //holder.notesAboutOvertime.setVisibility(View.VISIBLE);
                        holder.imageButtonRecycleView.setImageResource(android.R.drawable.arrow_up_float);
                        //holder.deleteItemButton.setVisibility(View.VISIBLE);
                        holder.hideShowLayout.setVisibility(View.VISIBLE);
                        break;
                    case View.VISIBLE:
                        //holder.notesAboutOvertime.setVisibility(View.GONE);
                        holder.imageButtonRecycleView.setImageResource(android.R.drawable.arrow_down_float);
                        //holder.deleteItemButton.setVisibility(View.GONE);
                        holder.hideShowLayout.setVisibility(View.GONE);
                        break;
                    case View.INVISIBLE:
                        //holder.notesAboutOvertime.setVisibility(View.VISIBLE);
                        //holder.deleteItemButton.setVisibility(View.VISIBLE);
                        holder.hideShowLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        // tutaj należy przypisać
        if (mItems != null)
        {
            Item item = mItems.get(position);
            if (item.getNumberOfHours() >= 0 && item.getNumberOfMinutes() >= 0 )
                holder.layout.setBackgroundColor(context.getColor(R.color.positiveColorOvertime)); // light green
            else
                holder.layout.setBackgroundColor(context.getColor(R.color.negativeColorOvertime)); // light red
            holder.dateRecycleView.setText(item.getDateOfOvertime());
            holder.dayRecycleView.setText(daysList[item.getDayOfWeek() - 1]);
            holder.hoursRecycleView.setText(String.valueOf(item.getNumberOfHours()));
            holder.minutesRecycleView.setText(String.valueOf(item.getNumberOfMinutes()));
            holder.notesAboutOvertime.setText(item.getNote());

            holder.deleteItemButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.deleteItem(item);
                }
            });

            holder.dateRecycleView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Calendar calendar = Calendar.getInstance();
                    int day  = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                        {
                            // dayOfMonth musi być + 1 bo Calendar.MONTH jest od 0-11
                            // a LocalDate używa od 1 do 12.
                            listener.changeDateOfOvertime(year, month + 1, dayOfMonth, item.getUid());
                        }
                    }, year, month, day)
                            .show();
                }
            });

            final int oldHoursValue = Integer.parseInt(holder.hoursRecycleView.getText().toString());
            holder.hoursRecycleView.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    try
                    {
                        int newHoursValue = Integer.parseInt(holder.hoursRecycleView.getText().toString());
                        int minutes = Integer.parseInt(holder.minutesRecycleView.getText().toString());
                        if ( (newHoursValue >= 0 && minutes < 0) || (newHoursValue < 0 && minutes >= 0) )
                            minutes = -minutes;
                        listener.changeNumberOfMinutesAndHours(newHoursValue, minutes, item.getUid());
                        return false;
                    }
                    catch (NumberFormatException e)
                    {
                        holder.hoursRecycleView.setText(oldHoursValue);
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            final int oldMinutesValue = Integer.parseInt(holder.minutesRecycleView.getText().toString());
            holder.minutesRecycleView.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    try
                    {
                        int newMinutesValue = Integer.parseInt(holder.minutesRecycleView.getText().toString());
                        if (Math.abs(newMinutesValue) < 60)
                        {
                            int hours = Integer.parseInt(holder.hoursRecycleView.getText().toString());
                            if ( (newMinutesValue < 0 && hours > 0) || (newMinutesValue >=0 && hours < 0) )
                                hours = -hours;
                            listener.changeNumberOfMinutesAndHours(hours, newMinutesValue, item.getUid());
                            return false;
                        }
                        else
                        {
                            holder.minutesRecycleView.setText(oldMinutesValue);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.incorrect_minutes_numbers)
                                    .setMessage(R.string.minutes_lower_60)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // nic nie rób przyjmij tylko do wiadomości, że jest za
                                            // duża liczba minut.
                                        }
                                    })
                                    .show();
                            return false;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                        holder.minutesRecycleView.setText(oldMinutesValue);
                        return false;
                    }
                }
            });

            final String oldNote = holder.notesAboutOvertime.getText().toString();
            holder.saveNoteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String newNote = holder.notesAboutOvertime.getText().toString();
                    if (!oldNote.equals(newNote))
                    {
                        listener.saveNote(newNote, item.getUid());
                        Toast.makeText(context, R.string.operation_saved, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public interface ItemListAdapterListener
    {
        void deleteItem(Item item);
        void changeDateOfOvertime(int chosenYear, int chosenMonth, int chosenDayOfWeek, int id);
        void changeNumberOfMinutesAndHours(int hours, int minutes, int id);
        void saveNote(String note, int id);
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


/*
<color name="colorPrimary">#008577</color>
    <color name="colorPrimaryDark">#00574B</color>
    <color name="colorAccent">#E61561</color>
 */


// ctrl + . minimalizuje funkcję lub obszer w którym aktualnie się znajdujemy












