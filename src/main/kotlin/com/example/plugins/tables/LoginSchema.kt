package com.example.plugins.tables

import com.example.model.Login
import com.example.model.LoginSqlState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class LoginService(private val connection: Connection) {

    companion object {
        private const val SELECT_ALL_FROM_LOGIN = "SELECT * FROM login"
        private const val CHECK_TABLE_EXISTS = "SELECT to_regclass('public.login')"

        private const val CREATE_TABLE_LOGINS =
            """                
        CREATE TABLE IF NOT EXISTS login (
            id SERIAL PRIMARY KEY, 
            username VARCHAR(255) NOT NULL UNIQUE, 
            password VARCHAR(255) NOT NULL, 
            authToken VARCHAR(255),
            CONSTRAINT fk_users FOREIGN KEY (id) REFERENCES users(eid)
        );
        """
        private const val SELECT_LOGIN_BY_ID_AND_PASSWORD = "SELECT * FROM login WHERE id = ? AND password = ?"
        private const val SELECT_LOGIN_BY_USERID_AND_PASSWORD =
            "SELECT * FROM login WHERE username = ? AND password = ?"
        private const val INSERT_LOGIN = "INSERT INTO login (username, password, authToken) VALUES (?, ?, ?)"
        private const val UPDATE_LOGIN = "UPDATE login SET username = ?, password = ?, authToken = ? WHERE id = ?"
        private const val DELETE_LOGIN = "DELETE FROM login WHERE id = ?"

        private const val INSERT_RANDOM_LOGINS = """
        INSERT INTO login (id, username, password, authToken) VALUES 
        (1, 'ravi.kumar', 'password123', 'token1'),
        (2, 'anita.sharma', 'password123', 'token2'),
        (3, 'suresh.rao', 'password123', 'token3'),
        (4, 'deepika.nair', 'password123', 'token4'),
        (5, 'vijay.singh', 'password123', 'token5'),
        (6, 'neha.joshi', 'password123', 'token6'),
        (7, 'aman.gupta', 'password123', 'token7'),
        (8, 'priya.iyer', 'password123', 'token8'),
        (9, 'rahul.verma', 'password123', 'token9'),
        (10, 'anjali.patil', 'password123', 'token10'),
        (11, 'rajesh.kumar', 'password123', 'token11'),
        (12, 'kavita.mehta', 'password123', 'token12'),
        (13, 'harish.rao', 'password123', 'token13'),
        (14, 'pooja.shah', 'password123', 'token14'),
        (15, 'manoj.desai', 'password123', 'token15'),
        (16, 'rina.gupta', 'password123', 'token16'),
        (17, 'kiran.naik', 'password123', 'token17'),
        (18, 'sneha.chatterjee', 'password123', 'token18'),
        (19, 'arjun.patel', 'password123', 'token19'),
        (20, 'sangeeta.kumari', 'password123', 'token20'),
        (21, 'vikram.sharma', 'password123', 'token21'),
        (22, 'divya.anand', 'password123', 'token22'),
        (23, 'rohit.jain', 'password123', 'token23'),
        (24, 'megha.reddy', 'password123', 'token24'),
        (25, 'akhil.menon', 'password123', 'token25'),
        (26, 'nidhi.verma', 'password123', 'token26'),
        (27, 'siddharth.singh', 'password123', 'token27'),
        (28, 'komal.desai', 'password123', 'token28'),
        (29, 'ajay.bansal', 'password123', 'token29'),
        (30, 'geeta.rao', 'password123', 'token30');
    """
    }

    init {
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(CREATE_TABLE_LOGINS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Function to insert random logins
    suspend fun insertRandomLogins() = withContext(Dispatchers.IO) {
        try {
            val statement = connection.prepareStatement(INSERT_RANDOM_LOGINS)
            val res = statement.executeUpdate()
            return@withContext res
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Function to create a new login entry
    suspend fun create(login: Login): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_LOGIN, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, login.username)
        statement.setString(2, login.password)
        statement.setString(3, login.authToken)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted login")
        }
    }

    // Function to read a login entry by id and password
    suspend fun read(id: Int, password: String): Login = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_LOGIN_BY_ID_AND_PASSWORD)
        statement.setInt(1, id)
        statement.setString(2, password)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val username = resultSet.getString("username")
            val authToken = resultSet.getString("authToken")
            return@withContext Login(id, username, password, authToken)
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun hasUser(username: String, password: String): LoginSqlState = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_LOGIN_BY_USERID_AND_PASSWORD)
        statement.setString(1, username)
        statement.setString(2, password)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val authToken = resultSet.getString("authToken")
            val id = resultSet.getInt("id")
            return@withContext LoginSqlState(login = Login(id, username, password, authToken), userFound = true)
        } else {
            return@withContext LoginSqlState(userFound = false)
        }
    }

    // Function to update a login entry
    suspend fun update(id: Int, login: Login) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_LOGIN)
        statement.setString(1, login.username)
        statement.setString(2, login.password)
        statement.setString(3, login.authToken)
        statement.setInt(4, id)
        statement.executeUpdate()
    }

    // Function to delete a login entry
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_LOGIN)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    suspend fun getAllUsers(): List<Login> = withContext(Dispatchers.IO) {
        val users = mutableListOf<Login>()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(SELECT_ALL_FROM_LOGIN)

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val username = resultSet.getString("username")
            val password = resultSet.getString("password")
            val authToken = resultSet.getString("authToken")
            users.add(Login(id, username, password, authToken))
        }

        resultSet.close()
        statement.close()

        return@withContext users
    }

    // Function to check if the login table is created and print its contents
    suspend fun checkTableStatus() = withContext(Dispatchers.IO) {
        try {
            // Check if the table exists
            val tableCheckStatement = connection.prepareStatement(CHECK_TABLE_EXISTS)
            val tableExistsResultSet = tableCheckStatement.executeQuery()
            var tableExists = false

            if (tableExistsResultSet.next()) {
                tableExists = tableExistsResultSet.getString(1) != null
            }

            // Print the table status
            if (tableExists) {
                println("Table 'login' exists.")

                // Print Table Contents
                printTableContents()
            } else {
                println("Table 'login' does not exist.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to print all contents of the login table
    private suspend fun printTableContents() = withContext(Dispatchers.IO) {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(SELECT_ALL_FROM_LOGIN)

        println("Contents of 'login' table:")
        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val username = resultSet.getString("username")
            val password = resultSet.getString("password")
            val authToken = resultSet.getString("authToken")
            println("ID: $id, Username: $username, Password: $password, AuthToken: $authToken")
        }
    }

}