package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static scores _score;
    private static Arena _ar;
    private static dw_graph_algorithms algo;
    public static game_service game;
    private static boolean firsRun;
    private static boolean change = false;
    private static List<CL_Pokemon> fictivePkm;
    private static long dt = 100;
    private static int sen, id;
    private static PriorityQueue<Container> queue;

    public static void main(String[] args) {
        if (args.length == 2) {
            id = Integer.parseInt(args[0]);
            sen = Integer.parseInt(args[1]);
        } else {
            String ID = popUp.getId();
            id = Integer.parseInt(ID);
            String SEN = popUp.getSen();
            sen = Integer.parseInt(SEN);
        }
        game = Game_Server_Ex2.getServer(sen);
        init(game);
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        int ind = 0;
        game.startGame();
        //boolean keepTheFuckRunning = true;
        while (game.isRunning()) {
       // while (keepTheFuckRunning) {
            try {
                moveAgents();
                _win.repaint();
                _ar.setTime("Time Left: " + (double) game.timeToEnd() / 1000);
                Thread.sleep(dt);
                dt = 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _score = new scores(game);
        _score.setSize(500, 400);
        _win.setVisible(false);
        _score.show();
        System.out.println(game.toString());
        //System.exit(0);
    }

    public static void init(game_service game) {
        fictivePkm = new ArrayList<>();
        fictivePkm.add(new CL_Pokemon(null, 0, 0, 0, null));
        firsRun = true;
        String pks = game.getPokemons();
        String graph = game.getGraph();
        String file = saveAsString(graph);
        _ar = new Arena();
        algo = new DWGraph_Algo();
        algo.load(file);
        _ar.setGraph(algo.getGraph());
        ArrayList<CL_Pokemon> pkms = Arena.json2Pokemons(pks);
        _ar.setPokemons(pkms);
        markProblematicEdges();
        _win = new MyFrame("Ex2");
        //menu = new HashMap<>();
        _win.setSize(1000, 700);
        try {
            String info = game.toString();
            JSONObject line = new JSONObject(info);
            JSONObject meow = line.getJSONObject("GameServer");
            int numOfAg = meow.getInt("agents");
            for (CL_Pokemon pk : pkms) {
                Arena.updateEdge(pk, _ar.getGraph());
            }
            queue = new PriorityQueue<>(new comp());
            markClosePkms(pkms);
            locateAgents(numOfAg, pkms);
            String lg = game.getAgents();
            List<CL_Agent> balls = Arena.getAgents(lg, _ar.getGraph());
            _ar.setAgents(balls);
            _win.update(_ar);
            _win.setVisible(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void markProblematicEdges() {
        for (edge_data ed : ((DWGraph_DS) algo.getGraph()).getE()) {
            geo_location src = algo.getGraph().getNode(ed.getSrc()).getLocation();
            geo_location dest = algo.getGraph().getNode(ed.getDest()).getLocation();
            double dist = src.distance(dest);
            if (dist < (0.001) / 2) {
                ((edgeData) ed).setShort(true);
            }
        }
    }

    public static void markClosePkms(List<CL_Pokemon> pkm) {
        for (int i = 0; i <= pkm.size() - 1; i++) {
            for (int j = i; j <= pkm.size() - 1; j++) {
                double dist = pkm.get(i).getLocation().distance(pkm.get(j).getLocation());
                if (dist < 0.006) {
                    pkm.get(i).getClosePkm().add(pkm.get(j));
                    pkm.get(j).getClosePkm().add(pkm.get(i));
                }
            }
        }
    }

    private static void locateAgents(int numOfAg, List<CL_Pokemon> pkms) {
        if (algo.isConnected()) {
            int numOfAgentLocated = 0;
            for (CL_Pokemon pkm : pkms) {
                if (pkm.getClosePkm().size() > 1) {
                    int nn = pkm.get_edge().getDest();
                    if (pkm.getType() < 0) {
                        nn = pkm.get_edge().getSrc();
                        game.addAgent(nn);
                        numOfAgentLocated++;
                    }
                }
            }
            if (numOfAgentLocated < numOfAg) {
                for (int i = 0; i < numOfAg - numOfAgentLocated; ++i) {
                    int ind = i % pkms.size();
                    CL_Pokemon c = pkms.get(ind);
                    int nn = c.get_edge().getDest();
                    if (c.getType() < 0) {
                        nn = c.get_edge().getSrc();
                    }
                    game.addAgent(nn);
                }
            }
        } else {
            List<List<node_data>> components = ((DWGraph_Algo) algo).getComponents();
            int counter = 1;
            for (int i = 0; i < components.size() - 1; ++i) {
                int nn = components.get(i).get(0).getKey();
                game.addAgent(nn);
                counter++;
            }

            while (counter < numOfAg) {
                int max = 0;
                for (int i = 0; i < components.size(); i++) {
                    if (components.get(i).size() > max)
                        max = i;
                }
                for (int i = 0; i < numOfAg - counter; ++i) {
                    int nn = components.get(max).get(0).getKey();
                    game.addAgent(nn);
                }
            }
        }

    }

    private static String saveAsString(String jsonG) {
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


    public static void moveAgents() {
        String lg = game.move();
        List<CL_Agent> balls = Arena.getAgents(lg, _ar.getGraph());
        String fs = game.getPokemons();
        List<CL_Pokemon> curr_pkms = Arena.json2Pokemons(fs);
        _ar.setAgents(balls);
        for (int a = 0; a < curr_pkms.size(); a++) {
            Arena.updateEdge(curr_pkms.get(a), _ar.getGraph());
        }
        for (int a = 0; a < balls.size(); a++) {
            Arena.updateEdgeForAgent(balls.get(a), _ar.getGraph());
        }
        for (CL_Agent agnt : balls) {
            if (agnt.getPath() == null) {
                change = true;
                break;
            }
        }
        if (change || firsRun) {
            _ar.setPokemons(curr_pkms);
            for (CL_Agent agent : balls) {
                for (CL_Pokemon pkm : curr_pkms) {
                    queue.add(new Container(agent, pkm, algo));
                }
            }
            chooseTarget2();
            firsRun = false;
        }

        for (CL_Agent agn : balls) {
            if (agn.get_curr_fruit() != fictivePkm) {
                int dest = nextNode2(agn);
                game.chooseNextEdge(agn.getID(), dest);
                _ar.set_info("Agent: " + agn.getID() + ", score: " + agn.getValue(), agn.getID());
            }
        }
    }


    private static void chooseTarget2() {
        while (!queue.isEmpty()) {
            Container c = queue.iterator().next();
            if (c.getPok().getNxtEater() == null && c.getAgent().get_curr_fruit() == null) {
                c.getAgent().set_curr_fruit(c.getPok());
                c.getPok().setNxtEater(c.getAgent());
                node_data n = _ar.getGraph().getNode(c.getPok().get_edge().getDest());
                c.getAgent().setPath(algo.shortestPath(c.getAgent().getSrcNode(), c.getPok().get_edge().getSrc()), n);
            }
            queue.poll();
        }
    }

    private static int nextNode2(CL_Agent agent) {
        int ans;
        if (agent.getPath().isEmpty()) {
            return -1;
        }
        if (agent.getPath().size() == 1) {
            edge_data ed = _ar.getGraph().getEdge(agent.getSrcNode(), agent.getPath().get(0).getKey());
            if (((edgeData) ed).getIsShort()) dt = 30;
            if ((agent.getSpeed() >= 5) && (ed.getWeight() < 2)) dt = 30;
            ans = agent.getPath().get(0).getKey();
            return ans;
        }
        ans = agent.getPath().get(1).getKey();
        agent.setNextNode(agent.getPath().get(1).getKey());
        return ans;
    }
}
