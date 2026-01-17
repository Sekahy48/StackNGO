package mvc.view.user;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * View that the user sees when signin up
 *
 */
public class SignUpView extends AbstractUserView {

    private TextField username;
    private PasswordField password, checkPassword;
    private Button loginBtn;
    private Button createAdminBtn;


    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public SignUpView() {
        super();
    }

    /**
     *
     * Method that returns the confirm button
     *
     * @return confirm button
     *
     */
    public Button getConfirmButton() { return this.confirmButton; }

    /**
     *
     * Method that returns the login button
     *
     * @return login button
     *
     */
    public Button getLoginButton() { return this.loginBtn; }

    /**
     *
     * Method that returns the createAdmin button
     * this method is intended for easier testing only!
     * @return createAdmin button
     *
     */
    public Button getCreateAdminButton() { return this.createAdminBtn; }


    /**
     *
     * Method that returns the password introduced by the user
     *
     * @return password
     *
     */
    public PasswordField getPassword() { return this.password; }

    public PasswordField getCheckPassword() { return this.checkPassword; }

    /**
     *
     * Method that returns the name of the account of the user
     *
     * @return name of the account
     *
     */
    public TextField getUsername() { return this.username; }

    @Override
protected void buildFields() {

    this.root = new VBox(10);

    // Imagen del logo
    Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
    ImageView logoView = new ImageView(logo);
    logoView.setFitWidth(120);  
    logoView.setFitHeight(120);  
    logoView.setPreserveRatio(true);

    // Label con imagen + texto debajo
    this.title = new Label("Registro", logoView);
    this.title.setFont(new Font(20));
    this.title.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
    this.title.setAlignment(Pos.CENTER);

    // Campos
    this.username = new TextField();
    this.username.setMaxWidth(300);
    this.password = new PasswordField();
    this.password.setMaxWidth(300);
    this.checkPassword = new PasswordField();
    this.checkPassword.setMaxWidth(300);

    this.username.setPromptText("Nombre de usuario");
    this.password.setPromptText("Contraseña");
    this.checkPassword.setPromptText("Contraseña");

    // Botones
    this.loginBtn = new Button("Iniciar sesión");
    this.confirmButton = new Button("Confirmar");
    this.createAdminBtn = new Button("Crear un admin");

    this.hBox = new HBox(10, this.loginBtn, this.confirmButton, this.createAdminBtn);
    this.hBox.setAlignment(Pos.CENTER);
    this.root.setAlignment(Pos.CENTER);

    this.root.getChildren().addAll(this.title, this.username, this.password, this.checkPassword, this.hBox);
}

}