package fr.isen.java2.db.daos;

import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Date;


import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {
	
	private DataSource getDataSource() {
		return DataSourceFactory.getDataSource();
	}

	public List<Movie> listMovies() {
		List<Movie> listOfMovies = new ArrayList<>();
		
		try (Connection connection = getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet results = statement.executeQuery("SELECT  movie.idmovie, movie.title, movie.release_date, movie.genre_id, genre.name, \n"
						+ "       movie.duration, movie.director, movie.summary FROM movie JOIN genre ON movie.genre_id = genre.idgenre")) {
					while (results.next()) {
						Genre genre = new Genre(results.getInt("genre_id"), results.getString("name"));
						Movie movie = new Movie(results.getInt("idmovie"),
												results.getString("title"),
												results.getDate("release_date").toLocalDate(),
												genre,
												results.getInt("duration"),
												results.getString("director"),
												results.getString("summary"));
						listOfMovies.add(movie);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfMovies;
		
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listOfMoviesByGenre = new ArrayList<>();
		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")) {
				statement.setString(1, genreName);
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						Genre genre = new Genre(results.getInt("genre_id"), results.getString("name"));
						Movie movie = new Movie(results.getInt("idmovie"),
												results.getString("title"),
												results.getDate("release_date").toLocalDate(),
												genre,
												results.getInt("duration"),
												results.getString("director"),
												results.getString("summary"));
						listOfMoviesByGenre.add(movie);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfMoviesByGenre;
	}

	public Movie addMovie(Movie movie) {
		try (Connection connection = getDataSource().getConnection()) {
			String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, movie.getTitle());
	            statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
	            statement.setInt(3, movie.getGenre().getId()); 
	            statement.setInt(4, movie.getDuration());
	            statement.setString(5, movie.getDirector());
	            statement.setString(6, movie.getSummary());
	            statement.executeUpdate();
	            ResultSet ids = statement.getGeneratedKeys();
	            if (ids.next()) {
	                return new Movie(ids.getInt(1), movie.getTitle(), movie.getReleaseDate(), movie.getGenre(), movie.getDuration(), movie.getDirector(), movie.getSummary());
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return null;
	}

}
