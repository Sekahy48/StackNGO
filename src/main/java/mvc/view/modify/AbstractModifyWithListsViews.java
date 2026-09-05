package mvc.view.modify;
 
import java.util.List;

import dataTransportLayer.EntryDTO; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

    public abstract class AbstractModifyWithListsViews<T extends EntryDTO, E extends EntryDTO, U extends EntryDTO> extends AbstractModifyView<T> {

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
            iconPane.getStyleClass().add("icon-pane");

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

        public void modifyFields(T dto, List<E> firstList, List<U> secondList) {
            super.modifyFields(dto); 

            this.setCurrentList1(firstList);
            this.setCurrentList2(secondList);

            this.createListTable1(list1Box, firstList);
            this.createListTable2(list2Box, secondList);
             
        }

        protected abstract void createListTable1(VBox table, List<E> list);
        protected abstract void createListTable2(VBox table, List<U> list);

        public VBox getList1(){
            return this.list1Box;
        }
        

        public VBox getList2(){
            return this.list2Box;
        }
 

        protected abstract List<E> getCurrentList1();
        protected abstract List<U> getCurrentList2();
        protected abstract void setCurrentList1(List<E> list);
        protected abstract void setCurrentList2(List<U> list);
    
    }
