package Action;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.opensymphony.xwork2.*;

import Model.Author;
import Model.Book;
import Dao.AuthorDao;
import Dao.BookDao;

public class BookAction extends ActionSupport{
    private ArrayList<Author> author;
    private ArrayList<Book> book;
    private String ISBN;
    private String[] ISBNs;
    private int authorID;
    private int authorCount;
    private int lastAuthorID;
    private String lastInputPage;
    private String authorName;
    private Book newB;
    private Author newA;
    private BookDao bookdao = BookDao.getBookDao();
    private AuthorDao authordao = AuthorDao.getAuthorDao();
    private Map<String, Object> session;
    private static final long s = 1L;
    private int year;
    private int month;
    private int day;
    private boolean changed;

    public ArrayList<Book> getBook(){
        return book;
    }

    public void setBook(ArrayList<Book> book){
        this.book = book;
    }

    public void updateBook() throws SQLException{
        System.out.println(ISBN);
        newB = bookdao.getBookByISBN(ISBN);
        String [] dateStr = newB.getPublishDate().split("-");
        setYear(Integer.parseInt(dateStr[0]));
        setMonth(Integer.parseInt(dateStr[1]));
        setDate(Integer.parseInt(dateStr[2]));
        System.out.println(Arrays.toString(dateStr));
        System.out.println(newB);
    }

    public void authorUpdate() throws SQLException{
        System.out.println(authorID);
        newA = authordao.getAuthorByAuthorID(authorID);
        System.out.println(newA);
    }

    public void commitBook() throws SQLException{
        System.out.println(newB);
        bookdao.updateBook(newB);
    }

    public void commitAuthor() throws SQLException{
        System.out.println(newA);
        authordao.updateAuthor(newA);
    }

    public void deleteBook() throws SQLException{
        bookdao.deleteBook(ISBN);
    }

    public void deleteBooks() throws SQLException{
        if(ISBNs != null){
            bookdao.deleteBooks(ISBNs);
        }
    }

    public void ListAllBooks() throws SQLException{
        book = bookdao.getAllBooks();
    }

    public void listAllAuthors() throws SQLException{
        author = authordao.getAllAuthors();
    }

    public String confirmAuthor() throws SQLException {
        book = bookdao.getBooksByAuthorID(lastAuthorID);
        if(book.size() == 0){
            return "no book";
        }else{
            return SUCCESS;
        }
    }

    public String searchBooks() throws SQLException {
        author = authordao.getAuthorsByName(authorName);
        authorCount = author.size();
        if(authorCount == 0){
            return "no such book";
        }else if(authorCount > 1){
            return "more than one";
        }else{
            lastAuthorID = author.get(0).getAuthorID();
            return confirmAuthor();
        }
    }

    public String getISBN(){
        return ISBN;
    }

    public void setISBN(String ISBN){
        this.ISBN = ISBN;
    }

    public String[] getISBNs(){
        return ISBNs;
    }

    public void setISBNs(String[] ISBNs){
        this.ISBNs = ISBNs;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ArrayList<Author> getAuthors() {
        return author;
    }

    public void setAuthors(ArrayList<Author> author) {
        this.author = author;
    }

    public int getLastAuthorID() {
        return lastAuthorID;
    }

    public void setLastAuthorID(int finalAuthorID) {
        this.lastAuthorID = lastAuthorID;
    }

    public int getAuthorCount() {
        return authorCount;
    }

    public void setSameAuthorCnt(int authorCount) {
        this.authorCount = authorCount;
    }

    public Book getNewBook() {
        return newB;
    }

    public void setNewBook(Book newB) {
        this.newB = newB;
    }

    public Author getNewAuthor() {
        return newA;
    }

    public void setNewAuthor(Author newA) {
        this.newA = newA;
    }

    public String addBook() throws SQLException {
        System.out.println(",,,");
        try {
            if (newA != null) {
                session = ActionContext.getContext().getApplication();
                setNewBook((Book) session.get("newBook"));
                newA.setAuthorID(getAuthorID());
                System.out.println("要加入的AUTHOR" + newA);
                authordao.addAuthor(newA);
            }
            if (newB != null)
                bookdao.addBook(newB);
        } catch (MySQLIntegrityConstraintViolationException e) {
            System.out.println("我要的错误" + e);
            session = ActionContext.getContext().getApplication();
            session.put("newBook", newB);
            return "noAuthor";
        }
        return SUCCESS;
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    @Override
    public void validate() {
        System.out.println("newAuthor is " + newA);
        System.out.println("newBook is " + newB);


        System.out.println("当前" + (changed?"是":"不是") + "修改中....");
        if (newA != null) {
            lastInputPage = "addAuthorForm";
            if (newA.getName() == null || newA.getName().length() == 0) {
                addFieldError("newAuthor.name", "姓名不能为空！");
            }
            if (newA.getCountry() == null || newA.getCountry().length() == 0) {
                addFieldError("newAuthor.country", "国家不能为空！");
            }

            if (newA.getAge() <= 0 || newA.getAge() > 150)
            {
                addFieldError("newAuthor.age", "请输入合法的年龄！");
            }
        }

        if (newB != null)
        {
            lastInputPage = "addBookForm";
            if (newB.getISBN() == null || newB.getISBN().length() == 0) {
                addFieldError("newBook.isbn", "ISBN号不能为空！");
            }
            if (newB.getTitle() == null || newB.getTitle().length() == 0) {
                addFieldError("newBook.title", "书名不能为空！");
            }
            if (newB.getPublisher() == null || newB.getPublisher().length() == 0) {
                addFieldError("newBook.publisher", "出版社不能为空！");
            }
            try {
                if (changed == false && bookdao.isISBNExist(newB.getISBN())) {
                    addFieldError("newBook.isbn", "ISBN号已存在！");
                }
                if (newB.getPrice() <= 0) {
                    addFieldError("newBook.price", "价格必须为正数！");
                }

                if (newB.getAuthorID() <= 0) {
                    addFieldError("newBook.authorID", "作者编号必须为正数！");
                }

                String dateStr = getYear() + "-" + getMonth() + "-" + getDay();

                if (!isValidDate(dateStr) || getYear() > 9999 ) {
                    addFieldError("day", "日期不合法！");
                }
                else {
                    newB.setPublishDate(dateStr);
                }

                setAuthorID(newB.getAuthorID());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDate(int day) {
        this.day = day;
    }

    public static void main(String[] args) {
        System.out.println(isValidDate("1900-2-29"));
    }

    public String getLastInputPage() {
        return lastInputPage;
    }

    public void setLastInputPage(String lastInputPage) {
        this.lastInputPage = lastInputPage;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }
}
