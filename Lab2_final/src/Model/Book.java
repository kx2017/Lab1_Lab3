package Model;

public class Book {
    private String ISBN;
    private String title;
    private int authorID;
    private String publisher;
    private String publishDate;
    private double price;

    public Book() {}
    public Book(String ISBN, String title, int authorID, String publisher, String publishDate, double price) {
        this.ISBN = ISBN;
        this.title = title;
        this.authorID = authorID;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.price = price;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String isbn) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book [isbn=" + ISBN + ", title=" + title + ", authorID=" + authorID + ", publisher=" + publisher
                + ", publishDate=" + publishDate + ", price=" + price + "]";
    }
}
