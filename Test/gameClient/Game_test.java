package gameClient;

import api.DWGraph_Algo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.fail;

class Game_test {
    private static CL_Agent avi, beni;
    static Arena _ar;
    static DWGraph_Algo algo;
    Ex2 game;
    private static PriorityQueue<Container> queue;
    // Ex2 game = new Ex2();

    public void initGame() {
        _ar = new Arena();
        algo = new DWGraph_Algo();
        game = new Ex2();
        game.TestCheck(00, 16);
        algo.init(_ar.getGraph());
        this.chooseTarget();
        this.nextNode();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        game.checkStopTest();
    }

    @Test
    void init() {
        initGame();

    }


    void chooseTarget() {
        avi = _ar.getAgents().get(1);
        beni = _ar.getAgents().get(0);
        System.out.println(avi.get_curr_fruit());
        System.out.println(avi.getSrcNode());
        if (avi.get_curr_fruit().get_edge().getSrc() != 4) {
            fail();
        }
        if (beni.get_curr_fruit().get_edge().getSrc() != 9) {
            fail();
        }
    }

    void nextNode() {
        int aviWay = Ex2.nextNode(avi);
        int beniWay = Ex2.nextNode(beni);
        if (aviWay != 4) {
            fail();
        }
        if (beniWay != 7) {
            fail();
        }
    }

    @BeforeEach
    void checkNotConSt() {
        File graphA = new File("./data/A4");
        graphA.renameTo(new File(graphA.getParent(), "A4ASecond"));
        graphA = new File("./data/TestGraph");
        graphA.renameTo(new File(graphA.getParent(), "A4"));
    }

    @AfterEach
    void checkNotConEd() {
        File graphA = new File("./data/A4");
        graphA.renameTo(new File(graphA.getParent(), "TestGraph"));
        graphA = new File("./data/A4ASecond");
        graphA.renameTo(new File(graphA.getParent(), "A4"));
    }
}