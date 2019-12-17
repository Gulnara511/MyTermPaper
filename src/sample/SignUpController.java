package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SignUpController {
    @FXML
    private Button registration;
    @FXML
    private TextField Login;

    @FXML
    private TextField Email;

    @FXML
    private PasswordField Password;

    @FXML
    private PasswordField ConfirmPassword;

    @FXML
    private Button vxod;

    @FXML
    private Button enter;

    @FXML
    void initialize() {

        vxod.setOnAction(event -> {
            try {
                signUpNewUser();
                vxod.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("applicationWindow.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

    }

    private void signUpNewUser() throws SQLException, ClassNotFoundException {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String login = Login.getText();
        String email = Email.getText();
        String password = Password.getText();
        String confirmPassword = ConfirmPassword.getText();

        User user = new User(login,email,password,confirmPassword);

        dbHandler.signUpUsers(user);
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
