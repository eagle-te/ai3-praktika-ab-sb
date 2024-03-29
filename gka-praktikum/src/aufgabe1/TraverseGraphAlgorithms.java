package aufgabe1;

import org.jgrapht.Graph;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: abg628
 * Date: 27.10.12
 * Time: 02:05
 */
public class TraverseGraphAlgorithms {

    final static private int START_STEP = 0;
    private static long GRAPH_ACCESS_COUNTER = 0;

    //ACCESS_COUNTER-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static long getAccessCounter(String graphName){
        long tmp = GRAPH_ACCESS_COUNTER;
        GRAPH_ACCESS_COUNTER = 0;
        return  tmp;
    }
    public static void initializeAccessCounter(){
        GRAPH_ACCESS_COUNTER = 0;
    }
    //BREADTH FIRST-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static List<CustomVertex> breadthFirst(Graph graph, String startVertex, String targetVertex) {
        List<CustomVertex> customVertexList = new ArrayList<CustomVertex>();
        //startVertex
        List<String> startAdjacentList = getAdjacentVertexes(graph, startVertex);
        CustomVertex vStart = new CustomVertex(startVertex, START_STEP, startAdjacentList);
        //targetVertex
        CustomVertex vTarget = new CustomVertex(targetVertex);

        customVertexList.add(vStart);

        return _breadthFirst(graph, customVertexList, vTarget);
    }

    private static List<CustomVertex> _breadthFirst(Graph graph, List<CustomVertex> customVertexList, CustomVertex targetVertex) {

        List<CustomVertex> newVertexList = new ArrayList<CustomVertex>(customVertexList);

        for (CustomVertex vertex : customVertexList) {

            if(!(vertex.isChecked())){
                for (String s : vertex.adjacentStringVertexes) {
                    CustomVertex newCustomVertex = new CustomVertex(s);
                    if (!(newVertexList.contains(newCustomVertex))) {
                        newCustomVertex.step = vertex.step + 1;
                        newCustomVertex.adjacentStringVertexes = new ArrayList<String>(getAdjacentVertexes(graph, newCustomVertex.label));
                        newVertexList.add(newCustomVertex);
                    }
                }
            }
            vertex.checked();
        }

        if (!(newVertexList.contains(targetVertex))) {
            return _breadthFirst(graph, newVertexList, targetVertex);
        } else {
            return newVertexList;
        }
    }


    //DEPTH FIRST-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public static List<CustomVertex> depthFirst(Graph g, String startVertex, String targetVertex) {
        Map<String, CustomVertex> map = new HashMap<String, CustomVertex>();
        map.put(startVertex, new CustomVertex(startVertex, 0, getAdjacentVertexes(g, startVertex)));
        _depthFirst(g, map.get(startVertex), map, 1);


        List<CustomVertex>list =  new ArrayList<CustomVertex>();
        for(Object name : g.vertexSet()){
            if(map.get(name) != null)
                list.add(map.get(name));
        }
        return  list;
    }


    private static void _depthFirst(Graph g, CustomVertex currentVertex, Map<String, CustomVertex> checkedMap, int steps) {
        //List<String> neighborsList = getAdjacentVertexes(g,currentVertex.label);

        for (String neighbor : currentVertex.adjacentStringVertexes) {
            // wenn die map den nachbar schon enthält, dann gucke ob der aktuelle steps wert
            // besser ist als der bereits eingetragene. Ist er besser,dann suche von da nach eventuell neuen wegen
            // andernfalls stoppe.
            if (checkedMap.containsKey(neighbor)) {
                if (checkedMap.get(neighbor).step > steps) {
                    checkedMap.get(neighbor).step = steps;
                    _depthFirst(g, checkedMap.get(neighbor), checkedMap, steps + 1);
                }

            } else {
                checkedMap.put(neighbor, new CustomVertex(neighbor, steps, getAdjacentVertexes(g, neighbor)));
                _depthFirst(g, checkedMap.get(neighbor), checkedMap, steps + 1);
            }

        }
    }

    //UTIL FUNCTIONS-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    private static List<String> reversePath(List<CustomVertex> path) {
        List<String> resultPath = new ArrayList<String>();
        ListIterator<CustomVertex> iter = path.listIterator(path.size());
        while (iter.hasPrevious()) {
            resultPath.add(iter.previous().label);
        }
        return resultPath;
    }

    /**
     * Bestimmt alle möglichen Wege auf der gegebnen
     * VertexList --[ diese enthält alle Markierten Knoten
     * sowie ihre Entferung (step) von dem Start Knoten (der Knoten mit der Entferung 0)
     * und deren benachbarte Knoten ]--
     *
     * @param vertexList  "Tabelle" siehe oben
     * @param target    Ziel Vertex
     * @return   Liste von Strings mit den kürzesten möglichen Pfaden
     */
    public static List<List<String>> findAllPaths(List<CustomVertex> vertexList, CustomVertex target) {
        List<List<String>> validPaths = new ArrayList<List<String>>();
        if(target == null)
            return validPaths;

        _findAllPaths(vertexList, target,new ArrayList<CustomVertex>(Arrays.asList(target)),validPaths);
       return  validPaths;
    }
    private static void _findAllPaths(List<CustomVertex> vertexList, CustomVertex target, List<CustomVertex> path,List<List<String>> validPaths) {
        if (target.step == 0)
            validPaths.add(reversePath(path));
        else {
            for (CustomVertex vertex : vertexList) {
                if (target.step - 1 == vertex.step && vertex.adjacentStringVertexes.contains(target.label)) {
                    path.add(vertex);
                    _findAllPaths(vertexList, vertex, path,validPaths);
                    path.remove(vertex);
                }
            }

        }
    }


    /**
     * Liefert die Benachbarten Knoten von einem gegebnen Knoten
     *
     * @param g  ein befülltes Graph Element aus dem JGraphT Paket
     * @param sourceVertex  Der Knoten von dem die Nachbarn bestimmt werden sollen
     * @return  Liste von Benachbarten Knoten als Strings
     */
    private static List<String> getAdjacentVertexes(Graph g, String sourceVertex) {
        Set<String> neighborList = new HashSet<String>();
        for (Object targetVertex : g.vertexSet()) {
            GRAPH_ACCESS_COUNTER++;
            if (g.getEdge(sourceVertex, targetVertex) != null) {
                neighborList.add(targetVertex.toString());
            }
        }
        neighborList.remove(sourceVertex);

        return new ArrayList<String>(neighborList);
    }

    /**
     *  Bestimmt den kürsten Weg auf einer gegeben vertexList
     *
     * @param vertexList
     * @param target
     * @return  liefert den kürzesten Weg zurück
     */
    public static List<String> getShortestPath(List<CustomVertex> vertexList,CustomVertex target){
        List<CustomVertex> path = new ArrayList<CustomVertex>(Arrays.asList(target));
        path = _getShortestPath(vertexList,target,path);
        return reversePath(path);

    }

    private static List<CustomVertex> _getShortestPath(List<CustomVertex> vertexList,CustomVertex target,List<CustomVertex> path){
        if(target.step == 0) return  path;
        else {
            for(CustomVertex vertex : vertexList){
                if(target.step-1 == vertex.step && vertex.adjacentStringVertexes.contains(target.label)){
                    path.add(vertex);
                    return _getShortestPath(vertexList, vertex, path);
                }
            }
            System.out.println("ERROR: there is no predecessor");
            System.out.println(vertexList);
            System.exit(1);
            return null;
        }
    }
    public static int getPathLength(List<String> paths){
        return paths.get(0).length()-1;
    }

    /**
     * Bestimmt einen Weg per Tiefensuchen zu einem gegeben Start und Ziel Vertex.
     * Die Tiefensuche ist nicht vollständig und liefert damit <b> nicht </b> kürzesten Weg.
     *
     * @param g - Der Graph aus dem JGraph Paket in dem gesucht werden soll
     * @param sourceVertex - von diesem Vertex aus wird die suche gestartet
     * @param targetVertex - wird dieser Vertex erreicht wird die suche beendet
     * @param path_output_goes_here - output parameter - in der mitgegebenen liste wird der Pfad als eine liste von Strings
     *                              eingefügt, wenn als parameter null übergeben wird, wird keine ausgabe pfad berechnet.
     * @return  true falls es einen weg gibt, false analog dazu
     */
    public static boolean depthFirstSearch(Graph g,String sourceVertex,String targetVertex,List<String> path_output_goes_here) {
        if(sourceVertex == targetVertex) return true;
        if(path_output_goes_here!=null &&!path_output_goes_here.isEmpty()) throw new IllegalArgumentException("path_output needs to be empty!");

        HashMap<String, CustomVertex> checkedMap = new HashMap<String, CustomVertex>();
        checkedMap.put(sourceVertex,new CustomVertex(sourceVertex,0,getAdjacentVertexes(g,sourceVertex)));
        boolean  result = _depthFirstSearch(g,sourceVertex,targetVertex,checkedMap,0);

        if(path_output_goes_here != null && result){
            List<CustomVertex> list = new ArrayList<CustomVertex>();
            list.addAll(checkedMap.values());
            path_output_goes_here.addAll(findAllPaths(list,checkedMap.get(targetVertex)).get(0));
        }
        return result;
    }
    private static boolean _depthFirstSearch(Graph g,String currentVertex,String targetVertex,Map<String,CustomVertex> checkedMap,int steps){
        if(currentVertex == targetVertex) return true;
        for (String benachbarterVertex : checkedMap.get(currentVertex).adjacentStringVertexes) {

                if (!checkedMap.containsKey(benachbarterVertex)) {
                    checkedMap.put(benachbarterVertex, new CustomVertex(benachbarterVertex, steps + 1, getAdjacentVertexes(g, benachbarterVertex)));
                    _depthFirstSearch(g, benachbarterVertex, targetVertex, checkedMap, steps + 1);
                }

                if(checkedMap.containsKey(targetVertex))return true;
        }
        return false;
    }

}
