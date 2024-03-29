package aufgabe4;

import aufgabe1.TraverseGraphAlgorithms;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 25.12.12
 * Time: 16:34
 */
public class EulerCircles {
    /**
     * Erstellt eine Eulertour auf dem gegebenen Graphen in quadratischer Zeitkomplexität ( O(AnzahlKanten²) ).
     *
     * @param g <b>PRECONDITION </b> - Der übergebene Graph muss zusammenhängend und ungerichtet sein und alle Knoten müsssen einen
     *          geraden Knotengrad aufweisen!
     * @return - Liste von Strings, Reihenfolge in der die Knoten sich in der Liste befinden beschreibt die Tour.
     */
    public static List<String> fleury(Graph g) {
        Graph workingCopy;
        if (g instanceof UndirectedGraph) {
            workingCopy = new Pseudograph(DefaultEdge.class);
            if (!Graphs.addGraph(workingCopy, g))
                throw new IllegalArgumentException("Can´t copy source graph to destination graph");
        } else {
            throw new IllegalArgumentException("Erstmal nur Ungerichtet Garphen");
        }

        List<String> path = new ArrayList<String>();
        //waehle irgendeinen knoten aus
        String currentVertex = (String) workingCopy.vertexSet().iterator().next();
        path.add(currentVertex);
        // solange es noch kanten im graphen gibt füge sie zur eulertour hinzu
        while (workingCopy.edgeSet().size() != 0) {
            //waehle unter den unmarkierten inzedenten Kanten zum currentVertex eine aus,
            // dabei sind zuerst die Kanten zuwählen die keine Schnittkanten sind.
            DefaultEdge preferredEdge = choosePreferredEdge(workingCopy, currentVertex);
            // setze den Zielknoten der Kante als neuen currentVertex;
            currentVertex = getTargetVertex(workingCopy, preferredEdge, currentVertex);
            // makiere die Kante
            workingCopy.removeEdge(preferredEdge);
            path.add(currentVertex);
        }
        return path;
    }

    /**
     * <b>Hilfsfunktion</b> zu EulerCircles.fleury(...)
     * <br>
     * Versucht zuerst die Kanten zu wählen die Keine Schnittkanten sind,
     * gibt es keine von diesen Kanten mehr, dann werden die Schnittkanten zurückgegeben
     *
     * Schnittkante ist eine Kante, die beim entfernen dazuführt, dass der Graph nicht mehr Zusammenhängend ist.
     * die Schnittkanten Bestimmung erfolgt durch den currentVertex und die Tiefensuche. Dabei wird höchsten
     * die Anzahl an Ausgehnden Kanten vom currentVertex mal Tiefensuche ausgeführt.
     *
     * Bsp:  Ungerichteter Graph G =( {a,b,c} , { {a,b},{a,c},{b,c} } )
     * currentVertex = a
     * Kanten von a => { {a,b} , {a,c} }
     * Erste Kante von a wird entfernt, nun wird mit Tiefensuche getestet ob vertex b noch erreichbar ist,
     * wenn er nun noch erreichbar ist, dann ist diese entfernte Kante keine Schnittkante. Andernfalls ist es
     * eine Schnittkante. In diesem Beispiel ist vertex b noch über c zu erreichen (weil ungerichtet)
     *
     * @param g              der Graph
     * @param currentVertex  der Ausgangsvertex
     * @return  die Kante
     */
    private static DefaultEdge choosePreferredEdge(Graph g, String currentVertex) {
        boolean isCuttingEdge = false;
        List<DefaultEdge> touchedEdgesOfCurrentVertex = new ArrayList<DefaultEdge>();
        touchedEdgesOfCurrentVertex.addAll(g.edgesOf(currentVertex));
        String targetVertex;
        for (DefaultEdge edge : touchedEdgesOfCurrentVertex) {

            if (g.getEdgeSource(edge).equals(currentVertex)) {
                targetVertex  = (String) g.getEdgeTarget(edge);
                g.removeEdge(currentVertex, targetVertex);
                // wenn es ohne diese Kante immer noch einen Weg zum targetVertex gibt, dann ist
                // der Graph noch zusammenhängend und es ist somit keine Schnittkante.
                isCuttingEdge = TraverseGraphAlgorithms.depthFirstSearch(g, currentVertex, targetVertex, null);
                g.addEdge(currentVertex, targetVertex, edge);
            }else{
                targetVertex  = (String) g.getEdgeSource(edge);
                g.removeEdge(targetVertex, currentVertex);
                // wenn es ohne diese Kante immer noch einen Weg zum targetVertex gibt, dann ist
                // der Graph noch zusammenhängend und es ist somit keine Schnittkante.
                isCuttingEdge = TraverseGraphAlgorithms.depthFirstSearch(g, currentVertex, targetVertex, null);
                g.addEdge(targetVertex,currentVertex, edge);
            }

            if (!isCuttingEdge) {
                return edge;
            }
        }
        for (DefaultEdge cuttingEdge : touchedEdgesOfCurrentVertex) {
            return cuttingEdge;
        }
        throw new IllegalArgumentException("Konnte keine Edge in diesem Vertex finden :: " + currentVertex);
    }

