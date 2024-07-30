package networks.nodes;

import models.objects.*;
import networks.Edge;

import java.util.ArrayList;
import java.util.SplittableRandom;

import consts.Const.AgentType;

public class Agent extends Node {
    
    private Gene B;
    private Gene L;
    private Gene Q;

    private double S;

    public double cost;
    public double reward;
    public double rewardMon;
    
    private AgentType aType = AgentType.Browser;

    private double prob_M;

    private Boolean poster = false;

    private int numOfAroundArticle = 0;

    //number of SNS objects by each agent
    private int numOfArticle             = 0;
    private int numOfViwed               = 0;
    private int numOfComment             = 0;
    private int numOfMetaComment         = 0;
    private int numOfCommentReceived     = 0;
    private int numOfMetaCommentReceived = 0;

    SplittableRandom random;

    //constructor
    public Agent(int num,SplittableRandom r){
        super(num);
        random = r;
        randomsetGenes();
        randomsetGeneQ();
    }

    //////////////////////////////////////////////////////////////////

    //get method

    public Gene getGeneB(){
        return B;
    }

    public Gene getGeneL(){
        return L;
    }

    public Gene getGeneQ(){
        return Q;
    }

    public double getS(){
        return S;
    }

    public double getCost(){
        return cost;
    }

    public double getReward(){
        return reward;
    }

    public double getRewardMon(){
        return rewardMon;
    }


    public double getFitness(){
        return reward+cost;
    }

    public double getUtility(){
        return (1-prob_M)*reward+prob_M*rewardMon+cost;
    }

    public AgentType getAgentType(){
        return aType;
    }

    public double getProbM(){
        return prob_M;
    }

    public Boolean isPoster(){
        return poster;
    }

    public int getNumOfAroundArticle(){
        return numOfAroundArticle;
    }

    public int getNumOfArticle(){
        return numOfArticle;
    }

    public int getNumOfViwed(){
        return numOfViwed;
    }

    public int getnumOfComment(){
        return numOfComment;
    }

    public int getnumOfMetaComment(){
        return numOfMetaComment;
    }
    
    public int getnumOfCommentReceived(){
        return numOfCommentReceived;
    }
    
    public int getnumOfMetaCommentReceived(){
        return numOfMetaCommentReceived;
    }

    //////////////////////////////////////////////////////////////////

    //get value method
    
    public double getB(){
        return B.getValue();
    }

    public double getL(){
        return L.getValue();
    }

    public double getQ(){
        return Q.getValueQ();
    }

    public double getQmin(){
        return Q.getValueMinQ();
    }

    //////////////////////////////////////////////////////////////////

    //set methods
    public void setRandom(SplittableRandom r){
        random = r;
        randomsetGenes();
        randomsetGeneQ();
        randomsetProbM();
    }

    //set B and L randomly
    public void randomsetGenes(){
        B = new Gene(random);
        L = new Gene(random);
    }

    public void randomsetGeneQ(){
        Q = new Gene(random);
    }

    public void setGenes(Gene geneB,Gene geneL){
        B = geneB;
        L = geneL;
    }

    public void setGeneQ(Gene geneQ){
        Q = geneQ;
    }

    public void setAgentType(AgentType at){
        aType = at;
    }

    //set S ranodmly
    public void randomsetS(){
        S = random.nextDouble();
    }
    
    public void randomsetProbM(){
        switch(aType){
            case PosterMon:
                prob_M = random.nextDouble(0.5,1.0);
                break;
            case PosterPsy:
                prob_M = random.nextDouble(0.0,0.5);
                break;
            case Browser:
                prob_M = 0.0;
                break;
        }
    }

    public void setPoster(){
        poster = true;
    }

    //////////////////////////////////////////////////////////////////

    //add method
    public void addCost(double c){
        cost += c;
    }

    public void addReward(double r){
        reward += r;
    }
    
    public void addRewardMon(double rm){
        rewardMon += rm;
    }
    
