package repository;

import model.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long>{
}
