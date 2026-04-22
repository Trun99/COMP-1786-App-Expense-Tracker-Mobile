package com.example.addprojectactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String PtyDatabaseName = "ExpenseTracker.db";
    private static final int PtyDatabaseVersion = 5; // Tăng version để làm sạch dữ liệu cũ

    private static final String PtyTableProjects = "projects_table";
    private static final String PtyColProjId = "id";
    private static final String PtyColProjCode = "proj_code";
    private static final String PtyColProjName = "proj_name";
    private static final String PtyColProjDesc = "proj_desc";
    private static final String PtyColProjStart = "proj_start";
    private static final String PtyColProjEnd = "proj_end";
    private static final String PtyColProjManager = "proj_manager";
    private static final String PtyColProjStatus = "proj_status";
    private static final String PtyColProjBudget = "proj_budget";
    private static final String PtyColProjReqs = "proj_reqs";
    private static final String PtyColProjClient = "proj_client";

    private static final String PtyTableExpenses = "expenses_table";
    private static final String PtyColExpId = "id";
    private static final String PtyColExpProjOwnerId = "project_owner_id";
    private static final String PtyColExpCode = "expense_code";
    private static final String PtyColExpDate = "expense_date";
    private static final String PtyColExpAmount = "expense_amount";
    private static final String PtyColExpCurrency = "expense_currency";
    private static final String PtyColExpType = "expense_type";
    private static final String PtyColExpPayMethod = "payment_method";
    private static final String PtyColExpClaimant = "claimant_name";
    private static final String PtyColExpStatus = "payment_status";
    private static final String PtyColExpDesc = "description";
    private static final String PtyColExpLoc = "location";

    private static final String PtyCreateProjTable = "CREATE TABLE " + PtyTableProjects + "("
            + PtyColProjId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PtyColProjCode + " TEXT,"
            + PtyColProjName + " TEXT,"
            + PtyColProjDesc + " TEXT,"
            + PtyColProjStart + " TEXT,"
            + PtyColProjEnd + " TEXT,"
            + PtyColProjManager + " TEXT,"
            + PtyColProjStatus + " TEXT,"
            + PtyColProjBudget + " REAL,"
            + PtyColProjReqs + " TEXT,"
            + PtyColProjClient + " TEXT"
            + ")";

    private static final String PtyCreateExpTable = "CREATE TABLE " + PtyTableExpenses + "("
            + PtyColExpId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PtyColExpProjOwnerId + " INTEGER,"
            + PtyColExpCode + " TEXT,"
            + PtyColExpDate + " TEXT,"
            + PtyColExpAmount + " REAL,"
            + PtyColExpCurrency + " TEXT,"
            + PtyColExpType + " TEXT,"
            + PtyColExpPayMethod + " TEXT,"
            + PtyColExpClaimant + " TEXT,"
            + PtyColExpStatus + " TEXT,"
            + PtyColExpDesc + " TEXT,"
            + PtyColExpLoc + " TEXT,"
            + "FOREIGN KEY(" + PtyColExpProjOwnerId + ") REFERENCES " + PtyTableProjects + "(" + PtyColProjId + ") ON DELETE CASCADE"
            + ")";

    public DatabaseHelper(Context PtyContext) {
        super(PtyContext, PtyDatabaseName, null, PtyDatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase PtyDb) {
        PtyDb.execSQL(PtyCreateProjTable);
        PtyDb.execSQL(PtyCreateExpTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase PtyDb, int PtyOldVer, int PtyNewVer) {
        PtyDb.execSQL("DROP TABLE IF EXISTS " + PtyTableExpenses);
        PtyDb.execSQL("DROP TABLE IF EXISTS " + PtyTableProjects);
        onCreate(PtyDb);
    }

    @Override
    public void onConfigure(SQLiteDatabase PtyDb) {
        super.onConfigure(PtyDb);
        PtyDb.setForeignKeyConstraintsEnabled(true);
    }

    public long addProject(Project PtyProject) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        ContentValues PtyValues = new ContentValues();
        PtyValues.put(PtyColProjCode, PtyProject.getCode());
        PtyValues.put(PtyColProjName, PtyProject.getName());
        PtyValues.put(PtyColProjDesc, PtyProject.getDescription());
        PtyValues.put(PtyColProjStart, PtyProject.getStartDate());
        PtyValues.put(PtyColProjEnd, PtyProject.getEndDate());
        PtyValues.put(PtyColProjManager, PtyProject.getManager());
        PtyValues.put(PtyColProjStatus, PtyProject.getStatus());
        PtyValues.put(PtyColProjBudget, PtyProject.getBudget());
        PtyValues.put(PtyColProjReqs, PtyProject.getRequirements());
        PtyValues.put(PtyColProjClient, PtyProject.getClient());
        long PtyId = PtyDb.insert(PtyTableProjects, null, PtyValues);
        PtyDb.close();
        return PtyId;
    }

    public List<Project> getAllProjects() {
        return searchProjects(null, null, null, null);
    }

    public List<Project> searchProjects(String PtyKey, String PtyStatus, String PtyDate, String PtyOwner) {
        List<Project> PtyList = new ArrayList<>();
        SQLiteDatabase PtyDb = this.getReadableDatabase();
        StringBuilder PtySelection = new StringBuilder();
        List<String> PtyArgs = new ArrayList<>();

        if (PtyKey != null && !PtyKey.isEmpty()) {
            PtySelection.append("(").append(PtyColProjName).append(" LIKE ? OR ").append(PtyColProjDesc).append(" LIKE ?)");
            PtyArgs.add("%" + PtyKey + "%");
            PtyArgs.add("%" + PtyKey + "%");
        }

        if (PtyStatus != null && !PtyStatus.equals("All")) {
            if (PtySelection.length() > 0) PtySelection.append(" AND ");
            PtySelection.append(PtyColProjStatus).append(" = ?");
            PtyArgs.add(PtyStatus);
        }

        if (PtyDate != null && !PtyDate.isEmpty()) {
            if (PtySelection.length() > 0) PtySelection.append(" AND ");
            PtySelection.append(PtyColProjStart).append(" = ?");
            PtyArgs.add(PtyDate);
        }

        if (PtyOwner != null && !PtyOwner.isEmpty()) {
            if (PtySelection.length() > 0) PtySelection.append(" AND ");
            PtySelection.append(PtyColProjManager).append(" LIKE ?");
            PtyArgs.add("%" + PtyOwner + "%");
        }

        String PtyFinalSelection = PtySelection.length() == 0 ? null : PtySelection.toString();
        String[] PtyFinalArgs = PtyArgs.isEmpty() ? null : PtyArgs.toArray(new String[0]);

        Cursor PtyCursor = PtyDb.query(PtyTableProjects, null, PtyFinalSelection, PtyFinalArgs, null, null, PtyColProjId + " DESC");
        if (PtyCursor.moveToFirst()) {
            do {
                Project PtyP = new Project();
                PtyP.setId(PtyCursor.getInt(PtyCursor.getColumnIndexOrThrow(PtyColProjId)));
                PtyP.setCode(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjCode)));
                PtyP.setName(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjName)));
                PtyP.setDescription(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjDesc)));
                PtyP.setStartDate(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjStart)));
                PtyP.setEndDate(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjEnd)));
                PtyP.setManager(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjManager)));
                PtyP.setStatus(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjStatus)));
                PtyP.setBudget(PtyCursor.getDouble(PtyCursor.getColumnIndexOrThrow(PtyColProjBudget)));
                PtyP.setRequirements(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjReqs)));
                PtyP.setClient(PtyCursor.getString(PtyCursor.getColumnIndexOrThrow(PtyColProjClient)));
                PtyList.add(PtyP);
            } while (PtyCursor.moveToNext());
        }
        PtyCursor.close();
        PtyDb.close();
        return PtyList;
    }

    public int updateProject(Project PtyP) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        ContentValues PtyV = new ContentValues();
        PtyV.put(PtyColProjCode, PtyP.getCode());
        PtyV.put(PtyColProjName, PtyP.getName());
        PtyV.put(PtyColProjDesc, PtyP.getDescription());
        PtyV.put(PtyColProjStart, PtyP.getStartDate());
        PtyV.put(PtyColProjEnd, PtyP.getEndDate());
        PtyV.put(PtyColProjManager, PtyP.getManager());
        PtyV.put(PtyColProjStatus, PtyP.getStatus());
        PtyV.put(PtyColProjBudget, PtyP.getBudget());
        PtyV.put(PtyColProjReqs, PtyP.getRequirements());
        PtyV.put(PtyColProjClient, PtyP.getClient());
        int PtyRes = PtyDb.update(PtyTableProjects, PtyV, PtyColProjId + " = ?", new String[]{String.valueOf(PtyP.getId())});
        PtyDb.close();
        return PtyRes;
    }

    public void deleteProject(int PtyId) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        PtyDb.delete(PtyTableProjects, PtyColProjId + " = ?", new String[]{String.valueOf(PtyId)});
        PtyDb.close();
    }

    public void deleteAllProjects() {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        PtyDb.execSQL("DELETE FROM " + PtyTableExpenses);
        PtyDb.execSQL("DELETE FROM " + PtyTableProjects);
        PtyDb.close();
    }

    public long addExpense(Expense PtyE) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        ContentValues PtyV = new ContentValues();
        PtyV.put(PtyColExpProjOwnerId, PtyE.getProjectId());
        PtyV.put(PtyColExpCode, PtyE.getExpenseId());
        PtyV.put(PtyColExpDate, PtyE.getDate());
        PtyV.put(PtyColExpAmount, PtyE.getAmount());
        PtyV.put(PtyColExpCurrency, PtyE.getCurrency());
        PtyV.put(PtyColExpType, PtyE.getType());
        PtyV.put(PtyColExpPayMethod, PtyE.getPaymentMethod());
        PtyV.put(PtyColExpClaimant, PtyE.getClaimant());
        PtyV.put(PtyColExpStatus, PtyE.getPaymentStatus());
        PtyV.put(PtyColExpDesc, PtyE.getDescription());
        PtyV.put(PtyColExpLoc, PtyE.getLocation());
        long PtyId = PtyDb.insert(PtyTableExpenses, null, PtyV);
        PtyDb.close();
        return PtyId;
    }

    public List<Expense> getExpensesForProject(int PtyProjId) {
        List<Expense> PtyList = new ArrayList<>();
        SQLiteDatabase PtyDb = this.getReadableDatabase();
        Cursor PtyC = PtyDb.query(PtyTableExpenses, null, PtyColExpProjOwnerId + " = ?",
                new String[]{String.valueOf(PtyProjId)}, null, null, PtyColExpId + " DESC");
        if (PtyC.moveToFirst()) {
            do {
                Expense PtyE = new Expense();
                PtyE.setId(PtyC.getInt(PtyC.getColumnIndexOrThrow(PtyColExpId)));
                PtyE.setProjectId(PtyC.getInt(PtyC.getColumnIndexOrThrow(PtyColExpProjOwnerId)));
                PtyE.setExpenseId(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpCode)));
                PtyE.setDate(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpDate)));
                PtyE.setAmount(PtyC.getDouble(PtyC.getColumnIndexOrThrow(PtyColExpAmount)));
                PtyE.setCurrency(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpCurrency)));
                PtyE.setType(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpType)));
                PtyE.setPaymentMethod(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpPayMethod)));
                PtyE.setClaimant(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpClaimant)));
                PtyE.setPaymentStatus(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpStatus)));
                PtyE.setDescription(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpDesc)));
                PtyE.setLocation(PtyC.getString(PtyC.getColumnIndexOrThrow(PtyColExpLoc)));
                PtyList.add(PtyE);
            } while (PtyC.moveToNext());
        }
        PtyC.close();
        PtyDb.close();
        return PtyList;
    }

    public int updateExpense(Expense PtyE) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        ContentValues PtyV = new ContentValues();
        PtyV.put(PtyColExpCode, PtyE.getExpenseId());
        PtyV.put(PtyColExpDate, PtyE.getDate());
        PtyV.put(PtyColExpAmount, PtyE.getAmount());
        PtyV.put(PtyColExpCurrency, PtyE.getCurrency());
        PtyV.put(PtyColExpType, PtyE.getType());
        PtyV.put(PtyColExpPayMethod, PtyE.getPaymentMethod());
        PtyV.put(PtyColExpClaimant, PtyE.getClaimant());
        PtyV.put(PtyColExpStatus, PtyE.getPaymentStatus());
        PtyV.put(PtyColExpDesc, PtyE.getDescription());
        PtyV.put(PtyColExpLoc, PtyE.getLocation());
        int PtyRes = PtyDb.update(PtyTableExpenses, PtyV, PtyColExpId + " = ?", new String[]{String.valueOf(PtyE.getId())});
        PtyDb.close();
        return PtyRes;
    }

    public void deleteExpense(int PtyId) {
        SQLiteDatabase PtyDb = this.getWritableDatabase();
        PtyDb.delete(PtyTableExpenses, PtyColExpId + " = ?", new String[]{String.valueOf(PtyId)});
        PtyDb.close();
    }

    public double getTotalExpenses(int PtyProjId) {
        SQLiteDatabase PtyDb = this.getReadableDatabase();
        Cursor PtyC = PtyDb.rawQuery("SELECT SUM(" + PtyColExpAmount + ") FROM " + PtyTableExpenses + " WHERE " + PtyColExpProjOwnerId + " = ?",
                new String[]{String.valueOf(PtyProjId)});
        double PtyTotal = 0;
        if (PtyC.moveToFirst()) {
            PtyTotal = PtyC.getDouble(0);
        }
        PtyC.close();
        PtyDb.close();
        return PtyTotal;
    }
}