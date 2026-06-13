package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Klasa odpowiedzialna za renderowanie graficzne stanu symulacji.
 * <p>
 * Odpowiada za czyszczenie ekranu, rysowanie mapy (świata), nakładanie cienia symbolizującego
 * noc oraz rysowanie poszczególnych agentów i obiektów przy użyciu ścieżek SVG.
 * </p>
 */
public class CanvasRenderingUtil {
    private final Canvas canvas;
    private final SimulationClock clock=SimulationClock.getInstance();

    /**
     * Definicja ścieżki wektorowej SVG reprezentującej ikonę wampira.
     */
    private final String vampireSVGPath="M 0,-30 Q -18,-30 -18,-10 L -30,-20 L " +
            "-22,0 Q -22,20 0,30 Q 22,20 22,0 L 30,-20 L 18,-10 Q 18,-30 0,-30" +
            " Z M 0,-23 Q 8,-13 16,-9 Q 16,-23 0,-23 Z M -16,-9 Q -8,-13 0," +
            "-23 Q 0,-23 -16,-9 Z M -10,0 A 3,3 0 1,1 -4,0 A 3,3 0 1,1 -10,0" +
            " Z M 4,0 A 3,3 0 1,1 10,0 A 3,3 0 1,1 4,0 Z M -8,11 L -6,11 L -7," +
            "16 Z M 8,11 L 6,11 L 7,16 Z M -10,10 Q 0,14 10,10 Q 0,12 -10,10 Z";

    /**
     * Definicja ścieżki wektorowej SVG reprezentującej ikonę człowieka.
     */
    private final String humanSVGPath="M 0,-10 A 12,12 0 1,1 0,-34 A 12,12 0 1,1" +
            " 0,-10 Z M 0,-4 C -17,-4 -30,5 -30,22 L -30,34 L 30,34 L 30,22 C " +
            "30,5 17,-4 0,-4 Z";

    /**
     * Definicja ścieżki wektorowej SVG reprezentującej czosnek.
     */
    private final String garlicSVGPath="M 0,-35 Q -2,-25 -7,-22 Q -30,-25 -30,3 Q" +
            " -30,35 0,35 Q 30,35 30,3 Q 30,-25 7,-22 Q 2,-25 0,-35 Z M -12,-16 Q" +
            " -12,10 -2,33 Q -16,20 -16,-6 Z M 12,-16 Q 16,20 2,33 Q 12,10 12,-6 Z" +
            " M -1,-23 L 1,-23 L 0,34 Z";

    /**
     * Konstruuje obiekt powiązany z płótnem (canvas) interfejsu graficznego.
     * * @param canvas Płótno JavaFX, na którym rysowana będzie grafika.
     */
    public CanvasRenderingUtil(Canvas canvas){
        this.canvas=canvas;
    }


    /**
     * Wykonuje pełny cykl renderowania aktualnego kadru stanu symulacji.
     * <p>
     * Metoda czyści całe płótno i wypełnia je zielonym tłem.
     * Następnie iteruje po liście obiektów do wyrenderowania, rozpoznaje ich typ i wywołuje
     * metody rysujące. Później, jeśli w symulacji panuje noc, nakłada
     * warstwę cienia ({@link #drawShadow(GraphicsContext)}).
     * </p>
     * * @param listToRender Lista lekkich obiektów {@link ObjectToRender} zwierających współrzędne i typy.
     */
    public void render(ArrayList<ObjectToRender> listToRender){
        if(listToRender==null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#50af3f"));
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for(ObjectToRender obj : listToRender){
            switch (obj.type()){
                case VAMPIRE->{
                    drawVampire(gc, obj.x(), obj.y(), "#3f0d73");
                }
                case TRAINED_HUMAN->{
                    drawHuman(gc, obj.x(), obj.y(), "#cd0606");
                }
                case HUMAN->{
                    drawHuman(gc, obj.x(), obj.y(), "#e5ba0f");
                }
                case GARLIC->{
                    drawGarlic(gc, obj.x(), obj.y(), "#bfbab7");
                }
                case GARLIC_CONTAINER_CELL->{
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

    /**
     * Rysuje ikonę wampira.
     * * @param gc       Kontekst graficzny 2D powiązany z płótnem.
     * @param x        Współrzędna x na płótnie, gdzie ma znaleźć się ikona.
     * @param y        Współrzędna y na płótnie, gdzie ma znaleźć się ikona.
     * @param hexColor Kolor ikony w formacie HEX.
     */
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

    /**
     * Rysuje ikonę człowieka.
     * * @param gc       Kontekst graficzny 2D powiązany z płótnem.
     * @param x        Współrzędna x na płótnie, gdzie ma znaleźć się ikona.
     * @param y        Współrzędna y na płótnie, gdzie ma znaleźć się ikona.
     * @param hexColor Kolor ikony w formacie HEX.
     */
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

    /**
     * Rysuje ikonę czosnku.
     * * @param gc       Kontekst graficzny 2D powiązany z płótnem.
     * @param x        Współrzędna x na płótnie, gdzie ma znaleźć się ikona.
     * @param y        Współrzędna y na płótnie, gdzie ma znaleźć się ikona.
     * @param hexColor Kolor ikony w formacie HEX.
     */
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

    /**
     * Nakłada półprzezroczystą warstwę na płótno
     * * @param gc       Kontekst graficzny 2D powiązany z płótnem.
     */
    public void drawShadow(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.color(0, 0, 0, 0.2));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.restore();
    }
}
