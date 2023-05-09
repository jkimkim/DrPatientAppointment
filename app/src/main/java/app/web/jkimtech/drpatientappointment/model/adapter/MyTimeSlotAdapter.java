package app.web.jkimtech.drpatientappointment.model.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.ApointementInformation;
import app.web.jkimtech.drpatientappointment.model.Common.Common;
import app.web.jkimtech.drpatientappointment.model.Interface.IRecyclerItemSelectedListener;
import app.web.jkimtech.drpatientappointment.model.TimeSlot;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {
    // Declare variables
    Context context;  // The context of the activity
    List<TimeSlot> timeSlotList;  // List of TimeSlot objects
    List<CardView> cardViewList;  // List of CardView objects
    LocalBroadcastManager localBroadcastManager;  // Instance of the local broadcast manager
    SimpleDateFormat simpleDateFormat;  // Instance of the SimpleDateFormat class

    // Constructor that takes in a context
    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();  // Initialize an empty list for the TimeSlot objects
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);  // Get an instance of the local broadcast manager
        cardViewList = new ArrayList<>();  // Initialize an empty list for the CardView objects
    }

    // Constructor that takes in a context and a list of TimeSlot objects
    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;  // Set the list of TimeSlot objects to the passed-in list
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);  // Get an instance of the local broadcast manager
        cardViewList = new ArrayList<>();  // Initialize an empty list for the CardView objects
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the time slot card view
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_time_slot, parent, false);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");  // Initialize the SimpleDateFormat with the desired format
        // Return a new instance of the MyViewHolder class with the inflated view
        return new MyViewHolder(itemView);
    }
    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // Set the text of the time slot TextView to the time slot string for the current position
        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        // If the time slot list is empty, display that the time slot is available
        if (timeSlotList.size() == 0) {
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));  // Set the background color of the card view to white
            holder.txt_time_slot_description.setText("Available");  // Set the description text to "Available"
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));  // Set the text color to black
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));  // Set the time slot text color to black
        } else {  // If the time slot list is not empty (i.e. there are time slots in the list)
            for (TimeSlot slotValue:timeSlotList){
                // Loop through the time slot list
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == position){
                    holder.card_time_slot.setTag(Common.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.txt_time_slot_description.setText("Full");
                    if(slotValue.getType().equals("Checked"))
                        holder.txt_time_slot_description.setText("Choosen");
                    holder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                }
            }
        }
        // Add the card view to the list of card views
        if (!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);
        // Check if the card view is not available
            holder.setiRecyclerItemSelectedListener((view, pos) -> {
                for(CardView cardView:cardViewList) {
                    // Loop through the list of card views
                    if (cardView.getTag() == null)
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }
                // Set the background color of the card view to orange
                holder.card_time_slot.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_blue_dark));
                // Create an intent with the action "key_enable_button_next"
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_TIME_SLOT,position);
                Common.currentTimeSlot = position ;
                intent.putExtra(Common.KEY_STEP,2);
                Log.e("pos ", "onItemSelectedListener: "+position );
                localBroadcastManager.sendBroadcast(intent);
                // Set the text of the time slot description TextView to "Choosen"
                if(Common.CurrentUserType == "doctor" && holder.txt_time_slot_description.getText().equals("Available")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.card_time_slot.getContext());
                    alert.setTitle("Block");
                    alert.setMessage("Are you sure you want to block?");
                    alert.setPositiveButton("Yes", (dialog, which) -> {
                        // Create an intent with the action "key_enable_button_next"
                        ApointementInformation apointementInformation = new ApointementInformation();
                        apointementInformation.setAppointmentType(Common.Currentaappointementatype);
                        apointementInformation.setDoctorId(Common.CurreentDoctor);
                        apointementInformation.setDoctorName(Common.CurrentDoctorName);
                        apointementInformation.setPath("Doctor/"+Common.CurreentDoctor+"/"+Common.simpleFormat.format(Common.currentDate.getTime())+"/"+String.valueOf(Common.currentTimeSlot));
                        apointementInformation.setType("full");
                        apointementInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                                .append(" on ")
                                .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
                        apointementInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

                        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                .collection("Doctor")
                                .document(Common.CurreentDoctor)
                                .collection(Common.simpleFormat.format(Common.currentDate.getTime()))
                                .document(String.valueOf(Common.currentTimeSlot));

                        bookingDate.set(apointementInformation);
                        dialog.dismiss();
                    });
                    alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    alert.show();
                }
            });
        }
    @Override
    // Returns the total number of items in the data set held by the adapter.
    public int getItemCount() {
        return 20;
    }
    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Declare the variables
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            // Set the iRecyclerItemSelectedListener variable to the iRecyclerItemSelectedListener parameter
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            // Call the superclass constructor
            super(itemView);
            // Initialize the variables
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);
            // Set an OnClickListener to the itemView
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Call the onItemSelectedListener method of the iRecyclerItemSelectedListener variable
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
