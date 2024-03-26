package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Tweet {

    @Id
    @GeneratedValue
    private Long id;

    private Integer author;

    private Timestamp posted;

    private Boolean deleted;

    private String content;

    // private Integer inReplyTo;

    // private Integer repostOf;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
        name = "user_likes",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userLikes;

    @ManyToMany
    @JoinTable(
        name = "user_mentions",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userMentions; 

    @ManyToMany
    @JoinTable(
        name = "tweet_hashtags",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags;

    @OneToMany(mappedBy = "replyTo")
    private List<Tweet> replies;

    @ManyToOne
    private Tweet replyTo;

    @OneToMany(mappedBy = "repostOf")
    private List<Tweet> reposts;

    @ManyToOne
    private Tweet repostOf;
}