    /**
     * <b>Hilfsfunktion</b> zu EulerCircles.fleury(...)
     * <br>  Liefert einen TargetVertex von einer Edge
     * @param g
     * @param edge
     * @param currentVertex
     * @return
     */
    private static String getTargetVertex(Graph g, DefaultEdge edge, String currentVertex) {
        String target = (String) g.getEdgeTarget(edge);
        if (g instanceof UndirectedGraph && target.equals(currentVertex)) {
            target = (String) g.getEdgeSource(edge);
        }
        return target;
    }

    /**
     *  Ermittelt eine Eulertour in linearer Zeitkomplexität ( O( AnzahlKanten ).
     * @param g <b>PRECONDITION</b> - Der gegebene Graph ist ungerichtet und zusammenhängend und alle Knoten
     *          besitzen einen geraden Grad
     * @return - Liste von Strings, Reihenfolge in der die Knoten sich in der Liste befinden beschreibt die Tour.
     */
    public static List<String> hierholzer(Graph g) {
        Graph workingCopy;
        if (g instanceof UndirectedGraph) {
            workingCopy = new Pseudograph(DefaultEdge.class);
            if (!Graphs.addGraph(workingCopy, g))
                throw new IllegalArgumentException("Can´t copy source graph to destination graph");
        } else {
            throw new IllegalArgumentException("Erstmal nur Ungerichtet Garphen");
        }

        List<String> path = new ArrayList();
        List<String> circlePath = new ArrayList();
        int randomChoosedEdges = workingCopy.vertexSet().size()/2;
        int currentVertexPosition = 0;
        //waehle irgendeinen knoten aus
        path.add((String) workingCopy.vertexSet().iterator().next());
        String currentVertex;

        // solange wie der Graph noch Kanten enthält erzeuge neue kreise
        while (workingCopy.edgeSet().size() != 0) {
            // bestimme den aktuellen knoten
            currentVertexPosition = findNextSuitableVertex(workingCopy, path,currentVertexPosition);
            currentVertex = path.get(currentVertexPosition);
            // entferne den aktuellen vertex aus dem weg, damit er später durch den von dort
            // erstellten kreis substituiert werden kann.
            path.remove(currentVertexPosition);

            // erstelle einen neuen kreis
//            circlePath = createCircle_RANDOM(workingCopy, currentVertex, Math.min(randomChoosedEdges, Math.max(workingCopy.edgeSet().size() - 1, 1)));
            circlePath = createCircle_firstEdge(workingCopy, currentVertex);
            // substituiere den kreis in den hauptkreis, an der position des aktuellenvertexes
            path.addAll(currentVertexPosition, circlePath);


        }

        return path;
    }

