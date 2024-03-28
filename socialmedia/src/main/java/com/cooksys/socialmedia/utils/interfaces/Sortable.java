package com.cooksys.socialmedia.utils.interfaces;

import java.sql.Timestamp;

public interface Sortable extends Deletable {
    Timestamp getPosted();
    void setPosted(Timestamp posted);

}
