package com.app.harcdis.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.R;
import com.app.harcdis.adminRole.model.AdminCardHolderModel;
import com.app.harcdis.click_interface.AdminForwardClickHandle;


import java.util.ArrayList;

public class SelectVerifiedAdapter extends RecyclerView.Adapter<SelectVerifiedAdapter.SelectVerifiedHolder> {
    Context context;

    private boolean isSelectedAll = false;
    private ArrayList<AdminCardHolderModel> arrayList;
    AdminForwardClickHandle adminForwardClickHandle;


    public SelectVerifiedAdapter(Context context, ArrayList<AdminCardHolderModel> arrayList, AdminForwardClickHandle adminForwardClickHandle) {
        this.context = context;
        this.arrayList = arrayList;
        this.adminForwardClickHandle = adminForwardClickHandle;
    }

    @NonNull
    @Override
    public SelectVerifiedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_to_demolish, parent, false);
        return new SelectVerifiedHolder(view);
    }

    public void filterList(ArrayList<AdminCardHolderModel> filterList) {
        arrayList = filterList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull SelectVerifiedHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminCardHolderModel AdminCardHolderModel = arrayList.get(position);

        holder.verified_by_text_view.setText("Verified By - " + AdminCardHolderModel.getVerifiedBy());
        holder.tcpId.setText("UID \n " + AdminCardHolderModel.getUID());
        holder.location.setText(AdminCardHolderModel.getN_murr_no() + "//" + AdminCardHolderModel.getN_khas_no() + "\n" + AdminCardHolderModel.getN_v_name() + ", " + AdminCardHolderModel.getN_t_name() + ", " + AdminCardHolderModel.getN_d_name());
        holder.point_auth_unauth_status.setText("Status - \uD83D\uDEA9 " + AdminCardHolderModel.getAuth_status());

        if (AdminCardHolderModel.getAuth_status().matches("Authorized")) {
            holder.point_auth_unauth_status.setBackground(context.getResources().getDrawable(R.drawable.btn_background2));
        } else if (AdminCardHolderModel.getAuth_status().matches("Unauthorized")) {
            holder.point_auth_unauth_status.setBackground(context.getResources().getDrawable(R.drawable.btn_background_red));
        } else {
            holder.point_auth_unauth_status.setBackground(context.getResources().getDrawable(R.drawable.btn_background_yellow));
        }

        holder.admin_approved_button.setOnClickListener(view -> {
            adminForwardClickHandle.ClickHandleForAdmin(AdminCardHolderModel.getUID(), "view");
        });


        holder.admin_forward_button.setOnClickListener(view -> {
            adminForwardClickHandle.ClickHandleForAdmin(AdminCardHolderModel.getUID(), "forward");

        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SelectVerifiedHolder extends RecyclerView.ViewHolder {
        TextView tcpId, location;

        TextView point_auth_unauth_status;
        TextView verified_by_text_view;

        Button admin_approved_button;
        Button admin_forward_button;


        public SelectVerifiedHolder(@NonNull View itemView) {
            super(itemView);
            tcpId = itemView.findViewById(R.id.tcpId);
            point_auth_unauth_status = itemView.findViewById(R.id.point_auth_unauth_status);
            location = itemView.findViewById(R.id.location);
            verified_by_text_view = itemView.findViewById(R.id.verified_by_text_view);
            admin_approved_button = itemView.findViewById(R.id.admin_approved_button);
            admin_forward_button = itemView.findViewById(R.id.admin_forward_button);

        }
    }
}