    /**
     * <b>Hilfsfunktion:</b> zu EulerCircles.hierholzer(...)
     * <br> Erstellt einen <b>ZUFÄLLIGEN </b> Kreis auf dem gegebenen Graphen von einem Ausgangsvertex aus.
     * Es wird eine übergebene Anzahl an zufälligen Kanten gewählt, danach wird geprüft ob der aktuelle
     * Vertex mit dem Ausgangsvertex übereinstimmmt, ist dies nicht der fall, dann wird per Tiefensuche
     * von dem aktuellen Vertex aus ein Weg zurück zum Ausgangsvertex bestimmt (Die verwendeten Kanten wurden
     * aus dem Graphen entfernt). Der Weg vom Ausgangsvertex bis zum aktuellen Vertex und der Weg vom
     * aktuellen Vertex zum Ausgangsvertex wird dann zum Kreis vereinigt.
     * @param g  der Graph, der nur eine kopie vom echten Graph ist die in EulerCircles.hierholzer(...) erstellt wird
     * @param currentVertex der Ausgangsvertex von dem aus der Kreis gestartet wird
     * @param countOfRandomChoosedEdges die Anzahl an zufällig zuwählenden Kanten
     * @return den Kreis als Liste von Strings, [a, b, c, a]
     */
    @Deprecated
    private static List<String> createCircle_RANDOM(Graph g, String currentVertex, int countOfRandomChoosedEdges) {
        String circleStartVertex = currentVertex, sourceVertex = null;
        List<String> circlePath = new ArrayList<String>();
        List<DefaultEdge> listOfRemovedEdges = new ArrayList<DefaultEdge>();
        Stack<RemovedEdge> removedEdges = new Stack<RemovedEdge>();

        DefaultEdge edge = null;

        for (int i = 0; i < countOfRandomChoosedEdges; i++) {
            // knoten in den circlePath aufnehmen
            circlePath.add(currentVertex);

            // eine zufällige kante wählen
            List<DefaultEdge> listOfTouchingEdges = new ArrayList<DefaultEdge>();
            listOfTouchingEdges.addAll(g.edgesOf(currentVertex));
            Collections.shuffle(listOfTouchingEdges);
            edge = listOfTouchingEdges.get(0);

            // neuen aktuellen knoten wählen
            sourceVertex = currentVertex;
            currentVertex = getTargetVertex(g, edge, currentVertex);
            // kante aus dem graphen entfernen
            g.removeEdge(edge);
            // und auf einen stack retten um später eventuell nochmal korrekturen am pfad vorzunehmen
            removedEdges.push(new RemovedEdge(edge, sourceVertex, currentVertex));

            // sollte der aktuelle vertex zu diesem zeitpunkt der Startvertex des kreises sein, dann haben wir ganz zufällig
            // einen kreis erzeugt und können dann auch hier schon direkt aussteigen.
            if (currentVertex.equals(circleStartVertex)) {
                circlePath.add(circleStartVertex);
                return circlePath;
            }
        }

        List<String> wayBack = new ArrayList<String>();
        RemovedEdge removedEdge;
        while (!TraverseGraphAlgorithms.depthFirstSearch(g, currentVertex, circleStartVertex, wayBack) && !removedEdges.empty()) {
            removedEdge = removedEdges.pop();
            g.addEdge(removedEdge.startVertex, removedEdge.targetVertex, removedEdge.edge);
            currentVertex = removedEdge.startVertex;
            circlePath = circlePath.subList(0, circlePath.size() - 1);
            wayBack.clear();
        }

        g.removeEdge(g.getEdge(currentVertex, wayBack.get(0)));
        for (int i = 0; i < wayBack.size() - 1; i++) {
            g.removeEdge(g.getEdge(wayBack.get(i), wayBack.get(i + 1)));
        }
        circlePath.addAll(wayBack);

        return circlePath;

    }

    /**
     * <b>Hilfsfunktion:</b> zu EulerCircles.hierholzer(...)
     * <br> Erstellt einen Kreis auf dem gegebenen Graphen von einem Ausgangsvertex aus, in dem immer die erste Kante des
     * aktuellen Knoten gewählt wird.
     * @param g  der Graph, der nur eine kopie vom echten Graph ist die in EulerCircles.hierholzer(...) erstellt wird
     * @param currentVertex der Ausgangsvertex von dem aus der Kreis gestartet wird
     * @return den Kreis als Liste von Strings, [a, b, c, a]
     */
    private static List<String> createCircle_firstEdge(Graph g,String currentVertex){
        String circleStartVertex = currentVertex, sourceVertex = null;
        List<String> circlePath = new ArrayList<String>();

        DefaultEdge edge = null;

        circlePath.add(currentVertex);

        // lösche solange Kanten bis man wieder am Startknoten des Kreises angekommen ist
        do{
            // erste Kante des knoten wählen
            edge = (DefaultEdge)g.edgesOf(currentVertex).iterator().next();

            // neuen aktuellen knoten wählen
            currentVertex = getTargetVertex(g, edge, currentVertex);
            // kante aus dem graphen entfernen
            g.removeEdge(edge);

            // knoten in den circlePath aufnehmen
            circlePath.add(currentVertex);
        }while(!circleStartVertex.equals(currentVertex));

        return circlePath;
    }

