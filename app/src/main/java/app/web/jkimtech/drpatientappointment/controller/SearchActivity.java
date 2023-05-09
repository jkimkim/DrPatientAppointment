package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Doctor;

public class SearchActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorRef = db.collection("Doctor");

    private DoctorAdapterFiltered adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        configureToolbar();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.searchPatRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = doctorRef.orderBy("name", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            adapter = new DoctorAdapterFiltered(task.getResult().toObjects(Doctor.class));
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        Drawable r= getResources().getDrawable(R.drawable.ic_local_hospital_black_24dp);
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" Speciality" );
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            // other code to configure the search view...
            searchView.setQueryHint("Search Doctor");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    DoctorAdapterFiltered.specialitySearch = false;
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch (item.getItemId()){
            case R.id.option_all:
                DoctorAdapterFiltered.specialitySearch = false;
                adapter.getFilter().filter("");
                return true;
            case R.id.option_general:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("General Practitioner");
                return true;
            case R.id.option_Dentist:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("Dentist");
                return true;
            case R.id.option_Ophthalmology:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("Ophthalmology");
                return true;
            case R.id.option_ORL:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("ORL");
                return true;
            case R.id.option_Pediatric:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("Pediatric");
                return true;
            case R.id.option_Radiology:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("Radiology");
                return true;
            case R.id.option_Rheumatology:
                DoctorAdapterFiltered.specialitySearch = true;
                adapter.getFilter().filter("Rheumatology");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configureToolbar() {
        // Get the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle("Doctors list");
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}