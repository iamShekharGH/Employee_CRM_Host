package com.example.plugins

import com.example.model.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class LoginService(private val connection: Connection) {

    companion object {
        private const val CREATE_TABLE_LOGINS =
            "CREATE TABLE login (" +
                    "id INT PRIMARY KEY REFERENCES users(id), " +
                    "username VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "authToken VARCHAR(255);"
        private const val SELECT_LOGIN_BY_ID_AND_PASSWORD = "SELECT * FROM login WHERE id = ? AND password = ?"
        private const val INSERT_LOGIN = "INSERT INTO login (username, password, authToken) VALUES (?, ?, ?)"
        private const val UPDATE_LOGIN = "UPDATE login SET username = ?, password = ?, authToken = ? WHERE id = ?"
        private const val DELETE_LOGIN = "DELETE FROM login WHERE id = ?"
        private const val INSERT_RANDOM_LOGINS = """
            INSERT INTO login (username, password, authToken) VALUES 
            ('ravi.kumar', 'password123', 'token1'),
            ('anita.sharma', 'password123', 'token2'),
            ('suresh.rao', 'password123', 'token3'),
            ('deepika.nair', 'password123', 'token4'),
            ('vijay.singh', 'password123', 'token5'),
            ('neha.joshi', 'password123', 'token6'),
            ('aman.gupta', 'password123', 'token7'),
            ('priya.iyer', 'password123', 'token8'),
            ('rahul.verma', 'password123', 'token9'),
            ('anjali.patil', 'password123', 'token10'),
            ('rajesh.kumar', 'password123', 'token11'),
            ('kavita.mehta', 'password123', 'token12'),
            ('harish.rao', 'password123', 'token13'),
            ('pooja.shah', 'password123', 'token14'),
            ('manoj.desai', 'password123', 'token15'),
            ('rina.gupta', 'password123', 'token16'),
            ('kiran.naik', 'password123', 'token17'),
            ('sneha.chatterjee', 'password123', 'token18'),
            ('arjun.patel', 'password123', 'token19'),
            ('sangeeta.kumari', 'password123', 'token20'),
            ('vikram.sharma', 'password123', 'token21'),
            ('divya.anand', 'password123', 'token22'),
            ('rohit.jain', 'password123', 'token23'),
            ('megha.reddy', 'password123', 'token24'),
            ('akhil.menon', 'password123', 'token25'),
            ('nidhi.verma', 'password123', 'token26'),
            ('siddharth.singh', 'password123', 'token27'),
            ('komal.desai', 'password123', 'token28'),
            ('ajay.bansal', 'password123', 'token29'),
            ('geeta.rao', 'password123', 'token30');
        """
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_LOGINS)
    }

    // Function to insert random logins
    suspend fun insertRandomLogins() = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_RANDOM_LOGINS)
        statement.executeUpdate()
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


}