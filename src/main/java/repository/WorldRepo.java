package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import world.World;

@Repository
@Component
public interface WorldRepo extends JpaRepository<World, Long> {
}
