package ru.rtuitlab.itlab.data.local.users

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.users.models.PropertyWithType
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

@Dao
interface UsersDao {
    @Transaction
    @Query("SELECT * FROM UserEntity")
    fun getUsers(): Flow<List<UserWithProperties>>

    @Query("SELECT * FROM UserPropertyEntity")
    suspend fun getProperties(): List<UserPropertyEntity>

    @Query("SELECT * FROM UserPropertyTypeModel WHERE id = :typeId")
    suspend fun getPropertyTypeById(typeId: String): UserPropertyTypeModel?

    @Query("SELECT * FROM UserPropertyTypeModel")
    suspend fun getPropertyTypes(): List<UserPropertyTypeModel>

    @Query("SELECT * FROM UserPropertyEntity")
    suspend fun getPropertiesWithTypes(): List<PropertyWithType>

    /**
     * @return flow of UserEntities whose data matches the query
     */
    @Transaction
    @Query("""
        SELECT * FROM UserEntity 
        WHERE firstName || ' ' || middleName || ' ' || lastName LIKE '%' || :query || '%'
    """)
    fun searchUsers(query: String): Flow<List<UserWithProperties>>

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUserById(id: String): UserWithProperties

    @Transaction
    @Upsert
    suspend fun upsertUser(
        user: UserEntity,
        properties: List<UserPropertyEntity>
    )

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Transaction
    @Upsert
    suspend fun upsertProperties(properties: List<UserPropertyEntity>)

    @Transaction
    @Upsert
    suspend fun upsertAll(
        users: List<UserEntity>,
        properties: List<UserPropertyEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Transaction
    @Update
    suspend fun updateUser(
        user: UserEntity,
        properties: List<UserPropertyEntity>
    )

    @Update
    suspend fun updateUser(user: UserEntity)

    @Update
    suspend fun updateUserProperty(property: UserPropertyEntity)

    @Transaction
    @Delete
    suspend fun deleteUser(
        user: UserEntity
    )

    @Transaction
    @Upsert
    suspend fun upsertPropertyTypes(types: List<UserPropertyTypeModel>)
}