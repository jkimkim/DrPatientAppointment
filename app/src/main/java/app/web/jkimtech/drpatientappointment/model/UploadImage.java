package app.web.jkimtech.drpatientappointment.model;

public class UploadImage {
    private String mName;
    private String mImageUrl;

    public UploadImage() {
        // Empty constructor needed for Firebase
    }

    public UploadImage(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}

