package Dao;

import java.sql.*;
import java.util.ArrayList;
import Dao.JDBCUtils;

import Model.Author;

public class AuthorDao {
    private static AuthorDao authordao = null;
    private static final String AUTHOR_TABLE_NAME = "Author";
    private Statement stmt;

    private AuthorDao() {}

    public static AuthorDao getAuthorDao()
    {
        if(authordao == null)
        {
            authordao = new AuthorDao();
        }
        return authordao;
    }

    private static Author ORM(ResultSet res) throws SQLException
    {
        return new Author(res.getInt("authorID"),res.getString("name"),res.getInt("age"),res.getString("country"));
    }


    public ArrayList<Author> getAuthorsByName(String name) throws SQLException
    {
        String sql = "SELECT * FROM " + AUTHOR_TABLE_NAME + " WHERE name = '" + name +"'";
        System.out.println("SQL语句" + sql);
        stmt = JDBCUtils.getStatement();
        ResultSet res = stmt.executeQuery(sql);
        ArrayList<Author> authors = new ArrayList<Author>();
        while(res.next())
        {
            Author author = ORM(res);
            authors.add(author);
        }
        return authors;
    }

    public ArrayList<Author> getAllAuthors() throws SQLException
    {
        String sql = "SELECT * FROM " + AUTHOR_TABLE_NAME;
        stmt = JDBCUtils.getStatement();
        ResultSet res = stmt.executeQuery(sql);
        ArrayList<Author> authors = new ArrayList<Author>();
        while(res.next())
        {
            Author author = ORM(res);
            authors.add(author);
        }
        return authors;
    }

    public void addAuthor(Author author) throws SQLException
    {
        String sql = "INSERT INTO " + AUTHOR_TABLE_NAME
                + " values(" + author.getAuthorID() +",'" + author.getName()
                + "'," + author.getAge() + ",'" + author.getCountry()
                + "')";
        System.out.println("插入作者的SQL语句" + sql);
        stmt = JDBCUtils.getStatement();
        stmt.executeUpdate(sql);
    }

    public Author getAuthorByAuthorID(int authorID) throws SQLException
    {
        String sql = "SELECT * FROM " + AUTHOR_TABLE_NAME + " WHERE authorID = " + authorID + "";
        stmt = JDBCUtils.getStatement();
        ResultSet res = stmt.executeQuery(sql);
        if(res.next())
            return ORM(res);
        return null;
    }


    public void updateAuthor(Author newAuthor) throws SQLException
    {
        String sql = "UPDATE " + AUTHOR_TABLE_NAME  + " SET "
                + "name = '" + newAuthor.getName() + "',"
                + "age = " + newAuthor.getAge() + ","
                + "country = '" + newAuthor.getCountry() + "' WHERE authorID = " + newAuthor.getAuthorID();
        System.out.println("更新作者语句" + sql);
        stmt = JDBCUtils.getStatement();
        stmt.executeUpdate(sql);
    }
}
