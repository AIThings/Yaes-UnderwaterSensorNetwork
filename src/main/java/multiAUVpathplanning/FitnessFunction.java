package main.java.multiAUVpathplanning;

//import java.io.*;
//import java.util.*;
//import java.text.*;

class FitnessFunction {

    public double AUVvelocity = 1;

    public FitnessFunction() {
    }

    public double doRawFitness(String FitnessBasis,
        boolean TranslateChromosomeForFitness, Chromosome C, Map MyMap,
        double LastPacketTS, double AUVSpeed, double DS, String DistanceType) {
        double VoI = 0;
        for (int i = 0; i < C.ChromosomeTour.length; i++) {
            VoI += TourFitness(FitnessBasis, TranslateChromosomeForFitness, C,
                    MyMap, LastPacketTS, i, AUVSpeed, DS, DistanceType);
        }
        return VoI;
    }

    public double AllToursFitness(String FitnessBasis,
        boolean TranslateChromosomeForFitness, Chromosome C, Map MyMap,
        double LastPacketTS, double AUVSpeed, double DS, String DistanceType) {
        double VoI = doRawFitness(FitnessBasis, TranslateChromosomeForFitness,
                C, MyMap, LastPacketTS, AUVSpeed, DS, DistanceType);
        return VoI;
    }

    public double TourFitness(String FitnessBasis,
        boolean TranslateChromosomeForFitness, Chromosome C, Map MyMap,
        double LastPacketTS, int AUV, double AUVSpeed, double DS,
        String DistanceType) {
        double VoI = 0;
        int i = AUV;

        if (TranslateChromosomeForFitness == true) {
            C.ChromosomeTour = MyMap.AdjustChromosome(C.ChromosomeTour,
                    C.NumOfAUVs, C.AUVTourMaxLength, DistanceType);
        }

        AUV Explorer = new AUV(AUVSpeed, DS, i);
        Explorer.CreateTour(MyMap, C.ChromosomeTour,
                C.ChromosomeTour[i].length);

        if (FitnessBasis == "Transmit") {
            double TimeToCompleteTour = Explorer
                    .ExecuteTourForMapTraversal(LastPacketTS, DistanceType);
            VoI = Explorer.VoIOfferAtAUVBasedOnTransmit(TimeToCompleteTour);
        } else if (FitnessBasis == "Retrieve") {
            Explorer.ExecuteTourForMapTraversal(LastPacketTS, DistanceType);
            VoI = Explorer.VoIOfferAtAUVBasedOnRetrieve();
        }

        return VoI;
    }

}