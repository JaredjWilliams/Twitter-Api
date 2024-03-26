package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private Integer inReplyTo;

    private Integer repostOf;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
