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
import com.app.harcdis.click_interface.ClickInterfaceVerified;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;


import java.util.ArrayList;

public class OfflineVerifiedPointAdapter extends RecyclerView.Adapter<OfflineVerifiedPointAdapter.OfflineVerifiedPointHolder> {
    Context context;
    ArrayList<VerifiedPointRecord> verifiedPointRecordArrayList;
    ClickInterfaceVerified clickInterfaceVerified;

    public OfflineVerifiedPointAdapter(Context context, ArrayList<VerifiedPointRecord> verifiedPointRecordArrayList, ClickInterfaceVerified clickInterfaceVerified) {
        this.context = context;
        this.verifiedPointRecordArrayList = verifiedPointRecordArrayList;
        this.clickInterfaceVerified = clickInterfaceVerified;

    }

    @NonNull
    @Override
    public OfflineVerifiedPointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offline_new_point_layout, parent, false);
        return new OfflineVerifiedPointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineVerifiedPointHolder holder, int position) {
        VerifiedPointRecord verifiedPointRecord = verifiedPointRecordArrayList.get(position);

        holder.n_d_name_textview.setText("District - " + verifiedPointRecord.getN_d_name());
        holder.n_t_name_textview.setText("Tehsil - " + verifiedPointRecord.getN_t_name());
        holder.n_v_name_textview.setText("Village - " + verifiedPointRecord.getN_v_name());
        holder.khasra_muraba_tv.setText("Muraba//Khasra No - " + verifiedPointRecord.getN_murra_no() + "//" + verifiedPointRecord.getN_khas_no());
        holder.feedback_textview.setText("Feedback - " + verifiedPointRecord.getFeedback());
        holder.remarks_textview.setText("Remarks - " + verifiedPointRecord.getRemarks());


        //Image One


        byte[] decodedString = Base64.decode(verifiedPointRecord.getImage1(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.image1.setImageBitmap(decodedByte);

        //Image One
        byte[] decodedString2 = Base64.decode(verifiedPointRecord.getImage2(), Base64.DEFAULT);
        Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
        holder.image2.setImageBitmap(decodedByte2);

        //Image One
        byte[] decodedString3 = Base64.decode(verifiedPointRecord.getImage3(), Base64.DEFAULT);
        Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
        holder.image3.setImageBitmap(decodedByte3);

        //Image One
        byte[] decodedString4 = Base64.decode(verifiedPointRecord.getImage4(), Base64.DEFAULT);
        Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);
        holder.image4.setImageBitmap(decodedByte4);

        holder.deleteRecordButton.setOnClickListener(view -> {
            clickInterfaceVerified.handleClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return verifiedPointRecordArrayList.size();
    }

    public class OfflineVerifiedPointHolder extends RecyclerView.ViewHolder {
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

        public OfflineVerifiedPointHolder(@NonNull View itemView) {
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
