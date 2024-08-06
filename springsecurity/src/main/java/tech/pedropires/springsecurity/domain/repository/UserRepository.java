package tech.pedropires.springsecurity.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.pedropires.springsecurity.domain.users.User;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


}