    public void viwedArticle(){
        numOfViwed++;
    }

    public void receiveComment(){
        numOfCommentReceived++;
    }

    public void receiveMetaComment(){
        numOfMetaCommentReceived++;
    }

    public void receiveCommentRatio(){
        numOfCommentReceived++;
    }

    public void receiveMetaCommentRatio(){
        numOfMetaCommentReceived++;
    }


    //////////////////////////////////////////////////////////////////
    
    //SNS Objicts methods

    public Article makeArticle(){
        numOfArticle++;
        return new Article(this);
    }

    public Comment makeComment(Article pa){
        numOfComment++;
        return new Comment(this,pa);
    }

    public MetaComment makeMetaComment(Comment pc){
        numOfMetaComment++;
        return new MetaComment(this,pc);
    }

    public ArticleRatio makeArticleRatio(){
        numOfArticle++;
        return new ArticleRatio(this);
    }

    public CommentRatio makeCommentRatio(ArticleRatio par){
        numOfComment++;
        return new CommentRatio(this,par);
    }

    public MetaCommentRatio makeMetaCommentRatio(CommentRatio pcr){
        numOfMetaComment++;
        return new MetaCommentRatio(this,pcr);
    }

    //////////////////////////////////////////////////////////////////

    public void countNumOfAroundArticle(){
        resetNumOfAroundArticle();
        for(Edge edge:Adjacent){
            Agent agent = edge.getTo();
            if(agent.isPoster())
                numOfAroundArticle++;
        }
    }

    public void resetNumOfAroundArticle(){
        numOfAroundArticle = 0;
    }


    //reset method

    public void resetAgent(){
        S                        = 0;
        cost                     = 0;
        reward                   = 0;
        rewardMon                = 0;
        numOfArticle             = 0;
        numOfViwed               = 0;
        numOfComment             = 0;
        numOfMetaComment         = 0;
        numOfCommentReceived     = 0;
        numOfMetaCommentReceived = 0;
    }

    public void resetPoster(){
        poster = false;
    }

    //////////////////////////////////////////////////////////////////
    
    //Override

    //to String
    @Override
    public String toString(){
        String str = "";
        str += getNum()+","+getDegree()+","+getAgentType().ordinal()+","+getB()+","+getL()+","+getQ()+","+getCost()+","+getReward()+","+getRewardMon()+","+getFitness()+","+getUtility()+","+getNumOfArticle()+","+getNumOfViwed()+","+getnumOfComment()+","+getnumOfMetaComment()+","+getnumOfCommentReceived()+","+getnumOfMetaCommentReceived()+"\n";
        return str;
    }

    //////////////////////////////////////////////////////////////////

    //clone method
    /**
     * clone instace of Agent class
     * but instace of Genes and list of adjacent are different
     */
    public Agent cloneAgent(){
        Agent copy = null;
        try{
            copy = (Agent)super.clone();
            copy.randomsetGenes();
            copy.randomsetGeneQ();
            copy.Adjacent=new ArrayList<Edge>();
            copy.resetAgent();
        }catch(Exception e){
            e.printStackTrace();
        }
        return copy;
    }

    //////////////////////////////////////////////////////////////////

    //for test

    public static void main(String args[]) {
        SplittableRandom r = new SplittableRandom(101);
        Agent agent = new Agent(1, r);
        Agent bgent = agent.cloneAgent();

        agent.addAdjacent(new Edge(bgent));
            
        //false コピーしたらGeneは別物
        System.out.println(agent.getGeneB() == bgent.getGeneB());
        //true bのL変更はaの近所から見れる
        bgent.L.changeGene(1);
        System.out.println(agent.Adjacent.get(0).getTo().getL() == bgent.getL());

        System.out.println(agent.getL());
        Gene gene=agent.getGeneL();
        for(int i=0;i<3;i++)
        System.out.println(gene.gene[i]);

    }

}
