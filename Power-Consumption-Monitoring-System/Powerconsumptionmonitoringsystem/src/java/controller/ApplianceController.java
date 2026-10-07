/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ApplianceDao;
import dao.AuditDao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Appliance;
import model.User;

/**
 *
 * @author hoanh
 */
@WebServlet(name = "ApplianceController", urlPatterns = {"/ApplianceController"})
public class ApplianceController extends HttpServlet {

   private static final int PAGE_SIZE = 10;
   private static String trim(String s){
       return s == null ? "" : s.trim();
   }
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                req.getRequestDispatcher("nilm_form.jsp").forward(req, res);
                return;
            }
            String keyword = Optional.ofNullable(req.getParameter("q")).orElse("");
            int page = Integer.parseInt(Optional.ofNullable(req.getParameter("page")).orElse("1"));
            req.setAttribute("items", new ApplianceDao().search(keyword, page, PAGE_SIZE));
            req.setAttribute("q", keyword);
            req.setAttribute("page", page);
            req.getRequestDispatcher("nilm_list.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("LOGIN_USER");
        String action = req.getParameter("action");   // create | update | delete
        try {
            ApplianceDao dao = new ApplianceDao();
            if ("create".equals(action)) {
                String name = trim(req.getParameter("appliance_name"));
                String room = trim(req.getParameter("room"));
                String householdStr = req.getParameter("household_id");
                String ratedStr = req.getParameter("rated_w");
                
                String error = validate(name, room, householdStr, ratedStr);
                if (error != null) {
                    req.setAttribute(error, error);
                    req.getRequestDispatcher("nilm_form.jsp").forward(req, res);
                    return;
                }
                
                Appliance item = new Appliance();
                item.setAppliance_name(name);
                item.setRoom(room);
                item.setHousehold_id(Integer.parseInt(householdStr));
                item.setRated_w(Float.parseFloat(ratedStr));
                
                int newId = dao.insert(item);
                new AuditDao().append(user.getUserId(), "CREATE", "APPLIANCE", newId, null);
            }
            res.sendRedirect("ApplianceController");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
    
    private String validate(String name, String room, String householdStr, String ratedStr){
        if (name.isEmpty() || name.length() > 50) {
            return "Appliance name is required and limited 50 characters";
        }
        if (room.isEmpty() || room.length() > 30) {
            return "Room is required and limited 30 characters";
        }
        try {
            int householdId = Integer.parseInt(householdStr);
            if (householdId <= 0) {
                return "Household ID must be a positive number.";
            }
        } catch (NumberFormatException e) {
            return "Household ID must be valid number.";
        }
        try {
            float rated = Float.parseFloat(ratedStr);
            if (rated <= 0) {
                return "Rated must be a positive number.";
            }
        } catch (NumberFormatException e) {
            return "Rated must be a valid number.";
        }
        return null;
    }
}
