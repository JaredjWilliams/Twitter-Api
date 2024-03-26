package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Credentials credentials;

    @Embedded
    private Profile profile;

    private Timestamp joined;

    private Boolean deleted;

    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets;

    // @ManyToMany(cascade = CascadeType.ALL)
    @ManyToMany
    @JoinTable(
        name = "user_likes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> tweetLikes;

    // @ManyToMany(cascade = CascadeType.ALL)
    @ManyToMany
    @JoinTable(
        name = "user_mentions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> tweetMentions; 

    @ManyToMany(mappedBy = "following")
    // @JoinTable(
    //     name = "followers_following",
    //     joinColumns = @JoinColumn(name = "follower_id"),
    //     inverseJoinColumns = @JoinColumn(name = "following_id")
    // )
    private List<User> followers;

    // modify set following in db
    @ManyToMany
    private List<User> following;


}
