package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimulationGUI extends Application {
    Simulation simulation=new Simulation();
    SimulationConfig config=SimulationConfig.getInstance();
    Canvas canvas =new Canvas(500, 500);

    public SimulationGUI(){}

    @Override
    public void start(Stage stage){
        VBox mainPanel=createMainPanel();
        VBox vampirePanel=createVampirePanel();
        VBox humanPanel=createHumanPanel();
        VBox trainedHumanPanel=createTrainedPanel();
        VBox startPanel=createStartPanel();

        BorderPane root=new BorderPane();
        javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale(2.0, 2.0, 0, 0);
        canvas.getTransforms().add(scale);
//        canvas.setScaleX(3);
//        canvas.setScaleY(3);
        root.setTop(canvas);
//
        HBox controlPanel=new HBox();
        controlPanel.setSpacing(15); // Odstępy między elementami (w pikselach)
        controlPanel.setPadding(new Insets(10)); // Margines wewnętrzny wokół panelu
        controlPanel.setAlignment(Pos.CENTER); // Wyśrodkowanie elementów w panelu
        controlPanel.setStyle("-fx-background-color: #ececec;");

        controlPanel.getChildren().addAll(mainPanel, vampirePanel, humanPanel, trainedHumanPanel, startPanel);
        root.setBottom(controlPanel);
        Scene scene = new Scene(root);
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
        gc.setFill(Color.LIGHTGREEN);
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for(ObjectToRender obj : listToRender){
            switch (obj.type()){
                case "VAMPIRE"->{
                    gc.setFill(Color.PURPLE);
                    gc.fillOval(obj.x(), obj.y(), 5,5);
                }
                case "HUMAN"->{
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(obj.x(), obj.y(), 5,5);
                }
                case "TRAINED_HUMAN"->{
                    gc.setFill(Color.BROWN);
                    gc.fillOval(obj.x(), obj.y(), 5,5);
                }
                case "GARLIC"->{
                    gc.setFill(Color.GRAY);
                    gc.fillOval(obj.x(), obj.y(), 5,5);
                }
                case "GARLIC_CONTAINER_CELL"->{
                    gc.setFill(Color.BLACK);
                    gc.fillRect(obj.x(), obj.y(), 5,5);
                }
            }

        }
    }

    private VBox createStartPanel(){
        Button initButton=new Button("Zainicjalizuj symulację");
        initButton.setOnAction((event)->{
            if(config.getWorldConfig().isInitiated()) return;
            canvas.setWidth(config.getWorldConfig().getWidth());
            canvas.setHeight(config.getWorldConfig().getHeight());
            Thread simulationThread=new Thread(simulation);
            simulationThread.setDaemon(true);
            simulationThread.start();
            startAnimationLoop();
            System.out.println(config.getHumanConfig().getAddProb());
        });

        VBox startButton=createNewToggleButtonWithLabel("Start/Stop","Włącz symulację", config.getWorldConfig()::isPaused,
                config.getWorldConfig()::setPaused);

        VBox panel=new VBox(5);
        panel.getChildren().addAll(initButton, startButton);

        return panel;
    }

    private VBox createMainPanel(){
        Label label=new Label("Główny panel ustawień");
        VBox mode=createNewToggleButtonWithLabel("Dzień-Noc", "Tryb", config.getWorldConfig()::isCycling, config.getWorldConfig()::setCycling);
        VBox boardWidth=createNewSpinnerWithLabel(100, 1000, config.getWorldConfig()::getWidth, config.getWorldConfig()::setWidth,
                "Szerokość planszy");
        VBox boardHeight=createNewSpinnerWithLabel(100, 1000, config.getWorldConfig()::getHeight, config.getWorldConfig()::setHeight,
                "Wysokość planszy");
        VBox initialVampireNumber=createNewSpinnerWithLabel(0, 80, config.getVampireConfig()::getInitialNumber, config.getVampireConfig()::setInitialNumber,
                "Początkowa liczba wampirów");
        VBox initialHumanNumber=createNewSpinnerWithLabel(0, 80, config.getHumanConfig()::getInitialNumber, config.getHumanConfig()::setInitialNumber, "Początkowa liczba zwykłych ludzi");
        VBox initialTrainedHumanNumber=createNewSpinnerWithLabel(0, 80, config.getTrainedHumanConfig()::getInitialNumber, config.getTrainedHumanConfig()::setInitialNumber, "Początkowa liczba wytrenowanych ludzi");

        VBox panel=new VBox(5);
        panel.getChildren().addAll(label, mode, boardWidth, boardHeight, initialVampireNumber, initialHumanNumber, initialTrainedHumanNumber);
        return panel;
    }

    private VBox createVampirePanel(){
        Label label=new Label("Panel ustawień wampirów");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getVampireConfig()::getEnergyBoost,config.getVampireConfig()::setEnergyBoost, "Pojedynczy boost energii wampira");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000,config.getVampireConfig()::getEnergyLoss,config.getVampireConfig()::setEnergyLoss, "Pojedyncza strata energii wampira");
        VBox panel=new VBox(5);
        panel.getChildren().addAll(label, energyBoost, energyLoss);
        return panel;
    }
    private VBox createHumanPanel(){
        Label label=new Label("Panel ustawień osób");
        VBox energyBoost=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyBoost,config.getHumanConfig()::setEnergyBoost, "Pojedynczy boost energii człowieka");
        VBox energyLoss=createNewSpinnerWithLabel(0, 1000, config.getHumanConfig()::getEnergyLoss,config.getHumanConfig()::setEnergyLoss, "Pojedyncza strata energii człowieka");
        VBox addProb=createNewSliderWithLabel(0, 10, config.getHumanConfig()::getAddProb,config.getHumanConfig()::setAddProb, "Prawdopodobieństwo urodzenia nowego człowieka w jednym kroku");
        VBox transformationProb=createNewSliderWithLabel(0, 10, config.getHumanConfig()::getTransformationProb,config.getHumanConfig()::setTransformationProb, "Prawdopodobieństwo zamiany w wampira");
        VBox panel=new VBox(5);
        panel.getChildren().addAll(label, energyBoost, energyLoss, addProb, transformationProb);
        return panel;
    }
    private VBox createTrainedPanel(){
        Label label=new Label("Panel ustawień wytrenowych osób");
        VBox garlicStockMax=createNewSpinnerWithLabel(0, 20,config.getTrainedHumanConfig()::getGarlicStockMax,config.getTrainedHumanConfig()::setGarlicStockMax, "Maksymalna liczba posiadanego czosnku");
        VBox recruitmentProb=createNewSliderWithLabel(0, 10, config.getTrainedHumanConfig()::getRecruitmentProb,config.getTrainedHumanConfig()::setRecruitmentProb, "Prawdopodobieństwo rekrutacji zwykłego człowieka");
        VBox throwProb=createNewSliderWithLabel(0, 10, config.getTrainedHumanConfig()::getThrowProb,config.getTrainedHumanConfig()::setThrowProb, "Prawdopodobieństwo rozrzucenia czosnku w jednym kroku");
        VBox panel=new VBox(5);
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