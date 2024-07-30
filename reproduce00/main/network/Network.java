package networks;

import consts.Const;
import consts.Const.AgentType;
import networks.nodes.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.SplittableRandom;

public class Network implements Cloneable{
    private int worldNum = -1;
    protected int NUM_OF_AGENTS = Const.NETWORK_NUM_OF_AGENTS;
    protected int NUM_OF_POSTER_PSY_AGENT = Const.NETWORK_NUM_OF_POSTER_PSY_AGENT;
    protected int NUM_OF_POSTER_MON_AGENT = Const.NETWORK_NUM_OF_POSTER_MON_AGENT;
    protected final int NUM_OF_BASIC_AGENTS = Const.NETWORK_NUM_OF_BASIC_AGENTS;
    protected int numOfEdges = 0;

    public ArrayList<Agent> agentList;

    public ArrayList<Agent> posterPsyList;
    public ArrayList<Agent> posterMonList;
    public ArrayList<Agent> browserList;

    SplittableRandom random;

    //constructor
    public Network(SplittableRandom r){
        setWorldNum(0);
        this.agentList = new ArrayList<Agent>();
        random = r;
    }

    //////////////////////////////////////////////////////////////////

    //get method

    public int getWorldNum(){
        return worldNum;
    }

    public int getNumOfAgents(){
        return agentList.size();
    }

    public int getNumOfEdges(){
        return numOfEdges;
    }

    public double getAveDegree(){
        double ret = 0.0;
        for(Agent agent : agentList){
            ret += agent.getDegree();
        }
        return ret / NUM_OF_AGENTS;
    }

    public double getAveClustCoefficient(){
        double ret = 0.0;
        for(Agent igent : agentList){

            double degi = igent.getDegree();
            double triangle = 0;

            for(Edge edge1 : igent.Adjacent){
                for(Edge edge2 : igent.Adjacent){
                    if(edge1==edge2) continue;

                    Agent jgent = edge1.getTo();
                    Agent kgent = edge2.getTo();

                    for(Edge edge3 : jgent.Adjacent)
                        if(edge3.getTo() == kgent) triangle++;
                    
                }
            }

            ret += ( degi!=1 ? triangle / (degi*(degi-1)) : 0.0 );

        }

        return ret / NUM_OF_AGENTS;
    }

    //////////////////////////////////////////////////////////////////

    //set method
    
    public void setWorldNum(int worldNum){
        this.worldNum = worldNum;
    }

    //////////////////////////////////////////////////////////////////

    //reset method
    
    public void resetNetwork(){
        for(Agent agent:agentList){
            agent.resetAgent();
        }
    }

    //////////////////////////////////////////////////////////////////

    //abstract method
    public void makeNetwork(){}

    //////////////////////////////////////////////////////////////////
    //add methods

    //add agent to List
    public void addAgents(Agent agent){
        agentList.add(agent);
    }

    //add "to" to adjacent of "from"
    public void addEdge(Agent from,Agent to){
        Edge edge = new Edge(to);
        from.addAdjacent(edge);
        numOfEdges++;
    }

    public void addBothEdge(Agent agent1,Agent agent2){
        addEdge(agent1, agent2);
        addEdge(agent2, agent1);
    }

    //////////////////////////////////////////////////////////////////
    
    //check method

    protected Boolean checkNumOfAgent(){
        return NUM_OF_AGENTS == agentList.size();
    }

    //////////////////////////////////////////////////////////////////

