package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.sql.DriverManager;

public class DatabaseHandler extends Configs{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://localhost:3306/userslist?serverTimezone=Europe/Moscow&useSSL=false";
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString,dbUser, dbPass);
        return dbConnection;
    }

    public void signUpUsers(User user) throws SQLException, ClassNotFoundException {
        try {
        String insert = " INSERT INTO " + Const.USERS_TABLE + " (" + Const.USERS_LOGIN + "," + Const.USERS_EMAIL + "," + Const.USERS_PASSWORD + ") " + " VALUES(?,?,?) ";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);

        prSt.setString(1, user.getLogin());
        prSt.setString(2, user.getEmail());
        if (user.getPassword().equals(user.getConfirm_password())) {
        prSt.setString(3, user.getPassword());
        } else { System.out.println("Неверный повтор пароля");

        }
        prSt.execute(); }

        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLIntegrityConstraintViolationException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Пользователь с данным логином уже зарегистрирован!");
            a.show();
            System.out.println("Пользователь с данным логином уже зарегистрирован!");
        }
        catch (SQLException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Повтор пароля неверный");
            a.show();
            System.out.println("Повтор пароля неверный");
        }
    }
    public ResultSet getUser(User user) throws SQLException, ClassNotFoundException {
        ResultSet resSet = null;

        String select = " SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERS_LOGIN + " =? AND " + Const.USERS_PASSWORD + " =? ";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getLogin());
            prSt.setString(2, user.getPassword());
            resSet = prSt.executeQuery();
        } catch ( SQLException e){
            e.printStackTrace();
        } catch ( ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resSet;
    }

    public void OpenNewScene (Button btn,String window) {
        btn.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
}
