package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimulationGUI extends Application {
    Simulation simulation=new Simulation();
    SimulationConfig config=SimulationConfig.getInstance();
    SimulationStats stats=SimulationStats.getInstance();
    SimulationClock clock=SimulationClock.getInstance();
    Canvas canvas =new Canvas(300, 300);
    HBox simulationPanel;
    HBox infoPanel;
    Label stepNumberLabel;
    Label hourNumberLabel;
    VBox interactionsPanel;
    VBox objectsPanel;

    public SimulationGUI(){}

    @Override
    public void start(Stage stage){
        VBox mainPanel=createMainPanel();
        VBox vampirePanel=createVampirePanel();
        VBox humanPanel=createHumanPanel();
        VBox trainedHumanPanel=createTrainedPanel();
        VBox startPanel=createStartPanel();
        infoPanel=createInfoPanel();

        BorderPane root=new BorderPane();
        javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale(1.9, 1.9, 0, 0);
        canvas.getTransforms().add(scale);
        Pane canvasPane=new Pane(canvas);
//        canvasPane.getStyleClass().add("canvas-holder");
//        canvas.widthProperty().bind(canvasPane.widthProperty());
//        canvas.heightProperty().bind(canvasPane.heightProperty());
//        canvas.setScaleX(3);
//        canvas.setScaleY(3);
//
        HBox controlPanel=new HBox();
        controlPanel.setSpacing(30); // Odstępy między elementami (w pikselach)
        controlPanel.setPadding(new Insets(15)); // Margines wewnętrzny wokół panelu
        controlPanel.setAlignment(Pos.CENTER); // Wyśrodkowanie elementów w panelu
        controlPanel.getStyleClass().add("control-hbox");
//    Style("-fx-background-color: #ececec;");

        simulationPanel=new HBox();
        simulationPanel.setSpacing(15); // Odstępy między elementami (w pikselach)
        simulationPanel.setPadding(new Insets(15)); // Margines wewnętrzny wokół panelu
        simulationPanel.setAlignment(Pos.CENTER); // Wyśrodkowanie elementów w panelu
        simulationPanel.setPrefHeight(410);
//        simulationPanel.setStyle("-fx-background-color: #ececec;");

        controlPanel.getChildren().addAll(mainPanel, vampirePanel, humanPanel, trainedHumanPanel, startPanel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMaxWidth(400);
        simulationPanel.getChildren().addAll(canvasPane,spacer, infoPanel);
        root.setTop(simulationPanel);
        root.setBottom(controlPanel);
        Scene scene = new Scene(root);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Symulacja wampirów");
        stage.setScene(scene);
        stage.show();
    }

    private void startAnimationLoop(){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ArrayList<ObjectToRender> listToRender=simulation.getSnapshot();
                render(listToRender);
                updateInfo();
            }
        };
        timer.start();
    }

    public void create(){
        launch();
    }

    //scena symulacji:
    private void render(ArrayList<ObjectToRender> listToRender){
        if(listToRender==null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
//        String grassColor=clock.getHour()>= clock.getSunriseHour() &&
//                clock.getHour()< clock.getSunsetHour() ? "#50af3f":"#24671d";
        gc.setFill(Color.web("#50af3f"));
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for(ObjectToRender obj : listToRender){
            switch (obj.type()){
                case ObjectType.VAMPIRE->{
                    drawVampire(gc, obj.x(), obj.y(), "#3f0d73");
                }
                case ObjectType.TRAINED_HUMAN->{
                    drawHuman(gc, obj.x(), obj.y(), "#cd0606");
                }
                case ObjectType.HUMAN->{
                    drawHuman(gc, obj.x(), obj.y(), "#e5ba0f");
                }
                case ObjectType.GARLIC->{
                    drawGarlic(gc, obj.x(), obj.y(), "#bfbab7");
                }
                case ObjectType.GARLIC_CONTAINER_CELL->{
                    gc.save();
                    gc.setFill(Color.web("#8a442e"));
                    gc.fillRect(obj.x(), obj.y(), 5,5);
                    gc.restore();
                }
            }

        }
        if(clock.isNight()){
            drawShadow(gc);
        }
    }

    private void drawVampire(GraphicsContext gc, int x, int y, String hexColor){
        gc.save();
        gc.translate(x, y);

        gc.scale(0.1, 0.1);
        // 3. Konfigurujemy kolor
        gc.setFill(Color.web(hexColor));

        // 4. Rysujemy wampira (współrzędne z kodu SVG, ale punkt startowy to teraz 0,0)
        gc.beginPath();
        gc.appendSVGPath("M 50,15 Q 32,15 32,35 L 20,25 L 28,45 Q 28,65 50,75 Q 72,65 72,45 L 80,25 L 68,35 Q 68,15 50,15 Z M 50,22 Q 58,32 66,36 Q 66,22 50,22 Z M 34,36 Q 42,32 50,22 Q 50,22 34,36 Z M 40,45 A 3,3 0 1,1 46,45 A 3,3 0 1,1 40,45 Z M 54,45 A 3,3 0 1,1 60,45 A 3,3 0 1,1 54,45 Z M 42,56 L 44,56 L 43,61 Z M 58,56 L 56,56 L 57,61 Z M 40,55 Q 50,59 60,55 Q 50,57 40,55 Z");
        gc.fill();

        // 5. Przywracamy oryginalny układ współrzędnych dla reszty programu
        gc.restore();
    }

    public void drawHuman(GraphicsContext gc, int x, int y, String hexColor) {
        // 1. Zapisujemy stan płótna
        gc.save();

        // 2. Przesuwamy punkt (0,0) na wybraną pozycję (x, y)
        gc.translate(x, y);

        // 3. Skalujemy układ współrzędnych
        gc.scale(0.1, 0.1);

        // 4. Ustawiamy kolor ikony
        gc.setFill(Color.web(hexColor));

        // 5. Rysujemy geometryczną postać człowieka (Głowa + Tułów/Ramiona)
        gc.beginPath();
        // Ścieżka SVG składająca się z koła (głowa) oraz łuków i linii (tułów)
        gc.appendSVGPath("M 50,40 A 12,12 0 1,1 50,16 A 12,12 0 1,1 50,40 Z " +
                "M 50,46 C 33,46 20,55 20,72 L 20,84 L 80,84 L 80,72 C 80,55 67,46 50,46 Z");
        gc.fill();

        // 6. Przywracamy stan płótna dla reszty obiektów
        gc.restore();
    }
    public void drawGarlic(GraphicsContext gc, int x, int y, String hexColor) {
        // 1. Zapisujemy stan płótna
        gc.save();

        // 2. Przesuwamy punkt (0,0) na pozycję (x, y)
        gc.translate(x, y);

        // 3. Skalujemy układ współrzędnych
        gc.scale(0.07, 0.07);

        // 4. Ustawiamy wybrany kolor
        gc.setFill(Color.web(hexColor));

        // 5. Rysujemy kształt czosnku z liniami podziału (wycięciami) między ząbkami
        gc.beginPath();
        gc.appendSVGPath("M 50,15 " +
                "Q 48,25 43,28 " +
                "Q 20,25 20,53 " +
                "Q 20,85 50,85 " +
                "Q 80,85 80,53 " +
                "Q 80,25 57,28 " +
                "Q 52,25 50,15 Z " +

                // Wycięcie / linia lewego ząbka
                "M 38,34 Q 38,60 48,83 Q 34,70 34,44 Z " +

                // Wycięcie / linia prawego ząbka
                "M 62,34 Q 66,70 52,83 Q 62,60 62,44 Z " +

                // Środkowy podział
                "M 49,27 L 51,27 L 50,84 Z");

        // Używamy reguły evenodd, aby wewnętrzne linie SVG zadziałały jako "wycięcia" w czosnku
        gc.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);
        gc.fill();

        // 6. Przywracamy stan płótna
        gc.restore();
    }
    public void drawShadow(GraphicsContext gc) {
        // poziomPrzyciemnienia: 0.0 (brak cienia) do 1.0 (całkowita czerń)
        // 0.5 oznacza 50% przyciemnienia (efekt wieczoru/mroku)

        gc.save(); // Zapisujemy stan

        // Ustawiamy czarny kolor z określoną przezroczystością
        gc.setFill(Color.color(0, 0, 0, 0.2));

        // Rysujemy prostokąt na całą wielkość canvasu
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.restore(); // Przywracamy stan
    }

    private void updateInfo(){
//        DecimalFormat df=new DecimalFormat("#.#");
        stepNumberLabel.setText(String.valueOf(clock.getStep()));
        hourNumberLabel.setText(String.valueOf((int)(Math.floor(clock.getHour()))));

        objectsPanel.getChildren().clear();
        for(Map.Entry<ObjectType, Integer> entry :stats.getObjectsNumber().entrySet()){
            VBox container=new VBox(5);
            Label textLabel=new Label(entry.getKey().getDescription());
            textLabel.getStyleClass().add("main-label");
            Label numberLabel=new Label(String.valueOf(entry.getValue()));
            container.getChildren().addAll(textLabel, numberLabel);
            objectsPanel.getChildren().add(container);
        }

        interactionsPanel.getChildren().clear();
        for(Map.Entry<InteractionType, Integer> entry :stats.getInteractionsNumber().entrySet()){
            VBox container=new VBox(5);
            Label textLabel=new Label(entry.getKey().getDescription());
            textLabel.getStyleClass().add("main-label");
            Label numberLabel=new Label(String.valueOf(entry.getValue()));
            container.getChildren().addAll(textLabel, numberLabel);
            interactionsPanel.getChildren().add(container);
        }
//        hourNumberLabel.setText(String.valueOf(clock.getStep()));
    }

    private HBox createInfoPanel(){
        Label stepTextLabel=new Label("Krok");
        stepTextLabel.getStyleClass().add("main-label");
        stepNumberLabel=new Label(String.valueOf(clock.getStep()));
        VBox stepPanel=new VBox(5);
        stepPanel.getChildren().addAll(stepTextLabel, stepNumberLabel);

        Label hourTextLabel=new Label("Godzina");
        hourTextLabel.getStyleClass().add("main-label");
        hourNumberLabel=new Label(String.valueOf((int)(Math.floor(clock.getHour()))));
        VBox hourPanel=new VBox(5);
        hourPanel.getChildren().addAll(hourTextLabel, hourNumberLabel);

        objectsPanel=new VBox(10);

        interactionsPanel=new VBox(10);

        VBox leftSidePanel=new VBox(15);
        leftSidePanel.getChildren().addAll(stepPanel, hourPanel, objectsPanel);

        HBox infoPanel=new HBox(50);
        infoPanel.getChildren().addAll(leftSidePanel, interactionsPanel);
        infoPanel.setPrefWidth(400);

        return infoPanel;
    }

