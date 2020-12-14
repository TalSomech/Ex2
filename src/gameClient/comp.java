package gameClient;

import java.util.Comparator;

public class comp implements Comparator<Container> {

    @Override
    public int compare(Container o1, Container o2) {
        return (int) (o1.getDist() - o2.getDist());
    }
}
