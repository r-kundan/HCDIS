package com.app.harcdis.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminSurveyHistoryScreen;
import com.app.harcdis.adminRole.MapViewScreen;
import com.app.harcdis.adminRole.model.AdminUserModel;


import java.util.ArrayList;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserHolder> {
    Context context;
    ArrayList<AdminUserModel> adminUserModelList;

    public AdminUserAdapter(Context context, ArrayList<AdminUserModel> adminUserModelList) {
        this.context = context;
        this.adminUserModelList = adminUserModelList;
    }

    @NonNull
    @Override
    public AdminUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_user_id_card, parent, false);
        return new AdminUserHolder(view);
    }


    public void filterList(ArrayList<AdminUserModel> filterList) {
        adminUserModelList = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserHolder holder, int position) {
        AdminUserModel adminUserModel = adminUserModelList.get(position);
        holder.name.setText(context.getString(R.string.username_) + adminUserModel.getUser_name());
        holder.userName.setText(context.getString(R.string.name_) + adminUserModel.getName());
        holder.userPhoneNumber.setText(context.getString(R.string.phone_no_) + adminUserModel.getMobile());
        holder.userRankLevel.setText(context.getString(R.string.role_) + adminUserModel.getDesignation());
        holder.userAssignedDist.setText(context.getString(R.string.assigned_district_) + adminUserModel.getN_d_name());

        holder.viewOnMapTextView.setOnClickListener(v -> {
            String query = "verifiedBy='" + adminUserModel.getMobile() + "'";
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onBindViewHolder: " + query);
            }
            Intent intent = new Intent(context, MapViewScreen.class);
            intent.putExtra("query", query);
            context.startActivity(intent);
        });
        holder.historyTextView.setOnClickListener(v -> {

            Intent intent = new Intent(context, AdminSurveyHistoryScreen.class);
            intent.putExtra("mobile", adminUserModel.getMobile());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return adminUserModelList.size();
    }

    public class AdminUserHolder extends RecyclerView.ViewHolder {
        TextView userName, name,userPhoneNumber, userRankLevel, userAssignedDist;

        TextView historyTextView, viewOnMapTextView;

        public AdminUserHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            name = itemView.findViewById(R.id.name);
            userPhoneNumber = itemView.findViewById(R.id.userPhoneNumber);
            userRankLevel = itemView.findViewById(R.id.userRankLevel);
            userAssignedDist = itemView.findViewById(R.id.userAssignedDist);
            viewOnMapTextView = itemView.findViewById(R.id.viewOnMapTextView);
            historyTextView = itemView.findViewById(R.id.historyTextView);
        }
    }
}
