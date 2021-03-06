package com.bridgestone.controller;

import com.bridgestone.entity.Edge;
import com.bridgestone.entity.GraphArea;
import com.bridgestone.entity.Node;
import com.bridgestone.properties.ApplicationProperties;
import com.bridgestone.redis.RedisRepository;
import com.bridgestone.utils.DocumentsCreator;
import com.bridgestone.utils.StreetInfo;

import java.util.*;

/**
 * Created by balmung on 28/08/17.
 */
public class ApplicationInitializer {

    private GraphArea graph;
    private HashMap<String, Edge> standByEdges;     //edges that are met but are not inserted into a street
    private HashMap<String, Edge> processed;

    private int length;

    private RedisRepository repository;
    private ApplicationInitializer() {
        /***ApplicationProperties.loadProperties() done while getting redis instance;*/
        this.repository = RedisRepository.getInstance();
        this.graph = new GraphArea();
        this.standByEdges = new HashMap<>();
        this.processed = new HashMap<>();
    }



    public void createGraph(){

        Collection<Node> nodes =  repository.getAll();
        for(Node node: nodes){
            System.err.println("nodo: " + node.getGraphKey() + "archi: " + node.getNumberOfEdges());

            this.graph.addNode(node);
        }
        repository.disconnectFromDB();

    }

    public void createStreets(){

        TopologyGraphController topologyGraphController = TopologyGraphController.getInstance();

        Map<String, Node> nodesMap = graph.getGraph();
        Collection<Node> nodes = nodesMap.values();
        Integer key = new Integer("0");
        for(Node node: nodes){

            Collection<Edge> edges = node.getEdges().values();
            if (topologyGraphController.checkIfCrossroad(node)){
                for(Edge edge: edges){
                    this.length = 0;
                    this.repository.connectDB();
                    streetCalc(edge, key);
                    this.repository.insertStreetSpeed(key.toString(), new StreetInfo(0, length));
                    this.repository.disconnectFromDB();
                    key++;

                }

            }
            else{

                for(Edge edge: edges){
                    this.standByEdges.put(Edge.makeGraphEdgeKey(edge), edge);
                }
            }
        }

        Collection<Edge> edges = this.standByEdges.values();

        for(Edge edge : edges){
            if(!this.processed.containsKey(Edge.makeGraphEdgeKey(edge))) {
                Node node = this.graph.getNodeByKey(edge.getStartNode());
                if (node.getNumberOfEdges() <= 2) {
                    //node has only one outcoming edge so it could be a start of a street
                    this.length = 0;
                    this.repository.connectDB();
                    streetCalc(edge, key);
                    this.repository.insertStreetSpeed(key.toString(), new StreetInfo(0, length));
                    this.repository.disconnectFromDB();
                    key++;
                }
            }
        }

    }

    //if a node has more than 2 starting vectors it is a crossroad
    public boolean checkIfCrossroad(Node node){
        if(node.getNumberOfEdges() > 2)
            return true;
        return false;
    }

    private Edge getForwardEdge(Node start, Node arrival){
        // taking all the vectors starting from the arrival section
        Collection<Edge> edges = arrival.getEdges().values();
        Edge nextEdge = null;
        for ( Edge edge : edges) {
            if ( !edge.getEndingNode().equals(start.getGraphKey())) {//NB: ending node is now the key of the Node
            /*if the ending side of the vector is the one where I start than it is not the next vector
              but a back-propaagtion vector
             */
                nextEdge = edge;
                break;
            }
        }
        return nextEdge;
    }


    /** N.B: the results will be updated into the instance result of CalculateMeanResult */
    private void streetCalc(Edge startingEdge, Integer key){
        // updating the values of result for the current vector analyzed

        if(this.processed.containsKey(Edge.makeGraphEdgeKey(startingEdge))){
            return;
        }

        Node arrivalNode = this.graph.getNodeByKey(startingEdge.getEndingNode());

        this.repository.insertEdge(Edge.makeGraphEdgeKey(startingEdge), key.toString());
        this.processed.putIfAbsent(Edge.makeGraphEdgeKey(startingEdge), startingEdge);
        this.length++;

        if(checkIfCrossroad(arrivalNode)){
            return;
        }


        //propagation of the operation until a crossroad
        //updating starting vector to the new vector to process

        /*
         *
         * (arrivalNode )------>(A)
         *       |               |
         *       |               |
         *       |               |
         *       v               v
         *      (B)--------->(common)
         *
         */

        ArrayList<Edge> forwardEdges = this.getForwardEdges(startingEdge, arrivalNode);

        for(Edge edge : forwardEdges){
            this.streetCalc(edge, key);
        }

    }


    private ArrayList<Edge> getForwardEdges(Edge startingEdge, Node arrivalNode){
        Collection<Edge> edges = arrivalNode.getEdges().values();
        //edges.sze() non può essere 3 perché altrimenti sarebbe un incrocio ed è stato escluso prima
        ArrayList<Edge> forwardEdges = new ArrayList<>();
        if (edges.size() == 2){
            Edge edges1[] = new Edge[2];
            edges1[0] = null;
            edges1[1] = null;
            int i = 0;
            //inserisco i due archi uscenti in edges1[]
            for ( Edge edge : edges) {
                /*inserting in edges1 only if they're forward vectors: the ending node is differenent
                from the start one */
                if ( !edge.getEndingNode().equals(startingEdge.getStartNode())) {
                    edges1[i] = edge;
                    i++;

                }
            }


            if (edges1[1] != null) { // i have two exiting arcs
                //posso andare a verificare se ho la situazione buona o meno
                HashMap<String, Edge> edgeA = this.graph.getNodeByKey(edges1[0].getEndingNode()).getEdges();
                HashMap<String, Edge> edgeB = this.graph.getNodeByKey(edges1[1].getEndingNode()).getEdges();

                String commonNode = "";
                for(Edge edge : edgeA.values()) {
                    commonNode = edge.getEndingNode();

                    for (Edge edge1 : edgeB.values()) {
                        if (commonNode.equals(edge1.getEndingNode())) {
                            //situazione bella in cui ho il quadrato
                            forwardEdges.add(edges1[1]);
                            forwardEdges.add(edges1[0]);
                            break;
                        }
                    }

                }

            } else {
                forwardEdges.add(edges1[0]);
            }

        } else {
            for ( Edge edge : edges) {
                if (!edge.getEndingNode().equals(startingEdge.getStartNode())) {
                    forwardEdges.add(edge);
                }
            }
        }

        return forwardEdges;
    }

    public void createIndexes(){
        DocumentsCreator.createIndexes();
    }


    public static void main(String args[]){
        ApplicationInitializer app = new ApplicationInitializer();
        app.createGraph();
        app.createStreets();
        app.createIndexes();
        System.err.println("FINE");
    }

}
