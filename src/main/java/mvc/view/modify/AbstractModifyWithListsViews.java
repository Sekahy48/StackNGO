package mvc.view.modify;
 
import java.util.ArrayList;
import java.util.List;

import creational.EventPrefabFactory;
import creational.ImageUtils;
import creational.UIPrefabsFactory;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.GenericDTO;
import identificators.EntryId;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

    public abstract class AbstractModifyWithListsViews extends AbstractModifyView {

        // ===== LISTAS (FX) =====
        protected VBox list1Box;
        protected VBox list2Box;

        protected Label list1Label;
        protected Label list2Label;

        @Override
        public void clear(){
            super.clear();

            list1Label.setText("");
            list2Label.setText("");

            list1Box.getChildren().clear();
            list2Box.getChildren().clear();

        }

        @Override
        protected void build() {

            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));

            /* =========================
            IZQUIERDA
            ========================= */

            currentNameLabel = new Label("Nombre actual");
            currentNameLabel.setMinHeight(30);

            newNameField = new TextField();
            newNameField.setPromptText("Nuevo nombre");

            VBox nameBox = new VBox(5, currentNameLabel, newNameField);

            currentDescArea = new TextArea("Descripción actual");
            currentDescArea.setEditable(false);
            currentDescArea.setWrapText(true);

            newDescArea = new TextArea();
            newDescArea.setPromptText("Nueva descripción");
            newDescArea.setWrapText(true);

            VBox descBox = new VBox(5, currentDescArea, newDescArea);
            descBox.setPrefHeight(150);

            // ===== LISTAS =====
            list1Label = new Label("Lista 1");
            list1Box = new VBox(5);


            list2Label = new Label("Lista 2");
            list2Box = new VBox(5);


            VBox leftBox = new VBox(20, nameBox, descBox);

            /* =========================
            DERECHA
            ========================= */

            currentIconView = new ImageView();
            currentIconView.setFitWidth(120);
            currentIconView.setFitHeight(120);
            currentIconView.setPreserveRatio(true);

            StackPane iconPane = new StackPane(currentIconView);
            iconPane.setPrefSize(140, 140);
            iconPane.setStyle("-fx-border-color: gray;");

            suggestIconBtn = new Button("Sugerir icono");
            suggestIconBtn.setPrefSize(140, 40);

            VBox iconBox = new VBox(15, iconPane, suggestIconBtn);
            iconBox.setAlignment(Pos.TOP_CENTER);

            GridPane centerGrid = new GridPane();
            centerGrid.setHgap(40);
            centerGrid.setVgap(20);

            centerGrid.add(leftBox, 0, 0);
            centerGrid.add(iconBox, 1, 0);

            root.setCenter(centerGrid);

            confirmBtn = new Button("Confirmar");
            confirmBtn.setPrefWidth(120);

            HBox bottomBox = new HBox(confirmBtn);
            bottomBox.setAlignment(Pos.CENTER_RIGHT);
            bottomBox.setPadding(new Insets(20, 0, 0, 0));

            root.setBottom(bottomBox);

            this.root.getChildren().add(root);
        }

        public void modifyFields(EntryDTO dto, List<GenericDTO> firstList, List<GenericDTO> secondList) {
            super.modifyFields(dto); 

            this.setCurrentList1(firstList);
            this.setCurrentList2(secondList);

            this.createListTable(list1Box, firstList, 1);
            this.createListTable(list2Box, secondList, 2);
             
        }

        protected abstract void createListTable(VBox table, List<GenericDTO> list, int whatList);

        public VBox getList1(){
            return this.list1Box;
        }

        public VBox getList2(){
            return this.list2Box;
        }
 

        protected abstract List<GenericDTO> getCurrentList1();
        protected abstract List<GenericDTO> getCurrentList2();
        protected abstract void setCurrentList1(List<GenericDTO> list);
        protected abstract void setCurrentList2(List<GenericDTO> list);
    
    }
