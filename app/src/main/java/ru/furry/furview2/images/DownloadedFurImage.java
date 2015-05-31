package ru.furry.furview2.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;

import ru.furry.furview2.system.Files;

/**
 * Basic class for downloaded but not saved images
 */
public class DownloadedFurImage extends FurImage {
    DateTime downloadedAt;
    BigInteger md5;
    String fileName;
    int fileSize;
    int fileWidth;
    int fileHeight;
    int previewWidth;
    int previewHeight;
    String rootPath;

    String filePath;
    String previewPath;
    Bitmap bImage = null;

    final int PREVIEW_QUALITY = 75;

    public DownloadedFurImage(
            String searchQuery,
            String description,
            int score,
            Rating rating,
            String fileUrl,
            String fileExt,
            String pageUrl,
            String author,
            DateTime createdAt,
            List<String> sources,
            List<String> tags,
            List<String> artists,
            DateTime downloadedAt,
            BigInteger md5,
            String rootPath,
            String fileName,
            int fileSize,
            int fileWidth,
            int fileHeight,
            int previewWidth,
            int previewHeight
    ) {
        super(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists);
                this.downloadedAt = downloadedAt;
        this.md5 = md5;
        this.rootPath = rootPath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.previewPath = String.format("%s/%s/%s", rootPath, Files.THUMBS, fileName);
        this.filePath = String.format("%s/%s/%s", rootPath, Files.IMAGES, fileName);
    }


    public DateTime getDownloadedAt() {
        return downloadedAt;
    }

    public BigInteger getMd5() {
        return md5;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getRootPath() {
        return rootPath;
    }

    public int getFileWidth() {
        return fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public Bitmap getImage() {
        if (bImage == null)
            this.bImage = BitmapFactory.decodeFile(filePath);
        return bImage;
    }

    public Bitmap getPreview() throws FileNotFoundException {
        File file = new File(previewPath);
        if (file.exists()) {
            Bitmap preview = BitmapFactory.decodeFile(previewPath);
            if (Math.abs(preview.getWidth() - previewWidth) + Math.abs(preview.getHeight() - previewHeight) > 10) {
                // if this is true then preview size is changed and it has been to be resized again
                return makePreview();
            } else {
                return preview;
            }
        }
        else {
            return makePreview();
        }
    }

    private Bitmap makePreview() throws FileNotFoundException {
        Bitmap bitmap = getImage();
        double hRatio = ((double)bitmap.getHeight()) / previewHeight;
        double wRatio = ((double)bitmap.getWidth()) / previewWidth;
        double coefficient = (hRatio > wRatio) ? hRatio : wRatio;
        Bitmap preview = Bitmap.createScaledBitmap(bitmap, (int)Math.floor(bitmap.getWidth() / coefficient), (int)Math.floor(bitmap.getHeight() / coefficient), true);
        preview.compress(Bitmap.CompressFormat.JPEG, PREVIEW_QUALITY, new FileOutputStream(previewPath));
        return preview;
    }
}
