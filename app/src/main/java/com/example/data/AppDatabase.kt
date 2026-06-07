package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Workspace::class, WorkspaceElement::class, WorkspaceRelationship::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "structurizr_c4_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.workspaceDao())
                }
            }
        }

        private suspend fun populateDatabase(dao: WorkspaceDao) {
            // Pre-populate with a gorgeous default C4 workspace (e.g., "Internet Banking System" style standard)
            val wsId = dao.insertWorkspace(
                Workspace(
                    name = "Internet Banking System",
                    description = "An example workspace demonstrating Simon Brown's C4 architecture model."
                )
            ).toInt()

            // People
            val customer = WorkspaceElement(
                id = "cus_${wsId}_customer",
                workspaceId = wsId,
                type = "PERSON",
                name = "Personal Banking Customer",
                description = "A customer of the bank who wants to view accounts and make payments.",
                x = 150f,
                y = 120f
            )
            val support = WorkspaceElement(
                id = "cus_${wsId}_support",
                workspaceId = wsId,
                type = "PERSON",
                name = "Customer Service Staff",
                description = "Internal bank staff who assist customers with banking issues.",
                x = 150f,
                y = 520f
            )

            // Systems
            val ibSystem = WorkspaceElement(
                id = "sys_${wsId}_ib",
                workspaceId = wsId,
                type = "SOFTWARE_SYSTEM",
                name = "Internet Banking System",
                description = "Allows customers to view information about their bank accounts and make payments.",
                x = 550f,
                y = 120f
            )
            val mainframe = WorkspaceElement(
                id = "sys_${wsId}_mainframe",
                workspaceId = wsId,
                type = "SOFTWARE_SYSTEM",
                name = "Mainframe Banking System",
                description = "Stores all core bank accounts, balances, transactions, and customer details.",
                x = 550f,
                y = 520f
            )
            val mailSystem = WorkspaceElement(
                id = "sys_${wsId}_mail",
                workspaceId = wsId,
                type = "SOFTWARE_SYSTEM",
                name = "E-mail System",
                description = "The internal Microsoft Exchange e-mail server system.",
                x = 950f,
                y = 120f
            )

            // Containers (inside Internet Banking System)
            val webApp = WorkspaceElement(
                id = "con_${wsId}_web",
                workspaceId = wsId,
                type = "CONTAINER",
                name = "Single-Page Web Application",
                description = "Delivers all Internet banking functionality to customers via their web browser.",
                technology = "React & Redux",
                parentId = ibSystem.id,
                x = 350f,
                y = 180f
            )
            val mobileApp = WorkspaceElement(
                id = "con_${wsId}_mobile",
                workspaceId = wsId,
                type = "CONTAINER",
                name = "Mobile Application",
                description = "Provides a subset of Internet banking functionality to customers via their mobile device.",
                technology = "Kotlin & Jetpack Compose",
                parentId = ibSystem.id,
                x = 350f,
                y = 420f
            )
            val apiApp = WorkspaceElement(
                id = "con_${wsId}_api",
                workspaceId = wsId,
                type = "CONTAINER",
                name = "API Application",
                description = "Provides banking functionality via a JSON/HTTPS API interface.",
                technology = "Spring Boot & REST",
                parentId = ibSystem.id,
                x = 750f,
                y = 180f
            )
            val dbApp = WorkspaceElement(
                id = "con_${wsId}_db",
                workspaceId = wsId,
                type = "CONTAINER",
                name = "Database",
                description = "Stores user login credentials, encrypted passwords, transaction logs, etc.",
                technology = "PostgreSQL Database Schema",
                parentId = ibSystem.id,
                x = 750f,
                y = 420f
            )

            // Components (inside API Application)
            val signupController = WorkspaceElement(
                id = "comp_${wsId}_signup",
                workspaceId = wsId,
                type = "COMPONENT",
                name = "Sign-in Controller",
                description = "Allows users to sign in and handles session verification.",
                technology = "Spring REST Controller",
                parentId = apiApp.id,
                x = 200f,
                y = 160f
            )
            val securityComp = WorkspaceElement(
                id = "comp_${wsId}_security",
                workspaceId = wsId,
                type = "COMPONENT",
                name = "Security Component",
                description = "Authenticates credentials and generates JSON web tokens (JWTs).",
                technology = "Spring Security Bean",
                parentId = apiApp.id,
                x = 420f,
                y = 160f
            )
            val accountSummary = WorkspaceElement(
                id = "comp_${wsId}_account",
                workspaceId = wsId,
                type = "COMPONENT",
                name = "Accounts Service",
                description = "Aggregates account information and formats transaction history.",
                technology = "Java Service Bean",
                parentId = apiApp.id,
                x = 200f,
                y = 420f
            )
            val dbRepo = WorkspaceElement(
                id = "comp_${wsId}_repo",
                workspaceId = wsId,
                type = "COMPONENT",
                name = "Data Repository Component",
                description = "Reads from and writes records to the PostgreSQL database.",
                technology = "Spring Data JPA Repository",
                parentId = apiApp.id,
                x = 640f,
                y = 420f
            )

            // Insert Elements
            dao.insertElement(customer)
            dao.insertElement(support)
            dao.insertElement(ibSystem)
            dao.insertElement(mainframe)
            dao.insertElement(mailSystem)

            dao.insertElement(webApp)
            dao.insertElement(mobileApp)
            dao.insertElement(apiApp)
            dao.insertElement(dbApp)

            dao.insertElement(signupController)
            dao.insertElement(securityComp)
            dao.insertElement(accountSummary)
            dao.insertElement(dbRepo)

            // Context Level Relationships
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_cus_ib", wsId, customer.id, ibSystem.id, "Uses", "HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_ib_mail", wsId, ibSystem.id, mailSystem.id, "Sends e-mails using", "SMTP"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_ib_mf", wsId, ibSystem.id, mainframe.id, "Gets balance & payments from", "XML/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_cus_mail", wsId, mailSystem.id, customer.id, "Delivers e-mails to", "SMTP"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_sup_ib", wsId, support.id, ibSystem.id, "Assists customers using", "HTTPS"))

            // Container Level Relationships
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_cus_web", wsId, customer.id, webApp.id, "Visits banking portal using", "HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_cus_mob", wsId, customer.id, mobileApp.id, "Uses banking activities on", "HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_web_api", wsId, webApp.id, apiApp.id, "Makes API calls to", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_mob_api", wsId, mobileApp.id, apiApp.id, "Makes API calls to", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_api_db", wsId, apiApp.id, dbApp.id, "Reads from & writes to", "JDBC"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_api_mf", wsId, apiApp.id, mainframe.id, "Sends offline processing commands to", "XML/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_api_mail", wsId, apiApp.id, mailSystem.id, "Triggers e-mails through", "SMTP"))

            // Component Level Relationships
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_web_signup", wsId, webApp.id, signupController.id, "Sends auth requests to", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_mob_signup", wsId, mobileApp.id, signupController.id, "Sends auth requests to", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_web_acc", wsId, webApp.id, accountSummary.id, "Fetches account details from", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_mob_acc", wsId, mobileApp.id, accountSummary.id, "Fetches account details from", "JSON/HTTPS"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_signup_sec", wsId, signupController.id, securityComp.id, "Verifies session token through", "Method Calls"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_sec_repo", wsId, securityComp.id, dbRepo.id, "Loads user profiles using", "Spring Injection"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_acc_repo", wsId, accountSummary.id, dbRepo.id, "Queries historical records using", "Spring Injection"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_repo_db", wsId, dbRepo.id, dbApp.id, "Performs CRUD queries on", "JDBC/SQL"))
            dao.insertRelationship(WorkspaceRelationship("rel_${wsId}_acc_mf", wsId, accountSummary.id, mainframe.id, "Checks real-time ledger status through", "XML/HTTPS"))
        }
    }
}
