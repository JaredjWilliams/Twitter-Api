package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;

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

        if (tweetRepository.getReferenceById(id).getDeleted()) {
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
        tweets.sort(new Comparator<TweetResponseDto>() {
            public int compare(TweetResponseDto t1, TweetResponseDto t2) {
                return t2.getPosted().compareTo(t1.getPosted());
            }
        });

        return tweets;
    }



    /*@Override
    public ContextDto getContext(TweetRequestDto tweetDto) {
        Tweet tweet = tweetMapper.requestDtoToEntity(tweetDto);
        if (!isTweetDeleted(tweet)){
            ContextDto contextDto 
        }
        throw new NotFoundException("This tweet has been deleted");
    }
    public boolean isTweetDeleted(tweet){
        return tweet != null && tweet.getDeleted();
    }*/
    

    @Override
    public List<UserResponseDto> getMentions(Long id) {

        Tweet tweet = tweetRepository.getReferenceById(id);

        if (tweet == null || tweet.getDeleted()) {
            throw new NotFoundException("Tweet not found with id: " + id);
        }

        return userMapper.entitiesToResponseDtos(tweet.getUserMentions());
    }

    @Override
    public List<TweetResponseDto> getTweets() {
        return tweetMapper.entitiesToResponseDtos(tweetRepository.findByDeletedFalseOrderByPostedDesc());
    }

    @Override
    public List<TweetResponseDto> getTweetReplies(Long id) {
        return tweetMapper.entitiesToResponseDtos(getTweetById(id).getReplies());

    }

    @Override
    public TweetResponseDto postRepostOfTweet(Long id, CredentialsDto credentialsDto) {
        User reposter = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
        Tweet tweet = new Tweet();
        Tweet repostedTweet = getTweetById(id);
        tweet.setAuthor(reposter);
        tweet.setRepostOf(repostedTweet);
        tweetRepository.saveAndFlush(tweet);
        return tweetMapper.entityToResponseDto(tweet);
    }
}
