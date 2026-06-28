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
 * View that shows what the user sees when entering the app
 *
 */
public class LoginView extends AbstractUserView {

    private TextField username;
    private PasswordField password;
    private Button signUpBtn;

    /**
     *
     * Constructor that receives a buffer where events will reside
     *
     */
    public LoginView() {
        super();
    }

    /**
     *
     * Method that gets the confirm button
     *
     * @return confirm button
     *
     */
    public Button getConfirmButton() { return this.confirmButton; }

    /**
     *
     * Method that gets the sign up button
     *
     * @return sign up button
     */
    public Button getSignUpBtn() { return this.signUpBtn; }

    /**
     *
     * Method that returns the username
     *
     * @return username
     *
     */
    public TextField getUsername() { return this.username; }

    /**
     *
     * Method that returns the password introduced
     *
     * @return password introduced
     *
     */
    public PasswordField getPassword() { return this.password; }

    @Override
    protected void buildFields() {
        this.root = new VBox(10);

        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(120);  
        logoView.setFitHeight(120);  
        logoView.setPreserveRatio(true);

        this.title = new Label("Acceso", logoView);
        this.title.setFont(new Font(20));
        this.title.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
        this.title.setAlignment(Pos.CENTER);

        this.username = new TextField();
        this.username.setMaxWidth(300);

        this.password = new PasswordField();
        this.password.setMaxWidth(300);

        this.username.setPromptText("Nombre de usuario");
        this.password.setPromptText("Contraseña");

        this.confirmButton = new Button("Confirmar");
        this.signUpBtn = new Button("Crear una cuenta");

        this.hBox = new HBox(10, this.confirmButton, this.signUpBtn);
        this.hBox.setAlignment(Pos.CENTER);
        this.root.setAlignment(Pos.CENTER);

        this.root.getChildren().addAll(this.title, this.username, this.password, this.hBox);
    }

    public void clearFields() {
        username.clear();
        password.clear();
    }

}