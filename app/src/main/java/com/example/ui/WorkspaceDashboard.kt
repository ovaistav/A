package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Workspace
import com.example.data.WorkspaceElement
import com.example.data.WorkspaceRelationship
import com.example.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDashboard(viewModel: WorkspaceViewModel) {
    val context = LocalContext.current
    val currentWorkspace by viewModel.currentWorkspace.collectAsStateWithLifecycle()
    val workspaces by viewModel.workspaces.collectAsStateWithLifecycle()

    var showAddWorkspaceDialog by remember { mutableStateOf(false) }

    var showHubImportDslDialog by remember { mutableStateOf(false) }

    if (currentWorkspace == null) {
        // Render Clean Minimalist HUB/List Screen
        Scaffold(
            topBar = {
                // Customized clean minimalist header with offline status indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Structurizr",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D1B20)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                            Text(
                                "LOCAL WORKSPACE • OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF49454F),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    
                    // Folder icon triggers quick DSL Import
                    IconButton(
                        onClick = { showHubImportDslDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFEADDFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Quick Import DSL",
                            tint = Color(0xFF21005D)
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddWorkspaceDialog = true },
                    containerColor = Color(0xFFD0BCFF),
                    contentColor = Color(0xFF21005D),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "ورک‌پیس جدید",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            containerColor = Color(0xFFFDF8F6) // Warm minimalism backdrop
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFDF8F6))
            ) {
                // Active Models section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E7FF)),
                    border = BorderStroke(1.dp, Color(0xFFD0BCFF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    "ACTIVE MODELS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF65558F),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "مدل سازی C4 آفلاین",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1D1B20),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(100))
                                    .border(1.dp, Color(0xFFD0BCFF), RoundedCornerShape(100))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Structurizr C4",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = Color(0xFF21005D)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        "پروژه‌های باز",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF65558F)
                                    )
                                    Text(
                                        text = String.format("%02d", workspaces.size),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Light,
                                        color = Color(0xFF1D1B20),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        "محیط کاملا محلی",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF65558F)
                                    )
                                    Text(
                                        text = "LOCAL",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Light,
                                        color = Color(0xFF1D1B20),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recent diagrams header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "متغیرها و داکیومنت‌ها",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF49454F)
                    )
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "شبکه",
                        tint = Color(0xFF49454F),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (workspaces.isEmpty()) {
                    // Styled empty state pointing to creation or import
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(20.dp)
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFF3E7FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = "Empty",
                                    tint = Color(0xFF65558F),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "هیچ ورک‌پیسی یافت نشد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "برای شروع یک مدل جدید بسازید یا از دکمه پوشه بالا جهت بارگذاری DSL استفاده کنید.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF49454F),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(workspaces) { ws ->
                            WorkspaceCard(
                                workspace = ws,
                                onClick = { viewModel.selectWorkspace(ws.id) },
                                onDelete = { viewModel.deleteWorkspace(ws) }
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Visual Architect Sandbox view
        WorkspaceSandbox(
            viewModel = viewModel,
            workspace = currentWorkspace!!,
            onBack = { viewModel.selectWorkspace(null) }
        )
    }

    if (showAddWorkspaceDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWorkspaceDialog = false },
            title = { Text("ورک‌پیس جدید") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام پروژه / سیستم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("توضیحات کلی") },
                        modifier = Modifier.fillMaxWidth(),
                        rows = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addWorkspace(name, desc)
                            showAddWorkspaceDialog = false
                        } else {
                            Toast.makeText(context, "نام پروژه الزامی است", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("ایجاد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWorkspaceDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    if (showHubImportDslDialog) {
        var importName by remember { mutableStateOf("") }
        var importDslText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showHubImportDslDialog = false },
            title = { Text("بارگذاری از کد Structurizr DSL") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "اسکریپت C4 Model DSL پلتفرم Structurizr را در اینجا وارد کنید تا سیستم‌های نرم‌افزاری و روابط آن به‌صورت کامپوننت فورا بارگذاری و مصورسازی شوند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F)
                    )
                    OutlinedTextField(
                        value = importName,
                        onValueChange = { importName = it },
                        label = { Text("نام مدل / معماری") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = importDslText,
                        onValueChange = { importDslText = it },
                        label = { Text("کد Structurizr DSL") },
                        modifier = Modifier.fillMaxWidth(),
                        rows = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importName.isNotBlank() && importDslText.isNotBlank()) {
                            viewModel.importDslToNewWorkspace(importName, "سیستم وارد شده از DSL", importDslText)
                            showHubImportDslDialog = false
                        } else {
                            Toast.makeText(context, "نام معماری و متن DSL هر دو الزامی هستند", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65558F))
                ) {
                    Text("بارگذاری و ترسیم")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHubImportDslDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun OutlinedTextField(value: String, onValueChange: (String) -> Unit, label: @Composable () -> Unit, modifier: Modifier, rows: Int) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        minLines = rows,
        maxLines = rows
    )
}

@Composable
fun WorkspaceCard(
    workspace: Workspace,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE8DEF8), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "Architecture diagram",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF21005D)
                )
            }

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workspace.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (workspace.description.isNotEmpty()) workspace.description else "مدل معماری بدون توضیحات",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Options/Trash button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف پروژه",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSandbox(
    viewModel: WorkspaceViewModel,
    workspace: Workspace,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val elements by viewModel.elements.collectAsStateWithLifecycle()
    val relationships by viewModel.relationships.collectAsStateWithLifecycle()
    val viewType by viewModel.viewType.collectAsStateWithLifecycle()

    val selectedParentSystemId by viewModel.selectedParentSystemId.collectAsStateWithLifecycle()
    val selectedParentContainerId by viewModel.selectedParentContainerId.collectAsStateWithLifecycle()

    val selectedElement by viewModel.selectedElement.collectAsStateWithLifecycle()
    val selectedRelationship by viewModel.selectedRelationship.collectAsStateWithLifecycle()

    var showAddElementDialog by remember { mutableStateOf(false) }
    var showAddRelationshipDialog by remember { mutableStateOf(false) }
    var showDslDialog by remember { mutableStateOf(false) }
    var showEditWorkspaceDialog by remember { mutableStateOf(false) }

    // Active Elements filter based on C4 view scopes
    val activeElements = remember(elements, viewType, selectedParentSystemId, selectedParentContainerId) {
        when (viewType) {
            "SYSTEM_CONTEXT" -> {
                elements.filter { it.type == "PERSON" || it.type == "SOFTWARE_SYSTEM" }
            }
            "CONTAINER" -> {
                if (selectedParentSystemId == null) emptyList()
                else {
                    // System Containers
                    val systemContainers = elements.filter { it.type == "CONTAINER" && it.parentId == selectedParentSystemId }
                    val containerIds = systemContainers.map { it.id }.toSet()

                    // External connections: find elements with relationships to/from these containers
                    val directConnections = relationships.filter {
                        (it.sourceId in containerIds && it.targetId !in containerIds) ||
                                (it.targetId in containerIds && it.sourceId !in containerIds)
                    }
                    val connectedExternalIds = directConnections.flatMap { listOf(it.sourceId, it.targetId) }.toSet() - containerIds
                    val externalDeps = elements.filter { it.id in connectedExternalIds && (it.type == "PERSON" || it.type == "SOFTWARE_SYSTEM") }

                    systemContainers + externalDeps
                }
            }
            "COMPONENT" -> {
                if (selectedParentContainerId == null) emptyList()
                else {
                    // Components inside selected container
                    val components = elements.filter { it.type == "COMPONENT" && it.parentId == selectedParentContainerId }
                    val componentIds = components.map { it.id }.toSet()

                    // External connections: find elements communicating directly with our components
                    val connections = relationships.filter {
                        (it.sourceId in componentIds && it.targetId !in componentIds) ||
                                (it.targetId in componentIds && it.sourceId !in componentIds)
                    }
                    val externalIds = connections.flatMap { listOf(it.sourceId, it.targetId) }.toSet() - componentIds
                    // Only display active communicating systems, containers, or people
                    val externalDeps = elements.filter { it.id in externalIds && it.type != "COMPONENT" }

                    components + externalDeps
                }
            }
            else -> emptyList()
        }
    }

    // Active relationships are relationships connecting active items displayed on canvasses
    val activeRelationships = remember(relationships, activeElements) {
        val activeIds = activeElements.map { it.id }.toSet()
        relationships.filter { it.sourceId in activeIds && it.targetId in activeIds }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.clickable { showEditWorkspaceDialog = true }) {
                        Text(
                            text = workspace.name,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "مدل سازی C4 • برای جزئیات لمس کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF49454F)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1D1B20)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDslDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "خروجی DSL / ورودی",
                            tint = Color(0xFF65558F)
                        )
                    }
                    IconButton(onClick = { showAddElementDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddBox,
                            contentDescription = "افزودن ماژول",
                            tint = Color(0xFF65558F)
                        )
                    }
                    IconButton(onClick = { showAddRelationshipDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.SettingsEthernet,
                            contentDescription = "ایجاد ارتباط",
                            tint = Color(0xFF65558F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDF8F6)) // Warm peach/beige backdrop for Clean Minimalism
        ) {
            // Scope/Diagram Selectors TabBar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFCAC4D0)))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabRow(
                    selectedTabIndex = when (viewType) {
                        "SYSTEM_CONTEXT" -> 0
                        "CONTAINER" -> 1
                        else -> 2
                    },
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    containerColor = Color.White,
                    contentColor = Color(0xFF65558F),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(
                                tabPositions[when (viewType) {
                                    "SYSTEM_CONTEXT" -> 0
                                    "CONTAINER" -> 1
                                    else -> 2
                                }]
                            ),
                            color = Color(0xFF65558F)
                        )
                    }
                ) {
                    Tab(
                        selected = viewType == "SYSTEM_CONTEXT",
                        onClick = { viewModel.setViewType("SYSTEM_CONTEXT") },
                        text = { Text("System Context", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = viewType == "CONTAINER",
                        onClick = { viewModel.setViewType("CONTAINER") },
                        text = { Text("Containers", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = viewType == "COMPONENT",
                        onClick = { viewModel.setViewType("COMPONENT") },
                        text = { Text("Components", style = MaterialTheme.typography.labelMedium) }
                    )
                }

                // Sub options row (choosing parent context)
                if (viewType == "CONTAINER") {
                    val systemElements = elements.filter { it.type == "SOFTWARE_SYSTEM" }
                    if (systemElements.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("سیستم والد C4:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 8.dp))
                            ScrollableRow {
                                systemElements.forEach { sys ->
                                    FilterChip(
                                        selected = selectedParentSystemId == sys.id,
                                        onClick = { viewModel.selectParentSystem(sys.id) },
                                        label = { Text(sys.name) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text("ابتدا یک سیستم نرم‌افزاری ایجاد کنید.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                } else if (viewType == "COMPONENT") {
                    val containerElements = elements.filter { it.type == "CONTAINER" }
                    if (containerElements.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("کانتینر والد:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 8.dp))
                            ScrollableRow {
                                containerElements.forEach { con ->
                                    FilterChip(
                                        selected = selectedParentContainerId == con.id,
                                        onClick = { viewModel.selectParentContainer(con.id) },
                                        label = { Text(con.name) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text("ابتدا یک کانتینر در منوی کانتینر ایجاد کنید.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Central Infinite Grid Canvas Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RectangleShape)
            ) {
                InteractiveC4Canvas(
                    activeElements = activeElements,
                    activeRelationships = activeRelationships,
                    selectedElement = selectedElement,
                    selectedRelationship = selectedRelationship,
                    viewType = viewType,
                    onSelectElement = { viewModel.selectElement(it) },
                    onSelectRelationship = { viewModel.selectRelationship(it) },
                    onElementPositionChanged = { id, x, y -> viewModel.updateElementPosition(id, x, y) }
                )

                // Quick visual indicator of canvas controls
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color(0xD0222222), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "راهنما: کشیدن ماژول به جهات برای چیدمان دستی • لمس برای مدیریت",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            // Context detailed actions bar when selection is active
            AnimatedVisibility(
                visible = selectedElement != null || selectedRelationship != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SelectionActionPanel(
                    selectedElement = selectedElement,
                    selectedRelationship = selectedRelationship,
                    onEditElement = { el ->
                        viewModel.updateElement(el)
                        viewModel.selectElement(null)
                    },
                    onDeleteElement = { el ->
                        viewModel.deleteElement(el)
                    },
                    onDeleteRelationship = { rel ->
                        viewModel.deleteRelationship(rel)
                    }
                )
            }
        }
    }

    // 1. Edit Workspace Dialog
    if (showEditWorkspaceDialog) {
        var name by remember { mutableStateOf(workspace.name) }
        var desc by remember { mutableStateOf(workspace.description) }

        AlertDialog(
            onDismissRequest = { showEditWorkspaceDialog = false },
            title = { Text("ویرایش اطلاعات پروژه") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام ورک‌پیس") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("توضیحات کلان") },
                        modifier = Modifier.fillMaxWidth(),
                        rows = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.updateWorkspace(workspace.copy(name = name, description = desc))
                            showEditWorkspaceDialog = false
                        }
                    }
                ) {
                    Text("ذخیره تغییرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditWorkspaceDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 2. Add Element Dialog
    if (showAddElementDialog) {
        var elType by remember { mutableStateOf("PERSON") }
        var elName by remember { mutableStateOf("") }
        var elDesc by remember { mutableStateOf("") }
        var elTech by remember { mutableStateOf("") }
        var parentId by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddElementDialog = false },
            title = { Text("افزودن ماژول معماری C4") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("نوع ماژول:", style = MaterialTheme.typography.bodySmall)
                    ScrollableRow {
                        listOf("PERSON" to "شخص/کاربر", "SOFTWARE_SYSTEM" to "سیستم نرم افزاری", "CONTAINER" to "کانتینر", "COMPONENT" to "کامپوننت").forEach { (type, label) ->
                            FilterChip(
                                selected = elType == type,
                                onClick = {
                                    elType = type
                                    parentId = null // reset parent context
                                },
                                label = { Text(label) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = elName,
                        onValueChange = { elName = it },
                        label = { Text("نام ماژول (مثلا: API سیستم مالی)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = elDesc,
                        onValueChange = { elDesc = it },
                        label = { Text("توضیحات عملکردی") },
                        modifier = Modifier.fillMaxWidth(),
                        rows = 2
                    )

                    if (elType == "CONTAINER" || elType == "COMPONENT") {
                        OutlinedTextField(
                            value = elTech,
                            onValueChange = { elTech = it },
                            label = { Text("تکنولوژی (مثلا: Postgres / Spring REST)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Scopes pairing check
                    if (elType == "CONTAINER") {
                        val softSystems = elements.filter { it.type == "SOFTWARE_SYSTEM" }
                        Text("متعلق به کدام سیستم نرم‌افزاری؟", style = MaterialTheme.typography.bodySmall)
                        ScrollableRow {
                            softSystems.forEach { sys ->
                                FilterChip(
                                    selected = parentId == sys.id,
                                    onClick = { parentId = sys.id },
                                    label = { Text(sys.name) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    } else if (elType == "COMPONENT") {
                        val containers = elements.filter { it.type == "CONTAINER" }
                        Text("متعلق به کدام Container؟", style = MaterialTheme.typography.bodySmall)
                        ScrollableRow {
                            containers.forEach { con ->
                                FilterChip(
                                    selected = parentId == con.id,
                                    onClick = { parentId = con.id },
                                    label = { Text(con.name) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (elName.isNotBlank()) {
                            // Validation checks for parents
                            if (elType == "CONTAINER" && parentId == null) {
                                Toast.makeText(context, "باید یک سیستم نرم‌افزاری والد را مشخص کنید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (elType == "COMPONENT" && parentId == null) {
                                Toast.makeText(context, "باید کانتینر والد را مشخص کنید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addElement(elType, elName, elDesc, elTech, parentId)
                            showAddElementDialog = false
                        } else {
                            Toast.makeText(context, "نام الزامی است", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("ثبت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddElementDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 3. Add Relationship Dialog
    if (showAddRelationshipDialog) {
        var sourceId by remember { mutableStateOf("") }
        var targetId by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var tech by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddRelationshipDialog = false },
            title = { Text("برقراری ارتباط بین ماژول‌ها") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("مبدا ارتباط (Source):", style = MaterialTheme.typography.bodySmall)
                    ScrollableRow {
                        elements.forEach { el ->
                            val descLabel = when(el.type) {
                                "PERSON" -> "شخص"
                                "SOFTWARE_SYSTEM" -> "سیستم"
                                "CONTAINER" -> "کاتینر"
                                else -> "کامپوننت"
                            }
                            FilterChip(
                                selected = sourceId == el.id,
                                onClick = { sourceId = el.id },
                                label = { Text("${el.name} ($descLabel)") },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("مقصد ارتباط (Target):", style = MaterialTheme.typography.bodySmall)
                    ScrollableRow {
                        elements.forEach { el ->
                            val descLabel = when(el.type) {
                                "PERSON" -> "شخص"
                                "SOFTWARE_SYSTEM" -> "سیستم"
                                "CONTAINER" -> "کاتینر"
                                else -> "کامپوننت"
                            }
                            FilterChip(
                                selected = targetId == el.id,
                                onClick = { targetId = el.id },
                                label = { Text("${el.name} ($descLabel)") },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("شرح رابطه (مثلاً: فراخوانی API / ارسال ایمیل)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tech,
                        onValueChange = { tech = it },
                        label = { Text("پروتکل/تکنولوژی (مثلاً: HTTPS / JSON / gRPC)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sourceId.isNotEmpty() && targetId.isNotEmpty() && desc.isNotEmpty()) {
                            if (sourceId == targetId) {
                                Toast.makeText(context, "مبدا و مقصد نمی‌توانند یکسان باشند", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addRelationship(sourceId, targetId, desc, tech)
                            showAddRelationshipDialog = false
                        } else {
                            Toast.makeText(context, "پر کردن مبدا، مقصد و شرح الزامی است", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("ثبت رابطه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRelationshipDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 4. Copious DSL Playground Dialog
    if (showDslDialog) {
        var dslCode by remember { mutableStateOf(viewModel.exportToDsl()) }
        val clipboardManager = LocalClipboardManager.current

        AlertDialog(
            onDismissRequest = { showDslDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Structurizr DSL")
                    Row {
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(dslCode))
                                Toast.makeText(context, "کپی شد!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کپی")
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "شما می‌توانید کد سناریو سیستم خود را در قالب Structurizr DSL در پایین دریافت کنید یا با کپی کد DSL معتبر به اینجا، آن را بازیابی کنید:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = dslCode,
                        onValueChange = { dslCode = it },
                        label = { Text("کد Structurizr DSL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        rows = 12
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            viewModel.importDsl(dslCode)
                            showDslDialog = false
                            Toast.makeText(context, "کد DSL با موفقیت بارگذاری شد!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "خطا در تجزیه DSL: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text("بارگذاری واردشده (Import)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDslDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }
}

private fun Modifier.fillSomeWidth(): Modifier {
    return this.fillMaxWidth()
}

@Composable
fun ScrollableRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        content = content
    )
}

/**
 * Custom-Drawn interactive drag canvas showing C4 Architecture diagram nodes and linking arrows
 */
@Composable
fun InteractiveC4Canvas(
    activeElements: List<WorkspaceElement>,
    activeRelationships: List<WorkspaceRelationship>,
    selectedElement: WorkspaceElement?,
    selectedRelationship: WorkspaceRelationship?,
    viewType: String,
    onSelectElement: (WorkspaceElement?) -> Unit,
    onSelectRelationship: (WorkspaceRelationship?) -> Unit,
    onElementPositionChanged: (id: String, x: Float, y: Float) -> Unit
) {
    val nodeWidth = 190f
    val nodeHeight = 110f

    // Scroll States so canvases are truly panning and infinite
    val scrollStateX = rememberScrollState()
    val scrollStateY = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollStateX)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(1500.dp) // Large scroll size space
                .verticalScroll(scrollStateY)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onSelectElement(null)
                        onSelectRelationship(null)
                    }
                    .background(Color(0xFFFCFDFE))
            ) {
                // Pre-draw Grid lines for authentic CAD software architectures feel
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSpacing = 40f
                    val strokeColor = Color(0xFFF1F3F5)
                    for (x in 0..(size.width / gridSpacing).toInt()) {
                        drawLine(strokeColor, Offset(x * gridSpacing, 0f), Offset(x * gridSpacing, size.height), strokeWidth = 1f)
                    }
                    for (y in 0..(size.height / gridSpacing).toInt()) {
                        drawLine(strokeColor, Offset(0f, y * gridSpacing), Offset(size.width, y * gridSpacing), strokeWidth = 1f)
                    }
                }

                // Custom Canvas drawing connection lines with directed labels & technologies
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Element centers lookup map
                    val posMap = activeElements.associate {
                        it.id to Offset(it.x + nodeWidth / 2f, it.y + nodeHeight / 2f)
                    }

                    for (relationship in activeRelationships) {
                        val start = posMap[relationship.sourceId]
                        val end = posMap[relationship.targetId]

                        if (start != null && end != null) {
                            val activeStrokeColor = if (selectedRelationship?.id == relationship.id) Color(0xFFFF9800) else C4Relationship
                            val isSelected = selectedRelationship?.id == relationship.id

                            // Draw Line
                            drawLine(
                                color = activeStrokeColor,
                                start = start,
                                end = end,
                                strokeWidth = if (isSelected) 3.5f else 2.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            )

                            // Arrowhead at destination, pulling back slightly
                            val dir = end - start
                            val length = sqrt(dir.x * dir.x + dir.y * dir.y)
                            if (length > 0) {
                                val u = dir / length
                                // Set tip pull-back so it meets elements boundaries perfectly (Radius: half nodeWidth/Height approx)
                                val borderOffset = 80f
                                val tip = end - u * borderOffset

                                val arrowSize = 18f
                                val angle = atan2(u.y, u.x)

                                // Draw triangle tip
                                val path = Path().apply {
                                    moveTo(tip.x, tip.y)
                                    lineTo(
                                        tip.x - arrowSize * cos(angle - PI / 6).toFloat(),
                                        tip.y - arrowSize * sin(angle - PI / 6).toFloat()
                                    )
                                    lineTo(
                                        tip.x - arrowSize * cos(angle + PI / 6).toFloat(),
                                        tip.y - arrowSize * sin(angle + PI / 6).toFloat()
                                    )
                                    close()
                                }
                                drawPath(path, color = activeStrokeColor)
                            }
                        }
                    }
                }

                // Interactive connection labels as separate floating controls so they are clickable
                activeRelationships.forEach { relationship ->
                    val elementsMap = activeElements.associateBy { it.id }
                    val startNode = elementsMap[relationship.sourceId]
                    val endNode = elementsMap[relationship.targetId]

                    if (startNode != null && endNode != null) {
                        val midX = (startNode.x + endNode.x) / 2 + nodeWidth / 4
                        val midY = (startNode.y + endNode.y) / 2 + nodeHeight / 4

                        val isSelected = selectedRelationship?.id == relationship.id

                        Box(
                            modifier = Modifier
                                .absoluteOffset { IntOffset(midX.toInt(), midY.toInt()) }
                                .background(
                                    if (isSelected) Color(0xFFFFF3E0) else Color(0xECFFFFFF),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFFFF9800) else Color(0xFFCCCCCC),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { onSelectRelationship(relationship) }
                                .padding(4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    relationship.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFE65100) else Color.DarkGray
                                )
                                if (relationship.technology.isNotEmpty()) {
                                    Text(
                                        "[${relationship.technology}]",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Rendering active Model Elements as customizable visual block cards
                activeElements.forEach { element ->
                    // Floating drag coordination
                    var dragOffsetX by remember(element.id) { mutableStateOf(0f) }
                    var dragOffsetY by remember(element.id) { mutableStateOf(0f) }

                    val isSelected = selectedElement?.id == element.id

                    // Official Color standard
                    val containerColor = when (element.type) {
                        "PERSON" -> C4Person
                        "SOFTWARE_SYSTEM" -> C4SoftwareSystem
                        "CONTAINER" -> C4Container
                        "COMPONENT" -> C4Component
                        else -> Color.Gray
                    }

                    Box(
                        modifier = Modifier
                            .absoluteOffset {
                                IntOffset(
                                    (element.x + dragOffsetX).toInt(),
                                    (element.y + dragOffsetY).toInt()
                                )
                            }
                            .size(nodeWidth.dp, nodeHeight.dp)
                            .shadowCustom(
                                elevation = if (isSelected) 8.dp else 2.dp,
                                color = if (isSelected) Color(0xFFFFC107) else Color(0x1F000000)
                            )
                            .background(containerColor, RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFFC107) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .pointerInput(element.id) {
                                detectDragGestures(
                                    onDragStart = {
                                        onSelectElement(element)
                                    },
                                    onDragEnd = {
                                        onElementPositionChanged(
                                            element.id,
                                            element.x + dragOffsetX,
                                            element.y + dragOffsetY
                                        )
                                        dragOffsetX = 0f
                                        dragOffsetY = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetX += dragAmount.x
                                        dragOffsetY += dragAmount.y
                                    }
                                )
                            }
                            .clickable { onSelectElement(element) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Elements Icon matching accessibility standards
                            Icon(
                                imageVector = when (element.type) {
                                    "PERSON" -> Icons.Default.Person
                                    "SOFTWARE_SYSTEM" -> Icons.Default.Dns
                                    "CONTAINER" -> Icons.Default.ViewCarousel
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = element.type,
                                tint = Color(0xCAF0F4F8),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = element.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            if (element.type == "CONTAINER" || element.type == "COMPONENT" || element.technology.isNotEmpty()) {
                                Text(
                                    text = if (element.technology.isNotEmpty()) "[${element.technology}]" else "[${element.type}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFD0E1FD),
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            } else {
                                Text(
                                    text = when(element.type) {
                                        "PERSON" -> "[Person]"
                                        "SOFTWARE_SYSTEM" -> "[Software System]"
                                        else -> "[Element]"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xBBFFFFFF),
                                    fontSize = 9.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = element.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF1F5F9),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                fontSize = 9.sp,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom canvas helper to paint shadow smoothly
fun Modifier.shadowCustom(
    elevation: androidx.compose.ui.unit.Dp,
    color: Color
) = this.drawBehind {
    val px = elevation.toPx()
    if (px > 0) {
        drawRect(
            color = color.copy(alpha = 0.15f),
            topLeft = Offset(-px/2, -px/2),
            size = androidx.compose.ui.geometry.Size(this@drawBehind.size.width + px, this@drawBehind.size.height + px)
        )
    }
}

/**
 * Control action dashboard shown at bottom when elements or relations are selected
 */
@Composable
fun SelectionActionPanel(
    selectedElement: WorkspaceElement?,
    selectedRelationship: WorkspaceRelationship?,
    onEditElement: (WorkspaceElement) -> Unit,
    onDeleteElement: (WorkspaceElement) -> Unit,
    onDeleteRelationship: (WorkspaceRelationship) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (selectedElement != null) {
                    Text("انتخاب شد: ${selectedElement.name}", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("نوع: ${selectedElement.type}", style = MaterialTheme.typography.labelMedium)
                } else if (selectedRelationship != null) {
                    Text("رابطه انتخاب شد: ${selectedRelationship.description}", fontWeight = FontWeight.Bold)
                    if (selectedRelationship.technology.isNotEmpty()) {
                        Text("تکنولوژی: ${selectedRelationship.technology}", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedElement != null) {
                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ویرایش")
                    }
                    Button(
                        onClick = { onDeleteElement(selectedElement) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف")
                    }
                } else if (selectedRelationship != null) {
                    Button(
                        onClick = { onDeleteRelationship(selectedRelationship) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف رابطه")
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedElement != null) {
        var elName by remember { mutableStateOf(selectedElement.name) }
        var elDesc by remember { mutableStateOf(selectedElement.description) }
        var elTech by remember { mutableStateOf(selectedElement.technology) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("ویرایش ماژول") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = elName,
                        onValueChange = { elName = it },
                        label = { Text("نام") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = elDesc,
                        onValueChange = { elDesc = it },
                        label = { Text("توضیحات") },
                        modifier = Modifier.fillMaxWidth(),
                        rows = 2
                    )
                    if (selectedElement.type == "CONTAINER" || selectedElement.type == "COMPONENT") {
                        OutlinedTextField(
                            value = elTech,
                            onValueChange = { elTech = it },
                            label = { Text("تکنولوژی") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (elName.isNotBlank()) {
                            onEditElement(selectedElement.copy(name = elName, description = elDesc, technology = elTech))
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("بروزرسانی")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
