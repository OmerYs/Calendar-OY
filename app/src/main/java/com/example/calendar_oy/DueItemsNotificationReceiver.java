package com.example.calendar_oy;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DueItemsNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "due_items_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.calendar_oy.ACTION_DUE_ITEM_NOTIFICATION")) {
            String itemDescription = intent.getStringExtra("itemDescription");
            long dueDate = intent.getLongExtra("due_date", System.currentTimeMillis());
            boolean itemCompleted = intent.getBooleanExtra("item_completed", false);

            if (!itemCompleted) {
                createNotificationChannel(context);
                sendNotification(context, itemDescription, dueDate);
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Due Item Notification";
            String description = "Notification for due items";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String itemDescription, long dueDate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "due_item_notifications")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Item Due")
                .setContentText(itemDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Due Items";
            String description = "Notifications for due items";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("due_item_notifications", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) dueDate, builder.build());

        //Toast message for debugging purposes
        Toast.makeText(context, "Notification sent for: " + itemDescription, Toast.LENGTH_SHORT).show();
    }
}
