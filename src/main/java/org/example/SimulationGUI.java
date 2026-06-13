package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Główna klasa GUI aplikacji oparta na bibliotece JavaFX.
 * <p>
 * Odpowiada za budowanie okna, w tym paneli konfiguracyjnych
 * (dla wampirów, ludzi, wytrenowanych ludzi), tablic statystyk oraz obszaru renderowania grafiki (canvas).
 * Klasa dziedziczy po {@link Application} i uruchamia pętlę renderowania wizualnego {@link AnimationTimer}.
 * </p>
 */
public class SimulationGUI extends Application {
    Simulation simulation=new Simulation();
    SimulationConfig config=SimulationConfig.getInstance();
    SimulationStats stats=SimulationStats.getInstance();
    SimulationClock clock=SimulationClock.getInstance();
    Canvas canvas =new Canvas(300, 300);
    CanvasRenderingUtil renderingUtil=new CanvasRenderingUtil(canvas);
    HBox simulationPanel;
    HBox infoPanel;
    Label stepNumberLabel;
    Label hourNumberLabel;
    VBox interactionsPanel;
    VBox objectsPanel;

    /**
     * Punkt wejścia dla aplikacji JavaFX.
     * <p>
     * Inicjalizuje i układa poszczególne panele interfejsu (panele konfiguracji, startowe
     * oraz informacyjne) w {@link BorderPane}. Konfiguruje scenę.
     * </p>
     * * @param stage Główne okno GUI.
     */
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

        HBox controlPanel=new HBox();
        controlPanel.setSpacing(30);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.getStyleClass().add("control-hbox");

        simulationPanel=new HBox();
        simulationPanel.setSpacing(15);
        simulationPanel.setPadding(new Insets(15));
        simulationPanel.setAlignment(Pos.CENTER);
        simulationPanel.setPrefHeight(410);

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

