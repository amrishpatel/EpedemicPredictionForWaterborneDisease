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

/**
 *
 * @author amrish
 */
public class Predict {
    
    /*
        Values: 48, 48
        Prediction:
        0 for ward id 22
        1 for ward id 7
        2 for ward id 20
        */
    
    public int start(String wardId, int noOfComplaints, int noOfCases)
    {
        int yellowRange=3;
        int redRange=9;
           String DB_URL = "jdbc:mysql://localhost:3306/";
        String USER="root";
        String PASS="";
          Connection conn = null;
   Statement stmt = null;
   double weightedSum=0.0;
      double meanNoOfComplaints=0.00;
      double stdNoOfComplaints=0.00;
      double meanNoOfReportedCases=0.00;
      double stdNoOfReportedCases=0.00;
      double meanWeightedSum=0.00;
      double stdWeightedSum=0.00;  
      double newNoOfComplaints=0.00;
      double newNoOfReportedCases=0.00;
      double w1=0.0;
      double w2=0.0;
      int returnVal=-1;
   
     try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      
       stmt = (Statement) conn.createStatement();
      String sql;
      sql = "SELECT wardId,mean_no_of_complaints,mean_no_of_cases,mean_weighted_sum,std_no_of_complaints,std_no_of_cases,std_weighted_sum,w1,w2   FROM dirty_water_prediction.stats where wardId="+wardId;
      ResultSet rs = stmt.executeQuery(sql);
      
      if(rs.next())
      {
          wardId=rs.getString("wardId");
          meanNoOfComplaints=Double.parseDouble(rs.getString("mean_no_of_complaints"));
          meanNoOfReportedCases=Double.parseDouble(rs.getString("mean_no_of_cases"));
          meanWeightedSum = Double.parseDouble(rs.getString("mean_weighted_sum"));
          stdNoOfComplaints=Double.parseDouble(rs.getString("std_no_of_complaints"));
          stdNoOfReportedCases=Double.parseDouble(rs.getString("std_no_of_cases"));
          stdWeightedSum=Double.parseDouble(rs.getString("std_weighted_sum")); 
          w1=Double.parseDouble(rs.getString("w1"));
          w2=Double.parseDouble(rs.getString("w2"));   
      }
      else
      {
          returnVal=-1;
          return -1;
      }
      
      newNoOfComplaints=(noOfComplaints-meanNoOfComplaints)/stdNoOfComplaints;
      newNoOfReportedCases=(noOfCases-meanNoOfReportedCases)/stdNoOfReportedCases; 
      weightedSum=w1*newNoOfComplaints+w2*newNoOfReportedCases;
         System.out.println(weightedSum);
      if (weightedSum < meanWeightedSum + yellowRange*stdWeightedSum)
      {
          returnVal=0;
          return 0;
      }
      else if (weightedSum < meanWeightedSum + redRange*stdWeightedSum)
      {
          returnVal=1;
          return 1;
      }
      else
      {
          returnVal=2;
          return 2;
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
   }//end try
        return returnVal;
        
        
    }
    
}
