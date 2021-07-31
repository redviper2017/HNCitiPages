package hn.techcom.com.hncitipages.Interfaces;

public interface OnAvatarLongClickListener {
    void onAvatarLongClick(
            String hnid,
            String name,
            String username,
            String location,
            String thumbnail,
            int supporterCount,
            int supportingCount,
            int postCount,
            String firstImage);
}
