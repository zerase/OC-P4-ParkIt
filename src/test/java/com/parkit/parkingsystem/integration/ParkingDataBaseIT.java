package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Integration tests checking that ticket content info is saved and updated properly in database.
 *
 * @author Salomé B.
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  private static void tearDown(){

  }

  @Test
  @DisplayName("Parking a car")
  public void testParkingACar() throws Exception {
    // Arrange :
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    // Act :
    parkingService.processIncomingVehicle();

    // Assert : check that a ticket is saved in DB and Parking table is updated with availability
    assertNotNull(ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber()));
    assertFalse(ticketDAO.getTicket(
            inputReaderUtil.readVehicleRegistrationNumber()).getParkingSpot().isAvailable());
  }

  @Test
  @DisplayName("Car exiting parking")
  public void testParkingLotExit() throws Exception {
    // Arrange :
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    Thread.sleep(1000);

    // Act :
    parkingService.processExitingVehicle();

    // Assert : check that the fare generated and out time are populated correctly in the database
    // assertTrue(
    //         ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber()).getPrice() > 0);
    assertEquals(
            0, ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber()).getPrice());
    assertNotNull(
            ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber()).getOutTime());
  }

  @Test
  @DisplayName("Parking a recurring car")
  public void testParkingRecurrentUser() throws Exception {
    // Arrange : 
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    Thread.sleep(500);
    parkingService.processExitingVehicle();
    Thread.sleep(500);

    // Act :
    parkingService.processIncomingVehicle();

    // Assert :
    assertTrue(
            ticketDAO.countByVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber()) > 0);
  }

}
