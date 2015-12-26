/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.dirtywater;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author akshar
 */
public class wardchart extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    private static String dbName = "dirty_water_prediction";
    private static String dbUser = "root";
    private static String dbPassword = "";

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        try {
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            System.out.println(dateFormat.format(date));

            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", dbUser, dbPassword);
            Statement s = conn.createStatement();
           // String query = "select * from " + dbName + ".ward_complains_cases where date <= '" + "2015-12-19"/*dateFormat.format(date)*/ + " and date >= 2015-12-13 and ward_id = 1 ' ;";
            String query = "SELECT * FROM " + dbName + ".ward_complains_cases  where date <= '"+ request.getParameter("end_date")+"' and date >= '"+ request.getParameter("start_date")+"' and ward_id = "+ Integer.parseInt(request.getParameter("ward_id"))+" ;";
            ResultSet rs = s.executeQuery(query);
            
            String jsonWardChart = "{\n"
                    + "                    \"type\": \"msline\",\n"
                    + "                    \"renderAt\": \"chart-container\",\n"
                    + "                    \"width\": \"550\",\n"
                    + "                    \"height\": \"350\",\n"
                    + "                    \"dataFormat\": \"json\",\n"
                    + "                    \"dataSource\": {\n"
                    + "                        \"chart\": {\n"
                    + "                            \"caption\": \"Water Hazard Indicators\",\n"
                    + "                            \"subCaption\": \"2015\",\n"
                    + "                            \"captionFontSize\": \"14\",\n"
                    + "                            \"subcaptionFontSize\": \"14\",\n"
                    + "                            \"subcaptionFontBold\": \"0\",\n"
                    + "                            \"paletteColors\": \"#0075c2,#1aaf5d\",\n"
                    + "                            \"bgcolor\": \"#ffffff\",\n"
                    + "                            \"showBorder\": \"0\",\n"
                    + "                            \"showShadow\": \"0\",\n"
                    + "                            \"showCanvasBorder\": \"0\",\n"
                    + "                            \"usePlotGradientColor\": \"0\",\n"
                    + "                            \"legendBorderAlpha\": \"0\",\n"
                    + "                            \"legendShadow\": \"0\",\n"
                    + "                            \"showAxisLines\": \"0\",\n"
                    + "                            \"showAlternateHGridColor\": \"0\",\n"
                    + "                            \"divlineThickness\": \"1\",\n"
                    + "                            \"divLineIsDashed\": \"1\",\n"
                    + "                            \"divLineDashLen\": \"1\",\n"
                    + "                            \"divLineGapLen\": \"1\",\n"
                    + "                            \"xAxisName\": \"Date\",\n"
                    + "                            \"yAxisName\": \"Numbers\",\n"
                    + "                            \"showValues\": \"0\"\n"
                    + "                        },\n"
                    + "                        \"categories\": [\n"
                    + "                            {\n"
                    + "                                \"category\": [\n";
            if(rs.next()) {
                System.out.println("Start Sequence");
                jsonWardChart = jsonWardChart + "                                    {\"label\": \"" +  rs.getDate("date").toString().replaceAll("2015-", "") + "\"}\n";
            }
            while(rs.next()) {
                System.out.println("Subsequent Sequence");
                jsonWardChart = jsonWardChart + ",                                    {\"label\": \"" +  rs.getDate("date").toString().replaceAll("2015-", "") + "\"}\n";
            } 
            jsonWardChart = jsonWardChart       + "                                ]\n"
                    + "                            }\n"
                    + "                        ],\n"
                    + "                        \"dataset\": [\n"
                    + "                            {\n"
                    + "                                \"seriesname\": \"Number of complains\",\n"
                    + "                                \"data\": [\n";
            if(rs.first()) {
                jsonWardChart = jsonWardChart + "                                    {\"value\": \"" +  rs.getInt("no_of_complains") + "\"}\n";
            }
            while(rs.next()) {
                jsonWardChart = jsonWardChart + ",                                    {\"value\": \"" +  rs.getInt("no_of_complains") + "\"}\n";
            }
                    
                   jsonWardChart = jsonWardChart + "                                ]\n"
                    + "                            },\n"
                    + "                            {\n"
                    + "                                \"seriesname\": \"Number of cases reported\",\n"
                    + "                                \"data\": [\n";
                   if(rs.first()) {
                jsonWardChart = jsonWardChart + "                                    {\"value\": \"" +  rs.getInt("no_of_reported_cases") + "\"}\n";
            }
            while(rs.next()) {
                jsonWardChart = jsonWardChart + ",                                    {\"value\": \"" +  rs.getInt("no_of_reported_cases") + "\"}\n";
            }
            
                   jsonWardChart = jsonWardChart + "                                ]\n"
                    + "                            }\n"
                    + "                        ],\n"
                    + "                        \"trendlines\": [\n"
                    + "                            {\n"
                    + "                                \"line\": [\n"
                    + "                                    {\n"
                    + "                                        \"startvalue\": \"1\",\n"
                    + "                                        \"color\": \"#6baa01\",\n"
                    + "                                        \"valueOnRight\": \"1\",\n"
                    + "                                        \"displayvalue\": \"Average\"\n"
                    + "                                    }\n"
                    + "                                ]\n"
                    + "                            }\n"
                    + "                        ]\n"
                    + "                    }\n"
                    + "                }"; 
            
            /* TODO output your page here. You may use following sample code. */
            out.println(jsonWardChart);
        } catch (Exception ex) {
            ex.printStackTrace();

        }  finally {
            out.close();
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(wardmap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
