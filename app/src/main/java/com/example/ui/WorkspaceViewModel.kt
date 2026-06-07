package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkspaceRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = WorkspaceRepository(database.workspaceDao())
    }

    // Workspaces List
    val workspaces: StateFlow<List<Workspace>> = repository.workspaces
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Selected WorkspaceId
    private val _currentWorkspaceId = MutableStateFlow<Int?>(null)
    val currentWorkspaceId: StateFlow<Int?> = _currentWorkspaceId.asStateFlow()

    // Current selected workspace detail flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentWorkspace: StateFlow<Workspace?> = _currentWorkspaceId
        .flatMapLatest { id ->
            if (id != null) repository.getWorkspaceById(id) else flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Elements inside current workspace
    @OptIn(ExperimentalCoroutinesApi::class)
    val elements: StateFlow<List<WorkspaceElement>> = _currentWorkspaceId
        .flatMapLatest { id ->
            if (id != null) repository.getElementsForWorkspace(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Relationships inside current workspace
    @OptIn(ExperimentalCoroutinesApi::class)
    val relationships: StateFlow<List<WorkspaceRelationship>> = _currentWorkspaceId
        .flatMapLatest { id ->
            if (id != null) repository.getRelationshipsForWorkspace(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // View type: "SYSTEM_CONTEXT", "CONTAINER", "COMPONENT"
    private val _viewType = MutableStateFlow("SYSTEM_CONTEXT")
    val viewType: StateFlow<String> = _viewType.asStateFlow()

    // Selected software system ID for container view scopes
    private val _selectedParentSystemId = MutableStateFlow<String?>(null)
    val selectedParentSystemId: StateFlow<String?> = _selectedParentSystemId.asStateFlow()

    // Selected container ID for component view scopes
    private val _selectedParentContainerId = MutableStateFlow<String?>(null)
    val selectedParentContainerId: StateFlow<String?> = _selectedParentContainerId.asStateFlow()

    // Interactive selections
    private val _selectedElement = MutableStateFlow<WorkspaceElement?>(null)
    val selectedElement: StateFlow<WorkspaceElement?> = _selectedElement.asStateFlow()

    private val _selectedRelationship = MutableStateFlow<WorkspaceRelationship?>(null)
    val selectedRelationship: StateFlow<WorkspaceRelationship?> = _selectedRelationship.asStateFlow()

    // Selects a workspace and updates default scoping
    fun selectWorkspace(workspaceId: Int?) {
        _currentWorkspaceId.value = workspaceId
        _selectedElement.value = null
        _selectedRelationship.value = null
        _viewType.value = "SYSTEM_CONTEXT"
        _selectedParentSystemId.value = null
        _selectedParentContainerId.value = null

        // Auto-select first systems if available
        if (workspaceId != null) {
            viewModelScope.launch {
                elements.collectLatest { list ->
                    if (list.isNotEmpty()) {
                        val firstSys = list.firstOrNull { it.type == "SOFTWARE_SYSTEM" }
                        if (firstSys != null && _selectedParentSystemId.value == null) {
                            _selectedParentSystemId.value = firstSys.id
                        }
                        val firstCon = list.firstOrNull { it.type == "CONTAINER" }
                        if (firstCon != null && _selectedParentContainerId.value == null) {
                            _selectedParentContainerId.value = firstCon.id
                        }
                    }
                }
            }
        }
    }

    fun setViewType(type: String) {
        _viewType.value = type
        _selectedElement.value = null
        _selectedRelationship.value = null
    }

    fun selectParentSystem(systemId: String) {
        _selectedParentSystemId.value = systemId
        _selectedElement.value = null
    }

    fun selectParentContainer(containerId: String) {
        _selectedParentContainerId.value = containerId
        _selectedElement.value = null
    }

    fun selectElement(element: WorkspaceElement?) {
        _selectedElement.value = element
        _selectedRelationship.value = null
    }

    fun selectRelationship(relationship: WorkspaceRelationship?) {
        _selectedRelationship.value = relationship
        _selectedElement.value = null
    }

    // Coordinate Updating for Draggable Canvas
    fun updateElementPosition(elementId: String, x: Float, y: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElementPosition(elementId, x, y)
        }
    }

    // CRUD Workspace
    fun addWorkspace(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertWorkspace(
                Workspace(name = name, description = description)
            )
            // Auto open the newly created workspace
            _currentWorkspaceId.value = id.toInt()
        }
    }

    fun updateWorkspace(workspace: Workspace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWorkspace(workspace.copy(lastModified = System.currentTimeMillis()))
        }
    }

    fun deleteWorkspace(workspace: Workspace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkspace(workspace)
            if (_currentWorkspaceId.value == workspace.id) {
                _currentWorkspaceId.value = null
            }
        }
    }

    // CRUD Elements
    fun addElement(type: String, name: String, description: String, technology: String, parentId: String?) {
        val workspaceId = _currentWorkspaceId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val prefix = when(type) {
                "PERSON" -> "per"
                "SOFTWARE_SYSTEM" -> "sys"
                "CONTAINER" -> "con"
                else -> "comp"
            }
            val uniqueId = "${prefix}_${workspaceId}_${System.currentTimeMillis()}"
            repository.insertElement(
                WorkspaceElement(
                    id = uniqueId,
                    workspaceId = workspaceId,
                    type = type,
                    name = name,
                    description = description,
                    technology = technology,
                    parentId = parentId,
                    x = 200f,
                    y = 200f
                )
            )
            // If we added a system/container and none was set yet, default it!
            if (type == "SOFTWARE_SYSTEM" && _selectedParentSystemId.value == null) {
                _selectedParentSystemId.value = uniqueId
            }
            if (type == "CONTAINER" && _selectedParentContainerId.value == null) {
                _selectedParentContainerId.value = uniqueId
            }
        }
    }

    fun updateElement(element: WorkspaceElement) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElement(element)
        }
    }

    fun deleteElement(element: WorkspaceElement) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteElement(element)
            if (_selectedElement.value?.id == element.id) {
                _selectedElement.value = null
            }
        }
    }

    // CRUD Relationships
    fun addRelationship(sourceId: String, targetId: String, description: String, technology: String) {
        val workspaceId = _currentWorkspaceId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val uniqueId = "rel_${workspaceId}_${System.currentTimeMillis()}"
            repository.insertRelationship(
                WorkspaceRelationship(
                    id = uniqueId,
                    workspaceId = workspaceId,
                    sourceId = sourceId,
                    targetId = targetId,
                    description = description,
                    technology = technology
                )
            )
        }
    }

    fun deleteRelationship(relationship: WorkspaceRelationship) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRelationship(relationship)
            if (_selectedRelationship.value?.id == relationship.id) {
                _selectedRelationship.value = null
            }
        }
    }

    // Import and Export Dsl
    fun importDsl(dslText: String) {
        val workspaceId = _currentWorkspaceId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val (importedElements, importedRelationships) = DslExporter.importFromDsl(dslText, workspaceId)
            for (element in importedElements) {
                repository.insertElement(element)
            }
            for (relationship in importedRelationships) {
                repository.insertRelationship(relationship)
            }
        }
    }

    fun importDslToNewWorkspace(name: String, description: String, dslText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val wsId = repository.insertWorkspace(Workspace(name = name, description = description)).toInt()
            val (importedElements, importedRelationships) = DslExporter.importFromDsl(dslText, wsId)
            for (element in importedElements) {
                repository.insertElement(element)
            }
            for (relationship in importedRelationships) {
                repository.insertRelationship(relationship)
            }
            _currentWorkspaceId.value = wsId
        }
    }

    fun exportToDsl(): String {
        val ws = currentWorkspace.value ?: return ""
        val elList = elements.value
        val relList = relationships.value
        return DslExporter.exportToDsl(ws, elList, relList)
    }
}
