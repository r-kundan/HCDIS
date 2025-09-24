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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.BuildConfig;
import com.app.harcdis.R;
import com.app.harcdis.adminRole.VideoScreen;
import com.app.harcdis.adminRole.model.SurveyHistoryModel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class SurveyHistoryAdapter extends RecyclerView.Adapter<SurveyHistoryAdapter.SurveyHistoryHolder> {
    Context context;
    ArrayList<SurveyHistoryModel> SurveyHistoryModelList;

    public SurveyHistoryAdapter(Context context, ArrayList<SurveyHistoryModel> SurveyHistoryModelList) {
        this.context = context;
        this.SurveyHistoryModelList = SurveyHistoryModelList;
    }

    @NonNull
    @Override
    public SurveyHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.survey_history_card_design, parent, false);
        return new SurveyHistoryHolder(view);
    }


    public void filterList(ArrayList<SurveyHistoryModel> filterList) {
        SurveyHistoryModelList = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyHistoryHolder holder, int position) {
        SurveyHistoryModel SurveyHistoryModel = SurveyHistoryModelList.get(position);

        holder.verification_by_tv_h.setText(SurveyHistoryModel.getVerifiedBy());
        holder.feedback_h.setText(SurveyHistoryModel.getFeedback());
        holder.landmarks_h.setText(SurveyHistoryModel.getNearByLandMark());
        holder.remarks_h.setText(SurveyHistoryModel.getRemarks());

        if (!Objects.equals("null", SurveyHistoryModel.getUploadimage())) {
            byte[] decodedString = Base64.decode(SurveyHistoryModel.getUploadimage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image_view_for_crop_admin_h.setImageBitmap(decodedByte);

        }
        //Image One
        if (!Objects.equals("null", SurveyHistoryModel.getUploadimage1())) {
            byte[] decodedString2 = Base64.decode(SurveyHistoryModel.getUploadimage1(), Base64.DEFAULT);
            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
            holder.image_view_for_crop2_admin_h.setImageBitmap(decodedByte2);
        }
        if (!Objects.equals("null", SurveyHistoryModel.getUploadimage2())) {
            byte[] decodedString3 = Base64.decode(SurveyHistoryModel.getUploadimage2(), Base64.DEFAULT);
            Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
            holder.image_view_for_crop3_admin_h.setImageBitmap(decodedByte3);
        }
        //Image One
        if (!Objects.equals("null", SurveyHistoryModel.getUploadimage3())) {
            byte[] decodedString4 = Base64.decode(SurveyHistoryModel.getUploadimage2(), Base64.DEFAULT);
            Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
            holder.image_view_for_crop4_admin_h.setImageBitmap(decodedByte4);
        }

        holder.videoRelativeLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoScreen.class);
            intent.putExtra("video_url",  SurveyHistoryModel.getUploadVideo());
            context.startActivity(intent);
        });


        try {
            String pickup_date = SurveyHistoryModel.getVerificationDate();


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            Date objDate = dateFormat.parse(pickup_date);
            SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm a , MMM dd,yyyy");
            String converted_Date = formatOut.format(objDate);

            holder.verification_date_tv_h.setText(converted_Date);

            if(BuildConfig.DEBUG) {
                Log.i("pickup_date", pickup_date);
                Log.i("converted_Date", converted_Date);
            }
        } catch (Exception e) {
            holder.verification_date_tv_h.setText(SurveyHistoryModel.getVerificationDate());
        }

    }

    @Override
    public int getItemCount() {
        return SurveyHistoryModelList.size();
    }

    public class SurveyHistoryHolder extends RecyclerView.ViewHolder {
        
        ImageView image_view_for_crop_admin_h, image_view_for_crop2_admin_h, image_view_for_crop3_admin_h, image_view_for_crop4_admin_h;
        TextView verification_date_tv_h, verification_by_tv_h, feedback_h, landmarks_h, remarks_h;
        RelativeLayout videoRelativeLayout;

        public SurveyHistoryHolder(@NonNull View itemView) {
            super(itemView);
            videoRelativeLayout = itemView.findViewById(R.id.videoRelativeLayout);
            verification_date_tv_h = itemView.findViewById(R.id.verification_date_tv_h);
            verification_by_tv_h = itemView.findViewById(R.id.verification_by_tv_h);
            feedback_h = itemView.findViewById(R.id.feedback_h);
            landmarks_h = itemView.findViewById(R.id.landmarks_h);
            remarks_h = itemView.findViewById(R.id.remarks_h);
            image_view_for_crop_admin_h = itemView.findViewById(R.id.image_view_for_crop_admin_h);
            image_view_for_crop2_admin_h = itemView.findViewById(R.id.image_view_for_crop2_admin_h);
            image_view_for_crop3_admin_h = itemView.findViewById(R.id.image_view_for_crop3_admin_h);
            image_view_for_crop4_admin_h = itemView.findViewById(R.id.image_view_for_crop4_admin_h);

        }
    }
}
