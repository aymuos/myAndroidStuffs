package com.aymuos.bookhub.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class],version = 1) // to mention the version of the database
abstract class BookDatabase:RoomDatabase(){

   abstract fun bookDao():BookDao

}