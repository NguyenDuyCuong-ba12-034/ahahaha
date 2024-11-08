package vn.edu.usth.new_flick_image.Model;

public class SlideItem {
    private String title;
    private String content;

    public SlideItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
