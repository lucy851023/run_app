package andbas.ui3_0628;

public class MyUsers {
    public String name;
    public String sex;
    public int level;
    public int exp;

    public MyUsers(){

    }


    public MyUsers(String name, String sex, int level, int exp) {
        this.name = name;
        this.sex = sex;
        this.level = level;
        this.exp = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
