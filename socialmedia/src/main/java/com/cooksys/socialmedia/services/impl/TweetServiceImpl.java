package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;

    public User getUser(String username) {

        User user = userRepository.findByCredentialsUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }

        return user;
    }

    public Tweet getTweetById(Long id) {
        if (tweetRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Tweet with id not found: " + id);
        }

        if (tweetRepository.getReferenceById(id).getDeleted() == true) {
            throw new NotFoundException("Tweet with id: " + id + " is deleted");
        }

        return tweetRepository.getReferenceById(id);
    }

    @Override
    public List<TweetResponseDto> getTweetsFromUserAndFollowers(String username) {
        User user = getUser(username);
        List<User> followers = user.getFollowers();

        List<TweetResponseDto> tweets = new ArrayList<>();
        tweets.addAll(tweetMapper.entitiesToResponseDtos(user.getTweets()));

        for (User follower : followers) {
            tweets.addAll(tweetMapper.entitiesToResponseDtos(follower.getTweets()));
        }

        // Sorting tweets by newest to oldest posted timestamps
        Collections.sort(tweets, new Comparator<TweetResponseDto>() {
            public int compare(TweetResponseDto t1, TweetResponseDto t2) {
                if (t1.getPosted() == null || t2.getPosted() == null)
                  return 0;
                return t2.getPosted().compareTo(t1.getPosted());
            }
          });

        return tweets;
    }

    @Override
    public List<TweetResponseDto> getTweetReplies(Long id) {
        return tweetMapper.entitiesToResponseDtos(getTweetById(id).getReplies());
    }
}