//    private VBox createInfoLabels(Label variableLabel, Supplier<Integer> getter, String labelText){
//        Label TextLabel=new Label(labelText);
//        TextLabel.getStyleClass().add("main-label");
//        variableLabel=new Label(String.valueOf(getter.get()));
//        VBox stepPanel=new VBox(5);
//        stepPanel.getChildren().addAll(TextLabel, variableLabel);
//
//    }

    private VBox createStartPanel(){
        Button initButton=new Button("Stwórz symulację");
        initButton.setOnAction((event)->{
            if(config.getWorldConfig().isInitiated()) return;
            canvas.setWidth(config.getWorldConfig().getWidth());
            canvas.setHeight(config.getWorldConfig().getHeight());
//            simulationPanel.requestLayout();
            Thread simulationThread=new Thread(simulation);
            simulationThread.setDaemon(true);
            simulationThread.start();
            startAnimationLoop();
            System.out.println(config.getHumanConfig().getAddProb());
        });

        VBox startButton=createNewToggleButtonWithLabel("Start/Stop","Włącz symulację", config.getWorldConfig()::isPaused,
                config.getWorldConfig()::setPaused);

        VBox panel=new VBox(15);
        panel.getChildren().addAll(initButton, startButton);

        return panel;
    }

    private VBox createMainPanel(){
        Label label=new Label("Główny panel ustawień");
        label.getStyleClass().add("main-label");
        VBox mode=createNewToggleButtonWithLabel("Dzień-Noc", "Tryb", config.getWorldConfig()::isCycling, config.getWorldConfig()::setCycling);
        VBox boardWidth=createNewSpinnerWithLabel(150, 300, config.getWorldConfig()::getWidth, config.getWorldConfig()::setWidth,
                "Szerokość planszy");
        VBox boardHeight=createNewSpinnerWithLabel(150, 200, config.getWorldConfig()::getHeight, config.getWorldConfig()::setHeight,
                "Wysokość planszy");
        VBox initialVampireNumber=createNewSpinnerWithLabel(0, 80, config.getVampireConfig()::getInitialNumber, config.getVampireConfig()::setInitialNumber,
                "Początkowa liczba wampirów");
        VBox initialHumanNumber=createNewSpinnerWithLabel(0, 80, config.getHumanConfig()::getInitialNumber, config.getHumanConfig()::setInitialNumber, "Początkowa liczba zwykłych ludzi");
        VBox initialTrainedHumanNumber=createNewSpinnerWithLabel(0, 80, config.getTrainedHumanConfig()::getInitialNumber, config.getTrainedHumanConfig()::setInitialNumber, "Początkowa liczba wytrenowanych ludzi");

        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, mode, boardWidth, boardHeight, initialVampireNumber, initialHumanNumber, initialTrainedHumanNumber);
        return panel;
    }

    private VBox createVampirePanel(){
        Label label=new Label("Panel ustawień wampirów");
        label.getStyleClass().add("main-label");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getVampireConfig()::getEnergyBoost,config.getVampireConfig()::setEnergyBoost, "Pojedynczy boost energii wampira");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000,config.getVampireConfig()::getEnergyLoss,config.getVampireConfig()::setEnergyLoss, "Pojedyncza strata energii wampira");
        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, energyBoost, energyLoss);
        return panel;
    }
    private VBox createHumanPanel(){
        Label label=new Label("Panel ustawień osób");
        label.getStyleClass().add("main-label");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyBoost,config.getHumanConfig()::setEnergyBoost, "Pojedynczy boost energii człowieka");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyLoss,config.getHumanConfig()::setEnergyLoss, "Pojedyncza strata energii człowieka");
        VBox addProb=createNewSliderWithLabel(0, 0.1f, config.getHumanConfig()::getAddProb,config.getHumanConfig()::setAddProb, "Prawdopodobieństwo urodzenia nowego człowieka w jednym kroku");
        VBox transformationProb=createNewSliderWithLabel(0, 10, config.getHumanConfig()::getTransformationProb,config.getHumanConfig()::setTransformationProb, "Prawdopodobieństwo zamiany w wampira");
        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, energyBoost, energyLoss, addProb, transformationProb);
        return panel;
    }
    private VBox createTrainedPanel(){
        Label label=new Label("Panel ustawień wytrenowych osób");
        label.getStyleClass().add("main-label");
        VBox garlicStockMax=createNewSpinnerWithLabel(0, 20,config.getTrainedHumanConfig()::getGarlicStockMax,config.getTrainedHumanConfig()::setGarlicStockMax, "Maksymalna liczba posiadanego czosnku");
        VBox recruitmentProb=createNewSliderWithLabel(0, 10, config.getTrainedHumanConfig()::getRecruitmentProb,config.getTrainedHumanConfig()::setRecruitmentProb, "Prawdopodobieństwo rekrutacji zwykłego człowieka");
        VBox throwProb=createNewSliderWithLabel(0, 1, config.getTrainedHumanConfig()::getThrowProb,config.getTrainedHumanConfig()::setThrowProb, "Prawdopodobieństwo rozrzucenia czosnku w jednym kroku");
        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, garlicStockMax, recruitmentProb, throwProb);
        return panel;
    }

    private VBox createNewSpinnerWithLabel(int minVal, int maxVal, Supplier<Integer> getter, Consumer<Integer> setter, String labelText){
        Label label=new Label(labelText);
        Spinner<Integer> spinner=new Spinner<>(minVal,maxVal, getter.get());
        spinner.setEditable(true);

        spinner.valueProperty().addListener((obs, oldVal, newVal)->{
            setter.accept(newVal);
        });

        VBox container=new VBox(5);
        container.getChildren().addAll(label, spinner);
        return container;
    }

    private VBox createNewSliderWithLabel(float minVal, float maxVal, Supplier<Float> getter, Consumer<Float> setter, String labelText){
        Label label=new Label(labelText);
        Slider slider=new Slider(minVal,maxVal, getter.get());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        slider.valueProperty().addListener((obs, oldVal, newVal)->{
            setter.accept(newVal.floatValue());
        });

        VBox container=new VBox(5);
        container.getChildren().addAll(label, slider);
        return container;
    }
    private VBox createNewToggleButtonWithLabel(String buttonText, String labelText,Supplier<Boolean> getter, Consumer<Boolean> setter){
        Label label=new Label(labelText);
//        ToggleGroup group = new ToggleGroup();

        ToggleButton button = new ToggleButton(buttonText);
//        optionA.setToggleGroup(group);
        button.setSelected(getter.get());

//        ToggleButton optionB = new ToggleButton(optionBText);
//        optionB.setToggleGroup(group);

        button.selectedProperty().addListener((obs,oldVal,newVal)->{
            setter.accept(newVal);
        });

//        HBox modeSelection = new HBox(optionA, optionB);

        VBox container=new VBox(5);
        container.getChildren().addAll(label, button);
        return container;
    }
}