package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CanvasRenderingUtil {
    private Canvas canvas;
    private SimulationClock clock=SimulationClock.getInstance();

    private String vampireSVGPath="M 50,15 Q 32,15 32,35 L 20,25 L 28,45 Q 28,65 50,75 Q 72," +
            "65 72,45 L 80,25 L 68,35 Q 68,15 50,15 Z M 50,22 Q 58,32 66,36 Q 66,22 50,22" +
            " Z M 34,36 Q 42,32 50,22 Q 50,22 34,36 Z M 40,45 A 3,3 0 1,1 46,45 A 3,3 0 1," +
            "1 40,45 Z M 54,45 A 3,3 0 1,1 60,45 A 3,3 0 1,1 54,45 Z M 42,56 L 44,56 L 43,6" +
            "1 Z M 58,56 L 56,56 L 57,61 Z M 40,55 Q 50,59 60,55 Q 50,57 40,55 Z";
    private String humanSVGPath="M 50,40 A 12,12 0 1,1 50,16 A 12,12 0 1,1 50,40 Z " +
            "M 50,46 C 33,46 20,55 20,72 L 20,84 L 80,84 L 80,72 C 80,55 67,46 50,46 Z";
    private String garlicSVGPath="M 50,15 Q 48,25 43,28 Q 20,25 20,53 Q 20,85 50,85 " +
            "Q 80,85 80,53 Q 80,25 57,28 Q 52,25 50,15 Z M 38,34 Q 38,60 48,83 Q 34,70 34,44 Z " +
            "M 62,34 Q 66,70 52,83 Q 62,60 62,44 Z M 49,27 L 51,27 L 50,84 Z";

    public CanvasRenderingUtil(Canvas canvas){
        this.canvas=canvas;
    }
    public void render(ArrayList<ObjectToRender> listToRender){
        if(listToRender==null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
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
        gc.setFill(Color.web(hexColor));

        gc.beginPath();
        gc.appendSVGPath(vampireSVGPath);
        gc.fill();

        gc.restore();
    }

    public void drawHuman(GraphicsContext gc, int x, int y, String hexColor) {
        gc.save();
        gc.translate(x, y);

        gc.scale(0.1, 0.1);
        gc.setFill(Color.web(hexColor));

        gc.beginPath();
        gc.appendSVGPath(humanSVGPath);
        gc.fill();

        gc.restore();
    }
    public void drawGarlic(GraphicsContext gc, int x, int y, String hexColor) {
        gc.save();
        gc.translate(x, y);

        gc.scale(0.07, 0.07);
        gc.setFill(Color.web(hexColor));

        gc.beginPath();
        gc.appendSVGPath(garlicSVGPath);
        gc.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);
        gc.fill();

        gc.restore();
    }
    public void drawShadow(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.color(0, 0, 0, 0.2));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.restore();
    }
}
