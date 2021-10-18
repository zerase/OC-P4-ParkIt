package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test processes (incoming and exiting) of the parking.
 *
 * @author Salomé B.
 */
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

  private static ParkingService parkingService;

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
    
  private Ticket ticket;

  @BeforeEach
  private void setUpPerTest() {
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    } catch (Exception e) {
      e.printStackTrace();
      throw  new RuntimeException("Failed to set up test mock objects");
    }
  }

  @Test
  @DisplayName("Exiting vehicle")
  public void processExitingVehicleTest() {
    when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
    when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    parkingService.processExitingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }
    
  @Test
  @DisplayName("Incoming vehicle")
  public void processIncomingVehicleTest() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    parkingService.processIncomingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO).saveTicket(any(Ticket.class));
  }

}
