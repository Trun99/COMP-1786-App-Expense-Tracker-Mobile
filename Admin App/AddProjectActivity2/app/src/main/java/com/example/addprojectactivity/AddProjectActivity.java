package com.example.addprojectactivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {

    private TextInputEditText PtyProjectCode, PtyProjectName, PtyProjectDesc, PtyStartDate, PtyEndDate,
            PtyManager, PtyBudget, PtyRequirements, PtyClient;
    private Spinner PtyStatusSpinner;
    private Button PtyBtnConfirm;
    private DatabaseHelper PtyDbHelper;
    private Project PtyExistingProject;
    private Toolbar PtyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        PtyDbHelper = new DatabaseHelper(this);

        PtyToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(PtyToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            PtyToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        PtyProjectCode = findViewById(R.id.projectCode);
        PtyProjectName = findViewById(R.id.projectName);
        PtyProjectDesc = findViewById(R.id.projectDesc);
        PtyStartDate = findViewById(R.id.startDate);
        PtyEndDate = findViewById(R.id.endDate);
        PtyManager = findViewById(R.id.manager);
        PtyStatusSpinner = findViewById(R.id.statusSpinner);
        PtyBudget = findViewById(R.id.budget);
        PtyRequirements = findViewById(R.id.requirements);
        PtyClient = findViewById(R.id.client);
        PtyBtnConfirm = findViewById(R.id.btnConfirm);

        ArrayAdapter<CharSequence> PtyStatusAdapter = ArrayAdapter.createFromResource(this,
                R.array.project_status_array, android.R.layout.simple_spinner_item);
        PtyStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PtyStatusSpinner.setAdapter(PtyStatusAdapter);

        PtyStartDate.setOnClickListener(v -> PtyShowDatePicker(PtyStartDate));
        PtyEndDate.setOnClickListener(v -> PtyShowDatePicker(PtyEndDate));

        if (getIntent().hasExtra("project")) {
            PtyExistingProject = (Project) getIntent().getSerializableExtra("project");
            PtyPopulateFields(PtyExistingProject);
            getSupportActionBar().setTitle("Edit Project");
            PtyBtnConfirm.setText("Update Project");
        } else {
            getSupportActionBar().setTitle("Add New Project");
        }

        PtyBtnConfirm.setOnClickListener(v -> PtyValidateInput());
    }

    private void PtyPopulateFields(Project PtyP) {
        PtyProjectCode.setText(PtyP.getCode());
        PtyProjectName.setText(PtyP.getName());
        PtyProjectDesc.setText(PtyP.getDescription());
        PtyStartDate.setText(PtyP.getStartDate());
        PtyEndDate.setText(PtyP.getEndDate());
        PtyManager.setText(PtyP.getManager());
        PtyBudget.setText(String.valueOf(PtyP.getBudget()));
        PtyRequirements.setText(PtyP.getRequirements());
        PtyClient.setText(PtyP.getClient());

        ArrayAdapter PtyAdapter = (ArrayAdapter) PtyStatusSpinner.getAdapter();
        int PtyPos = PtyAdapter.getPosition(PtyP.getStatus());
        PtyStatusSpinner.setSelection(PtyPos);
    }

    private void PtyShowDatePicker(TextInputEditText PtyEditText) {
        final Calendar PtyC = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> 
                PtyEditText.setText(day + "/" + (month + 1) + "/" + year),
                PtyC.get(Calendar.YEAR), PtyC.get(Calendar.MONTH), PtyC.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void PtyValidateInput() {
        if (PtyIsEmpty(PtyProjectCode)) { PtyProjectCode.setError("Required"); return; }
        if (PtyIsEmpty(PtyProjectName)) { PtyProjectName.setError("Required"); return; }
        if (PtyIsEmpty(PtyProjectDesc)) { PtyProjectDesc.setError("Required"); return; }
        if (PtyIsEmpty(PtyStartDate)) { PtyStartDate.setError("Required"); return; }
        if (PtyIsEmpty(PtyEndDate)) { PtyEndDate.setError("Required"); return; }
        if (PtyIsEmpty(PtyManager)) { PtyManager.setError("Required"); return; }
        if (PtyIsEmpty(PtyBudget)) { PtyBudget.setError("Required"); return; }

        PtyShowConfirmationDialog();
    }

    private void PtyShowConfirmationDialog() {
        String PtyMsg = "ID: " + PtyProjectCode.getText().toString() + "\n" +
                "Name: " + PtyProjectName.getText().toString() + "\n" +
                "Manager: " + PtyManager.getText().toString() + "\n" +
                "Status: " + PtyStatusSpinner.getSelectedItem().toString();

        new AlertDialog.Builder(this)
                .setTitle("Confirm Details")
                .setMessage(PtyMsg)
                .setPositiveButton("Confirm", (dialog, which) -> PtySaveToDatabase())
                .setNegativeButton("Edit", null)
                .show();
    }

    private void PtySaveToDatabase() {
        try {
            Project PtyNewProject = (PtyExistingProject != null) ? PtyExistingProject : new Project();
            PtyNewProject.setCode(PtyProjectCode.getText().toString());
            PtyNewProject.setName(PtyProjectName.getText().toString());
            PtyNewProject.setDescription(PtyProjectDesc.getText().toString());
            PtyNewProject.setStartDate(PtyStartDate.getText().toString());
            PtyNewProject.setEndDate(PtyEndDate.getText().toString());
            PtyNewProject.setManager(PtyManager.getText().toString());
            PtyNewProject.setStatus(PtyStatusSpinner.getSelectedItem().toString());
            PtyNewProject.setBudget(Double.parseDouble(PtyBudget.getText().toString()));
            PtyNewProject.setRequirements(PtyRequirements.getText().toString());
            PtyNewProject.setClient(PtyClient.getText().toString());

            long PtyRes = (PtyExistingProject != null) ? PtyDbHelper.updateProject(PtyNewProject) : PtyDbHelper.addProject(PtyNewProject);

            if (PtyRes != -1) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception PtyE) {
            Toast.makeText(this, "Error: " + PtyE.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean PtyIsEmpty(TextInputEditText PtyEt) {
        return PtyEt.getText() == null || PtyEt.getText().toString().trim().isEmpty();
    }
}