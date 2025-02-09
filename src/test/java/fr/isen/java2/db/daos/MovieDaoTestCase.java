package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListMovies() {
		 MovieDao movieDao = new MovieDao();
		 
		 List<Movie> movies = movieDao.listMovies();

		    // THEN
		    assertThat(movies).hasSize(3); // On s'attend à 3 films

		    assertThat(movies).extracting("id", "title", "releaseDate", "genre.id", "genre.name", "duration", "director", "summary")
		        .containsOnly(
		            tuple(1, "Title 1", LocalDate.of(2015, 11, 26), 1, "Drama", 120, "director 1", "summary of the first movie"),
		            tuple(2, "My Title 2", LocalDate.of(2015, 11, 14), 2, "Comedy", 114, "director 2", "summary of the second movie"),
		            tuple(3, "Third title", LocalDate.of(2015, 12, 12), 2, "Comedy", 176, "director 3", "summary of the third movie")
		        );
	 }
	
	 @Test
	 public void shouldListMoviesByGenre() {
		 MovieDao movieDao = new MovieDao();

		    // WHEN
		    List<Movie> comedyMovies = movieDao.listMoviesByGenre("Comedy");

		    // THEN
		    assertThat(comedyMovies).hasSize(2); 
		    assertThat(comedyMovies).extracting("id", "title", "releaseDate", "genre.id", "genre.name", "duration", "director", "summary")
		        .containsOnly(
		            tuple(2, "My Title 2", LocalDate.of(2015, 11, 14), 2, "Comedy", 114, "director 2", "summary of the second movie"),
		            tuple(3, "Third title", LocalDate.of(2015, 12, 12), 2, "Comedy", 176, "director 3", "summary of the third movie")
		        );

		 
		    List<Movie> unknownGenreMovies = movieDao.listMoviesByGenre("Unknown");
		    assertThat(unknownGenreMovies).isEmpty();
	 }
	
	 @Test
	 public void shouldAddMovie() throws Exception {
		 	Movie movie = new Movie(null, "Inception", LocalDate.of(2010, 7, 16), new Genre(1, "Sci-Fi"), 148, "Christopher Nolan", "A mind-bending thriller");

		    // WHEN
		 	MovieDao movieDao = new MovieDao();
		 	movieDao.addMovie(movie);

		    // THEN
		    Connection connection = DataSourceFactory.getDataSource().getConnection();
		    Statement statement = connection.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM movie WHERE title='Inception'");
		    assertThat(resultSet.next()).isTrue();
		    assertThat(resultSet.getInt("idmovie")).isNotNull();
		    assertThat(resultSet.getString("title")).isEqualTo("Inception");
		    assertThat(resultSet.getDate("release_date").toLocalDate()).isEqualTo(LocalDate.of(2010, 7, 16));
		    assertThat(resultSet.getInt("genre_id")).isEqualTo(1);
		    assertThat(resultSet.getInt("duration")).isEqualTo(148);
		    assertThat(resultSet.getString("director")).isEqualTo("Christopher Nolan");
		    assertThat(resultSet.getString("summary")).isEqualTo("A mind-bending thriller");
		    assertThat(resultSet.next()).isFalse();
		    resultSet.close();
		    statement.close();
		    connection.close();
	 }
}
