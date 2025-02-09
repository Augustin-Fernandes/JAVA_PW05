package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import java.sql.PreparedStatement;



import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	
	
	private DataSource getDataSource() {
		return DataSourceFactory.getDataSource();
	}

	public List<Genre> listGenres() {
		

	    List<Genre> listOfGenre = new ArrayList<>();
	    try (Connection connection = getDataSource().getConnection()) {
	    	try (Statement statement = connection.createStatement()) {
	    		 try (ResultSet results = statement.executeQuery("select * from genre")) {
	    			 while (results.next()) {
	    				 Genre genre = new Genre(results.getInt("idgenre"),results.getString("name"));
	    				 listOfGenre.add(genre);
	    			 }
	    		 }
	    	}
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return listOfGenre;
	    
	}

	public Genre getGenre(String name) {
		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM genre WHERE name = ?")) {
				statement.setString(1, name);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						return new Genre(results.getInt("idgenre"),results.getString("name"));
					}
				}
			}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return null;
	}

	public Genre addGenre(String name) {
		try (Connection connection = getDataSource().getConnection()) {
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, name);
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
				if (ids.next()) {
					return new Genre(ids.getInt(1),name);
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
