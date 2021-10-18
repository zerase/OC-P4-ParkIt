package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test service calculate the price.
 *
 * @author Salomé B.
 */
public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;
    
  // To round the price
  private double roundFare(double price) {
    return Math.round(price * 100) / 100.0;
  }

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
  }

  @Test
  @DisplayName("Calculate fare for a CAR")
  public void calculateFareCar() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  @DisplayName("Calculate fare for a BIKE")
  public void calculateFareBike() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  @DisplayName("Exception when calculate fare for UNKNOWN type")
  public void calculateFareUnkownType() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  @DisplayName("Exception when out-time is before in-time")
  public void calculateFareBikeWithFutureInTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  @DisplayName ("Calculate fare with less than 1 hour for a BIKE")
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); /* 45 minutes parking time should
                                                                      give 3/4th parking fare */
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  @DisplayName ("Calculate fare with less than 1 hour for a CAR")
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); /* 45 minutes parking time should
                                                                      give 3/4th parking fare */
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  @DisplayName ("Calculate fare with more than 1 day for a CAR")
  public void calculateFareCarWithMoreThanADayParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); /* 24 hours parking time
                                                                           should give (24 * parking
                                                                           fare per hour) */
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }
    
  @Test
  @DisplayName ("Calculate fare with more than 1 day for a BIKE")
  public void calculateFareBikeWithMoreThanADayParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); /* 24 hours parking time
                                                                           should give (24 * parking
                                                                           fare per hour) */
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(roundFare(24 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

}
