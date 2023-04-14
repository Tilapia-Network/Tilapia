package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import kotlinx.coroutines.runBlocking
import net.tiapiamc.config.Config
import net.tiapiamc.data.DatabaseManager
import net.tilapiamc.communication.DatabaseLogin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isTrue
import strikt.assertions.startsWith
import java.sql.DriverManager
import java.util.*

class DatabaseSessionTest: StringSpec() {
    object TestTable: Table() {
        val testKey = text("testKey")
    }

    init {
        lateinit var loginData: DatabaseLogin
        val db = Database.connect(Config.DATABASE_URL, user = Config.DATABASE_USER, password = Config.DATABASE_PASSWORD)
        DatabaseManager.database = db


        "Create Database Session" {
            runBlocking {
                loginData = DatabaseManager.createSession("127.0.0.1", listOf("test_database_1", "test_database_2"))
            }
        }

        "Check Login Name" {
            expectThat(loginData.username)
                .isA<String>()
                .startsWith("TEMP-")
            println("Login: ${loginData.username}, Password: ${loginData.password}")
        }

        "Verify Invalid Login" {
            expectThrows<Throwable> {
                DriverManager.getConnection(Config.DATABASE_URL, loginData.username, "AAAA")
            }
            expectThrows<Throwable> {
                DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, "AAAA")
            }
            expectThrows<Throwable> {
                transaction(Database.connect({ DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, "AAAA") })) {

                }
            }
        }
        "Login Database 1" {
            DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, loginData.password)
        }
        "Login Database 2" {
            DriverManager.getConnection(Config.DATABASE_URL + "/test_database_2", loginData.username, loginData.password)
        }
        "Create Table" {
            transaction(Database.connect({ DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, loginData.password) })) {
                SchemaUtils.create(TestTable)
            }
        }
        val key = UUID.randomUUID().toString()
        "Insert Data" {
            transaction(Database.connect({ DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, loginData.password) })) {
                TestTable.insert {
                    it[testKey] = key
                }
            }
        }

        "Fetch Data" {
            transaction(Database.connect({ DriverManager.getConnection(Config.DATABASE_URL + "/test_database_1", loginData.username, loginData.password) })) {
                expectThat(TestTable.select { TestTable.testKey.eq(key) }.any())
                    .isA<Boolean>()
                    .isTrue()
            }
        }

        "Clsoe Session" {
            runBlocking {
                DatabaseManager.closeSession(loginData.sessionId)
            }
        }

        afterSpec {
            transaction(db) {
                SchemaUtils.dropDatabase("test_database_1", "test_database_2")
            }
        }
    }
    override fun testCaseOrder(): TestCaseOrder {
        return TestCaseOrder.Sequential
    }



}