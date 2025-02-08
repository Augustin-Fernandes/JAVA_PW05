package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

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
		throw new RuntimeException("Method is not yet implemented");
	}

	public void addGenre(String name) {
		throw new RuntimeException("Method is not yet implemented");
	}
}
