package creational;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemStackDTO;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utilities.ThemeManager;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import utilities.ImageUtils;

public class UIPrefabsFactory {

    public static VBox createScrollableModifableList(Label titleLabel, VBox listBox, Button addButton){ 
        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(160);
        scrollPane.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox container = new VBox(5, titleLabel, scrollPane, addButton);
        container.setPadding(new Insets(5));
        container.setFillWidth(true);
        container.setMaxWidth(Double.MAX_VALUE);
        container.getStyleClass().add("scrollable-list-box");

        return container;
    }


    public static VBox createScrollableModifableListNoButton(Label titleLabel, VBox listBox){
        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(200, 160);

        VBox container = new VBox(5, titleLabel, scrollPane);
        container.setPadding(new Insets(5));
        container.getStyleClass().add("scrollable-list-box");

        return container;

    }

    public static HBox createRow(String text, Image icon, Button action) {
        HBox row = new HBox(5);

        ImageView iv = new ImageView(icon);
        iv.setFitHeight(25);
        iv.setFitWidth(25);

        Label lbl = new Label(text);
        lbl.setMaxWidth(Double.MAX_VALUE);          // acepta crecer
        HBox.setHgrow(lbl, Priority.ALWAYS);        // empuja horizontalmente

        row.getChildren().addAll(iv, lbl, action);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    public static HBox createRowNoButton(String text, Image icon) {
        HBox row = new HBox(5);
        ImageView iv = new ImageView(icon);
        iv.setFitHeight(25);
        iv.setFitWidth(25);
        Label lbl = new Label(text);

        row.getChildren().addAll(iv, lbl);
        return row;
    }

    public static  HBox createRowNoButtonWithAmount(String name, Image icon, int amount) {
        ImageView iv = new ImageView(icon);
        iv.setFitHeight(25);
        iv.setFitWidth(25);

        Label nameLabel = new Label(name);

        Label amountLabel = new Label(String.valueOf(amount));
        amountLabel.setMinWidth(32);
        amountLabel.setAlignment(Pos.CENTER);
        amountLabel.getStyleClass().add("amount-label");

        HBox row = new HBox(8, iv, nameLabel, amountLabel);
        row.setAlignment(Pos.CENTER);

        return row;
    }

    
    public static HBox createRow(String text, Image icon, Button action, TextField amount) {
        HBox row = createRow(text, icon, action);

        // Evitar que el TextField crezca demasiado
        amount.setMaxWidth(80); // ancho máximo fijo
        amount.setPrefWidth(60); // ancho preferido
        HBox.setHgrow(amount, Priority.NEVER); // no crecer al expandirse el HBox

        row.getChildren().add(amount);
        return row;
    }



    /**
     * Crea un popup genérico para seleccionar un elemento y añadirlo a un VBox.
     * 
     * @param triggerButton Botón que dispara el popup
     * @param items Lista de strings a mostrar en el popup
     * @param onSelect Callback que se ejecuta al seleccionar un item
     */
    public static void createSelectionPopup(Button triggerButton,
                                            List<EntryDTO> items,
                                            Consumer<EntryDTO> onSelect) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        ListView<EntryDTO> listView = new ListView<>();
        listView.getItems().addAll(items);
        listView.setPrefSize(300, 250);

        listView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                container.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(EntryDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item.name);
                    imageView.setImage(
                        item.imagePath != null && !item.imagePath.isEmpty()
                            ? ImageUtils.getImage(item.imagePath)
                            : null
                    );
                    setGraphic(container);
                }
            }
        });

        listView.setOnMouseClicked(e -> {
            EntryDTO selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                onSelect.accept(selected);
                popup.hide();
            }
        });

        popup.getContent().add(listView);

        double x = triggerButton.localToScene(0, 0).getX()
                + triggerButton.getScene().getWindow().getX();
        double y = triggerButton.localToScene(0, 0).getY()
                + triggerButton.getScene().getWindow().getY()
                + triggerButton.getHeight();

        popup.show(triggerButton.getScene().getWindow(), x, y);
    }


    public static Integer showAmount(String title, String text) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(text);
        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) return null;

        try {
            return Integer.parseInt(result.get());  
        } catch (NumberFormatException e) {
            return null;
        }
    }



    public static Button createRemoveButton(){
        Image icon = ThemeManager.getThemedImage("papelera.png");   // ruta relativa o URL
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(24);   // ancho deseado
        iconView.setFitHeight(24);  // alto deseado
        iconView.setPreserveRatio(true); // opcional, mantiene proporciones

        Button btn = new Button();
        btn.setGraphic(iconView);   // aquí pones la imagen en el botón
        btn.getStyleClass().add("icon-button");

        return btn;
    }

    public static void initSideBar(VBox sideBar, Button inventoryButton, Button userButton, Button itemButton,  Button collectionButton, Button componentButton, SplitPane splitPane, Node content) {
        int defaultSize = 50;
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.setPadding(new Insets(10));
        sideBar.setMaxWidth(75);

        // Inventory
        ImageView inventoryIcon = new ImageView(new Image("images/inventario.png"));
        inventoryIcon.setFitWidth(defaultSize);
        inventoryIcon.setFitHeight(defaultSize);

        inventoryButton.setGraphic(inventoryIcon);
        inventoryButton.getStyleClass().add("icon-button");

        // Private zone
        ImageView userIcon = new ImageView(new Image("images/usuario.png"));
        userIcon.setFitWidth(defaultSize);
        userIcon.setFitHeight(defaultSize);

        userButton.setGraphic(userIcon);
        userButton.getStyleClass().add("icon-button");

        // Collections
        ImageView collectionsIcon = new ImageView(new Image("images/caja.png"));
        collectionsIcon.setFitWidth(defaultSize);
        collectionsIcon.setFitHeight(defaultSize);

        collectionButton.setGraphic(collectionsIcon);
        collectionButton.getStyleClass().add("icon-button");
        
        // Components
        ImageView componentsIcon = new ImageView(new Image("images/components.png"));
        componentsIcon.setFitWidth(defaultSize);
        componentsIcon.setFitHeight(defaultSize);

        componentButton.setGraphic(componentsIcon);
        componentButton.getStyleClass().add("icon-button");

        // Items
        ImageView itemsIcon = new ImageView(new Image("images/items.png"));
        itemsIcon.setFitWidth(defaultSize);
        itemsIcon.setFitHeight(defaultSize);

        itemButton.setGraphic(itemsIcon);
        itemButton.getStyleClass().add("icon-button");

        sideBar.getChildren().addAll(userButton, collectionButton, componentButton, itemButton, inventoryButton);
        sideBar.getStyleClass().add("sidebar");
        sideBar.setMinWidth(75);
        sideBar.setPrefWidth(75);
        sideBar.setMaxWidth(75);

        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(sideBar, content);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        SplitPane.setResizableWithParent(sideBar, false);

        // Mantener la sidebar con ancho fijo tras cada cambio de tamaño
        splitPane.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            if (w > 0) splitPane.setDividerPositions(75.0 / w);
        });
    }
 

    public static Button createGoBackButton(){
        Image icon = ThemeManager.getThemedImage("volver.png");   // ruta relativa o URL
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(24);   // ancho deseado
        iconView.setFitHeight(24);  // alto deseado
        iconView.setPreserveRatio(true); // opcional, mantiene proporciones

        Button btn = new Button();
        btn.setGraphic(iconView);   // aquí pones la imagen en el botón
        btn.getStyleClass().add("icon-button");

        return btn;
    }

    public static TextField createAmountField() {
        TextField tf = new TextField("1");
        tf.setOnAction(e -> {
            if (!tf.getText().matches("\\d+")) {
                tf.setText("1");
            }
        });
        return tf;
    }

    public static void addSeveralItemStackRows(List<ItemStackDTO> dtos, VBox tagetList){
        for (ItemStackDTO dto : dtos){
            
            String itemName = dto.item.getName();
            String iconPath = dto.item.getImagePath();
            int amount = dto.amount;

            Image image = ImageUtils.getImage(iconPath);

            HBox row = UIPrefabsFactory.createRowNoButtonWithAmount(itemName, image, amount);

            tagetList.getChildren().add(row);
        }
    }

    
    public static void addPopUp(Button button, List<ItemDTO> listItems, Consumer<ItemDTO> onSelect) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        ListView<String> list = new ListView<>();
        Map<String, ItemDTO> items = new HashMap<>();

        for(ItemDTO itemDTO : listItems){
            list.getItems().add(itemDTO.getName());
            items.put(itemDTO.getName(), itemDTO);
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);

        scroll.setPrefWidth(300);
        scroll.setPrefHeight(250);

        popup.getContent().add(scroll);


        // Al hacer click en un item
        list.setOnMouseClicked(
                e -> {
                    String selected = list.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        onSelect.accept(items.get(selected));
                        popup.hide();
                    }
                }
        );

       // añadir coordenadas del botón
        popup.show(button.getScene().getWindow());
    }

    public static boolean rowExists(VBox box, String name){
        for (Node row : box.getChildren()) {
            if (row instanceof HBox){
                HBox hbox = (HBox) row;

                if (hbox.getChildren().size() > 1 && hbox.getChildren().get(1) instanceof Label){
                    Label label = (Label) hbox.getChildren().get(1);

                    if (label.getText().equals(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
