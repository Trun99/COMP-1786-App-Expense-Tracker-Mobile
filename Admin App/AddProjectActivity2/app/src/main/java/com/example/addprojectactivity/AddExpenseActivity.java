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

public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText PtyExpCode, PtyExpDate, PtyExpAmount, PtyExpCurrency,
            PtyClaimant, PtyExpDesc, PtyExpLocation;
    private Spinner PtyTypeSpinner, PtyPayMethodSpinner, PtyPayStatusSpinner;
    private Button PtyBtnSave;
    private DatabaseHelper PtyDbHelper;
    private int PtyTargetProjectId = -1;
    private Expense PtyExistingExp;
    private Toolbar PtyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        PtyDbHelper = new DatabaseHelper(this);

        PtyToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(PtyToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            PtyToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        PtyExpCode = findViewById(R.id.expenseCode);
        PtyExpDate = findViewById(R.id.expenseDate);
        PtyExpAmount = findViewById(R.id.expenseAmount);
        PtyExpCurrency = findViewById(R.id.expenseCurrency);
        PtyClaimant = findViewById(R.id.claimant);
        PtyExpDesc = findViewById(R.id.expenseDesc);
        PtyExpLocation = findViewById(R.id.expenseLocation);
        PtyTypeSpinner = findViewById(R.id.typeSpinner);
        PtyPayMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        PtyPayStatusSpinner = findViewById(R.id.paymentStatusSpinner);
        PtyBtnSave = findViewById(R.id.btnSaveExpense);

        PtySetupSpinner(PtyTypeSpinner, R.array.expense_type_array);
        PtySetupSpinner(PtyPayMethodSpinner, R.array.payment_method_array);
        PtySetupSpinner(PtyPayStatusSpinner, R.array.payment_status_array);

        PtyExpDate.setOnClickListener(v -> PtyShowDatePicker());

        if (getIntent().hasExtra("expense")) {
            PtyExistingExp = (Expense) getIntent().getSerializableExtra("expense");
            if (PtyExistingExp != null) {
                PtyTargetProjectId = PtyExistingExp.getProjectId();
                PtyPopulateFields(PtyExistingExp);
            }
            getSupportActionBar().setTitle("Edit Expense");
        } else {
            PtyTargetProjectId = getIntent().getIntExtra("project_id", -1);
            getSupportActionBar().setTitle("Add Expense");
        }

        PtyBtnSave.setOnClickListener(v -> PtyValidateAndConfirm());
    }

    private void PtySetupSpinner(Spinner PtyS, int PtyResId) {
        ArrayAdapter<CharSequence> PtyAdapter = ArrayAdapter.createFromResource(this,
                PtyResId, android.R.layout.simple_spinner_item);
        PtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PtyS.setAdapter(PtyAdapter);
    }

    private void PtyPopulateFields(Expense PtyE) {
        PtyExpCode.setText(PtyE.getExpenseId());
        PtyExpDate.setText(PtyE.getDate());
        PtyExpAmount.setText(String.valueOf(PtyE.getAmount()));
        PtyExpCurrency.setText(PtyE.getCurrency());
        PtyClaimant.setText(PtyE.getClaimant());
        PtyExpDesc.setText(PtyE.getDescription());
        PtyExpLocation.setText(PtyE.getLocation());

        ArrayAdapter PtyAdapter = (ArrayAdapter) PtyTypeSpinner.getAdapter();
        PtyTypeSpinner.setSelection(PtyAdapter.getPosition(PtyE.getType()));
        
        PtyAdapter = (ArrayAdapter) PtyPayMethodSpinner.getAdapter();
        PtyPayMethodSpinner.setSelection(PtyAdapter.getPosition(PtyE.getPaymentMethod()));
        
        PtyAdapter = (ArrayAdapter) PtyPayStatusSpinner.getAdapter();
        PtyPayStatusSpinner.setSelection(PtyAdapter.getPosition(PtyE.getPaymentStatus()));
    }

    private void PtyShowDatePicker() {
        final Calendar PtyC = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> 
                PtyExpDate.setText(day + "/" + (month + 1) + "/" + year),
                PtyC.get(Calendar.YEAR), PtyC.get(Calendar.MONTH), PtyC.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void PtyValidateAndConfirm() {
        if (PtyIsEmpty(PtyExpCode)) { PtyExpCode.setError("Required"); return; }
        if (PtyIsEmpty(PtyExpDate)) { PtyExpDate.setError("Required"); return; }
        if (PtyIsEmpty(PtyExpAmount)) { PtyExpAmount.setError("Required"); return; }
        if (PtyIsEmpty(PtyClaimant)) { PtyClaimant.setError("Required"); return; }

        PtyShowConfirmationDialog();
    }

    private void PtyShowConfirmationDialog() {
        String PtyMsg = "Exp ID: " + PtyExpCode.getText().toString() + "\n" +
                "Amount: " + PtyExpAmount.getText().toString() + " " + PtyExpCurrency.getText().toString() + "\n" +
                "Claimant: " + PtyClaimant.getText().toString();

        new AlertDialog.Builder(this)
                .setTitle("Confirm Expense")
                .setMessage(PtyMsg)
                .setPositiveButton("Confirm", (dialog, which) -> PtySaveToDatabase())
                .setNegativeButton("Edit", null)
                .show();
    }

    private void PtySaveToDatabase() {
        try {
            Expense PtyNewExp = (PtyExistingExp != null) ? PtyExistingExp : new Expense();
            PtyNewExp.setProjectId(PtyTargetProjectId);
            PtyNewExp.setExpenseId(PtyExpCode.getText().toString());
            PtyNewExp.setDate(PtyExpDate.getText().toString());
            PtyNewExp.setAmount(Double.parseDouble(PtyExpAmount.getText().toString()));
            PtyNewExp.setCurrency(PtyExpCurrency.getText().toString());
            PtyNewExp.setType(PtyTypeSpinner.getSelectedItem().toString());
            PtyNewExp.setPaymentMethod(PtyPayMethodSpinner.getSelectedItem().toString());
            PtyNewExp.setClaimant(PtyClaimant.getText().toString());
            PtyNewExp.setPaymentStatus(PtyPayStatusSpinner.getSelectedItem().toString());
            PtyNewExp.setDescription(PtyExpDesc.getText().toString());
            PtyNewExp.setLocation(PtyExpLocation.getText().toString());

            long PtyResult = (PtyExistingExp != null) ? PtyDbHelper.updateExpense(PtyNewExp) : PtyDbHelper.addExpense(PtyNewExp);

            if (PtyResult != -1) {
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