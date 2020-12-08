package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;
    private static int sen, id;
    private static dw_graph_algorithms algo;
    public static game_service game;
    private static List<CL_Agent> flags;
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
        String pks = game.getPokemons();
        String graph = game.getGraph();
        String file = saveAsString(graph);
        _ar = new Arena();
        algo = new DWGraph_Algo();
        algo.load(file);
        _ar.setGraph(algo.getGraph());
        _ar.setPokemons(Arena.json2Pokemons(pks));
        _win = new MyFrame("Ex2");
        _win.setSize(1000, 700);
        try {
            String info = game.toString();
            JSONObject line = new JSONObject(info);
            JSONObject meow = line.getJSONObject("GameServer");
            int numOfAg = meow.getInt("agents");
            flags=new LinkedList<>();
            List<CL_Pokemon> pkms = _ar.getPokemons();
            for (CL_Pokemon pk : pkms) {
                Arena.updateEdge(pk, _ar.getGraph());
            }

            for (int a = 0; a < numOfAg; a++) {
                int ind = a % pkms.size();
                CL_Pokemon c = pkms.get(ind);
                int nn = c.get_edge().getDest();
                if (c.getType() < 0) {
                    nn = c.get_edge().getSrc();
                }
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
                    agent.set_curr_fruit(pkm);
                }
                min = temp;
                nextPkms = pkm;
            }
        }
        if (nextPkms == agent.get_curr_fruit()&& nextPkms.getNxtEater()!=null) {
            nextPkms.getNxtEater().set_curr_fruit(null);
            flags.add(nextPkms.getNxtEater());
            nextPkms.setNxtEater(agent);
        }
        agent.set_curr_fruit(nextPkms);
        nextPkms.setMin_dist(min);
        flags.remove(agent);
    }

    private static int nextNode(CL_Agent agent) {
        if(agent.getPath().isEmpty()){
            return -1;
        }
        int ans=agent.getPath().get(0).getKey();
            agent.setNextNode(agent.getPath().get(0).getKey());
        agent.getPath().remove(0);
        return ans;
    }

    public static void moveAgents(){//TODO: print status
        //List<node_data> nds;
        String lg = game.move();
        List<CL_Agent> balls = Arena.getAgents(lg, _ar.getGraph());
        ///_ar.setAgents(balls);
        String fs = game.getPokemons();
        List<CL_Pokemon> curr_pkms = Arena.json2Pokemons(fs);
        for (int a = 0; a < curr_pkms.size(); a++) {
            Arena.updateEdge(curr_pkms.get(a), _ar.getGraph());
        }
        if (!_ar.getPokemons().equals(curr_pkms)) {
            _ar.setPokemons(curr_pkms);
            if(flags.isEmpty()){
                flags.addAll(balls);
            }
            while (!flags.isEmpty()) {
                for (CL_Agent agnt : flags) {
                    chooseTarget(agnt);
                }
            }
        }
            for (CL_Agent agn : balls) {
                //TODO:add if nds is empty
                node_data n=_ar.getGraph().getNode(agn.get_curr_fruit().get_edge().getDest());
                agn.setPath(algo.shortestPath(agn.getSrcNode(), agn.get_curr_fruit().get_edge().getSrc()),n);
                int dest = nextNode(agn);
                game.chooseNextEdge(agn.getID(), dest);
            }



    }
    //TODO: move the fuck out lines 184-187
}
