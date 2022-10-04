package com.ferdinand.pdftestapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* The favourite model will only hold the id as this is what will be used to query if a file is a favourite or not thus
* the other attributes are not needed. If the item queried from the db based on its id is null, then it is NOT a fav, otherwise it is
* */
@Entity(tableName = "favouritePdfs")
data class DbFavoritePdfFile(
    @PrimaryKey
    val id: Long
)
