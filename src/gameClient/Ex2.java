package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.dw_graph_algorithms;
import api.game_service;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.geom.Area;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ex2 implements Runnable {
    private MyFrame _win;
    private Arena _ar;
    private static int sen, id;
    private dw_graph_algorithms algo;

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
        game_service game = Game_Server_Ex2.getServer(sen);
        init(game);
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
        _win.show();
        try{
            String info= game.toString();
            JSONObject line=new JSONObject(info);
            JSONObject meow= line.getJSONObject("GameServer");
            int numOfAg= meow.getInt("agents");
            List<CL_Pokemon> pkms=_ar.getPokemons();
            for (CL_Pokemon pk:pkms) {
                Arena.updateEdge(pk,_ar.getGraph());
            }
            for(int a = 0;a<numOfAg;a++) {
                int ind = a%pkms.size();
                CL_Pokemon c = pkms.get(ind);
                int nn = c.get_edge().getDest();
                if(c.getType()<0 ) {nn = c.get_edge().getSrc();}
                game.addAgent(nn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void initAgents(List<CL_Agent> balls){
        for (int i = 0; i < balls.size(); i++) {
            Thread k=new Thread(balls.get(i));
            k.setName(""+i);
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
}
