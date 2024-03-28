package com.cooksys.socialmedia.entities;

import com.cooksys.socialmedia.utils.interfaces.Deletable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@Data
public class User implements Deletable {

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
    private List<Tweet> tweets;

    @ManyToMany
    @JoinTable(
        name = "user_likes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> tweetLikes;

    @ManyToMany(mappedBy="userMentions")
    private List<Tweet> tweetMentions; 

    @ManyToMany(mappedBy = "following")
    private List<User> followers;

    @ManyToMany
    private List<User> following;


}
