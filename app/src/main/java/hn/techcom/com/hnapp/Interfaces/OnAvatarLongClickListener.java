package hn.techcom.com.hnapp.Interfaces;

public interface OnAvatarLongClickListener {
    void onAvatarLongClick(
            String hnid,
            String name,
            String username,
            String location,
            String thumbnail,
            int supporterCount,
            int supportingCount,
            int postCount);
}
