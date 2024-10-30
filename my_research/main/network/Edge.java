package networks;

import networks.nodes.*;


public class Edge {
    
    private Agent to;

    //constlucor
    public Edge(Agent agent){
        this.to = agent;
    }

    //////////////////////////////////////////////////////////////////
   
    //get method

    public Agent getTo(){
        return to;  
    }

}
