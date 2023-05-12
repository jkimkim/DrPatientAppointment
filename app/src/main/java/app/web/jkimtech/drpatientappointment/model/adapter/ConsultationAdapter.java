package app.web.jkimtech.drpatientappointment.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.controller.FormInfo;
import app.web.jkimtech.drpatientappointment.model.Form;

public class ConsultationAdapter extends FirestoreRecyclerAdapter<Form, ConsultationAdapter.FormHolder>{

    public ConsultationAdapter(@NonNull FirestoreRecyclerOptions<Form> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FormHolder holder, int position, @NonNull final Form model) {
        FirebaseFirestore.getInstance().document("Doctor/" + model.getDoctor()).get().addOnSuccessListener(documentSnapshot -> holder.doctor_name.setText(documentSnapshot.getString("name")));
        holder.type.setText(model.getType());
        holder.btn.setOnClickListener(v -> openPage(v.getContext(), model));
        String[] date;
        if (model.getDateCreated() != null) {

            date = model.getDateCreated().toString().split(" ");
            // Thu Jun 04 14:46:12 GMT+01:00 2020
            holder.appointment_day_name.setText(date[0]);
            holder.appointment_day.setText(date[2]);
            holder.appointment_month.setText(date[1]);
            holder.doctor_view_title.setText(date[3]);
        }
    }

    private void openPage(Context wf,Form m){
        Intent i = new Intent(wf, FormInfo.class);
        i.putExtra("dateCreated", m.getDateCreated().toString());
        i.putExtra("doctor",m.getDoctor());
        i.putExtra("description",m.getDescription());
        wf.startActivity(i);
    }

    @NonNull
    @Override
    public FormHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.consultation_item,
                parent, false);
        return new FormHolder(v);
    }
    class FormHolder extends RecyclerView.ViewHolder {
        TextView doctor_name;
        TextView type;
        Button btn;
        TextView appointment_month;
        TextView appointment_day;
        TextView appointment_day_name;
        TextView doctor_view_title;

        public FormHolder(View itemView) {
            super(itemView);
            doctor_name = itemView.findViewById(R.id.doctor_name);
            type = itemView.findViewById(R.id.text_view_description);
            btn = itemView.findViewById(R.id.see_form_btn);
            appointment_month = itemView.findViewById(R.id.appointment_month);
            appointment_day = itemView.findViewById(R.id.appointment_day);
            appointment_day_name = itemView.findViewById(R.id.appointment_day_name);
            doctor_view_title = itemView.findViewById(R.id.doctor_view_title);
        }
    }
}
