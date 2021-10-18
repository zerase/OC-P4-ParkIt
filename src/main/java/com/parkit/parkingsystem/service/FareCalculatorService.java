package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Calculate the price.
 *
 * @author Salomé B.
 */
public class FareCalculatorService {

  /**
  * Calculate the fare according to the parking time.
  *
  * @param ticket
  *         an instance of Ticket object
  */
  public void calculateFare(Ticket ticket) {

    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
              "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    double inHour = ticket.getInTime().getTime(); //time in milliseconds
    double outHour = ticket.getOutTime().getTime(); //time in milliseconds

    double duration = (outHour - inHour) / (60 * 60 * 1000); //time in decimal hours

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        ticket.setPrice(roundFare(duration * Fare.CAR_RATE_PER_HOUR));
        break;
      }
      case BIKE: {
        ticket.setPrice(roundFare(duration * Fare.BIKE_RATE_PER_HOUR));
        break;
      }
      default: 
        throw new IllegalArgumentException("Unkown Parking Type");
    }
  }

  //To round the price
  private double roundFare(double price) {
    return Math.round(price * 100) / 100.0;
  }

}