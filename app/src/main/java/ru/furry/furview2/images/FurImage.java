package ru.furry.furview2.images;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.system.Utils;

import static ru.furry.furview2.system.Utils.joinList;

/**
 * Basic class for downloaded images
 */

public class FurImage extends RemoteFurImage implements Parcelable {
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "q#za0"; // just random string

    private static String codeDateTime(DateTime date) {
        return DATETIME_FORMAT.print(date);
    }

    private static DateTime decodeDateTime(String sDate) {
        return DATETIME_FORMAT.parseDateTime(sDate);
    }

    private static String codeRating(Rating rating) {
        String sRating;
        switch (rating) {
            case SAFE:
                sRating = "s";
                break;
            case QUESTIONABLE:
                sRating = "q";
                break;
            case EXPLICIT:
                sRating = "e";
                break;
            default:
                sRating = "na";
                break;
        }
        return sRating;
    }

    private static Rating decodeRating(String sRating) {
        Rating rating;
        switch (sRating) {
            case "s":
                rating = Rating.SAFE;
                break;
            case "q":
                rating = Rating.QUESTIONABLE;
                break;
            case "e":
                rating = Rating.EXPLICIT;
                break;
            default:
                rating = Rating.NA;
                break;
        }
        return rating;
    }


    private String author;
    private DateTime createdAt;
    private List<String> sources;
    private List<String> tags;
    private List<String> artists;

    private DateTime downloadedAt;
    private int score;
    private BigInteger md5;
    private String fileName;
    private int fileSize;
    private int fileWidth;
    private int fileHeight;

    @Deprecated
    private int previewWidth;
    @Deprecated
    private int previewHeight;
    @Deprecated
    private String rootPath;

    private long id;
    private List<String> localTags;
    private Integer localScore;

    private String filePath;

    public FurImage(String searchQuery, String description, int score, Rating rating, String fileUrl, String fileExt, String pageUrl, String author, DateTime createdAt, List<String> sources, List<String> tags, List<String> artists, DateTime downloadedAt, BigInteger md5, String fileName, int fileSize, int fileWidth, int fileHeight, int previewWidth, int previewHeight, String rootPath, String previewUrl) {
        super(searchQuery, description, rating, fileUrl, fileExt, pageUrl, previewUrl);
        this.author = author;
        this.createdAt = createdAt;
        this.score = score;
        this.sources = sources;
        this.tags = tags;
        this.artists = artists;
        this.downloadedAt = downloadedAt;
        this.md5 = md5;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.rootPath = rootPath;
        this.id = Utils.reduceMD5(md5);
    }

    @Override
    public String toString() {
        return super.toString() + (new StringBuilder().append("\ngetAuthor ")
                .append(this.getAuthor()).append("\n getCreatedAt ")
                .append(this.getCreatedAt()).append("\ngetDownloadedAt ")
                .append(this.getDownloadedAt()).append("\ngetArtists ")
                .append(this.getArtists()).append("\ngetSources ")
                .append(this.getSources()).append("\ngetScore ")
                .append(this.getScore()).append("\ngetRating ")
                .append(this.getRating()).append("\ngetTags ")
                .append(this.getTags()).append("\ngetFileName ")
                .append(this.getFileName()).append("\ngetFileHeight ")
                .append(this.getFileHeight()).append("\ngetFileWidth ")
                .append(this.getFileWidth()).append("\ngetFileSize ")
                .append(this.getFileSize()).append("\ngetMd5 ")
                .append(this.getMd5().toString(36)).append("\ngetFilePath ")
                .append(this.getFilePath()).append("\ngetID ")
                .append(this.getID()).append("\ngetLocalScore ")
                .append(this.getLocalScore()).append("\ngetLocalTags ")
                .append(this.getLocalTags()).append("\n")
                .toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        if (this.getMd5().equals(((FurImage) obj).getMd5())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getMd5().intValue();
    }

    public String getAuthor() {
        return author;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getSources() {
        return sources;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getArtists() {
        return artists;
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

    public int getFileWidth() {
        return fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    @Deprecated
    public int getPreviewWidth() {
        return previewWidth;
    }

    @Deprecated
    public int getPreviewHeight() {
        return previewHeight;
    }

    public String getRootPath() {
        return rootPath;
    }

    public List<String> getLocalTags() {
        return localTags;
    }

    public FurImage setLocalTags(List<String> localTags) {
        this.localTags = localTags;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Integer getLocalScore() {
        return localScore;
    }

    public FurImage setLocalScore(Integer localScore) {
        this.localScore = localScore;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public FurImage setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public long getID() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.getSearchQuery(),
                this.getDescription(),
                Integer.toString(this.getScore()),
                Integer.toString(this.getLocalScore()),
                codeRating(this.getRating()),
                Integer.toString(this.getLocalScore()),
                this.getFileUrl(),
                this.getFileExt(),
                this.getPageUrl(),
                this.getAuthor(),
                ((this.getArtists() != null) && (this.getArtists().size() > 0)) ? joinList(this.getArtists(), SEPARATOR) : "",
                codeDateTime(this.getCreatedAt()),
                ((this.getSources() != null) && (this.getSources().size() > 0)) ? joinList(this.getSources(), SEPARATOR) : "",
                codeDateTime(this.getDownloadedAt()),
                this.getMd5().toString(),
                this.getFileName(),
                Integer.toString(this.getFileSize()),
                Integer.toString(this.getFileWidth()),
                Integer.toString(this.getFileHeight()),
                this.getFilePath(),
                this.getPreviewUrl(),
                ((this.getTags() != null) && (this.getTags().size() > 0)) ? joinList(this.getTags(), SEPARATOR) : "",
                ((this.getLocalTags() != null) && (this.getLocalTags().size() > 0)) ? joinList(this.getLocalTags(), SEPARATOR) : "",
                this.getPreviewUrl()
        });
    }

    public static final Parcelable.Creator<FurImage> CREATOR = new Parcelable.Creator<FurImage>() {
        public FurImage createFromParcel(Parcel in) {
            return FurImage.createFromParcel(in);
        }

        public FurImage[] newArray(int size) {
            return new FurImage[size];
        }
    };


    public static FurImage createFromParcel(Parcel source) {
        String[] data = new String[24];
        source.readStringArray(data);
        return (new FurImageBuilder()
                .setSearchQuery(data[0])
                .setDescription(data[1])
                .setScore(Integer.parseInt(data[2]))
                .setLocalScore(Integer.parseInt(data[3]))
                .setRating(decodeRating(data[4]))
                .setLocalScore(Integer.parseInt(data[5]))
                .setFileUrl(data[6])
                .setFileExt(data[7])
                .setPageUrl(data[8])
                .setAuthor(data[9])
                .setArtists(Arrays.asList(data[10].split(SEPARATOR)))
                .setCreatedAt(decodeDateTime(data[11]))
                .setSources(Arrays.asList(data[12].split(SEPARATOR)))
                .setDownloadedAt(decodeDateTime(data[13]))
                .setMd5(new BigInteger(data[14]))
                .setFileName(data[15])
                .setFileSize(Integer.parseInt(data[16]))
                .setFileWidth(Integer.parseInt(data[17]))
                .setFileHeight(Integer.parseInt(data[18]))
                .setFilePath(data[19])
                .setPreviewUrl(data[20])
                .setTags(Arrays.asList(data[21].split(SEPARATOR)))
                .setLocalTags(Arrays.asList(data[22].split(SEPARATOR)))
                .setPreviewUrl(data[23])
                .createFurImage());
    }
}
