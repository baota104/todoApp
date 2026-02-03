package com.example.todoapp.data.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name", "user_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE // xóa user -> xóa luôn Category
        )
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    val name: String,
    val icon: Int,
    @ColumnInfo(name = "color_code")
    val colorCode: Int? = null,

    @ColumnInfo(name = "user_id")
    val userId: Int
)