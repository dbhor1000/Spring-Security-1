package Javacode.Spring.Security1.repository;

import Javacode.Spring.Security1.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity getReferenceByUsername(String username);
}
