package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workspace_elements",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkspaceElement(
    @PrimaryKey val id: String, // e.g. UUID or custom unique key
    val workspaceId: Int,
    val type: String, // "PERSON", "SOFTWARE_SYSTEM", "CONTAINER", "COMPONENT"
    val name: String,
    val description: String,
    val technology: String = "", // e.g. Spring Boot, Android, PostgreSQL
    val parentId: String? = null, // e.g. software system ID for container, or container ID for component
    val x: Float = 100f, // X position for visual diagramming
    val y: Float = 100f, // Y position for visual diagramming
    val colorHex: String? = null // Custom hex color override
)

@Entity(
    tableName = "workspace_relationships",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkspaceRelationship(
    @PrimaryKey val id: String, // unique relation ID
    val workspaceId: Int,
    val sourceId: String, // element ID
    val targetId: String, // element ID
    val description: String, // e.g. "Uses", "Sends emails using"
    val technology: String = "" // e.g. "HTTPS", "SMTP", "JDBC"
)
