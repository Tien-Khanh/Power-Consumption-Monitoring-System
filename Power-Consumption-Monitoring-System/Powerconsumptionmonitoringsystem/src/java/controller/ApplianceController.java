/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ApplianceDao;
import dao.AuditDao;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
            ApplianceDao dao = new ApplianceDao();
            if ("rooms".equals(action)) {
                int householdId;
                try {
                    householdId = Integer.parseInt(req.getParameter("householdId"));
                } catch (NumberFormatException e) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid household ID");
                    return;
                }
                if (householdId <= 0 || !dao.findHouseholdIds().contains(householdId)) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid household ID");
                    return;
                }
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(toJson(dao.findRoomsByHouseholdId(householdId)));
                return;
            }
            if ("add".equals(action)) {
                req.setAttribute("editing", false);
                req.getRequestDispatcher("nilm_form.jsp").forward(req, res);
                return;
            }
            if ("edit".equals(action)) {
                int id = parseId(req.getParameter("id"));
                if (id <= 0) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appliance ID");
                    return;
                }
                Appliance item = dao.findById(id);
                if (item == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                req.setAttribute("appliance", item);
                req.setAttribute("editing", true);
                req.getRequestDispatcher("nilm_form.jsp").forward(req, res);
                return;
            }
            String keyword = Optional.ofNullable(req.getParameter("q")).orElse("");
            int page = Math.max(1, Integer.parseInt(Optional.ofNullable(req.getParameter("page")).orElse("1")));
            List<Integer> householdIds = dao.findHouseholdIds();
            String householdIdValue = Optional.ofNullable(req.getParameter("householdId")).orElse("");
            String room = Optional.ofNullable(req.getParameter("room")).orElse("");
            List<String> rooms = new ArrayList<>();
            List<Appliance> items;
            String searchError = null;
            if ("1".equals(req.getParameter("search"))) {
                if (householdIdValue.isEmpty()) {
                    room = "";
                    items = dao.search("", page, PAGE_SIZE + 1);
                } else {
                    int householdId;
                    try {
                        householdId = Integer.parseInt(householdIdValue);
                    } catch (NumberFormatException e) {
                        householdId = -1;
                    }
                    if (!householdIds.contains(householdId)) {
                        searchError = "Please select a valid Household ID.";
                        householdIdValue = "";
                        room = "";
                        items = new ArrayList<>();
                    } else {
                        rooms = dao.findRoomsByHouseholdId(householdId);
                        if (!room.isEmpty() && !rooms.contains(room)) {
                            searchError = "Please select a valid room for this Household ID.";
                            room = "";
                            items = new ArrayList<>();
                        } else {
                            items = dao.searchByHousehold(householdId, room, page, PAGE_SIZE + 1);
                        }
                    }
                }
            } else {
                householdIdValue = "";
                room = "";
                items = dao.search(keyword, page, PAGE_SIZE + 1);
            }
            boolean hasNextPage = items.size() > PAGE_SIZE;
            if (hasNextPage) {
                items.remove(PAGE_SIZE);
            }
            req.setAttribute("items", items);
            req.setAttribute("q", keyword);
            req.setAttribute("page", page);
            req.setAttribute("hasNextPage", hasNextPage);
            req.setAttribute("householdIds", householdIds);
            req.setAttribute("householdId", householdIdValue);
            req.setAttribute("rooms", rooms);
            req.setAttribute("room", room);
            req.setAttribute("searchError", searchError);
            req.getRequestDispatcher("nilm_list.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String toJson(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"');
            for (char character : values.get(i).toCharArray()) {
                switch (character) {
                    case '"':
                        json.append("\\\"");
                        break;
                    case '\\':
                        json.append("\\\\");
                        break;
                    case '\b':
                        json.append("\\b");
                        break;
                    case '\f':
                        json.append("\\f");
                        break;
                    case '\n':
                        json.append("\\n");
                        break;
                    case '\r':
                        json.append("\\r");
                        break;
                    case '\t':
                        json.append("\\t");
                        break;
                    default:
                        if (character < 0x20) {
                            json.append(String.format("\\u%04x", (int) character));
                        } else {
                            json.append(character);
                        }
                        break;
                }
            }
            json.append('"');
        }
        return json.append(']').toString();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("LOGIN_USER");
        String action = req.getParameter("action");
        try {
            ApplianceDao dao = new ApplianceDao();
            if ("delete".equals(action)) {
                int id = parseId(req.getParameter("id"));
                if (id <= 0) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appliance ID");
                    return;
                }
                if (dao.delete(id) == 0) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                new AuditDao().append(user.getUserId(), "DELETE", "APPLIANCE", id, null);
                redirectToList(req, res);
                return;
            }
            if ("create".equals(action) || "update".equals(action)) {
                boolean editing = "update".equals(action);
                int id = editing ? parseId(req.getParameter("id")) : 0;
                if (editing && id <= 0) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appliance ID");
                    return;
                }
                Appliance item = new Appliance();
                item.setAppliance_id(id);
                String name = trim(req.getParameter("appliance_name"));
                String room = trim(req.getParameter("room"));
                String householdStr = req.getParameter("household_id");
                String ratedStr = req.getParameter("rated_w");

                String error = validate(name, room, householdStr, ratedStr);
                if (error != null) {
                    item.setAppliance_name(name);
                    item.setRoom(room);
                    item.setHousehold_id(parsePositiveInt(householdStr));
                    item.setRated_w(parsePositiveFloat(ratedStr));
                    req.setAttribute("appliance", item);
                    req.setAttribute("editing", editing);
                    req.setAttribute("error", error);
                    req.getRequestDispatcher("nilm_form.jsp").forward(req, res);
                    return;
                }

                item.setAppliance_name(name);
                item.setRoom(room);
                item.setHousehold_id(Integer.parseInt(householdStr));
                item.setRated_w(Float.parseFloat(ratedStr));

                if (editing) {
                    if (dao.update(item) == 0) {
                        res.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    new AuditDao().append(user.getUserId(), "UPDATE", "APPLIANCE", id, null);
                } else {
                    int newId = dao.insert(item);
                    new AuditDao().append(user.getUserId(), "CREATE", "APPLIANCE", newId, null);
                }
                redirectToList(req, res);
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appliance action");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private float parsePositiveFloat(String value) {
        try {
            float parsed = Float.parseFloat(value);
            return parsed > 0 && !Float.isInfinite(parsed) && !Float.isNaN(parsed) ? parsed : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder url = new StringBuilder(req.getContextPath()).append("/ApplianceController?page=");
        String page = req.getParameter("page");
        try {
            url.append(Math.max(1, Integer.parseInt(page)));
        } catch (NumberFormatException e) {
            url.append('1');
        }
        if ("1".equals(req.getParameter("search"))) {
            url.append("&search=1");
            appendQueryParameter(url, "householdId", req.getParameter("householdId"));
            appendQueryParameter(url, "room", req.getParameter("room"));
            appendQueryParameter(url, "q", req.getParameter("q"));
        }
        res.sendRedirect(url.toString());
    }

    private void appendQueryParameter(StringBuilder url, String name, String value) throws IOException {
        url.append('&').append(name).append('=')
                .append(URLEncoder.encode(value == null ? "" : value, "UTF-8"));
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
            if (rated <= 0 || Float.isInfinite(rated) || Float.isNaN(rated)) {
                return "Rated must be a positive number.";
            }
        } catch (NumberFormatException e) {
            return "Rated must be a valid number.";
        }
        return null;
    }
}
