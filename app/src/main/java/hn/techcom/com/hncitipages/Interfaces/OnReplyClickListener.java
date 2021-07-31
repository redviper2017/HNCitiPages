package hn.techcom.com.hncitipages.Interfaces;

import android.widget.ImageButton;
import android.widget.LinearLayout;

public interface OnReplyClickListener {
    void onReplyClick(int commentId, String reply, int position, LinearLayout replyLayout, ImageButton replyButton);
}
