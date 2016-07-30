package pathplanning;



import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import agents.ProgressState;
import pathplanning.Learning.Action;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;

public class testQlearning {

    @Ignore
    public void test() {
        HashMap<String, Double> hashmap1 = new HashMap();
        HashMap<Map.Entry<String, Double>, Double> hashmap2 = new HashMap();
        Map<String, Double> treeMap = new TreeMap<String, Double>();
        SortedSet<Map.Entry<String, Double>> sortedset =
                new TreeSet<Map.Entry<String, Double>>(
                        new Comparator<Map.Entry<String, Double>>() {
                            public int compare(Map.Entry<String, Double> e1,
                                Map.Entry<String, Double> e2) {
                                return e2.getValue().compareTo(e1.getValue());
                            }
                        });

        ProgressState state = new ProgressState("S1");
        Map.Entry<ProgressState, Double> stateEntry =
                new AbstractMap.SimpleEntry<ProgressState, Double>(state, 0.0);

        Map.Entry<String, Double> entry =
                new AbstractMap.SimpleEntry<String, Double>("Entry", 0.0);

        hashmap1.put("Entry1", 4.0);
        hashmap1.put("Entry2", 2.0);
        hashmap1.put("Entry3", 8.0);
        hashmap1.put("Entry4", 1.0);

        hashmap2.put(entry, 1.0);
        hashmap2.put(entry, 2.0);
        entry = new AbstractMap.SimpleEntry<String, Double>("Entry", 0.0);
        hashmap2.put(entry, 3.0);
        hashmap2.put(entry, 4.0);

        TextUi.println("Unsorted : " + hashmap1.toString());
        treeMap = new TreeMap<String, Double>(hashmap1);

        TextUi.println("Sorted : " + treeMap.toString());

        sortedset.addAll(hashmap1.entrySet());
        TextUi.println("Sorted Set: " + sortedset.toString());
        // assert ("Not yet implemented") != null;

    }

    @Test
    public void test2() {
        Map<Map.Entry<ProgressState, Action>, Double> QTable =
                new TreeMap<Entry<ProgressState, Action>, Double>(
                        new Comparator<Map.Entry<ProgressState, Action>>() {
                            public int compare(
                                Entry<ProgressState, Action> arg0,
                                Entry<ProgressState, Action> arg1) {
                                if (arg0.getKey().getStateName()
                                        .equals(arg1.getKey().getStateName()))
                                    return arg0.getValue()
                                            .compareTo(arg1.getValue());
                                return 1;
                            }
                        });
        Action action = Action.DOWN;
        ProgressState state = new ProgressState("S1", new Location(0, 0));
        Map.Entry<ProgressState, Action> retValentry =
                new AbstractMap.SimpleEntry<ProgressState, Action>(state,
                        action);
        QTable.put(retValentry, 10.0);

        action = Action.UP;
        retValentry = new AbstractMap.SimpleEntry<ProgressState, Action>(state,
                action);
        QTable.put(retValentry, 10.0);

        action = Action.RIGHT;
        retValentry = new AbstractMap.SimpleEntry<ProgressState, Action>(state,
                action);
        QTable.put(retValentry, 10.0);
        QTable.put(retValentry, 20.0);
        QTable.put(retValentry, 30.0);

        action = Action.UP;

        ProgressState state2 = new ProgressState("S1", new Location(0, 0));
        Map.Entry<ProgressState, Action> retValentry2 =
                new AbstractMap.SimpleEntry<ProgressState, Action>(state2,
                        action);

        TextUi.println(QTable.get(retValentry2));
    }

}
