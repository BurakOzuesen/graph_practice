import java.util.ArrayList;

public class Graph {


    // Classic graph attributes
    private int vertex_number;
    private int edges_number;
    private ArrayList<vertex> vertexArray = new ArrayList<vertex>();
    private ArrayList<edge> edgeArray = new ArrayList<edge>();


    // vertex class
    static class vertex{
        int capacity;
        String key;
        ArrayList<vertex> adjacent = new ArrayList<vertex>();
        boolean isElected;
        vertex parent;
        ArrayList<vertex> message_node = new ArrayList<vertex>();
        int degree;
        int child_counter;
        boolean visited;
        int distance;
    }

    // edge class
    static class edge{
        vertex[] points;
        boolean isInMST;
    }

    // getters and setters

    public int getVertex_number() {
        return vertex_number;
    }

    public void setVertex_number(int vertex_number) {
        this.vertex_number = vertex_number;
    }

    public int getEdges_number() {
        return edges_number;
    }

    public void setEdges_number(int edges_number) {
        this.edges_number = edges_number;
    }

    public ArrayList<vertex> getNodeArray() {
        return vertexArray;
    }

    public void setNodeArray(ArrayList<vertex> nodeArray) {
        this.vertexArray = nodeArray;
    }

    public ArrayList<edge> getEdgeArray() {
        return edgeArray;
    }

    public void setEdgeArray(ArrayList<edge> edgeArray) {
        this.edgeArray = edgeArray;
    }


    // takes edges and adds to graph
    public Graph addEdge(Graph G, vertex V1, vertex V2){
        for (int i = 0; i < G.getNodeArray().size(); i++){
            if (G.getNodeArray().get(i) == V1){
                for (int j = 0; j < G.getNodeArray().size(); j++){
                    if (G.getNodeArray().get(j) == V2){
                        G.getNodeArray().get(i).adjacent.add(V2);
                        G.getNodeArray().get(j).adjacent.add(V1);
                        G.getNodeArray().get(i).degree++;
                        G.getNodeArray().get(j).degree++;
                    }
                }
            }
        }
        return G;
    }

    // adds vertices to graph
    public Graph addVertex(Graph G, int capacity, String key, ArrayList<vertex> messageNode){
        vertex v1 = new vertex();
        v1.key = key;
        v1.capacity = capacity;
        v1.message_node = messageNode;
        G.getNodeArray().add(v1);
        return G;
    }

    public void printGraph(Graph G){
        System.out.println("Graph structure:");
        for (int i = 0; i < G.getNodeArray().size(); i++){
            System.out.print(G.getNodeArray().get(i).key+"("+G.getNodeArray().get(i).capacity+")-->");
            int last_index = 0;
            for (int j = 0; j < G.getNodeArray().get(i).adjacent.size(); j++){
                if (last_index == G.getNodeArray().get(i).adjacent.size() - 1){
                    System.out.print(G.getNodeArray().get(i).adjacent.get(j).key);
                }
                else {
                    System.out.print(G.getNodeArray().get(i).adjacent.get(j).key+" ");
                }
                last_index++;
            }
            System.out.println();
        }
    }

    // helper function for broadcast printing
    public String broadcastPrint(String str){
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ' ') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void broadcast(Graph G, ArrayList<vertex> queue, vertex source, ArrayList<vertex> reverse){
        System.out.println("Broadcast steps:");
        while (queue.size() != 0)
        {
            String message = "";
            String source_message = "";
            reverse.add(queue.get(0));
            source = queue.remove(0);

            if (source.adjacent.size() == 0){
                continue;
            }
            source_message = source.key+"-->";
            for (int i = 0; i < source.adjacent.size(); i++){
                vertex next = source.adjacent.get(i);
                if (!next.isElected){
                    next.isElected = true;
                    queue.add(next);
                    next.parent = source;
                    message += source.adjacent.get(i).key+" ";
                }
            }
            message = broadcastPrint(message);
            if (message.equals("")){
                continue;
            }
            System.out.print(source_message+message);
            System.out.println();
        }
    }

    // message sending function
    public void messageSend(Graph G, ArrayList<vertex> reverseQueue){
        System.out.println("Message passing:");
        for (int k = 0; k < reverseQueue.size(); k++){
            if (reverseQueue.get(k).parent == reverseQueue.get(k)){
                continue;
            }
            if (reverseQueue.get(k).message_node.get(0).capacity == reverseQueue.get(k).parent.message_node.get(0).capacity){
                for (int l = 0; l < reverseQueue.get(l).parent.message_node.size(); l++){
                    reverseQueue.get(k).parent.message_node.add(reverseQueue.get(k).message_node.get(l));
                }
            }
            else if (reverseQueue.get(k).message_node.get(0).capacity > reverseQueue.get(k).parent.message_node.get(0).capacity)
            {
                reverseQueue.get(k).parent.message_node.clear();
                for (int l = 0; l < reverseQueue.get(k).message_node.size(); l ++){
                    reverseQueue.get(k).parent.message_node.add(reverseQueue.get(k).message_node.get(l));
                }
            }
            System.out.print(reverseQueue.get(k).key+"--->");
            for (int l = 0; l < reverseQueue.get(k).message_node.size(); l++){
                System.out.print("["+reverseQueue.get(k).message_node.get(l).key+","+reverseQueue.get(k).message_node.get(l).capacity+"]");
            }
            System.out.println("--->"+reverseQueue.get(k).parent.key);
            reverseQueue.get(k).parent.child_counter++;
        }
    }

