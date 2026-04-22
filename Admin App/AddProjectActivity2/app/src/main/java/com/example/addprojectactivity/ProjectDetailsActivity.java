package com.example.addprojectactivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProjectDetailsActivity extends AppCompatActivity {

    private TextView PtyTvDetName, PtyTvDetCode, PtyTvDetDesc, PtyTvDetStartDate, PtyTvDetEndDate,
            PtyTvDetManager, PtyTvDetStatus, PtyTvDetBudget, PtyTvDetReqs, PtyTvDetClient;
    private Button PtyBtnManageExp, PtyBtnUpload;
    private Project PtyProject;
    private Toolbar PtyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        PtyProject = (Project) getIntent().getSerializableExtra("project");

        if (PtyProject == null) {
            finish();
            return;
        }

        PtyToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(PtyToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Project Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            PtyToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        PtyTvDetName = findViewById(R.id.detName);
        PtyTvDetCode = findViewById(R.id.detCode);
        PtyTvDetDesc = findViewById(R.id.detDesc);
        PtyTvDetStartDate = findViewById(R.id.detStartDate);
        PtyTvDetEndDate = findViewById(R.id.detEndDate);
        PtyTvDetManager = findViewById(R.id.detManager);
        PtyTvDetStatus = findViewById(R.id.detStatus);
        PtyTvDetBudget = findViewById(R.id.detBudget);
        PtyTvDetReqs = findViewById(R.id.detRequirements);
        PtyTvDetClient = findViewById(R.id.detClient);
        PtyBtnManageExp = findViewById(R.id.btnManageExpenses);
        PtyBtnUpload = findViewById(R.id.btnUpload);

        PtyDisplayDetails();

        PtyBtnManageExp.setOnClickListener(v -> {
            Intent PtyIntent = new Intent(this, ExpenseListActivity.class);
            PtyIntent.putExtra("project", PtyProject);
            startActivity(PtyIntent);
        });

        PtyBtnUpload.setOnClickListener(v -> {
            if (PtyIsNetworkAvailable()) {
                PtyUploadProject();
            } else {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PtyDisplayDetails() {
        PtyTvDetName.setText(PtyProject.getName());
        PtyTvDetCode.setText("Code: " + PtyProject.getCode());
        PtyTvDetDesc.setText(PtyProject.getDescription());
        PtyTvDetStartDate.setText(PtyProject.getStartDate());
        PtyTvDetEndDate.setText(PtyProject.getEndDate());
        PtyTvDetManager.setText(PtyProject.getManager());
        PtyTvDetStatus.setText(PtyProject.getStatus());
        PtyTvDetBudget.setText(String.format("%.2f", PtyProject.getBudget()));
        PtyTvDetReqs.setText(PtyProject.getRequirements());
        PtyTvDetClient.setText(PtyProject.getClient());
    }

    private boolean PtyIsNetworkAvailable() {
        ConnectivityManager PtyCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo PtyNi = PtyCm.getActiveNetworkInfo();
        return PtyNi != null && PtyNi.isConnected();
    }

    private void PtyUploadProject() {
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(ProjectDetailsActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
        }, 2000);
    }
}