package com.cooksys.socialmedia.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

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

    @CreationTimestamp
    private Timestamp joined;

    private Boolean deleted = false;

    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    private List<Tweet> tweets;

    @ManyToMany
    @JoinTable(
        name = "user_likes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    @ToString.Exclude
    private List<Tweet> tweetLikes;

    @ManyToMany(mappedBy="userMentions")
    @ToString.Exclude
    private List<Tweet> tweetMentions; 

    @ManyToMany(mappedBy = "following")
    @ToString.Exclude
    private List<User> followers;

    @ManyToMany
    @ToString.Exclude
    private List<User> following;


}
