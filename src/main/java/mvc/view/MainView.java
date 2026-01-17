package mvc.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

/**
 *
 * View that shows what the user sees when opening Stack & Go
 *
 */
public class MainView extends AbstractView {

    private Button enterButton;

    public MainView() {
        super();
    }

    /**
     *
     * Method that gets the button used to enter the application
     *
     * @return button
     */
    public Button getEnterButton() { return this.enterButton; }

    @Override
    protected void build() {
        Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(120);  
        logoView.setFitHeight(120);  
        logoView.setPreserveRatio(true);
        
        Label title = new Label("", logoView);
        title.setFont(new Font(30));

        this.enterButton = new Button("Entrar");

        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(20);

        this.root.getChildren().addAll(title, this.enterButton);
    }
}
