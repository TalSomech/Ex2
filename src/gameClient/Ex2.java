package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Ex2 implements Runnable {
    private MyFrame _win;
    private static Arena _ar;
    private static int sen, id;
    private static dw_graph_algorithms algo;
    public static game_service game;
    public static void main(String[] args) {
        if (args.length == 2) {
            sen = Integer.parseInt(args[0]);
            id = Integer.parseInt(args[1]);
        } else {
            sen = 1;
            id = 111111;
        }
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        game = Game_Server_Ex2.getServer(sen);
        init(game);
        int ind=0;
        long dt=100;
        game.startGame();
        while(game.isRunning()) {
            moveAgents(game, _ar.getGraph());
            try {
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

    public void init(game_service game) {
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
        _win.update(_ar);
        _win.setVisible(true);
        try {
            String info = game.toString();
            JSONObject line = new JSONObject(info);
            JSONObject meow = line.getJSONObject("GameServer");
            int numOfAg = meow.getInt("agents");
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
            initAgents(Arena.getAgents(game.getAgents(),_ar.getGraph()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initAgents(List<CL_Agent> balls) {
        for (int i = 0; i < balls.size(); i++) {
            Thread k = new Thread(balls.get(i));
            k.start();
        }
    }

    public String saveAsString(String jsonG) {
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

    public static synchronized void chooseTarget(CL_Agent agent) {
        String fs =  game.getPokemons();
        List<CL_Pokemon> pkms = Arena.json2Pokemons(fs);
        double min = Integer.MAX_VALUE;
        double temp = 0;
        CL_Pokemon nextPkms = null;
        for (CL_Pokemon pkm : pkms) {
            temp = algo.shortestPathDist(agent.getNextNode(), pkm.get_edge().getSrc());
            if (temp < min) {
                if (temp < pkm.getMin_dist()) {
                    agent.set_curr_fruit(pkm);
                }
                min = temp;
                nextPkms = pkm;
            }
        }
        if (nextPkms == agent.get_curr_fruit()) {
            nextPkms.getNxtEater().set_curr_fruit(null);
            nextPkms.setNxtEater(agent);
        }
        agent.set_curr_fruit(nextPkms);
      //  return //nextPkms.get_edge().getSrc();
    }

    private static int nextNode(List<node_data> l, CL_Agent agent) {
        if(l.isEmpty()){
            return -1;
        }
        int ans=l.get(0).getKey();
            agent.setNextNode(l.get(0).getKey());
            l.remove(0);
        return ans;
    }

    private static void moveAgents(game_service game, directed_weighted_graph gg) {//TODO: print status
        List<node_data> nds;
        String lg = game.move();
        List<CL_Agent> balls = Arena.getAgents(lg, gg);
        _ar.setAgents(balls);
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        for(int a = 0;a<ffs.size();a++) {
            Arena.updateEdge(ffs.get(a),gg);
        }
        for (CL_Agent agn:balls) {
            if(agn.get_curr_fruit()!=null) {
                nds = algo.shortestPath(agn.getSrcNode(), agn.get_curr_fruit().get_edge().getSrc());
                nds.add(nds.size() - 1, gg.getNode(agn.get_curr_fruit().get_edge().getDest()));
                int dest = nextNode(nds, agn);
                game.chooseNextEdge(agn.getID(), dest);
            }
        }
    }
}
