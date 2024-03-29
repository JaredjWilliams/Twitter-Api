package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.HashtagService;
import com.cooksys.socialmedia.utils.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetMapper tweetMapper;


    @Override
    public List<TweetResponseDto> getHashtagsByLabel(String label) {
        List<Tweet> tweets = tweetRepository.findByHashtagsLabelEquals("#" + label);
        if (tweets.isEmpty()) {
            throw new BadRequestException("Hashtag does not exist");
        }
        return tweetMapper.entitiesToResponseDtos(Sort.filterNotDeletedAndSortDesc(tweets));
    }

    @Override
    public List<HashtagDto> getHashtags() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        for (Hashtag hashtag : hashtags) {
            hashtag.setLabel(hashtag.getLabel().substring(1));
        }
        return hashtagMapper.entitiesToDtos(hashtags);
    }
}
