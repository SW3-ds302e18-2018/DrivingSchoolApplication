package dk.aau.cs.ds302e18.service.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/* Spring uses JpaRepository to connect to the database. Here the lesson repository in the database is connected to.
   The repository can now be be called through the objects from the LessonRepository class. */
@Repository
public interface LogbookRepository extends JpaRepository<Logbook, Long> {
}
