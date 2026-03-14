# 🌊 Ocean View Resort Management System

![Java](https://img.shields.io/badge/Java-Backend-orange)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![HTML](https://img.shields.io/badge/HTML-Frontend-red)
![CSS](https://img.shields.io/badge/CSS-Styling-purple)
![JavaScript](https://img.shields.io/badge/JavaScript-Logic-yellow)
![Status](https://img.shields.io/badge/Project-Completed-success)

A **web-based resort management system** designed to help resorts manage reservations, employees, billing, and guest services efficiently.

---

# 📌 Project Overview

The **Ocean View Resort Management System** provides a digital platform for managing resort operations.  
It includes **Admin and Employee dashboards** to handle staff management, reservations, payments, and analytics.

---

# 👨‍💼 Admin Functionalities

## 🔐 Authentication
- Admin **Register**
- Admin **Login**
- Admin **Logout**
- Cookie-based authentication for secure sessions.

---

## 🔑 Forgot Password
If the admin forgets their password:

1. Click **Forgot Password**
2. Enter registered email
3. System sends a **password reset link**
4. Admin updates password using the link.

---

## 👥 Employee Management

### ➕ Add Employee
Admin can add new staff members.

Features:
- Only **Receptionists** get access to the **Employee Dashboard**
- Login credentials (**username & password**) are automatically sent to the receptionist's email.

---

### 📋 Manage Employees

Admin can:

| Action | Description |
|------|-------------|
| 👁 View | See employee details |
| ✏ Update | Modify employee information |
| ❌ Delete | Remove employees |

⚠ If a **Receptionist is deleted**, their **login credentials are also removed**.

---

# 👨‍💻 Employee Functionalities

## 🔐 Employee Login
Employees can log in to the dashboard after authentication.

---

## 🔑 Forgot Password
Employees can reset their password using email verification.

Steps:
1. Click **Forgot Password**
2. Enter email
3. Receive reset link
4. Update password

---

# 🏨 Reservation System

### 📝 Make Reservation
Employees can create reservations for guests including:

- Guest name
- Room number
- Check-in date
- Check-out date

---

### 📋 Manage Reservations

Employees can:

| Action | Description |
|------|-------------|
| 👁 View | See reservations |
| ✏ Update | Modify booking details |
| ❌ Delete | Remove reservations |

---

# 💰 Billing System

### 🧾 Calculate Bill
Employees can calculate the guest’s total bill.

Supported payment methods:

- 💵 Cash
- 💳 Card

---

# 📧 Invoice Generation

After payment is completed:

- The system generates an **invoice**
- The invoice is **automatically sent to the guest's email**

---

# 📊 Dashboard Features

The system provides real-time analytics including:

📈 Real-Time Payment Chart  
🏨 Total Reservations  
👨‍💼 Total Employees  
🚪 Room Status (Available / Reserved / Occupied)

---

# 🛠️ Technology Stack

| Technology | Purpose |
|-----------|---------|
| HTML | Frontend structure |
| CSS | Styling |
| JavaScript | Client-side logic |
| Java (Servlets/JSP) | Backend |
| MySQL | Database |
| Apache Tomcat | Web server |
| JavaMail API | Email notifications |

---

# 🗄️ Database Structure

Main system tables:

---

# ⚙️ Installation Guide

## 1️⃣ Clone the Repository

```bash
[git clone https://github.com/yourusername/oceanview-resort.git](https://github.com/NaveenEkanayake/OceanViewResort.git)
