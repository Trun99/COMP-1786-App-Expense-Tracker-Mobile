package com.example.addprojectactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {

    private RecyclerView PtyRvExpenses;
    private ExpenseAdapter PtyAdapter;
    private DatabaseHelper PtyDbHelper;
    private List<Expense> PtyExpenseList = new ArrayList<>();
    private Project PtyCurrentProject;
    private TextView PtyTvProjectHeader;
    private FloatingActionButton PtyFabAddExp;
    private Toolbar PtyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        PtyDbHelper = new DatabaseHelper(this);
        PtyCurrentProject = (Project) getIntent().getSerializableExtra("project");

        if (PtyCurrentProject == null) {
            Toast.makeText(this, "Error: Project not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        PtyToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(PtyToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Project Expenses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            PtyToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        PtyTvProjectHeader = findViewById(R.id.tvProjectHeader);
        PtyTvProjectHeader.setText("Project: " + PtyCurrentProject.getName());

        PtyRvExpenses = findViewById(R.id.rvExpenses);
        PtyFabAddExp = findViewById(R.id.fabAddExpense);

        PtyRvExpenses.setLayoutManager(new LinearLayoutManager(this));
        PtyAdapter = new ExpenseAdapter(PtyExpenseList, this);
        PtyRvExpenses.setAdapter(PtyAdapter);

        PtyFabAddExp.setOnClickListener(v -> {
            Intent PtyIntent = new Intent(ExpenseListActivity.this, AddExpenseActivity.class);
            PtyIntent.putExtra("project_id", PtyCurrentProject.getId());
            startActivity(PtyIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PtyLoadExpenses();
    }

    private void PtyLoadExpenses() {
        PtyExpenseList = PtyDbHelper.getExpensesForProject(PtyCurrentProject.getId());
        PtyAdapter.setExpenses(PtyExpenseList);
    }

    @Override
    public void onEditClick(Expense PtyExpense) {
        Intent PtyIntent = new Intent(this, AddExpenseActivity.class);
        PtyIntent.putExtra("expense", PtyExpense);
        startActivity(PtyIntent);
    }

    @Override
    public void onDeleteClick(Expense PtyExpense) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    PtyDbHelper.deleteExpense(PtyExpense.getId());
                    PtyLoadExpenses();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}