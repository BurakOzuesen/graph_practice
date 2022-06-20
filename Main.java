import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


public class Main {

    public static void main(String[] args) throws IOException {

        // reading input file and creating my arrays in here
        System.setOut(new PrintStream(new FileOutputStream("output.txt")));
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        int number_of_lines = 0;
        while (reader.readLine() != null) number_of_lines++;
        reader.close();
        File file = new File(args[0]);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line;
        int line_counter = 0;
        Graph graph = new Graph();
        ArrayList<Graph.vertex> broadcastQueue = new ArrayList<Graph.vertex>();
        ArrayList<Graph.vertex> reverseQueue = new ArrayList<Graph.vertex>();
        ArrayList<Graph.vertex> bestVertices = new ArrayList<Graph.vertex>();
        ArrayList<Graph.vertex> possibleRoots = new ArrayList<Graph.vertex>();
        ArrayList<Graph.vertex> leaves = new ArrayList<Graph.vertex>();
        Graph MST = new Graph();


        while ((line=br.readLine()) != null){
            sb.append(line);
            sb.append("\n");


            // if input's first line

            if (line_counter == 0){
                String[] myNewString = line.split(" ");
                for (int i = 0; i < myNewString.length; i = i + 2){
                    // Set node's key and capacity in there
                    Graph.vertex vertex = new Graph.vertex();
                    vertex.key = myNewString[i];
                    vertex.capacity = Integer.parseInt(myNewString[i+1]);
                    vertex.message_node.add(vertex);
                    graph.addVertex(graph,vertex.capacity,vertex.key, vertex.message_node);
                    MST.addVertex(MST,vertex.capacity,vertex.key, vertex.message_node);
                }

            }



            else if (line_counter == 1){
                // choose the source vertex in there
                for (int i = 0; i < graph.getNodeArray().size(); i++){
                    if (graph.getNodeArray().get(i).key.equals(line)){
                        graph.getNodeArray().get(i).parent = graph.getNodeArray().get(i);
                        graph.getNodeArray().get(i).isElected = true;
                        broadcastQueue.add(graph.getNodeArray().get(i));
                    }
                }
            }


            else {
                String[] myNewString = line.split(" ");
                Graph.vertex v1 = new Graph.vertex();
                Graph.vertex v2 = new Graph.vertex();
                v1.key = myNewString[0];
                v2.key = myNewString[1];
                for (int iter_of_V1 = 0; iter_of_V1 < graph.getNodeArray().size(); iter_of_V1++){
                    if (v1.key.equals(graph.getNodeArray().get(iter_of_V1).key)){
                        for (int iter_of_V2 = 0; iter_of_V2 < graph.getNodeArray().size(); iter_of_V2++){
                            if (v2.key.equals(graph.getNodeArray().get(iter_of_V2).key)){
                                graph.addEdge(graph,graph.getNodeArray().get(iter_of_V1), graph.getNodeArray().get(iter_of_V2));
                            }
                        }
                    }
                }
            }
            line_counter++;
        }



        graph.printGraph(graph);
        Graph.vertex source = new Graph.vertex();
        for (int i = 0; i < graph.getNodeArray().size(); i++){
            if (graph.getNodeArray().get(i).parent == broadcastQueue.get(0)){
                source = graph.getNodeArray().get(i);
            }
        }

        graph.broadcast(graph, broadcastQueue, source, reverseQueue);
        Collections.reverse(reverseQueue);
        graph.messageSend(graph, reverseQueue);
        graph.bestNodePrint(source, bestVertices);
        graph.setVertex_number(graph.getNodeArray().size());
        MST.distance(MST,reverseQueue,leaves,possibleRoots);

    }

}
