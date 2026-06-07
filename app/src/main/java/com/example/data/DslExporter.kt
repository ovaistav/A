package com.example.data

object DslExporter {

    fun exportToDsl(
        workspace: Workspace,
        elements: List<WorkspaceElement>,
        relationships: List<WorkspaceRelationship>
    ): String {
        val sb = StringBuilder()
        sb.append("workspace \"${workspace.name}\" \"${workspace.description}\" {\n\n")
        sb.append("    model {\n")

        // 1. Export People
        val people = elements.filter { it.type == "PERSON" }
        for (person in people) {
            val elementId = sanitizeId(person.name)
            sb.append("        $elementId = person \"${person.name}\" \"${person.description}\"\n")
        }
        if (people.isNotEmpty()) sb.append("\n")

        // 2. Export Software Systems and their child Containers
        val systems = elements.filter { it.type == "SOFTWARE_SYSTEM" }
        for (system in systems) {
            val systemId = sanitizeId(system.name)
            sb.append("        $systemId = softwareSystem \"${system.name}\" \"${system.description}\" {\n")

            // Containers within this system
            val containers = elements.filter { it.type == "CONTAINER" && it.parentId == system.id }
            for (container in containers) {
                val containerId = sanitizeId(container.name)
                val techPart = if (container.technology.isNotEmpty()) "\"${container.technology}\"" else "\"\""
                sb.append("            $containerId = container \"${container.name}\" \"${container.description}\" $techPart {\n")

                // Components within this container
                val components = elements.filter { it.type == "COMPONENT" && it.parentId == container.id }
                for (component in components) {
                    val componentId = sanitizeId(component.name)
                    val compTechPart = if (component.technology.isNotEmpty()) "\"${component.technology}\"" else "\"\""
                    sb.append("                $componentId = component \"${component.name}\" \"${component.description}\" $compTechPart\n")
                }
                sb.append("            }\n")
            }
            sb.append("        }\n\n")
        }

        // 3. Export Relationships
        // Map elements to their DSL identifiers
        val idMap = elements.associate { it.id to sanitizeId(it.name) }
        for (rel in relationships) {
            val sourceDslId = idMap[rel.sourceId]
            val targetDslId = idMap[rel.targetId]
            if (sourceDslId != null && targetDslId != null) {
                val descPart = "\"${rel.description}\""
                val techPart = if (rel.technology.isNotEmpty()) " \"${rel.technology}\"" else ""
                sb.append("        $sourceDslId -> $targetDslId $descPart$techPart\n")
            }
        }

        sb.append("    }\n\n")

        // 4. Export standard views block matching Structurizr's representation
        sb.append("    views {\n")
        for (system in systems) {
            val systemId = sanitizeId(system.name)
            sb.append("        systemContext $systemId \"SystemContext_${systemId}\" \"System Context for ${system.name}\" {\n")
            sb.append("            include *\n")
            sb.append("            autolayout lr\n")
            sb.append("        }\n\n")

            val containers = elements.filter { it.type == "CONTAINER" && it.parentId == system.id }
            if (containers.isNotEmpty()) {
                sb.append("        container $systemId \"Containers_${systemId}\" \"Containers in ${system.name}\" {\n")
                sb.append("            include *\n")
                sb.append("            autolayout lr\n")
                sb.append("        }\n\n")

                for (container in containers) {
                    val containerId = sanitizeId(container.name)
                    val components = elements.filter { it.type == "COMPONENT" && it.parentId == container.id }
                    if (components.isNotEmpty()) {
                        sb.append("        component $containerId \"Components_${containerId}\" \"Components in ${container.name}\" {\n")
                        sb.append("            include *\n")
                        sb.append("            autolayout lr\n")
                        sb.append("        }\n\n")
                    }
                }
            }
        }

        sb.append("        theme default\n")
        sb.append("    }\n")
        sb.append("}\n")

        return sb.toString()
    }

