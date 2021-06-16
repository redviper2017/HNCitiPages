package hn.techcom.com.hnapp.Interfaces;

import android.widget.ImageButton;
import android.widget.LinearLayout;

public interface OnReplyClickListener {
    void onReplyClick(int commentId, String reply, int position, LinearLayout replyLayout, ImageButton replyButton);
}
