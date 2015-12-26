/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.dirtywater;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


/**
 *
 * @author amrish
 */
public class Train {
    public int start()
    {
        String DB_URL = "jdbc:mysql://localhost:3306/";
        String USER="root";
        String PASS="";
          Connection conn = null;
   Statement stmt = null;
     try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      
      
 
      double meanNoOfComplaints=0.00;
      double stdNoOfComplaints=0.00;
      double meanNoOfReportedCases=0.00;
      double stdNoOfReportedCases=0.00;
      double meanWeightedSum=0.00;
      double stdWeightedSum=0.00;
      double newNoOfComplaints=0.00;
      double newNoOfReportedCases=0.00;
      double w1=0.5;
      double w2=0.5;
      int noOfComplaints=0;
      int noOfReportedCases=0;
      String wardId="";
      double weightedSum=0.00;
      
      
      for(int j=1;j<=23;j++)
      {
      DescriptiveStatistics statsNoOfComplaints = new DescriptiveStatistics();
      DescriptiveStatistics statsNoOfReportedCases = new DescriptiveStatistics();
      DescriptiveStatistics statsWeightedSum = new DescriptiveStatistics();
      wardId=j+"";
      stmt = (Statement) conn.createStatement();
      String sql;
      sql = "SELECT ward_id, date, no_of_complains, no_of_reported_cases FROM dirty_water_prediction.ward_complains_cases where ward_id="+wardId;
      ResultSet rs = stmt.executeQuery(sql);

      //STEP 5: Extract data from result set
      while(rs.next()){
         //Retrieve by column name
         wardId  = rs.getString("ward_id");
         String date = rs.getObject("date").toString();
         noOfComplaints = rs.getInt("no_of_complains");
         noOfReportedCases = rs.getInt("no_of_reported_cases");

         statsNoOfComplaints.addValue(noOfComplaints);
         statsNoOfReportedCases.addValue(noOfReportedCases);
         //Display values
         //System.out.print("wardoId: " + wardId);
         //System.out.print("date: " + date);
         //System.out.print("noOfComplaints: " + noOfComplaints);
        // System.out.println("noOfReportedCases: " + noOfReportedCases);
      }
      rs.close();
      stmt.close();
      
      meanNoOfComplaints=statsNoOfComplaints.getMean();
      stdNoOfComplaints=statsNoOfComplaints.getStandardDeviation();
      meanNoOfReportedCases=statsNoOfReportedCases.getMean();
      stdNoOfReportedCases=statsNoOfReportedCases.getStandardDeviation();      
      
      stmt = (Statement) conn.createStatement();
      sql = "SELECT ward_id, date, no_of_complains, no_of_reported_cases FROM dirty_water_prediction.ward_complains_cases where ward_id="+wardId;
      rs = stmt.executeQuery(sql);

      while(rs.next()){
         //Retrieve by column name
         wardId  = rs.getString("ward_id");
         String date = rs.getObject("date").toString();
         noOfComplaints = rs.getInt("no_of_complains");
         noOfReportedCases = rs.getInt("no_of_reported_cases");

         newNoOfComplaints=(noOfComplaints - meanNoOfComplaints )/stdNoOfComplaints;
         newNoOfReportedCases= (noOfReportedCases - meanNoOfReportedCases)/stdNoOfReportedCases;
         weightedSum = w1*newNoOfComplaints + w2*newNoOfReportedCases;
         
         statsWeightedSum.addValue(weightedSum);
         
          //System.out.println("old complaints : " + noOfComplaints);
          //System.out.println("new complaints : " + newNoOfComplaints);
          //System.out.println("old cases : " + noOfReportedCases);
          //System.out.println("new cases : " + newNoOfReportedCases);
         //Display values
         //System.out.print("wardoId: " + wardId);
         //System.out.print("date: " + date);
         //System.out.print("noOfComplaints: " + noOfComplaints);
        // System.out.println("noOfReportedCases: " + noOfReportedCases);
      }
      
         meanWeightedSum=statsWeightedSum.getMean();
         stdWeightedSum = statsWeightedSum.getStandardDeviation();
         
         System.out.println(meanNoOfComplaints);
         System.out.println(stdNoOfComplaints);
         System.out.println(meanNoOfReportedCases);
         System.out.println(stdNoOfReportedCases);
      //STEP 6: Clean-up environment
      rs.close();
      stmt.close();
      conn.close();
      
            conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);

        stmt = (Statement) conn.createStatement();
        
        
      sql = "INSERT INTO dirty_water_prediction.stats VALUES (" + wardId + "," + meanNoOfComplaints + "," + meanNoOfReportedCases + "," + meanWeightedSum + "," + stdNoOfComplaints + "," + stdNoOfReportedCases + "," + stdWeightedSum + "," + w1 + "," + w2 + ")";
      stmt.executeUpdate(sql);

      }
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
      return 1;
   }//end try
    }
}