    // prints best nodes in graph
    public void bestNodePrint(vertex source, ArrayList<vertex> bestVertices){
        String message = "";
        for (int i = 0; i < source.message_node.size(); i++){
            bestVertices.add(source.message_node.get(i));
        }
        System.out.print("Best node-->");
        for (int i = 0; i < bestVertices.size(); i++){
            message += bestVertices.get(i).key+", ";
        }
        message = bestNodeUtil(message);
        System.out.println(message);
    }

    // helper print function
    public String bestNodeUtil(String str){
        str = str.substring(0, str.length() - 2);
        return str;
    }

    // calculates best roots
    public void distance(Graph MST, ArrayList<vertex> reverseQueue, ArrayList<vertex> leaves, ArrayList<vertex> possibleRoots){
        MST.setNodeArray(reverseQueue);
        for (int i = 0; i < MST.getNodeArray().size(); i++){
            MST.getNodeArray().get(i).adjacent.clear();
        }
        for (int i = 0; i < MST.getNodeArray().size(); i++){

            if (MST.getNodeArray().get(i).parent == MST.getNodeArray().get(i)){
                continue;
            }
            MST.getNodeArray().get(i).adjacent.add(MST.getNodeArray().get(i).parent);
            MST.getNodeArray().get(i).parent.adjacent.add(MST.getNodeArray().get(i));
        }
        for (int i = 0; i < MST.getNodeArray().size(); i++){
            if (MST.getNodeArray().get(i).adjacent.size() == 1){
                leaves.add(MST.getNodeArray().get(i));
            }
        }
        possibleRoots = MST.getNodeArray();
        ArrayList<Graph.vertex> correctRoots = new ArrayList<Graph.vertex>();

        for (int i = 0; i < possibleRoots.size(); i++){
            Graph.vertex temp = new Graph.vertex();
            temp.distance = possibleRoots.get(i).distance;
            temp.key = possibleRoots.get(i).key;
            temp.visited = possibleRoots.get(i).visited;
            temp.adjacent = possibleRoots.get(i).adjacent;
            temp.child_counter = possibleRoots.get(i).child_counter;
            temp.parent = possibleRoots.get(i).parent;
            temp.degree = possibleRoots.get(i).degree;
            temp.message_node = possibleRoots.get(i).message_node;
            temp.capacity = possibleRoots.get(i).capacity;
            temp.isElected = possibleRoots.get(i).isElected;
            correctRoots.add(temp);
        }

        ArrayList<Graph.vertex> visited = new ArrayList<>();
        visited = MST.getNodeArray();
        ArrayList<Graph.vertex> myQueue = new ArrayList<>();

        for (int k = 0; k < leaves.size(); k++){
            myQueue.add(leaves.get(k));
            myQueue.get(0).visited = true;
            while (!myQueue.isEmpty()){
                Graph.vertex current = new Graph.vertex();
                current = myQueue.get(0);
                for (int i = 0; i < current.adjacent.size(); i++){
                    if (current.adjacent.get(i).visited){
                        continue;
                    }
                    else{
                        current.adjacent.get(i).distance = current.distance + 1;
                        current.adjacent.get(i).visited = true;
                        myQueue.add(current.adjacent.get(i));
                    }
                }
                for (int j = 0; j < MST.getNodeArray().size(); j ++) {
                    if (!MST.getNodeArray().get(j).visited) {
                        MST.getNodeArray().get(j).distance++;
                    }
                }
                myQueue.remove(0);
            }
            if (k == 0){
                for (int l = 0; l < possibleRoots.size(); l++){
                    correctRoots.get(l).distance = possibleRoots.get(l).distance;
                }
            }

            for (int i = 0; i < possibleRoots.size(); i++){
                for (int j = 0; j < correctRoots.size(); j++){
                    if (correctRoots.get(j).key.equals(possibleRoots.get(i).key)){
                        if (Math.abs(correctRoots.get(j).distance - possibleRoots.get(i).distance) >= 2){
                            correctRoots.remove(j);
                        }
                    }
                }
            }
            for (int i = 0; i < possibleRoots.size(); i++){
                possibleRoots.get(i).visited = false;
                possibleRoots.get(i).distance = 0;
            }
        }
        printDistance(correctRoots);

    }
    public void printDistance(ArrayList<vertex> correctRoots){
        String message = "";

        System.out.print("Possible roots-->");
        for (int i = 0; i < correctRoots.size(); i++){
            message += correctRoots.get(i).key+", ";
        }
        message = bestPrintUtil(message);
        System.out.println(message);
    }
    public String bestPrintUtil(String str){
        try{
            str = str.substring(0, str.length() - 2);
        }
        catch (Exception e){
            System.out.println("none");
        }
        return str;
    }
}