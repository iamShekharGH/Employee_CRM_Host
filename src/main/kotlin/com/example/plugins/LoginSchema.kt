package com.example.plugins

import com.example.model.EmployeeGender
import com.example.model.UserInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement


class LoginService(private val connection: Connection) {

    companion object {
        private const val CREATE_TABLE_USERS =
            "CREATE TABLE users (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR(255),\n" +
                    "    title VARCHAR(255),\n" +
                    "    email VARCHAR(255),\n" +
                    "    age INT,\n" +
                    "    birthday DATE,\n" +
                    "    photo_url TEXT,\n" +
                    "    salary BIGINT,\n" +
                    "    employee_gender VARCHAR(10),\n" +
                    "    present_today BOOLEAN,\n" +
                    "    salary_credited BOOLEAN\n" +
                    ");"
        private const val SELECT_USER_BY_ID = "SELECT id, name, title, email, age, birthday, photo_url, salary, employee_gender, present_today, salary_credited FROM users WHERE id = ?"
        private const val INSERT_USER = "INSERT INTO users (name, title, email, age, birthday, photo_url, salary, employee_gender, present_today, salary_credited) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_USER = "UPDATE users SET name = ?, title = ?, email = ?, age = ?, birthday = ?, photo_url = ?, salary = ?, employee_gender = ?, present_today = ?, salary_credited = ? WHERE id = ?"
        private const val DELETE_USER = "DELETE FROM users WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USERS)
    }

    suspend fun createUser(user: UserInformation): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.name)
        statement.setString(2, user.title)
        statement.setString(3, user.email)
        statement.setInt(4, user.age)
        statement.setDate(5, java.sql.Date.valueOf(user.birthday))
        statement.setString(6, user.photoUrl)
        statement.setLong(7, user.salary)
        statement.setString(8, user.employeeGender.name)
        statement.setBoolean(9, user.presentToday)
        statement.setBoolean(10, user.salaryCredited)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }

    // Read a user
    suspend fun readUser(id: Int): UserInformation = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext UserInformation(
                eid = resultSet.getInt("id"),
                name = resultSet.getString("name"),
                title = resultSet.getString("title"),
                email = resultSet.getString("email"),
                age = resultSet.getInt("age"),
                birthday = resultSet.getDate("birthday").toString(),
                photoUrl = resultSet.getString("photo_url"),
                salary = resultSet.getLong("salary"),
                employeeGender = EmployeeGender.valueOf(resultSet.getString("employee_gender")),
                presentToday = resultSet.getBoolean("present_today"),
                salaryCredited = resultSet.getBoolean("salary_credited")
            )
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a user
    suspend fun updateUser(id: Int, user: UserInformation) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setString(1, user.name)
        statement.setString(2, user.title)
        statement.setString(3, user.email)
        statement.setInt(4, user.age)
        statement.setDate(5, java.sql.Date.valueOf(user.birthday))
        statement.setString(6, user.photoUrl)
        statement.setLong(7, user.salary)
        statement.setString(8, user.employeeGender.name)
        statement.setBoolean(9, user.presentToday)
        statement.setBoolean(10, user.salaryCredited)
        statement.setInt(11, id)
        statement.executeUpdate()
    }

    // Delete a user
    suspend fun deleteUser(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

}