    /**
     * <b>Hilfsfunktion:</b> zu EulerCircles.hierholzer(...)
     * <br>
     *  bestimmt den nächsten passenden Knoten aus einem gegebenen Kreis der einen Knotengrade größer 0 hat.
     * @param g der Graph
     * @param circle der Kreis als Liste von Strings
     * @return  die Position in dem Kreis
     */
    private static int findNextSuitableVertex(Graph g, List<String> circle,int old_pos) {
        int index = old_pos;

        for (String circleMemberVertex : circle.subList(old_pos,circle.size())) {
            if (g.edgesOf(circleMemberVertex).size() > 0) return index;
            index++;
        }

        throw new IllegalArgumentException("Dieser kreis hat keine edges mehr!");
    }

    /** <b>Hilfsklasse: </b>zu EulerCircles.hierholzer(...)<br>
     * Dient zum halten der gelöschten Edges, um sie Eventuell nochmal wiederherstellen zu können
     */
    private static class RemovedEdge {
        public DefaultEdge edge;
        public String startVertex;
        public String targetVertex;

        private RemovedEdge(DefaultEdge edge, String startVertex, String targetVertex) {
            this.edge = edge;
            this.startVertex = startVertex;
            this.targetVertex = targetVertex;
        }
    }

    private static class UndirectedJGraphWithAdjazenzMatrix{
        Graph jgraph;
        int[][] adjazenzMatrix;
        Map<String,Integer> indeceMap = new HashMap<String,Integer>();

        private UndirectedJGraphWithAdjazenzMatrix(Graph graph) {
            if(!(graph instanceof UndirectedGraph))
                throw new IllegalArgumentException("übergebener Graph ist nicht ungerichtet");
            this.jgraph = graph;
            adjazenzMatrix = MatrixFeatures.createAdjazenzMatrix(graph,indeceMap);
        }

        public void removeEdge(String source,String target) {
            jgraph.removeEdge(source, target);
            MatrixFeatures.removeUndirectedEdge(adjazenzMatrix,indeceMap.get(source),indeceMap.get(target));
        }
        public void removeEdge(DefaultEdge edge) {
             removeEdge((String) jgraph.getEdgeSource(edge),(String) jgraph.getEdgeTarget(edge));
        }
        public void addEdge(String source,String target,DefaultEdge edge) {
            jgraph.addEdge(source, target, edge);
            MatrixFeatures.addUndirectedEdge(adjazenzMatrix, indeceMap.get(source), indeceMap.get(target));
        }

        public boolean isThereAWay(String source, String target) {
            return  MatrixFeatures.isThereAWay(adjazenzMatrix,indeceMap.get(source),indeceMap.get(target));
        }
        public Collection<DefaultEdge> edgeSet() {
            return (Collection<DefaultEdge>) jgraph.edgeSet();
        }
        public Collection<String> vertexSet() {
            return (Collection<String>) jgraph.vertexSet();
        }
        public Graph getGraph(){
            return jgraph;
        }
    }

