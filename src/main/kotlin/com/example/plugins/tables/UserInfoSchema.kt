package com.example.plugins.tables

import com.example.model.EmployeeGender
import com.example.model.UserInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Date
import java.sql.Statement


class UserInfoService(private val connection: Connection) {

    companion object {
        private const val CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS users (\n" +
                    "    eid SERIAL PRIMARY KEY,\n" +
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
        private const val SELECT_USER_BY_ID =
            "SELECT eid, name, title, email, age, birthday, photo_url, salary, employee_gender, present_today, salary_credited FROM users WHERE eid = ?"
        private const val INSERT_USER =
            "INSERT INTO users (name, title, email, age, birthday, photo_url, salary, employee_gender, present_today, salary_credited) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_USER =
            "UPDATE users SET name = ?, title = ?, email = ?, age = ?, birthday = ?, photo_url = ?, salary = ?, employee_gender = ?, present_today = ?, salary_credited = ? WHERE eid = ?"
        private const val DELETE_USER = "DELETE FROM users WHERE eid = ?"
        private const val INSERT_RANDOM_USERS = """
            INSERT INTO users (eid, name, title, email, age, birthday, photo_url, salary, employee_gender, present_today, salary_credited) VALUES 
        (1, 'Ravi Kumar', 'Software Engineer', 'ravi.kumar@example.com', 28, '1995-03-15', 'https://example.com/photos/ravi', 600000, 'MALE', true, false),
        (2, 'Anita Sharma', 'QA Engineer', 'anita.sharma@example.com', 32, '1991-07-22', 'https://example.com/photos/anita', 550000, 'FEMALE', true, false),
        (3, 'Suresh Rao', 'Project Manager', 'suresh.rao@example.com', 40, '1983-09-11', 'https://example.com/photos/suresh', 1200000, 'MALE', true, true),
        (4, 'Deepika Nair', 'Software Engineer', 'deepika.nair@example.com', 27, '1996-02-16', 'https://example.com/photos/deepika', 600000, 'FEMALE', true, true),
        (5, 'Vijay Singh', 'Product Owner', 'vijay.singh@example.com', 35, '1988-11-02', 'https://example.com/photos/vijay', 1300000, 'MALE', true, false),
        (6, 'Neha Joshi', 'Scrum Master', 'neha.joshi@example.com', 30, '1993-05-04', 'https://example.com/photos/neha', 700000, 'FEMALE', false, true),
        (7, 'Aman Gupta', 'DevOps Engineer', 'aman.gupta@example.com', 29, '1994-12-19', 'https://example.com/photos/aman', 800000, 'MALE', true, false),
        (8, 'Priya Iyer', 'Software Engineer', 'priya.iyer@example.com', 26, '1997-06-27', 'https://example.com/photos/priya', 600000, 'FEMALE', true, false),
        (9, 'Rahul Verma', 'Systems Analyst', 'rahul.verma@example.com', 33, '1990-08-30', 'https://example.com/photos/rahul', 750000, 'MALE', true, true),
        (10, 'Anjali Patil', 'Business Analyst', 'anjali.patil@example.com', 31, '1992-10-05', 'https://example.com/photos/anjali', 800000, 'FEMALE', true, true),
        (11, 'Rajesh Kumar', 'Software Engineer', 'rajesh.kumar@example.com', 29, '1994-04-21', 'https://example.com/photos/rajesh', 600000, 'MALE', false, false),
        (12, 'Kavita Mehta', 'QA Engineer', 'kavita.mehta@example.com', 28, '1995-01-10', 'https://example.com/photos/kavita', 550000, 'FEMALE', true, true),
        (13, 'Harish Rao', 'Architect', 'harish.rao@example.com', 45, '1978-03-18', 'https://example.com/photos/harish', 1500000, 'MALE', true, false),
        (14, 'Pooja Shah', 'UX Designer', 'pooja.shah@example.com', 26, '1997-09-15', 'https://example.com/photos/pooja', 500000, 'FEMALE', true, false),
        (15, 'Manoj Desai', 'Software Engineer', 'manoj.desai@example.com', 30, '1993-11-25', 'https://example.com/photos/manoj', 600000, 'MALE', true, true),
        (16, 'Rina Gupta', 'Product Manager', 'rina.gupta@example.com', 35, '1988-07-09', 'https://example.com/photos/rina', 1400000, 'FEMALE', true, false),
        (17, 'Kiran Naik', 'DevOps Engineer', 'kiran.naik@example.com', 34, '1989-04-13', 'https://example.com/photos/kiran', 800000, 'MALE', true, false),
        (18, 'Sneha Chatterjee', 'Software Engineer', 'sneha.chatterjee@example.com', 25, '1998-02-20', 'https://example.com/photos/sneha', 600000, 'FEMALE', false, true),
        (19, 'Arjun Patel', 'Project Manager', 'arjun.patel@example.com', 38, '1985-05-28', 'https://example.com/photos/arjun', 1200000, 'MALE', true, true),
        (20, 'Sangeeta Kumari', 'Software Engineer', 'sangeeta.kumari@example.com', 27, '1996-11-14', 'https://example.com/photos/sangeeta', 600000, 'FEMALE', true, true),
        (21, 'Vikram Sharma', 'QA Engineer', 'vikram.sharma@example.com', 28, '1995-06-12', 'https://example.com/photos/vikram', 550000, 'MALE', true, false),
        (22, 'Divya Anand', 'Software Engineer', 'divya.anand@example.com', 24, '1999-03-23', 'https://example.com/photos/divya', 600000, 'FEMALE', true, false),
        (23, 'Rohit Jain', 'Product Owner', 'rohit.jain@example.com', 35, '1988-10-10', 'https://example.com/photos/rohit', 1300000, 'MALE', true, true),
        (24, 'Megha Reddy', 'Scrum Master', 'megha.reddy@example.com', 31, '1992-01-07', 'https://example.com/photos/megha', 700000, 'FEMALE', true, false),
        (25, 'Akhil Menon', 'DevOps Engineer', 'akhil.menon@example.com', 28, '1995-07-26', 'https://example.com/photos/akhil', 800000, 'MALE', false, true),
        (26, 'Nidhi Verma', 'HR Manager', 'nidhi.verma@example.com', 33, '1990-12-05', 'https://example.com/photos/nidhi', 900000, 'FEMALE', true, true),
        (27, 'Siddharth Singh', 'Software Engineer', 'siddharth.singh@example.com', 29, '1994-09-01', 'https://example.com/photos/siddharth', 600000, 'MALE', true, true),
        (28, 'Komal Desai', 'Finance Analyst', 'komal.desai@example.com', 27, '1996-05-16', 'https://example.com/photos/komal', 600000, 'FEMALE', true, false),
        (29, 'Ajay Bansal', 'Software Engineer', 'ajay.bansal@example.com', 28, '1995-01-12', 'https://example.com/photos/ajay', 600000, 'MALE', false, true),
        (30, 'Geeta Rao', 'Systems Analyst', 'geeta.rao@example.com', 32, '1991-08-25', 'https://example.com/photos/geeta', 750000, 'FEMALE', true, true);
        """

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USERS)
    }

    // Function to insert random users
    suspend fun insertRandomUsers() = withContext(Dispatchers.IO) {
        try {
            val DELETE_ALL_USERS = "DELETE FROM users"
            val statementDelAll = connection.prepareStatement(DELETE_ALL_USERS)
            statementDelAll.executeUpdate()
            val statement = connection.prepareStatement(INSERT_RANDOM_USERS)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun getAllUsers(): List<UserInformation> = withContext(Dispatchers.IO) {
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM users")

            val userList = mutableListOf<UserInformation>()

            while (resultSet.next()) {
                val user = UserInformation(
                    eid = resultSet.getInt("eid"),  // Make sure the column name "eid" matches your table definition
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
                userList.add(user)
            }

            return@withContext userList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun createUser(user: UserInformation): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.name)
        statement.setString(2, user.title)
        statement.setString(3, user.email)
        statement.setInt(4, user.age)
        statement.setDate(5, Date.valueOf(user.birthday))
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
                eid = resultSet.getInt("eid"),
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
        statement.setDate(5, Date.valueOf(user.birthday))
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