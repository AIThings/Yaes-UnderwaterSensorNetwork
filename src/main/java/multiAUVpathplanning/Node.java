package multiAUVpathplanning;

import java.util.ArrayList;

public class Node implements Comparable<Node> {

    public double coordinate_X;
    public double coordinate_Y;

    public int NodeIdentifier = -1;

    public Node[][] Neighbors = new Node[3][3];

    public ArrayList<Packet> Packets = new ArrayList<Packet>();

    public Node(int X_loc, int Y_loc, int ID) {
        coordinate_X = X_loc;
        coordinate_Y = Y_loc;
        NodeIdentifier = ID;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Neighbors[i][j] = null;
            }
        }
    }

    public void AcquirePacket(Packet NewPacket) {
        Packets.add(NewPacket);
    }

    public double VoIOfferAtNode(double TimeInstant) {
        double VoIVal = 0;
        for (int i = 0; i < Packets.size(); i++) {
            VoIVal += Packets.get(i).currentVoIValue(TimeInstant);
        }
        return VoIVal;
    }

    @Override
    public int compareTo(Node Comp) {
        double TS1 =
                this.Packets.get(this.Packets.size() - 1).TimeStampAcquired;
        double TS2 =
                Comp.Packets.get(Comp.Packets.size() - 1).TimeStampAcquired;
        double TimeInstant = (TS1 > TS2) ? TS1 : TS2;
        if (this.VoIOfferAtNode(TimeInstant) < Comp
                .VoIOfferAtNode(TimeInstant)) {
            return -1;
        }
        if (this.VoIOfferAtNode(TimeInstant) > Comp
                .VoIOfferAtNode(TimeInstant)) {
            return 1;
        }
        // implement: else randomly choose between 0, 1 & -1
        return 0;
    }

}
