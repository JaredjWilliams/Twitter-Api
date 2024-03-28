package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.entities.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {

    // This method will return a list of all the usernames mentioned in a tweet.
    public static List<String> forUsers(Tweet tweet) {
        List<String> mentions = new ArrayList<>();
        Pattern pattern = Pattern.compile("@([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(tweet.getContent());

        while (matcher.find()) {
            String usernameMentioned = matcher.group(1);
            mentions.add(usernameMentioned);
        }

        return mentions;
    }

    // This method will return a list of all the hashtags mentioned in a tweet.
    public static List<String> forHashtags(Tweet tweet) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(tweet.getContent());

        while (matcher.find()) {
            String label = matcher.group(0);
            hashtags.add(label);
        }

        return hashtags;
    }
}
