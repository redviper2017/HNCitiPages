package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnNotificationClickListener;
import hn.techcom.com.hncitipages.Models.Notification;
import hn.techcom.com.hncitipages.Models.ResultViewLikes;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Notification> allNotifications = new ArrayList<>();
    private Context context;
    private OnNotificationClickListener onNotificationClickListener;

    private static final String TAG = "NotificationAdapter";

    public NotificationAdapter(ArrayList<Notification> allNotifications, Context context, OnNotificationClickListener onNotificationClickListener) {
        this.allNotifications = allNotifications;
        this.context = context;
        this.onNotificationClickListener = onNotificationClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = allNotifications.get(position);
        ((NotificationViewHolder) holder).bind(notification);
    }

    @Override
    public int getItemCount() {
        return allNotifications == null ? 0 : allNotifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CircleImageView avatar;
        public MaterialTextView message, time;
        private LinearLayout notificationLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar             = itemView.findViewById(R.id.avatar_post);
            message            = itemView.findViewById(R.id.message_post);
            time               = itemView.findViewById(R.id.time_post);
            notificationLayout = itemView.findViewById(R.id.layout_notification_post);

            notificationLayout.setOnClickListener(this);
        }

        void bind(Notification notification){
            String fromUserFullname = notification.getNotificationUser().getFullName();
            String fromUserMessage = notification.getMessage();
            String fromUserHnid = notification.getNotificationUser().getHnid();
            String fromUserImage = notification.getNotificationUser().getProfileImgThumbnail();
            String fromUserTime = notification.getDate();
            String messageString = "<b>" + fromUserFullname + "</b> " + fromUserMessage.toLowerCase();

            time.setText(fromUserTime);
            message.setText(Html.fromHtml(messageString));
            Glide.with(context).load(fromUserImage).centerCrop().into(avatar);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.layout_notification_post) {
                int position = getAbsoluteAdapterPosition();
                String type = allNotifications.get(position).getNotificationType();
                String name = allNotifications.get(position).getNotificationUser().getFullName();
                int postId = allNotifications.get(position).getPost();

                boolean isSupported = allNotifications.get(position).getNotificationUser().getIsSupported();
                if (type.equals("S")) {
                    String id = allNotifications.get(position).getNotificationUser().getHnid();
                    onNotificationClickListener.onNotificationClick(type,name,id,isSupported,postId);
                }else {
                    String id = String.valueOf(allNotifications.get(position).getId());
                    onNotificationClickListener.onNotificationClick(type,name,id,isSupported,postId);
                }
            }
        }
    }
}