    /**
     * Aktywuje pętlę aktualizacji GUI (renderowania obrazu i aktualizacji statystyk symulacji)
     */
    private void startAnimationLoop(){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ArrayList<ObjectToRender> listToRender=simulation.getSnapshot();
                renderingUtil.render(listToRender);
                updateInfo();
            }
        };
        timer.start();
    }

    /**
     * Inicjalizuje symulację
     * <p>
     *     Ustawia wysokość i szerokość canvas, uruchamia wątek silnika symulacji i rozpoczyna pętlę aktualizacji GUI
     * </p>
     */
    private void initSimulation(){
        if(config.getWorldConfig().isInitiated()) return;
        canvas.setWidth(config.getWorldConfig().getWidth());
        canvas.setHeight(config.getWorldConfig().getHeight());
        Thread simulationThread=new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();
        startAnimationLoop();
    }

    /**
     * Aktualizuje panel informacyjny okna symulacji, przypisuje nowe dane do labeli.
     */
    private void updateInfo(){
        stepNumberLabel.setText(String.valueOf(clock.getStep()));
        hourNumberLabel.setText(String.valueOf((int)(Math.floor(clock.getHour()))));

        objectsPanel.getChildren().clear();
        for(Map.Entry<ObjectType, Integer> entry :stats.getObjectsMap().entrySet()){
            VBox container=new VBox(5);
            Label textLabel=new Label(entry.getKey().getDescription());
            textLabel.getStyleClass().add("main-label");
            Label numberLabel=new Label(String.valueOf(entry.getValue()));
            container.getChildren().addAll(textLabel, numberLabel);
            objectsPanel.getChildren().add(container);
        }

        interactionsPanel.getChildren().clear();
        for(Map.Entry<InteractionType, Integer> entry :stats.getInteractionsMap().entrySet()){
            VBox container=new VBox(5);
            Label textLabel=new Label(entry.getKey().getDescription());
            textLabel.getStyleClass().add("main-label");
            Label numberLabel=new Label(String.valueOf(entry.getValue()));
            container.getChildren().addAll(textLabel, numberLabel);
            interactionsPanel.getChildren().add(container);
        }
    }

    /**
     * Tworzy panel informacyjny GUI, zawierający krok, godzinę i statystyki symulacji.
     * @return Stworzony panel informacyjny.
     */
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

    /**
     * Tworzy panel startowy zawierający przycisk inicjalizacji i startu symulacji.
     * W
     * Dodaje listner na event przyciśnięcia przycisku inicjalizacji. Event handler wywołuje metodę inicjalizacyjną.
     * @return Panel startowy okna GUI
     */
    private VBox createStartPanel(){
        Button initButton=new Button("Stwórz symulację");
        initButton.setOnAction((event)->{
            initSimulation();
        });

        VBox startButton=createNewToggleButtonWithLabel("Start/Stop","Włącz symulację", config.getWorldConfig()::isPaused,
                config.getWorldConfig()::setPaused);

        VBox panel=new VBox(15);
        panel.getChildren().addAll(initButton, startButton);

        return panel;
    }

    /**
     * Tworzy główny panel konfiguracyjny GUI.
     * @return Panel główny konfiguracji.
     */
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

    /**
     * Tworzy panel konfiguracyjny GUI dla wampirów.
     * @return Panel konfiguracji wampirów.
     */
    private VBox createVampirePanel(){
        Label label=new Label("Panel ustawień wampirów");
        label.getStyleClass().add("main-label");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getVampireConfig()::getEnergyBoost,config.getVampireConfig()::setEnergyBoost, "Pojedynczy boost energii wampira");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000,config.getVampireConfig()::getEnergyLoss,config.getVampireConfig()::setEnergyLoss, "Pojedyncza strata energii wampira");
        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, energyBoost, energyLoss);
        return panel;
    }

    /**
     * Tworzy panel konfiguracyjny GUI dla ludzi.
     * @return Panel konfiguracji ludzi.
     */
    private VBox createHumanPanel(){
        Label label=new Label("Panel ustawień osób");
        label.getStyleClass().add("main-label");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyBoost,config.getHumanConfig()::setEnergyBoost, "Pojedynczy boost energii człowieka");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyLoss,config.getHumanConfig()::setEnergyLoss, "Pojedyncza strata energii człowieka");
        VBox addProb=createNewSliderWithLabel(0, 0.1f, config.getHumanConfig()::getAddProb,config.getHumanConfig()::setAddProb, "Prawdopodobieństwo urodzenia nowego człowieka w jednym kroku");
        VBox transformationProb=createNewSliderWithLabel(0, 20, config.getHumanConfig()::getTransformationProb,config.getHumanConfig()::setTransformationProb, "Prawdopodobieństwo zamiany w wampira");
        VBox panel=new VBox(10);
        panel.getChildren().addAll(label, energyBoost, energyLoss, addProb, transformationProb);
        return panel;
    }

    /**
     * Tworzy panel konfiguracyjny GUI dla wytrenowanych ludzi.
     * @return Panel konfiguracji wytrenowanych ludzi.
     */
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

    /**
     * Tworzy kontener labela ze spinnerem.
     * <p>
     *     Aktualizuje wybrane ustawienie symulacji za każdym razem, gdy zmienia się wartość spinnera.
     * </p>
     * @return Kontener spinnera.
     */
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

    /**
     * Tworzy kontener labela ze sliderem.
     * <p>
     *     Aktualizuje wybrane ustawienie symulacji za każdym razem, gdy zmienia się wartość slidera.
     * </p>
     * @return Kontener slidera.
     */
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

    /**
     * Tworzy kontener labela z toggle buttonem.
     * <p>
     *     Aktualizuje wybrane ustawienie symulacji za każdym razem, gdy zmienia się stan buttona (true lub false)
     * </p>
     * @return Kontener toggle buttona.
     */
    private VBox createNewToggleButtonWithLabel(String buttonText, String labelText,Supplier<Boolean> getter, Consumer<Boolean> setter){
        Label label=new Label(labelText);

        ToggleButton button = new ToggleButton(buttonText);
        button.setSelected(getter.get());


        button.selectedProperty().addListener((obs,oldVal,newVal)->{
            setter.accept(newVal);
        });

        VBox container=new VBox(5);
        container.getChildren().addAll(label, button);
        return container;
    }
}