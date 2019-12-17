package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.animation.Shake;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController extends ApplicationWindowController{
    @FXML
    private Button vxod;
    @FXML
    private TextField login;

    @FXML
    private PasswordField singInPassword;

    @FXML
    private Button enter;

    @FXML
    private Button registration;

    @FXML
    void initialize () {
        vxod.setOnAction((event) -> {
            System.out.println(login.getText());

            String loginText = login.getText().trim();
            String passwordText = singInPassword.getText().trim();
            if(!loginText.equals("")&&!passwordText.equals("")) {
                try {
                    loginUser(loginText,passwordText);

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ERROR");
            }

        });

        registration.setOnAction((event) -> {
            OpenNewScene(registration, "/sample/signUp.fxml");
        });

    }

    private String loginUser(String loginText, String passwordText) throws SQLException, ClassNotFoundException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setLogin(loginText);
        user.setPassword(passwordText);
        ResultSet result = dbHandler.getUser(user);
        int counter = 0;
        try {
        while (result.next()) {
            counter++;
        } } catch (SQLException e) {
            e.printStackTrace();
        }
        if (counter>=1) {
            vxod.setOnAction(event -> {
                OpenNewScene(vxod, "/sample/applicationWindow.fxml");
            });
        } else {
            Shake userLoginAnimation = new Shake(login);
            Shake userPasswordAnimation = new Shake(singInPassword);
            userLoginAnimation.playAnim();
            userPasswordAnimation.playAnim();
        }
        return login.getText();
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
        if (btn.getText().equals("SIGN IN")) {
        ApplicationWindowController applicationWindowController = loader.getController();
        applicationWindowController.usersLogin(login.getText());}
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        stage.showAndWait();
    }
}
