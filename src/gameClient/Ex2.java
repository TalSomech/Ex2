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
    private static Arena _ar;
    private static dw_graph_algorithms algo;
    public static game_service game;
    private static boolean firsRun;
    private static boolean change = false;
    private static long dt = 110;
    private static int id;
    private static PriorityQueue<Container> queue;

    public static void main(String[] args) {
        int sen=11;
        if (args.length == 2) {
            try{
            id = Integer.parseInt(args[0]);
            sen = Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                System.out.println("invalid input , try again");
                System.exit(0);
            }
        } else {
            id = popUp.getId();
            sen = popUp.getSen();
        }
        game = Game_Server_Ex2.getServer(sen);
        init(game);
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        game.startGame();
        game.login(id);
        while (game.isRunning()) {
            try {
                dt = 100;
                moveAgents();
                _win.repaint();
                _ar.setTime("Time Left: " + (double) game.timeToEnd() / 1000);
                Thread.sleep(dt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        scores _score = new scores(game);
        _score.setSize(500, 400);
        _win.setVisible(false);
        _score.setVisible(true);
        System.out.println(game.toString());
    }

    public static void init(game_service game) {
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
        _win = new MyFrame("Ex2");
        _win.setSize(1000, 700);
        try {
            String info = game.toString();
            JSONObject line = new JSONObject(info);
            JSONObject meow = line.getJSONObject("GameServer");
            int numOfAg = meow.getInt("agents");
            for (CL_Pokemon pk : pkms) {
                Arena.updateEdge(pk, _ar.getGraph());
            }
            queue = new PriorityQueue<>();
            markClosePkms(_ar.getPokemons());
            locateAgents(numOfAg, _ar.getPokemons());
            String lg = game.getAgents();
            List<CL_Agent> balls = Arena.updateAgents(lg, _ar.getGraph());
            _ar.setAgents(balls);
            _win.update(_ar);
            _win.setVisible(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void markClosePkms(List<CL_Pokemon> pkm) {
        for (CL_Pokemon curpk : pkm) {
            for (CL_Pokemon othpkm : pkm) {
                List<node_data> dista = algo.shortestPath(curpk.get_edge().getSrc(), othpkm.get_edge().getSrc());
                if (dista.size() < 2 && curpk != othpkm) {
                    if (othpkm.getClspkm() == null) {
                        curpk.setClosePkm();
                        othpkm.setClspkm(curpk);
                    } else {
                        othpkm.getClspkm().setClosePkm();
                        curpk.setClspkm(curpk.getClspkm());
                    }
                }
            }
        }
    }

    private static void locateAgents(int numOfAg, List<CL_Pokemon> pkms) {
        if (algo.isConnected()) {
            PriorityQueue<CL_Pokemon> q = new PriorityQueue<>((o1, o2) -> o2.getClosePkm() - o1.getClosePkm());
            q.addAll(_ar.getPokemons());
            for (int i = 0; i < numOfAg && i < pkms.size(); i++) {
                CL_Pokemon nn = q.poll();
                game.addAgent(nn.get_edge().getSrc());
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
                    counter++;
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
        List<CL_Agent> balls = Arena.updateAgents(lg, _ar.getGraph());
        String fs = game.getPokemons();
        List<CL_Pokemon> curr_pkms = Arena.json2Pokemons(fs);
        _ar.setAgents(balls);
        for (CL_Pokemon curr_pkm : curr_pkms) {
            Arena.updateEdge(curr_pkm, _ar.getGraph());
        }
        checkChange();
        if (change || firsRun) {
            setWays(curr_pkms);
        }
        for (CL_Agent agn : _ar.getAgents()) {
                int dest = nextNode(agn);
                game.chooseNextEdge(agn.getID(), dest);
                _ar.set_info("Agent: " + agn.getID() + ", score: " + agn.getValue(), agn.getID());

        }
    }
    private static void checkChange(){
        for (CL_Agent agnt : _ar.getAgents()) {
            if (firsRun || agnt.getPath().isEmpty()) {
                change = true;
                break;
            }
        }
    }

    private static void setWays(List<CL_Pokemon> curr_pkms){
        _ar.setPokemons(curr_pkms);
        for (CL_Agent agent : _ar.getAgents()) {
            for (CL_Pokemon pkm : curr_pkms) {
                queue.add(new Container(agent, pkm, algo));
            }
        }
        chooseTarget();
        firsRun = false;
    }

    private static void chooseTarget() {
        while (!queue.isEmpty()) {
            Container c = queue.iterator().next();
            if (c.getPok().getNxtEater() == null && c.getAgent().get_curr_fruit() == null && c.getDist() != -1) {
                if (c.getAgent().getLastEaten() != null) {
                    if(c.getAgent().getPath().size()==1)
                    if (c.getAgent().getLastEaten().equals(c.getPok().getLocation().toString())&&c.getAgent().getSpeed()>=5) {
                        c.getAgent().counter++;
                       c.getAgent().set_SDT(dt,c.getPok());
                       dt=c.getAgent().get_sg_dt();
                       if(c.getAgent().counter>2 && dt==100){
                           dt=30;
                       }
                    }
                }
                if(c.getAgent().getLastEaten()!=null) {
                    if (!c.getAgent().getLastEaten().equals(c.getPok().getLocation().toString()))
                        c.getAgent().counter = 1;
                }
                c.getAgent().set_curr_fruit(c.getPok());
                c.getPok().setNxtEater(c.getAgent());
                node_data n = _ar.getGraph().getNode(c.getPok().get_edge().getDest());
                c.getAgent().setPath(algo.shortestPath(c.getAgent().getSrcNode(), c.getPok().get_edge().getSrc()), n);
            }
            queue.poll();
        }
    }
    private static int nextNode(CL_Agent agent) {
        int ans;
        if (agent.getPath().isEmpty()) {
            return -1;
        }
        if (agent.getPath().size() == 1) {
            agent.setLastEaten(agent.get_curr_fruit().getLocation().toString());
            ans = agent.getPath().get(0).getKey();
            agent.getPath().remove(0);
            return ans;
        }
        ans = agent.getPath().get(1).getKey();
        agent.setNextNode(agent.getPath().get(1).getKey());
        agent.getPath().remove(0);
        return ans;
    }
}
