package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * App that allows you to manage parking tickets for entry and exit of vehicles
 * such as cars or bikes.
 *
 * @author Salomé B.
 */
public class App {

  private static final Logger logger = LogManager.getLogger("App");

  public static void main(String[] args) {
    logger.info("Initializing Parking System");
    InteractiveShell.loadInterface();
  }

}
