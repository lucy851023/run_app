package andbas.ui3_0628;

public class Players {
    public String email;
    public String ready;

    public Players(String email, String ready) {
        this.email = email;
        this.ready = ready;
    }

    public Players() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }
}
