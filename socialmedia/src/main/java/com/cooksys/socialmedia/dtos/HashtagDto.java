package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class HashtagDto {

    @NonNull
    private String label;

    @NonNull
    private Timestamp firstUsed;

    @NonNull
    private Timestamp lastUsed;

}
