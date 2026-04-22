package com.example.addprojectactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.PtyProjectViewHolder> {

    private List<Project> PtyProjectList;
    private OnProjectClickListener PtyListener;
    private DatabaseHelper PtyDbHelper;

    public interface OnProjectClickListener {
        void onEditClick(Project PtyProject);
        void onDeleteClick(Project PtyProject);
        void onItemClick(Project PtyProject);
    }

    public ProjectAdapter(List<Project> PtyProjectList, OnProjectClickListener PtyListener, DatabaseHelper PtyDbHelper) {
        this.PtyProjectList = PtyProjectList;
        this.PtyListener = PtyListener;
        this.PtyDbHelper = PtyDbHelper;
    }

    @NonNull
    @Override
    public PtyProjectViewHolder onCreateViewHolder(@NonNull ViewGroup PtyParent, int PtyViewType) {
        View PtyView = LayoutInflater.from(PtyParent.getContext()).inflate(R.layout.item_project, PtyParent, false);
        return new PtyProjectViewHolder(PtyView);
    }

    @Override
    public void onBindViewHolder(@NonNull PtyProjectViewHolder PtyHolder, int PtyPos) {
        Project PtyProject = PtyProjectList.get(PtyPos);
        PtyHolder.PtyTvName.setText(PtyProject.getName());
        PtyHolder.PtyTvCode.setText("Code: " + PtyProject.getCode());
        PtyHolder.PtyTvStatus.setText("Status: " + PtyProject.getStatus());
        
        if (PtyDbHelper != null) {
            try {
                double PtyTotal = PtyDbHelper.getTotalExpenses(PtyProject.getId());
                PtyHolder.PtyTvTotal.setText(String.format("£%.2f", PtyTotal));
            } catch (Exception PtyE) {
                PtyHolder.PtyTvTotal.setText("£0.00");
            }
        }

        PtyHolder.PtyBtnEdit.setOnClickListener(v -> PtyListener.onEditClick(PtyProject));
        PtyHolder.PtyBtnDelete.setOnClickListener(v -> PtyListener.onDeleteClick(PtyProject));
        PtyHolder.itemView.setOnClickListener(v -> PtyListener.onItemClick(PtyProject));
    }

    @Override
    public int getItemCount() {
        return PtyProjectList != null ? PtyProjectList.size() : 0;
    }

    public void setProjects(List<Project> PtyProjects) {
        this.PtyProjectList = PtyProjects;
        notifyDataSetChanged();
    }

    static class PtyProjectViewHolder extends RecyclerView.ViewHolder {
        TextView PtyTvName, PtyTvCode, PtyTvStatus, PtyTvTotal;
        ImageButton PtyBtnEdit, PtyBtnDelete;

        public PtyProjectViewHolder(@NonNull View PtyItem) {
            super(PtyItem);
            PtyTvName = PtyItem.findViewById(R.id.tvProjectName);
            PtyTvCode = PtyItem.findViewById(R.id.tvProjectCode);
            PtyTvStatus = PtyItem.findViewById(R.id.tvProjectStatus);
            PtyTvTotal = PtyItem.findViewById(R.id.tvTotalExpenses);
            PtyBtnEdit = PtyItem.findViewById(R.id.btnEdit);
            PtyBtnDelete = PtyItem.findViewById(R.id.btnDelete);
        }
    }
}