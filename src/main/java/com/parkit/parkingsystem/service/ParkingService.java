package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage the processes of the parking.
 *
 * @author Salomé B.
 */
public class ParkingService {

  private static final Logger logger = LogManager.getLogger("ParkingService");

  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDAO parkingSpotDAO;
  private  TicketDAO ticketDAO;
  
  private int pctDiscount = Fare.PCT_DISCOUNT_REC_USERS;

  /**
   * Constructor of ParkingService.
   *
   * @param inputReaderUtil
   *         the user entry
   * @param parkingSpotDAO
   *         get and update parking spot in database
   * @param ticketDAO
   *         get and update ticket
   */
  public ParkingService(InputReaderUtil inputReaderUtil,
                        ParkingSpotDAO parkingSpotDAO,
                        TicketDAO ticketDAO) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDAO = parkingSpotDAO;
    this.ticketDAO = ticketDAO;
  }

  /**
   * Capture info to verify and save ticket in database.
   * Process when vehicle enter.
   */
  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehicleRegNumber();
        final int ticketsQuantity = ticketDAO.countByVehicleRegNumber(vehicleRegNumber);
        parkingSpot.setAvailable(false);
        parkingSpotDAO.updateParking(parkingSpot); /* allot this parking space and mark
                                                      it's availability as false */
        Date inTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setDiscount((ticketsQuantity > 0) ? pctDiscount : 0);
        ticketDAO.saveTicket(ticket);
        System.out.println("Generated Ticket and saved in DB");
        if (ticketsQuantity > 0) {
          System.out.println("Welcome back! As a recurring user of our parking lot, "
                               + "you'll benefit from a " + pctDiscount + "% discount.");
        }
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber
                           + " is:" + inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }
  }

  private String getVehicleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  /**
   * Get the next parking spot number available.
   *
   * @return parkingSpot
   *         returns the number of parking spot available
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehicleType();
      parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }
    return parkingSpot;
  }

  private ParkingType getVehicleType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }
  
  /**
   * Update out-time and calculate the fare for a user.
   * Process when vehicle exit.
   */
  public void processExitingVehicle() {
    try {
      String vehicleRegNumber = getVehicleRegNumber();
      int ticketsQuantity = ticketDAO.countByVehicleRegNumber(vehicleRegNumber);
      Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
      Date outTime = new Date();
      ticket.setOutTime(outTime);
      ticket.setDiscount((ticketsQuantity > 0) ? pctDiscount : 0);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDAO.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber()
                           + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }
  }

}
