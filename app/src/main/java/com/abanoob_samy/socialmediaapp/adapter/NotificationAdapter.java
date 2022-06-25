package com.abanoob_samy.socialmediaapp.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.NotificationItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.NotificationModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private Activity context;
    private List<NotificationModel> notificationModels;

    public NotificationAdapter(Activity context, List<NotificationModel> notificationModels) {
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationHolder(LayoutInflater.from(context)
                .inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {

        NotificationModel notificationModel = notificationModels.get(position);

        holder.binding.tvNotification.setText(notificationModel.getNotification());

        String dateInMin;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            long millis = notificationModel.getTimeStamp().toInstant().toEpochMilli();

            dateInMin = DateUtils.getRelativeTimeSpanString(millis,
                    System.currentTimeMillis(),
                    60000,
                    DateUtils.FORMAT_ABBREV_ALL).toString();
        }
        else {

            long millis = notificationModel.getTimeStamp().getTime();

            dateInMin = DateUtils.getRelativeTimeSpanString(millis,
                    System.currentTimeMillis(),
                    60000,
                    DateUtils.FORMAT_ABBREV_ALL).toString();

            dateInMin.replace("+", "");
        }

        System.out.println(dateInMin);

        holder.binding.tvTime.setText(dateInMin);
    }

    @Override
    public int getItemCount() {

        if (notificationModels != null) {
            return notificationModels.size();
        } else
            return 0;
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {

        private NotificationItemBinding binding;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationItemBinding.bind(itemView);
        }
    }

}
