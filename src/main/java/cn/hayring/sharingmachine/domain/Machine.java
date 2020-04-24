package cn.hayring.sharingmachine.domain;

public class Machine extends BaseDomain {

    public static final int MAINTENANCE = 2;

    public static final int RUN = 1;

    public static final int SUSPEND = 0;

    public static final int SHUTDOWN = -1;


    private Integer id;

    private String address;

    private Integer status;

    private Double longitude;

    private Double latitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
