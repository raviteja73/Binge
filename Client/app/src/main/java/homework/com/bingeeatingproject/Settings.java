package homework.com.bingeeatingproject;

/**
 * Created by svemulapalli on 11/10/16.
 */

public class Settings {

    public int icon;
    public String title;
    public Settings(){
        super();
    }

    public Settings(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
