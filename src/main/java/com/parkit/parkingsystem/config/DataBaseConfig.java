package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage opening and closing connection to database (prod).
 *
 * @author Salomé B.
 */
public class DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseConfig");

  /**
   * Open MySQL database connection.
   *
   * @return returns connection to database
   * @throws ClassNotFoundException
   *         class doesn't exist
   * @throws SQLException
   *         couldn't connect to database
   */
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    logger.info("Create DB connection");
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/prod?serverTimezone=Europe/Paris", "root", "rootroot");
  }

  /**
   * Close MySQL database connection.
   *
   * @param con
   *         an instance of Connection that should be closed
   */
  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        logger.info("Closing DB connection");
      } catch (SQLException e) {
        logger.error("Error while closing connection", e);
      }
    }
  }

  /**
   * Close prepared statement.
   *
   * @param ps
   *         an instance of PreparedStatement that should be closed
   */
  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        logger.info("Closing Prepared Statement");
      } catch (SQLException e) {
        logger.error("Error while closing prepared statement", e);
      }
    }
  }

  /**
   * Close result set.
   *
   * @param rs
   *         an instance of ResultSet that should be closed
   */
  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        logger.info("Closing Result Set");
      } catch (SQLException e) {
        logger.error("Error while closing result set", e);
      }
    }
  }

}
