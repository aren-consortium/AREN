<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Initialisation de la plateforme AREN</title>
  <link rel="shortcut icon" href="assets/img/favicon.ico">
</head>

<style>
  body>* {
    width: 500px;
    margin: 50px auto;
    border: 1px solid black;
    border-radius: 20px;
    padding: 25px 50px;
    box-shadow: 5px 5px 10px -5px black;
    text-align: center;
    font-family: sans-serif;
  }

  table {
    margin: 10px auto;
  }

  table tr td:first-child {
    text-align: right;
  }

  table tr td {
    padding: 5px 10px;
  }

  input {
    border-radius: 10px;
    padding: 5px;
    font-size: 1rem;
    border-color: orange;
  }

  #error {
    color: #fff;
    padding: 10px;
    display: inline-block;
    border-radius: 5px;
    background-color: orange;
    animation: blinkingBackground 2s;
  }

  @keyframes blinkingBackground {
    0% {
      background-color: orange;
    }

    50% {
      background-color: white;
    }

    100% {
      background-color: orange;
    }
  }
</style>

<body>

  <% if (request.getAttribute("dbExists") !=null) { %>

    <div>
      The database already exists and has been link to.<br>
      Contact your sysadmin to know who is the AREN superadmin.<br>
      You need to empty the database to recreate a superadmin.<br>
      If you need to connect to another database, change the settings in
      WEB-INF/classes/database.properties<br><br>
      <a href="<%=request.getContextPath()%>">Got to home page</a>
    </div>

  <% } else { %>
    
    <form method="POST" action="">

      <% if (request.getAttribute("error") !=null) { %>
        <div id="error">
          <b>${error}</b><br>
          <details>${errorDetails}</details>
        </div>
      <% } %>
      
      <% if (request.getAttribute("setAdmin") !=null) { %>

        <h1>Super Admin Configuration</h1>
        <table>
          <tr>
            <td>Super Admin Mail</td>
            <td><input type="mail" name="adminMail" value="" placeholder="" required /></td>
          </tr>
          <tr>
            <td>Super Admin Username</td>
            <td><input type="mail" name="adminUsername" value="admin" placeholder="" required /></td>
          </tr>
          <tr>
            <td>Super Admin Password</td>
            <td><input type="password" name="adminPassword" value="" placeholder="" required /></td>
          </tr>
          <tr>
            <td>Repeat Password</td>
            <td><input type="password" name="adminPasswordBis" value="" placeholder="" required /></td>
          </tr>
        </table>
        <input type="hidden" name="setAdmin" value="1" placeholder="" />

      <% } else { %>

        <h1>PSQL Database Configuration</h1>
        <table>
          <tr>
            <td>Server</td>
            <td><input type="text" name="server" value="localhost" required /></td>
          </tr>
          <tr>
            <td>Port</td>
            <td><input type="text" name="port" value="5432" required /></td>
          </tr>
          <tr>
            <td>Database</td>
            <td><input type="text" name="name" value="aren" required /></td>
          </tr>
          <tr>
            <td>User</td>
            <td><input type="text" name="user" value="" required /></td>
          </tr>
          <tr>
            <td>Password</td>
            <td><input type="password" name="password" value="" required /></td>
          </tr>
        </table>
        <input type="hidden" name="setDatabase" value="1" placeholder="" />

      <% } %>

      <input type="submit" value="Submit" />
    </form>

  <% } %>

</body>
</html>