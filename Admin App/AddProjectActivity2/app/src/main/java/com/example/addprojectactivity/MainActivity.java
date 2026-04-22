package com.example.addprojectactivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ProjectAdapter.OnProjectClickListener {

    private RecyclerView PtyRvProjects;
    private ProjectAdapter PtyAdapter;
    private DatabaseHelper PtyDbHelper;
    private List<Project> PtyProjectList = new ArrayList<>();
    private TextInputEditText PtyEtSearch;
    private FloatingActionButton PtyFabAdd;
    private Button PtyBtnReset, PtyBtnAdvancedSearch, PtyBtnSyncAll;
    private Toolbar PtyToolbar;
    private BottomNavigationView PtyBottomNavigation;
    private CoordinatorLayout PtyProjectsLayout;
    private LinearLayout PtySyncLayout;

    private DatabaseReference PtyMDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PtyDbHelper = new DatabaseHelper(this);

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseOptions PtyOptions = new FirebaseOptions.Builder()
                        .setApplicationId("1:1234567890:android:abcdef")
                        .setDatabaseUrl("https://mobileappexpensetracker-default-rtdb.firebaseio.com/")
                        .setProjectId("mobileappexpensetracker")
                        .build();
                FirebaseApp.initializeApp(this, PtyOptions);
            }
            PtyMDatabase = FirebaseDatabase.getInstance().getReference();
        } catch (Exception PtyE) {
            PtyE.printStackTrace();
        }

        PtyToolbar = findViewById(R.id.toolbar);
        if (PtyToolbar != null) {
            setSupportActionBar(PtyToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Expense Tracker");
            }
        }

        PtyRvProjects = findViewById(R.id.rvProjects);
        PtyEtSearch = findViewById(R.id.etSearch);
        PtyFabAdd = findViewById(R.id.fabAdd);
        PtyBtnReset = findViewById(R.id.btnReset);
        PtyBtnAdvancedSearch = findViewById(R.id.btnAdvancedSearch);
        PtyBtnSyncAll = findViewById(R.id.btnSyncAll);
        PtyBottomNavigation = findViewById(R.id.bottom_navigation);
        PtyProjectsLayout = findViewById(R.id.projects_layout);
        PtySyncLayout = findViewById(R.id.sync_layout);

        PtyBottomNavigation.setOnItemSelectedListener(PtyItem -> {
            int PtyItemId = PtyItem.getItemId();
            if (PtyItemId == R.id.nav_projects) {
                PtyProjectsLayout.setVisibility(View.VISIBLE);
                PtySyncLayout.setVisibility(View.GONE);
                if (getSupportActionBar() != null) getSupportActionBar().setTitle("Expense Tracker");
                return true;
            } else if (PtyItemId == R.id.nav_upload) {
                PtyProjectsLayout.setVisibility(View.GONE);
                PtySyncLayout.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null) getSupportActionBar().setTitle("Cloud Sync");
                return true;
            }
            return false;
        });

        PtyRvProjects.setLayoutManager(new LinearLayoutManager(this));
        PtyAdapter = new ProjectAdapter(PtyProjectList, this, PtyDbHelper);
        PtyRvProjects.setAdapter(PtyAdapter);

        PtyFabAdd.setOnClickListener(PtyV -> startActivity(new Intent(MainActivity.this, AddProjectActivity.class)));

        PtyBtnReset.setOnClickListener(PtyV -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Database")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (PtyDialog, PtyWhich) -> {
                        PtyDbHelper.deleteAllProjects();
                        loadProjects();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        PtyBtnAdvancedSearch.setOnClickListener(PtyV -> showAdvancedSearchDialog());

        PtyBtnSyncAll.setOnClickListener(PtyV -> {
            if (isNetworkAvailable()) {
                syncDataToFirebase();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        PtyEtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence PtyS, int PtyStart, int PtyCount, int PtyAfter) {}
            @Override public void onTextChanged(CharSequence PtyS, int PtyStart, int PtyBefore, int PtyCount) { loadProjects(PtyS.toString()); }
            @Override public void afterTextChanged(Editable PtyS) {}
        });
    }

    private void syncDataToFirebase() {
        if (PtyMDatabase == null) {
            Toast.makeText(this, "Firebase Connection Error", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Project> PtyAllProjects = PtyDbHelper.getAllProjects();
        if (PtyAllProjects.isEmpty()) {
            Toast.makeText(this, "No data to sync", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog PtyProgressDialog = new ProgressDialog(this);
        PtyProgressDialog.setTitle("Firebase Sync");
        PtyProgressDialog.setMessage("Uploading data...");
        PtyProgressDialog.setCancelable(false);
        PtyProgressDialog.show();
        Map<String, Object> PtyUpdates = new HashMap<>();
        for (Project PtyP : PtyAllProjects) {
            List<Expense> PtyExpenses = PtyDbHelper.getExpensesForProject(PtyP.getId());
            Map<String, Object> PtyData = new HashMap<>();
            PtyData.put("id", PtyP.getId());
            PtyData.put("code", PtyP.getCode());
            PtyData.put("name", PtyP.getName());
            PtyData.put("description", PtyP.getDescription());
            PtyData.put("startDate", PtyP.getStartDate());   // thêm
            PtyData.put("endDate", PtyP.getEndDate());       // thêm
            PtyData.put("manager", PtyP.getManager());
            PtyData.put("status", PtyP.getStatus());         // thêm
            PtyData.put("budget", PtyP.getBudget());
            PtyData.put("requirements", PtyP.getRequirements()); // thêm
            PtyData.put("client", PtyP.getClient());         // thêm
            PtyData.put("expenses", PtyExpenses);
            PtyUpdates.put("projects/" + PtyP.getCode(), PtyData);
        }
        PtyMDatabase.updateChildren(PtyUpdates).addOnCompleteListener(PtyTask -> {
            PtyProgressDialog.dismiss();
            if (PtyTask.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Sync Successful!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Sync Failed: " + PtyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager PtyCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo PtyNi = PtyCm.getActiveNetworkInfo();
        return PtyNi != null && PtyNi.isConnected();
    }

    private void showAdvancedSearchDialog() {
        View PtyView = LayoutInflater.from(this).inflate(R.layout.dialog_advanced_search, null);
        TextInputEditText PtyEtOwner = PtyView.findViewById(R.id.etSearchOwner);
        TextInputEditText PtyEtDate = PtyView.findViewById(R.id.etSearchDate);
        Spinner PtyStatusSpinner = PtyView.findViewById(R.id.searchStatusSpinner);

        String[] PtyStatuses = {"All", "Active", "Completed", "On Hold"};
        ArrayAdapter<String> PtyStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PtyStatuses);
        PtyStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PtyStatusSpinner.setAdapter(PtyStatusAdapter);

        PtyEtDate.setOnClickListener(PtyV -> {
            Calendar PtyC = Calendar.getInstance();
            new DatePickerDialog(this, (PtyV1, PtyY, PtyM, PtyD) -> PtyEtDate.setText(PtyD + "/" + (PtyM + 1) + "/" + PtyY),
                    PtyC.get(Calendar.YEAR), PtyC.get(Calendar.MONTH), PtyC.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setView(PtyView)
                .setPositiveButton("Search", (PtyDialog, PtyWhich) -> {
                    PtyProjectList = PtyDbHelper.searchProjects(PtyEtSearch.getText().toString(),
                            PtyStatusSpinner.getSelectedItem().toString(), PtyEtDate.getText().toString(), PtyEtOwner.getText().toString());
                    PtyAdapter.setProjects(PtyProjectList);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }

    private void loadProjects() {
        String PtyKeyword = PtyEtSearch.getText() != null ? PtyEtSearch.getText().toString() : "";
        loadProjects(PtyKeyword);
    }

    private void loadProjects(String PtyKeyword) {
        if (PtyDbHelper != null) {
            PtyProjectList = PtyDbHelper.searchProjects(PtyKeyword, null, null, null);
            if (PtyAdapter != null) PtyAdapter.setProjects(PtyProjectList);
        }
    }

    @Override public void onEditClick(Project PtyProject) {
        Intent PtyIntent = new Intent(this, AddProjectActivity.class);
        PtyIntent.putExtra("project", PtyProject);
        startActivity(PtyIntent);
    }

    @Override public void onDeleteClick(Project PtyProject) {
        new AlertDialog.Builder(this).setTitle("Delete").setMessage("Are you sure?")
                .setPositiveButton("Yes", (PtyD, PtyW) -> { PtyDbHelper.deleteProject(PtyProject.getId()); loadProjects(); })
                .setNegativeButton("No", null).show();
    }

    @Override public void onItemClick(Project PtyProject) {
        Intent PtyIntent = new Intent(this, ProjectDetailsActivity.class);
        PtyIntent.putExtra("project", PtyProject);
        startActivity(PtyIntent);
    }
}