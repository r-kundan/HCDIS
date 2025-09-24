package com.app.harcdis.point_forward_flow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.R;
import com.app.harcdis.point_forward_flow.model.ForwardedModel;

import java.util.ArrayList;

public class ForwardedPointAdapter extends RecyclerView.Adapter<ForwardedPointAdapter.ForwardPointHolder> {
    Context context;

    ArrayList<ForwardedModel> forwardedModelArrayList;

    public ForwardedPointAdapter(Context context, ArrayList<ForwardedModel> forwardedModelArrayList) {
        this.context = context;
        this.forwardedModelArrayList = forwardedModelArrayList;
    }

    @NonNull
    @Override
    public ForwardedPointAdapter.ForwardPointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forwarded_point_card_layout, parent, false);
        return new ForwardPointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForwardedPointAdapter.ForwardPointHolder holder, int position) {
        holder.uid_text_view.setText("UID " + forwardedModelArrayList.get(position).getUID());
        holder.assign_by_text_view.setText("Assigned By " + forwardedModelArrayList.get(position).getAssigner_name());
        holder.assign_date_text_view.setText("Assigned Date " + forwardedModelArrayList.get(position).getAssign_date());


        holder.view_on_map_text_view.setOnClickListener(view -> {


        });
        holder.submit_report_text_view.setOnClickListener(view -> {
            Intent intent = new Intent(context, ForwardPointFormScreen.class);
            intent.putExtra("uid_key", forwardedModelArrayList.get(position).getUID());
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return forwardedModelArrayList.size();
    }

    public class ForwardPointHolder extends RecyclerView.ViewHolder {
        TextView uid_text_view, assign_by_text_view, assign_date_text_view;
        TextView submit_report_text_view, view_on_map_text_view;

        public ForwardPointHolder(@NonNull View itemView) {
            super(itemView);
            uid_text_view = itemView.findViewById(R.id.uid_text_view);
            assign_by_text_view = itemView.findViewById(R.id.assign_by_text_view);
            assign_date_text_view = itemView.findViewById(R.id.assign_date_text_view);
            submit_report_text_view = itemView.findViewById(R.id.submit_report_text_view);
            view_on_map_text_view = itemView.findViewById(R.id.view_on_map_text_view);
        }
    }
}
