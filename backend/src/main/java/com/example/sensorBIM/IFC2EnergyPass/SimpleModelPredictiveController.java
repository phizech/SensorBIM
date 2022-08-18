package com.example.sensorBIM.IFC2EnergyPass;

import com.example.sensorBIM.model.Room;

import java.util.List;

class SimpleModelPredictiveController implements Strategy {

    private final double rInteriorSurface = 0.13;    // m2K/W
    private final double rOuterSurface = 0.04;       // m2K/W
    private final float rEarth = 1;                 // m2K/W TODO: Daniel checks actual value

    private double rOuterTemperature;               // Celsius
    private double rEarthTemperature;               // Celsius

    private double rTotal;                           // K/W
    private double uTotal;                           // W/K
    private double rOuterAir;                        // K/W
    private double rEarthTotal;                      // K/W

    // TODO pass model and extract values here
    public SimpleModelPredictiveController(Room room) {

    }

    public SimpleModelPredictiveController(double rInsulation, double rConcrete, double rCeiling, double rWood, double rRoof, double rPerimeterInsulation, List<Double> wallAreas, List<Double> insulationAreas, Double floorArea, Double ceilingArea, List<Double> partitionAreas) {
        init(rInsulation, rConcrete, rWood, rCeiling, rRoof, rPerimeterInsulation, wallAreas, insulationAreas, floorArea, ceilingArea, partitionAreas);
    }

    /**
     * @param rInsulation  Resistance of the insulation layer
     * @param rConcrete    Resistance of the concrete layer
     * @param rCeiling
     * @param rRoof The topmost ceiling a.k.a. the roof
     * @param ceilingArea TODO: @Markus fragen nach Deckenaufbau
     */
    private void init(double rInsulation, double rConcrete, double rWood, double rCeiling, double rRoof, double rPerimeterInsulation, List<Double> wallAreas, List<Double> insulationAreas, Double floorArea, Double ceilingArea, List<Double> partitionAreas) {
        // r wall including insulation
        var rWallInsulation = rInteriorSurface + rInsulation + rConcrete + rOuterSurface;
        var rWall = rInteriorSurface + rConcrete + rOuterSurface;
        var rWallPartition = rInteriorSurface + rConcrete + rWood + rOuterSurface;     // TODO Daniel checks standard EN 52016, Melanie checks if Wall of antechamber can be derived
        var rCeilingCombined = rInteriorSurface + rCeiling + rRoof + rOuterSurface;     // TODO Daniel checks standard EN 52016, Melanie checks if Wall of antechamber can be derived

        rEarthTotal = (rEarth + rConcrete + rInteriorSurface + rPerimeterInsulation) * floorArea;    // TODO Daniel checks with Andreas/Markus

        var sumOfWallAreas = wallAreas.stream().reduce(0.0, Double::sum);
        var sumOfInsulationAreas = insulationAreas.stream().reduce(0.0, Double::sum);
        var sumOfPartitionAreas = insulationAreas.stream().reduce(0.0, Double::sum);
        var rWallTotal = 1 / (1 / (rWallInsulation * sumOfInsulationAreas) + 1 / (rWall * sumOfWallAreas) + 1 / (rWallPartition * sumOfPartitionAreas));

        var rCeilingTotal = (rCeiling) * ceilingArea;
    }

    @Override
    public void execute() {

    }
}