package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkspaceRepository(private val dao: WorkspaceDao) {

    val workspaces: Flow<List<Workspace>> = dao.getWorkspaces()

    fun getWorkspaceById(id: Int): Flow<Workspace?> = dao.getWorkspaceById(id)

    fun getElementsForWorkspace(workspaceId: Int): Flow<List<WorkspaceElement>> =
        dao.getElementsForWorkspace(workspaceId)

    fun getRelationshipsForWorkspace(workspaceId: Int): Flow<List<WorkspaceRelationship>> =
        dao.getRelationshipsForWorkspace(workspaceId)

    suspend fun insertWorkspace(workspace: Workspace): Long =
        dao.insertWorkspace(workspace)

    suspend fun updateWorkspace(workspace: Workspace) =
        dao.updateWorkspace(workspace)

    suspend fun deleteWorkspace(workspace: Workspace) =
        dao.deleteWorkspace(workspace)

    suspend fun insertElement(element: WorkspaceElement) =
        dao.insertElement(element)

    suspend fun updateElement(element: WorkspaceElement) =
        dao.updateElement(element)

    suspend fun updateElementPosition(elementId: String, x: Float, y: Float) =
        dao.updateElementPosition(elementId, x, y)

    suspend fun deleteElement(element: WorkspaceElement) =
        dao.deleteElement(element)

    suspend fun deleteElementById(elementId: String) =
        dao.deleteElementById(elementId)

    suspend fun insertRelationship(relationship: WorkspaceRelationship) =
        dao.insertRelationship(relationship)

    suspend fun deleteRelationship(relationship: WorkspaceRelationship) =
        dao.deleteRelationship(relationship)

    suspend fun deleteRelationshipById(relationshipId: String) =
        dao.deleteRelationshipById(relationshipId)
}