    //assign agent type
    public void updatePropaty(){

        posterPsyList = new ArrayList<>();
        posterMonList = new ArrayList<>();
        browserList   = new ArrayList<>();
        
        ArrayList<Integer> numberList = new ArrayList<>();
        for(int numOfAgent = 0;numOfAgent < NUM_OF_AGENTS;numOfAgent++)
            numberList.add(numOfAgent);

        

       //shuffle

        Collections.shuffle(numberList,new Random(random.nextInt()));

        int numOfPosterAgent = NUM_OF_POSTER_MON_AGENT + NUM_OF_POSTER_PSY_AGENT;

        for(int numOfAgent = 0;numOfAgent < NUM_OF_POSTER_PSY_AGENT;numOfAgent++){
            Agent agent = agentList.get(numberList.get(numOfAgent));
            agent.setAgentType(AgentType.PosterPsy);
            posterPsyList.add(agent);
        }
        
        for(int numOfAgent = NUM_OF_POSTER_PSY_AGENT;numOfAgent < numOfPosterAgent;numOfAgent++){
            Agent agent = agentList.get(numberList.get(numOfAgent));
            agent.setAgentType(AgentType.PosterMon);
            posterMonList.add(agent);
        }

        for(int numOfAgent = numOfPosterAgent;numOfAgent < NUM_OF_AGENTS;numOfAgent++){
            Agent agent = agentList.get(numberList.get(numOfAgent));
            agent.setAgentType(AgentType.Browser);
            browserList.add(agent);
        }

        /* 
        for(int numOfAgent = 0;numOfAgent < NUM_OF_AGENTS;numOfAgent++){
            Agent agent = agentList.get(numberList.get(numOfAgent));
            switch(numOfAgent%4){
                case 0:
                    agent.setAgentType(AgentType.PosterPsy);
                    posterPsyList.add(agent);
                    break;
                case 1:
                    agent.setAgentType(AgentType.Browser);
                    browserList.add(agent);
                    break;
                case 2:
                    agent.setAgentType(AgentType.PosterMon);
                    posterMonList.add(agent);
                    break;
                case 3:
                    agent.setAgentType(AgentType.Browser);
                    browserList.add(agent);
                    break;
            }
        }

        */

        if(posterPsyList.size()+posterMonList.size()+browserList.size()!=NUM_OF_AGENTS){
            System.out.print(posterPsyList.size());
            System.out.print(posterMonList.size());
            System.out.print(browserList.size());
            System.out.println("Network: number of agents err");
            System.exit(-1);
        }

        for(Agent agent:agentList){
            agent.randomsetProbM();
        }
    
    }

    //Override

    @Override
    public String toString(){
        String str = "";
        for(Agent from:agentList){
            str += from.getNum()+"";
            for(Edge edge:from.Adjacent){
                Agent to = edge.getTo();
                str += " "+to.getNum()+"";
            }
            str += "\n";
        }
        return str;
    }

    //////////////////////////////////////////////////////////////////

    //clone method

    public Network cloneNetwork(SplittableRandom r){
        Network copy = null;
        try{

            copy = (Network)super.clone();
            copy.agentList = new ArrayList<Agent>();
            
            //copy AgentList
            for(Agent agent:agentList){
                copy.agentList.add(agent.cloneAgent());
            }

            if(r!=null){
                copy.random = r;
                for(Agent cpAgent:copy.agentList){
                    cpAgent.setRandom(r);
                }
            }

            if(posterMonList!=null){
                copy.posterPsyList = new ArrayList<Agent>();
                copy.posterMonList = new ArrayList<Agent>();
                copy.browserList   = new ArrayList<Agent>();
                //copy AgentList
                for(Agent agent:agentList){
                    switch(agent.getAgentType()){
                        case PosterPsy:
                            copy.posterPsyList.add(copy.agentList.get(agent.getNum()));
                            break;
                        case PosterMon:
                            copy.posterMonList.add(copy.agentList.get(agent.getNum()));
                            break;
                        case Browser:
                            copy.browserList.add(copy.agentList.get(agent.getNum()));
                            break;
                    }
                }

            }
            
            //copy and update Adjacent
            copy.numOfEdges = 0;
            for(Agent agent:agentList){
                for(Edge e:agent.Adjacent){
                    int newFromNum = agent.getNum();
                    int newToNum   = e.getTo().getNum();
                    Edge edge      = new Edge(copy.agentList.get(newToNum));
                    copy.agentList.get(newFromNum).addAdjacent(edge);
                    copy.numOfEdges++;
                }
            }
            
            if(numOfEdges != copy.numOfEdges){
                System.out.println("cloneNet: Adjacent err.");
                System.exit(-1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return copy;
    }

    //////////////////////////////////////////////////////////////////

    //for test

    public static void main(String args[]) throws IOException{

        FileWriter writer = new FileWriter("testNet.txt");

        SplittableRandom r = new SplittableRandom(1001);
        Network net = new BA(r);
        net.makeNetwork();

        Network clone=net.cloneNetwork(null);

        //net model
        writer.write(net.toString()+"\n");
        //net agents
        for(Agent agent:net.agentList){
            writer.write(agent.toString());
        }
        writer.write("\n");

        //clone model
        writer.write(clone.toString()+"\n");
        //clone agents
        for(Agent agent:clone.agentList){
            writer.write(agent.toString());
        }
        writer.write("\n");

        //adjacentまでAgentがコピーされているかの確認

        //net adjacent agents (compare to net agents)
        Agent b = net.agentList.get(0);
        for(Edge e:b.Adjacent){
            writer.write(e.getTo().toString());
        }
        writer.write("\n");

        
        //net adjacent agents (compare to net agents)
        Agent a = clone.agentList.get(0);
        for(Edge e:a.Adjacent){
            writer.write(e.getTo().toString());
        }

        writer.close();
    }
}
