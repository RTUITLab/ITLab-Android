package ru.rtuitlab.itlab.data.local.users

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties

@Dao
interface UsersDao {
    @Transaction
    @Query("SELECT * FROM UserEntity")
    fun getUsers(): Flow<List<UserWithProperties>>

    /**
     * @return flow of UserEntities whose data matches the query
     */
    @Transaction
    @Query("""
        SELECT * FROM UserEntity 
        WHERE firstName || ' ' || middleName || ' ' || lastName LIKE '%' || :query || '%'
    """)
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    fun getUserById(id: Int): UserWithProperties

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserWithProperties)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserWithProperties>)

    @Transaction
    @Update
    suspend fun updateUser(user: UserWithProperties)

    @Transaction
    @Delete
    suspend fun deleteUser(user: UserWithProperties)
}