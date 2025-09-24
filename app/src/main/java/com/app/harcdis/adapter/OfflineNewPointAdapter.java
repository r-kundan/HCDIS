package com.app.harcdis.adapter;

import android.content.Context;
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
import com.app.harcdis.click_interface.ClickInterfaceNew;
import com.app.harcdis.offline_storage.entites.NewPointRecord;


import java.util.ArrayList;

public class OfflineNewPointAdapter extends RecyclerView.Adapter<OfflineNewPointAdapter.NewPointHolder> {
    Context context;
    ArrayList<NewPointRecord> newPointRecordArrayList;
    ClickInterfaceNew clickInterfaceNew;

    public OfflineNewPointAdapter(Context context, ArrayList<NewPointRecord> newPointRecordArrayList, ClickInterfaceNew clickInterface) {
        this.context = context;
        this.newPointRecordArrayList = newPointRecordArrayList;
        this.clickInterfaceNew = clickInterface;
    }

    @NonNull
    @Override
    public NewPointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offline_new_point_layout, parent, false);
        return new NewPointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPointHolder holder, int position) {
        NewPointRecord newPointRecord = newPointRecordArrayList.get(position);
        holder.n_d_name_textview.setText("District - " + newPointRecord.getN_d_name());
        holder.n_t_name_textview.setText("Tehsil - " + newPointRecord.getN_t_name());
        holder.n_v_name_textview.setText("Village - " + newPointRecord.getN_v_name());
        holder.khasra_muraba_tv.setText("Muraba//Khasra No - " + newPointRecord.getN_murr_no() + "//" + newPointRecord.getN_khas_no());
        holder.feedback_textview.setText("Feedback - " + newPointRecord.getFeedback());

        holder.remarks_textview.setText("Remarks - " + newPointRecord.getRemarks());

        byte[] decodedString = Base64.decode(newPointRecord.getImage1(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.image1.setImageBitmap(decodedByte);

        //Image One
        byte[] decodedString2 = Base64.decode(newPointRecord.getImage2(), Base64.DEFAULT);
        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
        holder.image2.setImageBitmap(decodedByte2);

        //Image One
        byte[] decodedString3 = Base64.decode(newPointRecord.getImage3(), Base64.DEFAULT);
        Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
        holder.image3.setImageBitmap(decodedByte3);

        //Image One
        byte[] decodedString4 = Base64.decode(newPointRecord.getImage4(), Base64.DEFAULT);
        Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
        holder.image4.setImageBitmap(decodedByte4);

        holder.deleteRecordButton.setOnClickListener(view -> {
            clickInterfaceNew.onClickHandle(position);
        });


    }

    @Override
    public int getItemCount() {
        return newPointRecordArrayList.size();
    }

    public class NewPointHolder extends RecyclerView.ViewHolder {
        TextView n_d_name_textview;
        TextView n_t_name_textview;
        TextView n_v_name_textview;
        TextView khasra_muraba_tv;
        TextView feedback_textview;
        TextView remarks_textview;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
        ImageView deleteRecordButton;

        public NewPointHolder(@NonNull View itemView) {
            super(itemView);
            n_d_name_textview = itemView.findViewById(R.id.n_d_name_textview);
            n_t_name_textview = itemView.findViewById(R.id.n_t_name_textview);
            n_v_name_textview = itemView.findViewById(R.id.n_v_name_textview);
            khasra_muraba_tv = itemView.findViewById(R.id.khasra_muraba_tv);
            feedback_textview = itemView.findViewById(R.id.feedback_textview);
            remarks_textview = itemView.findViewById(R.id.remarks_textview);
            image1 = itemView.findViewById(R.id.image1);
            image4 = itemView.findViewById(R.id.image4);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            deleteRecordButton = itemView.findViewById(R.id.deleteRecordButton);
        }
    }
}
