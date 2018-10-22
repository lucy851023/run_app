package andbas.ui3_0628;

public class Records {
    public String time;
    public String date;
    public float length;

    public Records(){

    }

    public Records(String time, String date, float length) {
        this.time = time;
        this.date = date;
        this.length = length;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }
}

