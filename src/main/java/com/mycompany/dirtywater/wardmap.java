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
public class wardmap extends HttpServlet {

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
            String query = "select * from " + dbName + ".ward_complains_cases where date='" + dateFormat.format(date) + "' ;";
            ResultSet rs = s.executeQuery(query);

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>\n"
                    + "<!--\n"
                    + "To change this license header, choose License Headers in Project Properties.\n"
                    + "To change this template file, choose Tools | Templates\n"
                    + "and open the template in the editor.\n"
                    + "-->\n"
                    + "<html>\n"
                    + "    <head>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width\">\n"
                    + "        <title>AMC Map</title>\n"
                    + "        <script type=\"text/javascript\" src=\"fusioncharts.js\"></script>\n"
                    + "        <script type=\"text/javascript\" src=\"fusioncharts.theme.fint.js\"></script>\n"
                    + "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js\" type=\"text/javascript\"></script>\n"
                    + "        <script type=\"text/javascript\">\n"
                    + "\n"
                    + "            FusionCharts.ready(function() {\n"
                    + "\n"
                    + "                var salesByState = new FusionCharts({\n"
                    + "                    \"type\": \"maps/france\",\n"
                    + "                    \"renderAt\": \"chartContainer\",\n"
                    + "                    \"width\": \"600\",\n"
                    + "                    \"height\": \"400\",\n"
                    + "                    \"dataFormat\": \"json\",\n"
                    + "                    \"dataSource\": {\n"
                    + "                        \"chart\": {\n"
                    + "                            \"caption\": \"Epidemic Prediction of Water Borne Diseases\",\n"
                    + "                            \"subcaption\": \"Live\",\n"
                    + "                            \"entityFillHoverColor\": \"#cccccc\",\n"
                    + "                            \"thememaps/usa\": \"fint\"\n"
                    + "                        },\n"
                    + "                        \"colorrange\": {\n"
                    + "                            \"color\": [\n"
                    + "                                {\n"
                    + "                                    \"minvalue\": \"0\",\n"
                    + "                                    \"maxvalue\": \"1\",\n"
                    + "                                    \"code\": \"#00FF00\",\n"
                    + "                                    \"displayValue\": \"Safe\"\n"
                    + "                                },\n"
                    + "                                {\n"
                    + "                                    \"minvalue\": \"1\",\n"
                    + "                                    \"maxvalue\": \"2\",\n"
                    + "                                    \"code\": \"#FFFF00\",\n"
                    + "                                    \"displayValue\": \"Warning\"\n"
                    + "                                },\n"
                    + "                                {\n"
                    + "                                    \"minvalue\": \"2\",\n"
                    + "                                    \"maxvalue\": \"3\",\n"
                    + "                                    \"code\": \"#FF0000\",\n"
                    + "                                    \"displayValue\": \"Critical\"\n"
                    + "                                }\n"
                    + "                            ]\n"
                    + "                        },\n"
                    + "                        \"data\": [\n");

            if (rs.next()) {
                String ward_id = rs.getString("ward_id");
                if (ward_id.length() == 1) {
                    ward_id = "00" + ward_id;
                } else {
                    ward_id = "0" + ward_id;
                }
                out.println("{\n"
                        + "                                \"id\": \"" + rs.getString("ward_id") + "\",\n"
                        + "                                \"value\": \"" + new Predict().start(rs.getString("ward_id"), rs.getInt("no_of_complains"), rs.getInt("no_of_reported_cases")) + "\"\n"
                        + "\n"
                        + "                            }");
            }

            while (rs.next()) {
                String ward_id = rs.getString("ward_id");
                if (ward_id.length() == 1) {
                    ward_id = "00" + ward_id;
                } else {
                    ward_id = "0" + ward_id;
                }
                out.println(",{\n"
                        + "                                \"id\": \"" + ward_id + "\",\n"
                        + "                                \"value\": \"" + new Predict().start(rs.getString("ward_id"), rs.getInt("no_of_complains"), rs.getInt("no_of_reported_cases")) + "\"\n"
                        + "\n"
                        + "                            }\n");
            }

            out.println("                        ]\n"
                    + "                    },\n"
                    + "                    \"events\": {\n"
                    + "                        \"entityClick\": function(evt, data) {\n"
                    + "                            $.ajax({\n"
                    + "                                url: \"http://localhost:8084/dirtywater/wardchart?start_date=2015-12-21&end_date=2015-12-27&ward_id=\"+data.id,\n"
                    + "                                dataType: 'json',\n"
                    + "                                success: function(data) {\n"
                    + "                                    var visitChart = new FusionCharts(data).render();\n"
                    + "                                }\n"
                    + "                            });\n"
                    + "                        }\n"
                    + "                    }\n"
                    + "                });\n"
                    + "                salesByState.render();\n"
                    + "            });\n"
                    + "        </script>\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <div id=\"chartContainer\" style=\"float:left\">A US map will load here!</div>\n"
                    + "        <div id=\"chart-container\" style=\"float:right; margin-right: 100px\">FusionCharts will render here</div>\n"
                    + "    </body>\n"
                    + "</html>\n"
                    + "");
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
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
