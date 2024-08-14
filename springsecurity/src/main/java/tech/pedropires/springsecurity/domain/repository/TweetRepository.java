package tech.pedropires.springsecurity.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.pedropires.springsecurity.domain.tweets.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {


}
