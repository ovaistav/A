package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {

    @Query("SELECT * FROM workspaces ORDER BY lastModified DESC")
    fun getWorkspaces(): Flow<List<Workspace>>

    @Query("SELECT * FROM workspaces WHERE id = :id")
    fun getWorkspaceById(id: Int): Flow<Workspace?>

    @Query("SELECT * FROM workspace_elements WHERE workspaceId = :workspaceId")
    fun getElementsForWorkspace(workspaceId: Int): Flow<List<WorkspaceElement>>

    @Query("SELECT * FROM workspace_relationships WHERE workspaceId = :workspaceId")
    fun getRelationshipsForWorkspace(workspaceId: Int): Flow<List<WorkspaceRelationship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    @Update
    suspend fun updateWorkspace(workspace: Workspace)

    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(element: WorkspaceElement)

    @Update
    suspend fun updateElement(element: WorkspaceElement)

    @Query("UPDATE workspace_elements SET x = :x, y = :y WHERE id = :elementId")
    suspend fun updateElementPosition(elementId: String, x: Float, y: Float)

    @Delete
    suspend fun deleteElement(element: WorkspaceElement)

    @Query("DELETE FROM workspace_elements WHERE id = :elementId")
    suspend fun deleteElementById(elementId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: WorkspaceRelationship)

    @Delete
    suspend fun deleteRelationship(relationship: WorkspaceRelationship)

    @Query("DELETE FROM workspace_relationships WHERE id = :relationshipId")
    suspend fun deleteRelationshipById(relationshipId: String)
}
