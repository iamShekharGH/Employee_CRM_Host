package com.example.plugins.tables

import com.example.model.Employee
import com.example.model.Gender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class EmployeeService(private val connection: Connection) {

    companion object {

        private const val CREATE_TABLE_EMPLOYEES =
            "CREATE TABLE IF NOT EXISTS employees (" +
                    "eid SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "gender VARCHAR(10), " +
                    "title VARCHAR(255), " +
                    "photoUrl VARCHAR(255), " +
                    "presentToday BOOLEAN, " +
                    "salaryCredited BOOLEAN);"

        private const val SELECT_ALL_FROM_EMPLOYEES = "SELECT * FROM employees"

        private const val INSERT_RANDOM_EMPLOYEES = """
            INSERT INTO employees (eid, name, gender, title, photoUrl, presentToday, salaryCredited) VALUES 
            (1, 'Ravi Kumar', 'MALE', 'Software Engineer', 'https://example.com/photos/ravi', true, false),
            (2, 'Anita Sharma', 'FEMALE', 'QA Engineer', 'https://example.com/photos/anita', true, false),
            (3, 'Suresh Rao', 'MALE', 'Project Manager', 'https://example.com/photos/suresh', true, true),
            (4, 'Deepika Nair', 'FEMALE', 'Software Engineer', 'https://example.com/photos/deepika', true, true),
            (5, 'Vijay Singh', 'MALE', 'Product Owner', 'https://example.com/photos/vijay', true, false),
            (6, 'Neha Joshi', 'FEMALE', 'Scrum Master', 'https://example.com/photos/neha', false, true),
            (7, 'Aman Gupta', 'MALE', 'DevOps Engineer', 'https://example.com/photos/aman', true, false),
            (8, 'Priya Iyer', 'FEMALE', 'Software Engineer', 'https://example.com/photos/priya', true, false),
            (9, 'Rahul Verma', 'MALE', 'Systems Analyst', 'https://example.com/photos/rahul', true, true),
            (10, 'Anjali Patil', 'FEMALE', 'Business Analyst', 'https://example.com/photos/anjali', true, true),
            (11, 'Rajesh Kumar', 'MALE', 'Software Engineer', 'https://example.com/photos/rajesh', false, false),
            (12, 'Kavita Mehta', 'FEMALE', 'QA Engineer', 'https://example.com/photos/kavita', true, true),
            (13, 'Harish Rao', 'MALE', 'Architect', 'https://example.com/photos/harish', true, false),
            (14, 'Pooja Shah', 'FEMALE', 'UX Designer', 'https://example.com/photos/pooja', true, false),
            (15, 'Manoj Desai', 'MALE', 'Software Engineer', 'https://example.com/photos/manoj', true, true),
            (16, 'Rina Gupta', 'FEMALE', 'Product Manager', 'https://example.com/photos/rina', true, false),
            (17, 'Kiran Naik', 'MALE', 'DevOps Engineer', 'https://example.com/photos/kiran', true, false),
            (18, 'Sneha Chatterjee', 'FEMALE', 'Software Engineer', 'https://example.com/photos/sneha', false, true),
            (19, 'Arjun Patel', 'MALE', 'Project Manager', 'https://example.com/photos/arjun', true, true),
            (20, 'Sangeeta Kumari', 'FEMALE', 'Software Engineer', 'https://example.com/photos/sangeeta', true, true),
            (21, 'Vikram Sharma', 'MALE', 'QA Engineer', 'https://example.com/photos/vikram', true, false),
            (22, 'Divya Anand', 'FEMALE', 'Software Engineer', 'https://example.com/photos/divya', true, false),
            (23, 'Rohit Jain', 'MALE', 'Product Owner', 'https://example.com/photos/rohit', true, true),
            (24, 'Megha Reddy', 'FEMALE', 'Scrum Master', 'https://example.com/photos/megha', true, false),
            (25, 'Akhil Menon', 'MALE', 'DevOps Engineer', 'https://example.com/photos/akhil', false, true),
            (26, 'Nidhi Verma', 'FEMALE', 'HR Manager', 'https://example.com/photos/nidhi', true, true),
            (27, 'Siddharth Singh', 'MALE', 'Software Engineer', 'https://example.com/photos/siddharth', true, true),
            (28, 'Komal Desai', 'FEMALE', 'Finance Analyst', 'https://example.com/photos/komal', true, false),
            (29, 'Ajay Bansal', 'MALE', 'Software Engineer', 'https://example.com/photos/ajay', false, true),
            (30, 'Geeta Rao', 'FEMALE', 'Systems Analyst', 'https://example.com/photos/geeta', true, true);
            """
        private const val SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employees WHERE eid = ?"
        private const val INSERT_EMPLOYEE =
            "INSERT INTO employees (name, gender, title, photoUrl, presentToday, salaryCredited) VALUES (?, ?, ?, ?, ?, ?)"
        private const val UPDATE_EMPLOYEE =
            "UPDATE employees SET name = ?, gender = ?, title = ?, photoUrl = ?, presentToday = ?, salaryCredited = ? WHERE eid = ?"
        private const val DELETE_EMPLOYEE = "DELETE FROM employees WHERE eid = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_EMPLOYEES)
    }

    suspend fun getAllEmployees(): List<Employee> = withContext(Dispatchers.IO) {
        val employees = mutableListOf<Employee>()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM employees")

        while (resultSet.next()) {
            val eid = resultSet.getInt("eid")
            val name = resultSet.getString("name")
            val gender = Gender.valueOf(resultSet.getString("gender"))
            val title = resultSet.getString("title")
            val photoUrl = resultSet.getString("photoUrl")
            val presentToday = resultSet.getBoolean("presentToday")
            val salaryCredited = resultSet.getBoolean("salaryCredited")
            employees.add(Employee(eid, name, gender, title, photoUrl, presentToday, salaryCredited))
        }

        resultSet.close()
        statement.close()

        return@withContext employees
    }

    // Create new employee
    suspend fun create(employee: Employee): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, employee.name)
        statement.setString(2, employee.gender.name)
        statement.setString(3, employee.title)
        statement.setString(4, employee.photoUrl)
        statement.setBoolean(5, employee.presentToday)
        statement.setBoolean(6, employee.salaryCredited)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted employee")
        }
    }

    // Function to insert random employees
    suspend fun insertRandomEmployees() = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_RANDOM_EMPLOYEES)
        statement.executeUpdate()
    }


    // Read an employee
    suspend fun read(eid: Int): Employee = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_EMPLOYEE_BY_ID)
        statement.setInt(1, eid)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val gender = Gender.valueOf(resultSet.getString("gender"))
            val title = resultSet.getString("title")
            val photoUrl = resultSet.getString("photoUrl")
            val presentToday = resultSet.getBoolean("presentToday")
            val salaryCredited = resultSet.getBoolean("salaryCredited")
            return@withContext Employee(eid, name, gender, title, photoUrl, presentToday, salaryCredited)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update an employee
    suspend fun update(eid: Int, employee: Employee) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_EMPLOYEE)
        statement.setString(1, employee.name)
        statement.setString(2, employee.gender.name)
        statement.setString(3, employee.title)
        statement.setString(4, employee.photoUrl)
        statement.setBoolean(5, employee.presentToday)
        statement.setBoolean(6, employee.salaryCredited)
        statement.setInt(7, eid)
        statement.executeUpdate()
    }

    // Delete an employee
    suspend fun delete(eid: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_EMPLOYEE)
        statement.setInt(1, eid)
        statement.executeUpdate()
    }

}