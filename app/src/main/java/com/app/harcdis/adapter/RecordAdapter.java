package com.app.harcdis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.harcdis.R;
import com.app.harcdis.model.SurveyModel;
import com.app.harcdis.screens.SimpleImageScreen;


import java.util.ArrayList;
import java.util.Objects;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {

    Context context;
    ArrayList<SurveyModel> surveyModelArrayList;

    public RecordAdapter(Context context, ArrayList<SurveyModel> surveyModelArrayList) {
        this.context = context;
        this.surveyModelArrayList = surveyModelArrayList;


    }


    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tcp_history_card, parent, false);
        return new RecordHolder(view);

    }


    public void filterList(ArrayList<SurveyModel> filterList) {
        surveyModelArrayList = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {

        SurveyModel surveyModel = surveyModelArrayList.get(position);

        holder.history_gisId.setText(context.getString(R.string.id_) + surveyModel.getGisId());
        holder.history_n_d_name.setText(context.getString(R.string.district_) + surveyModel.getN_d_name());
        holder.history_n_t_name.setText(context.getString(R.string.tehsil_) + surveyModel.getN_t_name());
        holder.history_n_v_name.setText(context.getString(R.string.village_) + surveyModel.getN_v_name());
        holder.history_khasra_murba_number.setText(context.getString(R.string.murabba_khasra_) + surveyModel.getN_murr_no() + "//" + surveyModel.getN_khas_no());
        holder.history_status.setText(context.getString(R.string.verified_) + surveyModel.getVerified());
        holder.history_verified_by.setText(context.getString(R.string.verified_by_) + surveyModel.getUser_name());

        holder.history_date.setText(context.getString(R.string.date) + surveyModel.getVerificationDate().split("T")[0]);


        if (!Objects.equals("null", surveyModel.getUploadimage())) {
            byte[] decodedString = Base64.decode(surveyModel.getUploadimage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.history_image.setImageBitmap(decodedByte);

        }

        //Image One
        if (!Objects.equals("null", surveyModel.getUploadimage1())) {
            byte[] decodedString2 = Base64.decode(surveyModel.getUploadimage1(), Base64.DEFAULT);
            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
            holder.history_image1.setImageBitmap(decodedByte2);
        }

        if (!Objects.equals("null", surveyModel.getUploadimage2())) {
            byte[] decodedString3 = Base64.decode(surveyModel.getUploadimage2(), Base64.DEFAULT);
            Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
            holder.history_image2.setImageBitmap(decodedByte3);
        }
        //Image One
        if (!Objects.equals("null", surveyModel.getUploadimage3())) {
            byte[] decodedString4 = Base64.decode(surveyModel.getUploadimage3(), Base64.DEFAULT);
            Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
            holder.history_image3.setImageBitmap(decodedByte4);
        }

        holder.history_image.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url",  surveyModel.getUploadimage());
            context.startActivity(intent);
        });
        holder.history_image1.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url",  surveyModel.getUploadimage1());
            context.startActivity(intent);
        });
        holder.history_image2.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url",  surveyModel.getUploadimage2());
            context.startActivity(intent);
        });
        holder.history_image3.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimpleImageScreen.class);
            intent.putExtra("image_url",  surveyModel.getUploadimage3());
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return surveyModelArrayList.size();
    }

    public class RecordHolder extends RecyclerView.ViewHolder {

        TextView history_gisId, history_n_d_name, history_n_t_name, history_n_v_name, history_khasra_murba_number, history_verified_by, history_status, history_date;
        ImageView history_image1, history_image2, history_image3, history_image;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            history_gisId = itemView.findViewById(R.id.history_gisId);
            history_n_d_name = itemView.findViewById(R.id.history_n_d_name);
            history_n_t_name = itemView.findViewById(R.id.history_n_t_name);
            history_n_v_name = itemView.findViewById(R.id.history_n_v_name);
            history_khasra_murba_number = itemView.findViewById(R.id.history_khasra_murba_number);
            history_status = itemView.findViewById(R.id.history_status);
            history_verified_by = itemView.findViewById(R.id.history_verified_by);
            history_image1 = itemView.findViewById(R.id.history_image1);
            history_image2 = itemView.findViewById(R.id.history_image2);
            history_image3 = itemView.findViewById(R.id.history_image3);
            history_image = itemView.findViewById(R.id.history_image);
            history_date = itemView.findViewById(R.id.history_date);


        }
    }

}
