package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.dw_graph_algorithms;
import api.game_service;
import api.node_data;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;
    private static int sen, id;
    private static dw_graph_algorithms algo;
    public static game_service game;
    private static boolean flag;
    private static HashMap<Integer,CL_Pokemon> menu;
    private static List<CL_Pokemon> poks;
    public static void main(String[] args) {
        if (args.length == 2) {
            sen = Integer.parseInt(args[0]);
            id = Integer.parseInt(args[1]);
        } else {
            sen = 1;
            id = 111111;
        }
        game = Game_Server_Ex2.getServer(sen);
        //load agents and pokemon
        //Game runner start
        init(game);
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        int ind=0;
        long dt=100;
        game.startGame();
        while(game.isRunning()) {
            try {
                moveAgents();
                if(ind%1==0) {_win.repaint();}
                _ar.setTime("Time Left: "+(double)game.timeToEnd()/1000);
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void init(game_service game) {
        flag=true;
        String pks = game.getPokemons();
        String graph = game.getGraph();
        String file = saveAsString(graph);
        _ar = new Arena();
        algo = new DWGraph_Algo();
        algo.load(file);
        _ar.setGraph(algo.getGraph());
        _ar.setPokemons(Arena.json2Pokemons(pks));
        _win = new MyFrame("Ex2");
        menu=new HashMap<>();
        _win.setSize(1000, 700);
        try {
            String info = game.toString();
            JSONObject line = new JSONObject(info);
            JSONObject meow = line.getJSONObject("GameServer");
            int numOfAg = meow.getInt("agents");
            List<CL_Pokemon> pkms = _ar.getPokemons();
            for (CL_Pokemon pk : pkms) {
                Arena.updateEdge(pk, _ar.getGraph());
            }
            poks=new LinkedList<>(_ar.getPokemons());
            for (int a = 0; a < numOfAg; a++) {
                int ind = a % pkms.size();
                menu.put(a,null);
                CL_Pokemon c = pkms.get(ind);
                int nn = c.get_edge().getDest();
                if (c.getType() < 0) {
                    nn = c.get_edge().getSrc();
                }
                //game.addAgent(nn);
                game.addAgent(nn);
            }
            String lg = game.getAgents();
            List<CL_Agent> balls = Arena.getAgents(lg, _ar.getGraph());
            _ar.setAgents(balls);
            _win.update(_ar);
            _win.setVisible(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String saveAsString(String jsonG) {
        try {
            FileWriter r = new FileWriter("graph.json");
            r.write(jsonG);
            r.flush();
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "graph.json";
    }

    public static void chooseTarget(CL_Agent agent) {
        //String fs =  game.getPokemons();
        List<CL_Pokemon> pkms = _ar.getPokemons();
        double min = Integer.MAX_VALUE;
        double temp = 0;
        CL_Pokemon nextPkms = null;
        for (CL_Pokemon pkm : pkms) {
            temp = algo.shortestPathDist(agent.getSrcNode(), pkm.get_edge().getSrc());
            if (temp < min) {
                if (temp < pkm.getMin_dist()) {
                   // agent.set_curr_fruit(pkm);
                    menu.put(agent.getID(), pkm);
                }
                min = temp;
                nextPkms = pkm;
            }
        }
        if (nextPkms == agent.get_curr_fruit()&& nextPkms.getNxtEater()!=null) {
            //nextPkms.getNxtEater().set_curr_fruit(null);
            //flags.add(nextPkms.getNxtEater());
            menu.put(nextPkms.getNxtEater().getID(),null);
        }
        //agent.set_curr_fruit(nextPkms);
        nextPkms.setNxtEater(agent);
        nextPkms.setMin_dist(min);
        menu.put(agent.getID(),nextPkms);
       // flags.remove(agent);
    }

    private static int nextNode(CL_Agent agent) {
        int ans;
        if(agent.getPath()==null||agent.getPath().size()==0) {
            node_data n = _ar.getGraph().getNode(menu.get(agent.getID()).get_edge().getDest());
            agent.setPath(algo.shortestPath(agent.getSrcNode(), menu.get(agent.getID()).get_edge().getSrc()), n);
        }
        if(agent.getPath().isEmpty()){
            return -1;
        }
        if(agent.getPath().size()==1){
            menu.put(agent.getID(),null);
            ans=agent.getPath().get(0).getKey();
        return ans;
        }
        ans=agent.getPath().get(1).getKey();
        agent.setNextNode(agent.getPath().get(1).getKey());
        return ans;
    }

    public static void moveAgents(){//TODO: print status
        //List<node_data> nds;
        String lg = game.move();
        List<CL_Agent> balls = Arena.getAgents(lg, _ar.getGraph());
        String fs = game.getPokemons();
        List<CL_Pokemon> curr_pkms = Arena.json2Pokemons(fs);
//        List<CL_Pokemon> arPkms=_ar.getPokemons();
//        _ar.setPokemons(curr_pkms);
        _ar.setAgents(balls);
        for (int a = 0; a < curr_pkms.size(); a++) {
            Arena.updateEdge(curr_pkms.get(a), _ar.getGraph());
        }
        int k=0;
        if (!poks.equals(curr_pkms)||flag) {
            poks=new LinkedList<>(curr_pkms);
            flag=false;
            _ar.setPokemons(curr_pkms);
            while(menu.containsValue(null)){//TODO: add if numOfAgents>numOfFruits
                for(Integer agntID: menu.keySet()){
                    chooseTarget(balls.get(agntID));
                }
            }
        }
            for (CL_Agent agn : balls) {
                    int dest = nextNode(agn);
//                node_data n = _ar.getGraph().getNode(menu.get(agn.getID()).get_edge().getDest());
//                agn.setPath(algo.shortestPath(agn.getSrcNode(), menu.get(agn.getID()).get_edge().getSrc()), n);
//                for (int i=0; i<agn.getPath().size(); i++){
//                    game.chooseNextEdge(agn.getID(),agn.getPath().get(i).getKey());
//                }
                     game.chooseNextEdge(agn.getID(), dest);
           }
    }
    //TODO: move the fuck out lines 184-187
}
