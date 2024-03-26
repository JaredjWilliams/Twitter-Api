package com.cooksys.socialmedia.entities;


import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Hashtag {

    @Id
    @GeneratedValue
    private Long id;

    private String label;

    private Timestamp firstUsed;

    private Timestamp lastUsed;

    @ManyToMany
    @JoinTable(
        name = "tweet_hashtags",
        joinColumns = @JoinColumn(name = "hashtag_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> tweets;

}
