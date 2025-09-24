package com.app.harcdis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.AdminSurveyHistoryScreen;
import com.app.harcdis.adminRole.PointOnMapScreen;
import com.app.harcdis.adminRole.model.AdminCardHolderModel;
import com.app.harcdis.screens.SimpleImageScreen;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AdminCardAdapter extends RecyclerView.Adapter<AdminCardAdapter.AdminCardHolder> {
    Context context;
    ArrayList<AdminCardHolderModel> arrayList;

    public AdminCardAdapter(Context context, ArrayList<AdminCardHolderModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdminCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_record_show_layout, parent, false);
        return new AdminCardHolder(view);
    }

    public void filterList(ArrayList<AdminCardHolderModel> filterList) {
        arrayList = filterList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull AdminCardHolder holder, int position) {
        AdminCardHolderModel adminCardHolderModel = arrayList.get(position);
        if (adminCardHolderModel.getVerified().equalsIgnoreCase("Y")) {

            holder.status_tv.setTextColor(context.getResources().getColor(R.color.green));
            holder.view_card.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.history_tv.setVisibility(View.VISIBLE);
            holder.no_history_tv.setVisibility(View.GONE);
            holder.near_by_landmark_textview.setVisibility(View.VISIBLE);
            holder.feedback_textview.setVisibility(View.VISIBLE);

        } else if (adminCardHolderModel.getVerified().equalsIgnoreCase("N")) {
            holder.view_card.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.red));
            holder.history_tv.setVisibility(View.GONE);
            holder.no_history_tv.setVisibility(View.VISIBLE);
            holder.near_by_landmark_textview.setVisibility(View.GONE);
            holder.feedback_textview.setVisibility(View.GONE);
        }

        holder.gis_id.setText(adminCardHolderModel.getUID());
        holder.d_name.setText(adminCardHolderModel.getN_d_name());
        holder.t_name.setText(adminCardHolderModel.getN_t_name());
        holder.v_name.setText(adminCardHolderModel.getN_v_name());
        holder.ca_plan_textview.setText(adminCardHolderModel.getCa_name());
        holder.dv_plan_textview.setText(adminCardHolderModel.getDev_plan());

        holder.verification_by_tv.setText(adminCardHolderModel.getUser_name());
        holder.status_tv.setText(adminCardHolderModel.getVerified());

        holder.near_by_landmark_textview.setText(adminCardHolderModel.getNearByLandmark());
        holder.feedback_textview.setText(adminCardHolderModel.getFeedback());

        holder.n_murr_khas_name.setText(adminCardHolderModel.getN_murr_no() + "//" + adminCardHolderModel.getN_khas_no());


        if (!Objects.equals("null", adminCardHolderModel.getUploadimage())) {
            byte[] decodedString = Base64.decode(adminCardHolderModel.getUploadimage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image_view_for_crop_admin.setImageBitmap(decodedByte);

        }

        //Image One

        if (!Objects.equals("null", adminCardHolderModel.getUploadimage1())) {
            byte[] decodedString2 = Base64.decode(adminCardHolderModel.getUploadimage1(), Base64.DEFAULT);
            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
            holder.image_view_for_crop2_admin.setImageBitmap(decodedByte2);
        }
        if (!Objects.equals("null", adminCardHolderModel.getUploadimage2())) {
            byte[] decodedString3 = Base64.decode(adminCardHolderModel.getUploadimage2(), Base64.DEFAULT);
            Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
            holder.image_view_for_crop3_admin.setImageBitmap(decodedByte3);
        }
        //Image One
        if (!Objects.equals("null", adminCardHolderModel.getUploadimage3())) {
            byte[] decodedString4 = Base64.decode(adminCardHolderModel.getUploadimage3(), Base64.DEFAULT);
            Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
            holder.image_view_for_crop4_admin.setImageBitmap(decodedByte4);
        }

        holder.image_view_for_crop_admin.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url", adminCardHolderModel.getUploadimage());
            context.startActivity(intent);
        });
        holder.image_view_for_crop2_admin.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url", adminCardHolderModel.getUploadimage1());
            context.startActivity(intent);
        });
        holder.image_view_for_crop3_admin.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url", adminCardHolderModel.getUploadimage2());
            context.startActivity(intent);
        });
        holder.image_view_for_crop4_admin.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url", adminCardHolderModel.getUploadimage3());
            context.startActivity(intent);
        });


        try {
            String pickup_date = adminCardHolderModel.getEntry_date();
            Log.i("pickup_date", pickup_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            Date objDate = dateFormat.parse(pickup_date);
            SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm a , MMM dd,yyyy");
            String converted_Date = formatOut.format(objDate);
            if (BuildConfig.DEBUG) {
                Log.i("converted_Date", converted_Date);
            }
            holder.verification_date_tv.setText(converted_Date);
        } catch (Exception e) {
            holder.verification_date_tv.setText(adminCardHolderModel.getEntry_date());
        }


        holder.additionalArrowLayout.setOnClickListener(v -> {
            if (holder.additionalLayout.getVisibility() == View.VISIBLE) {
                holder.additionalLayout.setVisibility(View.GONE);
                holder.downArrowImage.setRotation(0);
            } else if (holder.additionalLayout.getVisibility() == View.GONE) {
                holder.additionalLayout.setVisibility(View.VISIBLE);
                holder.downArrowImage.setRotation(180);
            }
        });


        holder.viewOnMap.setOnClickListener(v -> {
            String query = "gisId ='" + adminCardHolderModel.getGisId() + "'";
            if (BuildConfig.DEBUG) {
                Log.d("TAG", "onBindViewHolder: " + query);
            }
            Intent intent = new Intent(context, PointOnMapScreen.class);
            intent.putExtra("latitude", adminCardHolderModel.getLatitude());
            intent.putExtra("longitude", adminCardHolderModel.getLongitude());
            intent.putExtra("query", query);
            context.startActivity(intent);

        });


        holder.history_tv.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminSurveyHistoryScreen.class);
            intent.putExtra("gisId", adminCardHolderModel.getGisId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class AdminCardHolder extends RecyclerView.ViewHolder {
        TextView d_name;
        TextView t_name, v_name, n_murr_khas_name;
        TextView ca_plan_textview, dv_plan_textview, verification_date_tv;
        TextView verification_by_tv, status_tv, viewOnMap, history_tv, no_history_tv;
        TextView near_by_landmark_textview, feedback_textview;
        View view_card;
        ImageView image_view_for_crop2_admin, image_view_for_crop_admin, image_view_for_crop3_admin, image_view_for_crop4_admin;
        TextView gis_id;
        ImageView downArrowImage;
        LinearLayout additionalLayout, additionalArrowLayout;


        public AdminCardHolder(@NonNull View itemView) {
            super(itemView);
            gis_id = itemView.findViewById(R.id.gis_id);
            d_name = itemView.findViewById(R.id.d_name);
            t_name = itemView.findViewById(R.id.t_name);
            v_name = itemView.findViewById(R.id.v_name);
            n_murr_khas_name = itemView.findViewById(R.id.n_murr_khas_name);
            ca_plan_textview = itemView.findViewById(R.id.ca_plan_textview);
            dv_plan_textview = itemView.findViewById(R.id.dv_plan_textview);
            verification_date_tv = itemView.findViewById(R.id.verification_date_tv);
            verification_by_tv = itemView.findViewById(R.id.verification_by_tv);
            status_tv = itemView.findViewById(R.id.status_tv);
            view_card = itemView.findViewById(R.id.view_card);
            image_view_for_crop2_admin = itemView.findViewById(R.id.image_view_for_crop2_admin);
            image_view_for_crop_admin = itemView.findViewById(R.id.image_view_for_crop_admin);
            image_view_for_crop3_admin = itemView.findViewById(R.id.image_view_for_crop3_admin);
            image_view_for_crop4_admin = itemView.findViewById(R.id.image_view_for_crop4_admin);
            additionalLayout = itemView.findViewById(R.id.additionalLayout);
            downArrowImage = itemView.findViewById(R.id.downArrowImage);
            additionalArrowLayout = itemView.findViewById(R.id.additionalArrowLayout);
            viewOnMap = itemView.findViewById(R.id.viewOnMap);
            history_tv = itemView.findViewById(R.id.history_tv);
            no_history_tv = itemView.findViewById(R.id.no_history_tv);
            feedback_textview = itemView.findViewById(R.id.feedback_textview);
            near_by_landmark_textview = itemView.findViewById(R.id.near_by_landmark_textview);
        }
    }
}
