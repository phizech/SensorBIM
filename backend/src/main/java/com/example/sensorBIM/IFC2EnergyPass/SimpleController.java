package com.example.sensorBIM.IFC2EnergyPass;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

// helper class for pairs of (uvalue / area)
class UValueArea {
    private Double uValue;
    private Double area;

    public Double getUValue() {
        return uValue;
    }

    public Double getArea() {
        return area;
    }

    public UValueArea(double uValue, double area) {
        this.uValue = uValue;
        this.area = area;
    }

}

public class SimpleController implements Strategy {

    private double rA;
    private double rE;
    private double capacity;

    private double[] fittedOuterTemperature;
    private double fittedEarthTemperature;

    private Semaphore sema;

    public SimpleController(int setValue) {

    }

    /**
     * @param walls    List of a walls uValue and Area
     * @param ceiling  A ceilings uValue and Area
     * @param ground   A floors / grounds uValue and Area
     * @param capacity
     */
    public SimpleController(List<UValueArea> walls, UValueArea ceiling, UValueArea ground, Double capacity) {
        this.rE = 1d / (ground.getArea() * ground.getUValue());
        this.capacity = capacity;

        double hValue = 0.0;
        for (var wall : walls) {
            hValue += (wall.getArea() * wall.getUValue());
        }
        hValue += ceiling.getUValue() * ceiling.getArea();

        // Infiltration
        // Dichte luft: 1.204 kg/m³
        double airDensity = 1.204;
        // Wärmekapazität 1.01 kg / kJ / K
        double airHeatCapacity = 1.01;

        // airExchangeRate 0.4 1/h TODO Melanie: get from BIM
        var airExchangeRate = 0.4;

        hValue += airDensity * airHeatCapacity * airExchangeRate / 3.6;
        rA = 1 / hValue;
    }

    public void computeHeatingPower() throws InterruptedException {
        sema.acquire();

        // get forecast data TODO: Zoe: Kelvin, function to get datetime and unix seconds. only insert 10h
        // time, temp
        List<Pair<Long, Double>> data = new ArrayList<>();
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (var observation : data) {
            obs.add(observation.getValue0(), observation.getValue1());
        }
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);
        fittedOuterTemperature = fitter.fit(obs.toList());

        // january until december monthly average (15th of the month).
        var earthTemperature = Arrays.asList(0.67, -0.15, 1.3, 3.56, 9.49, 14.06, 17.14, 18.08, 16.48, 12.93, 8.19, 3.79);
        // TODO: Check how this does double interpolation
        List<Double> months = Arrays.asList(1209600d, 3888000d, 6307200d, 8985600d, 11577600d, 14256000d, 16848000d, 19526400d, 22204800d, 24796800d, 27475200d, 30067200d);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        var spline = linearInterpolator.interpolate(months.stream().mapToDouble(Double::doubleValue).toArray(), earthTemperature.stream().mapToDouble(Double::doubleValue).toArray());
        fittedEarthTemperature = spline.value(0.0);

        sema.release();
    }


    public void execute() throws Exception {
        try {
            sema.acquire();

            // get current temp
            // get set temperature
            // turn on or off

            sema.release();
        } catch (InterruptedException e) {
            throw new Exception(e);
        }
    }
}