package com.example.addprojectactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.PtyExpenseViewHolder> {

    private List<Expense> PtyExpenseList;
    private OnExpenseClickListener PtyListener;

    public interface OnExpenseClickListener {
        void onEditClick(Expense PtyExpense);
        void onDeleteClick(Expense PtyExpense);
    }

    public ExpenseAdapter(List<Expense> PtyExpenseList, OnExpenseClickListener PtyListener) {
        this.PtyExpenseList = PtyExpenseList;
        this.PtyListener = PtyListener;
    }

    @NonNull
    @Override
    public PtyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup PtyParent, int PtyViewType) {
        View PtyView = LayoutInflater.from(PtyParent.getContext()).inflate(R.layout.item_expense, PtyParent, false);
        return new PtyExpenseViewHolder(PtyView);
    }

    @Override
    public void onBindViewHolder(@NonNull PtyExpenseViewHolder PtyHolder, int PtyPos) {
        Expense PtyExpense = PtyExpenseList.get(PtyPos);
        PtyHolder.PtyTvType.setText(PtyExpense.getType());
        PtyHolder.PtyTvAmount.setText(String.format("%.2f %s", PtyExpense.getAmount(), PtyExpense.getCurrency()));
        PtyHolder.PtyTvDate.setText("Date: " + PtyExpense.getDate());
        PtyHolder.PtyTvClaimant.setText("Claimant: " + PtyExpense.getClaimant());
        PtyHolder.PtyTvStatus.setText("Status: " + PtyExpense.getPaymentStatus());

        PtyHolder.PtyBtnEdit.setOnClickListener(v -> PtyListener.onEditClick(PtyExpense));
        PtyHolder.PtyBtnDelete.setOnClickListener(v -> PtyListener.onDeleteClick(PtyExpense));
    }

    @Override
    public int getItemCount() {
        return PtyExpenseList != null ? PtyExpenseList.size() : 0;
    }

    public void setExpenses(List<Expense> PtyExpenses) {
        this.PtyExpenseList = PtyExpenses;
        notifyDataSetChanged();
    }

    static class PtyExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView PtyTvType, PtyTvAmount, PtyTvDate, PtyTvClaimant, PtyTvStatus;
        ImageButton PtyBtnEdit, PtyBtnDelete;

        public PtyExpenseViewHolder(@NonNull View PtyItem) {
            super(PtyItem);
            PtyTvType = PtyItem.findViewById(R.id.tvExpenseType);
            PtyTvAmount = PtyItem.findViewById(R.id.tvExpenseAmount);
            PtyTvDate = PtyItem.findViewById(R.id.tvExpenseDate);
            PtyTvClaimant = PtyItem.findViewById(R.id.tvExpenseClaimant);
            PtyTvStatus = PtyItem.findViewById(R.id.tvExpenseStatus);
            PtyBtnEdit = PtyItem.findViewById(R.id.btnEditExpense);
            PtyBtnDelete = PtyItem.findViewById(R.id.btnDeleteExpense);
        }
    }
}