    private fun sanitizeId(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9]"), "")
            .replaceFirstChar { it.lowercase() }
            .takeIf { it.isNotEmpty() } ?: "element"
    }

    /**
     * Highly responsive import parser that parses simplified DSL structure
     */
    fun importFromDsl(dsl: String, workspaceId: Int): Pair<List<WorkspaceElement>, List<WorkspaceRelationship>> {
        val elements = mutableListOf<WorkspaceElement>()
        val relationships = mutableListOf<WorkspaceRelationship>()

        val lines = dsl.lines()
        var currentSystemId: String? = null
        var currentContainerId: String? = null

        // DSL naming to real IDs map
        val dslIdToRealId = mutableMapOf<String, String>()

        for (line in lines) {
            val rLine = line.trim()
            if (rLine.isEmpty()) continue

            // Parse Person
            // user = person "User Name" "Description"
            if (rLine.contains(" = person ") || rLine.contains("=person")) {
                val parts = rLine.split("=", limit = 2)
                val dslId = parts[0].trim()
                val rest = parts[1].trim()
                val inner = rest.substringAfter("person").trim()
                val (name, desc) = parseQuotes(inner)
                val realId = "per_${workspaceId}_${System.currentTimeMillis()}_${elements.size}"
                dslIdToRealId[dslId] = realId
                elements.add(
                    WorkspaceElement(
                        id = realId,
                        workspaceId = workspaceId,
                        type = "PERSON",
                        name = name,
                        description = desc,
                        x = 100f + elements.size * 30,
                        y = 100f
                    )
                )
            }
            // Parse SoftwareSystem start
            // ib = softwareSystem "Web UI" "Some desc" {
            else if (rLine.contains(" = softwareSystem ") || rLine.contains("=softwareSystem")) {
                val parts = rLine.split("=", limit = 2)
                val dslId = parts[0].trim()
                val rest = parts[1].trim()
                val inner = rest.substringAfter("softwareSystem").trim()
                val (name, desc) = parseQuotes(inner)
                val realId = "sys_${workspaceId}_${System.currentTimeMillis()}_${elements.size}"
                dslIdToRealId[dslId] = realId
                currentSystemId = realId
                elements.add(
                    WorkspaceElement(
                        id = realId,
                        workspaceId = workspaceId,
                        type = "SOFTWARE_SYSTEM",
                        name = name,
                        description = desc,
                        x = 150f + elements.size * 40,
                        y = 150f
                    )
                )
            }
            // Parse Container start
            // web = container "Mobile App" "Some Desc" "React Native" {
            else if (rLine.contains(" = container ") || rLine.contains("=container")) {
                val parts = rLine.split("=", limit = 2)
                val dslId = parts[0].trim()
                val rest = parts[2].substringAfter("container").trim() // Wait, parts of splitting '=' is 2. Let's make it robust
                val inner = parts[1].substringAfter("container").trim()
                val quotesList = extractAllQuotes(inner)
                val name = quotesList.getOrNull(0) ?: "Container"
                val desc = quotesList.getOrNull(1) ?: ""
                val tech = quotesList.getOrNull(2) ?: ""
                val realId = "con_${workspaceId}_${System.currentTimeMillis()}_${elements.size}"
                dslIdToRealId[dslId] = realId
                currentContainerId = realId
                elements.add(
                    WorkspaceElement(
                        id = realId,
                        workspaceId = workspaceId,
                        type = "CONTAINER",
                        name = name,
                        description = desc,
                        technology = tech,
                        parentId = currentSystemId,
                        x = 200f + elements.size * 30,
                        y = 200f
                    )
                )
            }
            // Parse Component
            // db = component "Repo" "Reads data" "Postgres"
            else if (rLine.contains(" = component ") || rLine.contains("=component")) {
                val parts = rLine.split("=", limit = 2)
                val dslId = parts[0].trim()
                val inner = parts[1].substringAfter("component").trim()
                val quotesList = extractAllQuotes(inner)
                val name = quotesList.getOrNull(0) ?: "Component"
                val desc = quotesList.getOrNull(1) ?: ""
                val tech = quotesList.getOrNull(2) ?: ""
                val realId = "comp_${workspaceId}_${System.currentTimeMillis()}_${elements.size}"
                dslIdToRealId[dslId] = realId
                elements.add(
                    WorkspaceElement(
                        id = realId,
                        workspaceId = workspaceId,
                        type = "COMPONENT",
                        name = name,
                        description = desc,
                        technology = tech,
                        parentId = currentContainerId,
                        x = 250f + elements.size * 30,
                        y = 250f
                    )
                )
            }
            // Close block of System or Container
            else if (rLine == "}") {
                if (currentContainerId != null) {
                    currentContainerId = null
                } else if (currentSystemId != null) {
                    currentSystemId = null
                }
            }
            // Parse Relationships
            // user -> ib "Uses" "HTTPS"
            else if (rLine.contains("->")) {
                val parts = rLine.split("->")
                if (parts.size == 2) {
                    val sourceDsl = parts[0].trim()
                    val targetPart = parts[1].trim()
                    val targetDsl = targetPart.substringBefore("\"").trim().replace(Regex("[^a-zA-Z0-9]"), "")
                    val innerQuotes = extractAllQuotes(targetPart)
                    val desc = innerQuotes.getOrNull(0) ?: "Uses"
                    val tech = innerQuotes.getOrNull(1) ?: ""

                    // Add a deferred relationship using the DSL ID names
                    // Since it depends on resolved real IDs, we will map them
                    relationships.add(
                        WorkspaceRelationship(
                            id = "rel_${workspaceId}_${System.currentTimeMillis()}_${relationships.size}",
                            workspaceId = workspaceId,
                            sourceId = sourceDsl, // Temporary place DSL ID, we resolve later
                            targetId = targetDsl, // Temporary place DSL ID, we resolve later
                            description = desc,
                            technology = tech
                        )
                    )
                }
            }
        }

        // Map relationships from DSL names to Real IDs
        val resolvedRelationships = relationships.mapNotNull { rel ->
            val realSource = dslIdToRealId[rel.sourceId]
            val realTarget = dslIdToRealId[rel.targetId]
            if (realSource != null && realTarget != null) {
                rel.copy(sourceId = realSource, targetId = realTarget)
            } else {
                null
            }
        }

        return Pair(elements, resolvedRelationships)
    }

    private fun parseQuotes(input: String): Pair<String, String> {
        val list = extractAllQuotes(input)
        return Pair(list.getOrNull(0) ?: "Element", list.getOrNull(1) ?: "")
    }

    private fun extractAllQuotes(input: String): List<String> {
        val matches = Regex("\"([^\"]*)\"").findAll(input)
        return matches.map { it.groupValues[1] }.toList()
    }
}
