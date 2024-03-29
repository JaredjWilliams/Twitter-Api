package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByAuthorOrderByPostedDesc(User user);

    List<Tweet> findByDeletedFalseOrderByPostedDesc();

    List<Tweet> findByHashtagsLabelEquals(String label);
}