    /**
     * Erstellt eine Eulertour auf dem gegebenen Graphen in quadratischer Zeitkomplexität ( O(AnzahlKanten²) ).
     *
     * @param g <b>PRECONDITION </b> - Der übergebene Graph muss zusammenhängend und ungerichtet sein und alle Knoten müsssen einen
     *          geraden Knotengrad aufweisen!
     * @return - Liste von Strings, Reihenfolge in der die Knoten sich in der Liste befinden beschreibt die Tour.
     */
    public static List<String> fleury_adjazenMatrix(Graph g) {
        Graph graphCopy;
        UndirectedJGraphWithAdjazenzMatrix workingCopy;
        if (g instanceof UndirectedGraph) {
            graphCopy = new Pseudograph(DefaultEdge.class);
            if (!Graphs.addGraph(graphCopy, g))
                throw new IllegalArgumentException("Can´t copy source jgraph to destination jgraph");
            workingCopy = new UndirectedJGraphWithAdjazenzMatrix(graphCopy);
        } else {
            throw new IllegalArgumentException("Erstmal nur Ungerichtet Garphen");
        }

        List<String> path = new ArrayList<String>();
        //waehle irgendeinen knoten aus
        String currentVertex = (String) workingCopy.vertexSet().iterator().next();
        path.add(currentVertex);
        // solange es noch kanten im graphen gibt füge sie zur eulertour hinzu
        while (workingCopy.edgeSet().size() != 0) {
            //waehle unter den unmarkierten inzedenten Kanten zum currentVertex eine aus,
            // dabei sind zuerst die Kanten zuwählen die keine Schnittkanten sind.
            DefaultEdge preferredEdge = choosePreferredEdge_adjazenzMatrix(workingCopy, currentVertex);
            // setze den Zielknoten der Kante als neuen currentVertex;
            currentVertex = getTargetVertex(workingCopy.jgraph, preferredEdge, currentVertex);
            // makiere die Kante
            workingCopy.removeEdge(preferredEdge);
            path.add(currentVertex);
        }
        return path;
    }

    /**
     * <b>Hilfsfunktion</b> zu EulerCircles.fleury(...)
     * <br>
     * Versucht zuerst die Kanten zu wählen die Keine Schnittkanten sind,
     * gibt es keine von diesen Kanten mehr, dann werden die Schnittkanten zurückgegeben
     *
     * Schnittkante ist eine Kante, die beim entfernen dazuführt, dass der Graph nicht mehr Zusammenhängend ist.
     * die Schnittkanten Bestimmung erfolgt durch den currentVertex und die Matrixpotenzierung.
     *
     * Bsp:  Ungerichteter Graph G =( {a,b,c} , { {a,b},{a,c},{b,c} } )
     * currentVertex = a
     * Kanten von a => { {a,b} , {a,c} }
     * Erste Kante von a wird entfernt, nun wird mit Tiefensuche getestet ob vertex b noch erreichbar ist,
     * wenn er nun noch erreichbar ist, dann ist diese entfernte Kante keine Schnittkante. Andernfalls ist es
     * eine Schnittkante. In diesem Beispiel ist vertex b noch über c zu erreichen (weil ungerichtet)
     *
     * @param g              der Graph
     * @param currentVertex  der Ausgangsvertex
     * @return  die Kante
     */
    private static DefaultEdge choosePreferredEdge_adjazenzMatrix(UndirectedJGraphWithAdjazenzMatrix g, String currentVertex) {
        boolean isCuttingEdge = false;
        List<DefaultEdge> touchedEdgesOfCurrentVertex = new ArrayList<DefaultEdge>();
        touchedEdgesOfCurrentVertex.addAll(g.jgraph.edgesOf(currentVertex));
        String targetVertex;
        for (DefaultEdge edge : touchedEdgesOfCurrentVertex) {

            if (g.jgraph.getEdgeSource(edge).equals(currentVertex)) {
                 targetVertex = (String) g.jgraph.getEdgeTarget(edge);
                g.removeEdge(currentVertex, targetVertex);
                // wenn es ohne diese Kante immer noch einen Weg zum targetVertex gibt, dann ist
                // der Graph noch zusammenhängend und es ist somit keine Schnittkante.
                isCuttingEdge = g.isThereAWay(currentVertex, targetVertex);
                g.addEdge(currentVertex, targetVertex, edge);

            }else{
                targetVertex  = (String) g.jgraph.getEdgeSource(edge);
                g.removeEdge(targetVertex, currentVertex);
                // wenn es ohne diese Kante immer noch einen Weg zum targetVertex gibt, dann ist
                // der Graph noch zusammenhängend und es ist somit keine Schnittkante.
                isCuttingEdge = g.isThereAWay(currentVertex, targetVertex);
                g.addEdge(targetVertex,currentVertex, edge);
            }
            if (!isCuttingEdge) {
                return edge;
            }
        }
        for (DefaultEdge cuttingEdge : touchedEdgesOfCurrentVertex) {
            return cuttingEdge;
        }
        throw new IllegalArgumentException("Konnte keine Edge in diesem Vertex finden :: " + currentVertex);
    }

}
