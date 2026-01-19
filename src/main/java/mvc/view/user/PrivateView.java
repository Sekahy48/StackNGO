package mvc.view.user;

import creational.UIPrefabsFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * View that shows what the user sees in their personal space
 *
 */
public class PrivateView extends AbstractUserView {

    private Button addCollectionButton;
    private Button seeCollectionsButton;
    private Button exportCollectionsButton;
    private Button importCollectionsButton;
    private Button adminButton;

    private MenuItem logoutItem;
    private MenuItem deleteAccountItem;

    private MenuButton userMenu;
    private Label userTitleLabel;

    private HBox userBar;
    private Separator separator;

    /* ================= GETTERS ================= */

    public Button getAddCollectionButton() { return this.addCollectionButton; }
    public Button getSeeCollectionsButton() { return this.seeCollectionsButton; }
    public Button getExportCollectionsButton() { return this.exportCollectionsButton; }
    public Button getImportCollectionsButton() { return this.importCollectionsButton; }
    public Button getAdminButton() { return this.adminButton; }

    public MenuItem getLogoutItem() { return this.logoutItem; }
    public MenuItem getDeleteAccountItem() { return this.deleteAccountItem; }

    public Label getUserTitleLabel() { return this.userTitleLabel; }
    /* ================= SETTER ================= */

    public void setUserTitleText(String text) {
        this.userTitleLabel.setText(text);
    }

    /* ================= CONSTRUCTOR ================= */

    public PrivateView() {
        super();
    }

    @Override
    protected void buildFields() {

        /* ===== BOTONES ===== */
        addCollectionButton = new Button("Añadir una colección");
        seeCollectionsButton = new Button("Ver colecciones");
        exportCollectionsButton = new Button("Exportar colecciones");
        importCollectionsButton = new Button("Importar colecciones");
        adminButton = new Button("Panel del admin");


        /* ===== TÍTULO ===== */
        this.title = new Label("Espacio privado");
        this.title.setFont(new Font(20));
 

        /* ===== MENÚ USUARIO ===== */
        userMenu = new MenuButton("Opciones de usuario");
        logoutItem = new MenuItem("Cerrar sesión");
        deleteAccountItem = new MenuItem("Borrar cuenta");
        userMenu.getItems().addAll(logoutItem, deleteAccountItem);

        /* ===== LABEL GRANDE A LA DERECHA ===== */
        userTitleLabel = new Label();
        userTitleLabel.setFont(new Font(18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userBar = new HBox(10, spacer, userTitleLabel, userMenu);
        userBar.setAlignment(Pos.CENTER_LEFT);

        separator = new Separator();

        /* ===== BOTONES EN COLUMNA ===== */
        VBox actionsColumn = new VBox(10,
                addCollectionButton,
                seeCollectionsButton,
                exportCollectionsButton,
                importCollectionsButton,
                adminButton
        );

        /* ===== CONTENIDO ===== */
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(userBar, separator, actionsColumn);

        this.initSidebar(content);

        this.root = new VBox();
        this.root.getChildren().add(splitPane);
    }
}
