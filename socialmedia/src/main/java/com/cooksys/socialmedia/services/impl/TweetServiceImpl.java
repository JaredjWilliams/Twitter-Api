package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetMapper tweetMapper;

    public User getUser(String username) {

        User user = userRepository.findByCredentialsUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }

        return user;
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
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);

        if (!isUsernameValid(tweetRequestDto.getCredentialsDto().getUsername())) {
            throw new NotFoundException("Username not found");
        }

        if (tweetRequestDto.getContent().isBlank()) {
            throw new BadRequestException("Tweet content cannot be blank");
        }
        Tweet newTweet = tweetMapper.requestDtoToEntity(tweetRequestDto);

        newTweet.setAuthor(getUser(tweetRequestDto.getCredentialsDto().getUsername()));
        newTweet.setInReplyTo(tweet);
        tweet.getReplies().add(newTweet);


        return null;
    }

    private void processTweet(Tweet tweet) {

    }

    private void processMentions(Tweet tweet) {
        Pattern pattern = Pattern.compile("@([a-zA-Z])");
        Matcher matcher = pattern.matcher(tweet.getContent());

        while (matcher.find()) {
            String usernameMentioned = matcher.group(1);
            if (userRepository.existsByCredentialsUsername(usernameMentioned)) {
                User user = userRepository.findByCredentialsUsername(usernameMentioned);
                tweet.getUserMentions().add(user);
            }
        }
    }

    private void processHashtags(Tweet tweet) {
        Pattern pattern = Pattern.compile("#([a-zA-Z])");
        Matcher matcher = pattern.matcher(tweet.getContent());

        while (matcher.find()) {
            String label = matcher.group(0);
            Hashtag hashtag = hashtagRepository.findByLabel(label);
            if (hashtag != null) {
                hashtag.getTweets().add(tweet);
                hashtagRepository.save(hashtag);
            } else {
                hashtag = hashtagMapper.
            }
        }
    }


    private Tweet getTweet(Long id) {
        Optional<Tweet> tweet = tweetRepository.findById(id);

        if (tweet.isEmpty()) {
            throw new BadRequestException("Tweet is not found");
        }

        return tweet.get();
    }

    private void validateTweet(Tweet tweet) {
        if (tweet.getDeleted()) {
            throw new NotFoundException("Tweet has been deleted");
        }
    }

    private boolean isUsernameValid(String username) {
        return userRepository.existsByCredentialsUsername(username);
    }
}
