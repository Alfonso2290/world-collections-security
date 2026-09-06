package security.world_collections_security.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import security.world_collections_security.entity.sql.UserAccess;


public interface UserAccessRepository extends JpaRepository<UserAccess, Long>{

}
