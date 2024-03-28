package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final HashtagRepository hashtagRepository;
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

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

        String username = tweetRequestDto.getCredentialsDto().getUsername();
        String password = tweetRequestDto.getCredentialsDto().getPassword();

        if (tweetRequestDto.getContent() == null || tweetRequestDto.getContent().isEmpty()) {
            throw new BadRequestException("Tweet must have content in request body to be created.");
        }

        if (tweetRequestDto.getCredentialsDto() == null) {
            throw new BadRequestException("Tweet must have credentials in request body to be created.");
        }

        if (userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()) == null) {
            throw new NotFoundException("User not found with given username: " + username); 
        }
        else if(!userRepository.findByCredentialsUsername(username).getCredentials().getPassword().equals(password)) {
            throw new BadRequestException("Incorrect password for user: " + username);
        }
        

        Tweet tweet =  tweetMapper.requestDtoToEntity(tweetRequestDto);
        tweet.setAuthor(userRepository.findByCredentialsUsername(username));

        String[] content = tweet.getContent().split("\\s+");
        List<User> usersMentioned = new ArrayList<>();

        // Map user mentions
        for (String subString : content) {
            if (subString.charAt(0) == '@') {
                User userMentioned = userRepository.findByCredentialsUsername(subString.substring(1));

                if (userMentioned != null) {
                    List<Tweet> tweets = userMentioned.getTweetMentions();
                    tweets.add(tweet);
                    userMentioned.setTweetMentions(tweets);
                    usersMentioned.add(userMentioned);
                    userRepository.saveAndFlush(userMentioned);
                }
            }
        }

        tweet.setUserMentions(usersMentioned);
        tweetRepository.saveAndFlush(tweet);


        // Map user hashtags
        for (String subString : content) {
            // Add hashtag to tweet 
            if (subString.charAt(0) == '#') {

                Hashtag hashtag = hashtagRepository.findByLabel(subString); 

                // Check if hashtag already exists in hashtagRepository
                if (hashtag == null) {
                    // Create new hashtag in repository 
                    Hashtag newHashtag = new Hashtag();
                    newHashtag.setLabel(subString);
                    newHashtag.setFirstUsed(tweet.getPosted());
                    newHashtag.setLastUsed(tweet.getPosted());
                    newHashtag.setTweets(Arrays.asList(tweet));
                    hashtagRepository.saveAndFlush(newHashtag);
                }

                // Update timestamp if hashtag is already saved
                else {
                    hashtag.setLastUsed(tweet.getPosted());
                }

            }
        }

        return tweetMapper.entityToResponseDto(tweet);
    }
}
