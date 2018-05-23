package com.nosuchserver.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * save to local file json bean
 *
 * Created by rere on 18-5-10.
 */
public class LocalSavaDataBean {

    /**
     * ssid :
     * bssidSelect :
     * bssidGroup : [{"ssid":"","bssid":"","recordTime":1234567891011},{"ssid":"","bssid":"","recordTime":1234567891011}]
     * latitude : 23.12
     * longitude : 113.25
     */

    private String ssid;
    private String bssidSelect;
    private double latitude;
    private double longitude;
    private List<BssidGroupBean> bssidGroup;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssidSelect() {
        return bssidSelect;
    }

    public void setBssidSelect(String bssidSelect) {
        this.bssidSelect = bssidSelect;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<BssidGroupBean> getBssidGroup() {
        return bssidGroup;
    }

    public void setBssidGroup(List<BssidGroupBean> bssidGroup) {
        this.bssidGroup = bssidGroup;
    }

    public static class BssidGroupBean {
        /**
         * ssid :
         * bssid :
         * recordTime : 1234567891011
         */

        private String ssid;
        private String bssid;
        private long recordTime;

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getBssid() {
            return bssid;
        }

        public void setBssid(String bssid) {
            this.bssid = bssid;
        }

        public long getRecordTime() {
            return recordTime;
        }

        public void setRecordTime(long recordTime) {
            this.recordTime = recordTime;
        }

        @Override
        public String toString() {
            return "BssidGroupBean{" +
                    "ssid='" + ssid + '\'' +
                    ", bssid='" + bssid + '\'' +
                    ", recordTime=" + recordTime +
                    '}';
        }

        public static void sort(List<BssidGroupBean> list) {
            Collections.sort(list, new Comparator<BssidGroupBean>() {

                @Override
                public int compare(BssidGroupBean o1, BssidGroupBean o2) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (null == o1 && null == o2) {
                        return 0;
                    }

                    if (null == o2) {
                        return 1;
                    } else if (null == o1) {
                        return -1;
                    }

                    // sort recordTime, ssid
                    if (o1.ssid.equals(o2.ssid)) {
                        return (int) Math.signum(o1.getRecordTime() - o2.getRecordTime());
                    } else {
                        return o1.ssid.compareTo(o2.ssid);
                    }
                }
            });
            Collections.reverse(list);
        }
    }

    @Override
    public String toString() {
        return "LocalSavaDataBean{" +
                "ssid='" + ssid + '\'' +
                ", bssidSelect='" + bssidSelect + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bssidGroup=" + bssidGroup +
                '}';
    }
}
